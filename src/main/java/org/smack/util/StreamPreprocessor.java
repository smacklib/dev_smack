/*
 * Copyright © 2016 Daimler TSS. All Rights Reserved.
 *
 * Reproduction or transmission in whole or in part, in any form or by any
 * means, is prohibited without the prior written consent of the copyright
 * owner.
 */
package org.smack.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

/**
 * Uses a Stream and resolves C-like preprocessor commands.
 *
 * @version $Id$
 * @author OTTALE
 */
public class StreamPreprocessor extends InputStream{
    /**
     * Is used by the {@link StreamPreprocessor} to get a stream form a resource name.
     * @see Resolver#resolveName
     */
    public static interface Resolver{
        /**
         * Get an InputStream from a name.
         * @param name the name of the resource (kind of URI but not a real URI).
         * @return {@link InputStream} for the passed name. Does not return {@code null}
         * @throws IOException If the name cannot be resolved.
         */
        InputStream resolveName(String name) throws IOException;

        /**
         * Create a new Resolver that is repositioned to the passed position, if necessary.
         * @param name the name of the resource (kind of URI but not a real URI).
         * @return a Resolver matching the passed filename
         * @throws IOException In case of an error.
         */
        Resolver reposition(String name) throws IOException;
    }

    /**
     * The InputStream from the resource handily wrapped.
     */
    final private LineNumberReader lnr;

    /**
     * The Resolver to get other {@link #InputStream}s from a resource name.
     */
    final private Resolver res;

    /**
     * The character set used to convert binary to textual data and vice versa.
     */
    final private Charset cs;

    /**
     * If this is not null read() will return {@link #nextHierarchy#read()}
     */
    private InputStream currentInputStream = null;

    /**
     * Create an instance based on the platform's default character set.
     * @param is InputStream to process.
     * @param res The Resolver used to resolve include directives.
     */
    public StreamPreprocessor(InputStream is, Resolver res){
        this(is,res,Charset.defaultCharset());
    }

    /**
     * Create an instance based on an explicitly defined character set.
     * @param is InputStream to process.
     * @param res The Resolver used to resolve include directives.
     * @param cs The character set to use in character transformations.
     */
    public StreamPreprocessor(InputStream is, Resolver res, Charset cs) {
        this.cs = cs;
        this.res = res;
        lnr = new LineNumberReader(new InputStreamReader(is,cs));
    }

    @Override
    public int read() throws IOException
    {
        if(currentInputStream != null){
            int result = currentInputStream.read();
            if(result != -1){
                return result;
            }
            currentInputStream.close();
            currentInputStream = null;
        }

        String currentLineStr = lnr.readLine();
        if(currentLineStr == null)
            return -1;
        currentLineStr += System.lineSeparator();

        currentInputStream = preProcess(currentLineStr);
        return currentInputStream.read();

    }

    /**
     * Evaluates what preprocessor command was read and execute the corresponding function.
     *
     * This is the place where you should add a new preprocessor instructions.
     *
     * @param pPLine the raw line which contains the preprocessor commands(e.g. #Include).
     * @return The InputStream to read from next.
     *
     * @throws IOException   if the preprocessor instruction is unknown or<br>
     *                       if the preprocessor command execution led to an Exception.
     */
    private InputStream preProcess(String pPLine) throws IOException {
        if(pPLine.trim().startsWith("#include")){
            return pPComandInclude(pPLine);
        }
//        //add other preprocessor commands here
//        else if(string.startsWith("#OtherPreProcessorInstructions"){
//            ppCommandOtherPPAction();
//        }
        return new ByteArrayInputStream(pPLine.getBytes(cs));
    }

    /**
     * Uses the {@link #Resolver} (res) to set the <code>nextHierarchy</code> attribute.
     *
     * @param includeLine the raw line starting with #include (ignoring what is before "#include ").
     * @throws IOException if an I/O Exception occurs while loading the resource.
     */
    private InputStream pPComandInclude(String includeLine) throws IOException {

        // TODO better use a regular expression finding:
        // <space> # <space> include <space> optional-quoted argument

        String filterStr = "#include ";
        int i = includeLine.indexOf(filterStr);
        int debugIndex = i+filterStr.length();
        String name = includeLine.substring(debugIndex).trim();

        name = StringUtil.trim( name, "\"" );

        InputStream newStream = res.resolveName(name);
        Resolver newResolver = res.reposition(name);
        return new StreamPreprocessor(newStream, newResolver);
    }

    @Override
    public void close() throws IOException
    {
        if ( currentInputStream == null )
            return;

        currentInputStream.close();
        currentInputStream = null;
    }

    @Override
    public int read( byte[] b ) throws IOException
    {
        return read( b, 0, b.length );
    }

    /**
     * Differs in exception handling from the base class implementation.
     * The base class propagates an exception only when reading the first
     * byte and ignores any follow-up exceptions.  We do not want this
     * and propagate all exceptions.
     */
    @Override
    public int read( byte[] b, int off, int len ) throws IOException
    {
        for ( int i = 0 ; i < len ; i++ )
        {
            int c = read();

            if ( c == -1 )
                return i > 0 ? i : -1;

            b[ i+off ] = (byte)c;
        }

        return len;
    }
}
