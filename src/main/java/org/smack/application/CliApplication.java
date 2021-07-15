/* $Id$
 *
 * Copyright © 2013-2019 Michael G. Binz
 */
package org.smack.application;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.SourceVersion;

import org.smack.util.JavaUtil;
import org.smack.util.ReflectionUtil;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.collections.MultiMap;
import org.smack.util.converters.StringConverter;
import org.smack.util.converters.StringConverter.Converter;

/**
 * A base class for console applications.
 *
 * @author MICBINZ
 */
abstract public class CliApplication
{
    private static final Logger LOG =
            Logger.getLogger( CliApplication.class.getName() );

    /**
     * Used to mark cli command operations.
     */
    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.METHOD )
    protected @interface Command {
        String name() default StringUtil.EMPTY_STRING;
        /**
         * @deprecated  Use the @Named annotation.
         */
        String[] argumentNames() default {};
        /**
         * @deprecated Use Command.description.
         */
        String shortDescription() default StringUtil.EMPTY_STRING;
        String description() default StringUtil.EMPTY_STRING;
    }

    /**
     * Used to mark cli properties.
     */
    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.FIELD )
    protected @interface Property {
        /**
         * Single dash arg.
         */
        String name() default StringUtil.EMPTY_STRING;
        String description() default StringUtil.EMPTY_STRING;
    }

    /**
     * Used to add information on the implementation class.
     */
    @Retention( RetentionPolicy.RUNTIME )
    @Target( {ElementType.TYPE, ElementType.PARAMETER} )
    public @interface Named {
        String value()
            default StringUtil.EMPTY_STRING;
        String description()
            default StringUtil.EMPTY_STRING;
    }

    /**
     * A name to be used if the command should be callable without
     * a dedicated name.
     */
    private static final String UNNAMED = "*";

    /**
     * A map of all commands implemented by this cli. Keys are
     * command name and number of arguments, the value represents
     * the respective method.
     */
    private final MultiMap<String, Integer, CommandHolder> _commandMap =
            getCommandMap( getClass() );

    private final Map<String,PropertyHolder> _propertyMap =
            getPropertyMap( this );

    private static final StringConverter _converters =
            ServiceManager.getApplicationService( StringConverter.class );

    protected final static <T> void addConverter( Class<T> cl, Converter<String,T> c )
    {
        _converters.put( cl, c );
    }

    static
    {
        addConverter(
                File.class,
                CliApplication::stringToFile );
    }

    private String _currentCommand =
            StringUtil.EMPTY_STRING;

    protected final String currentCommand()
    {
        return _currentCommand;
    }

    /**
     * A fallback called if no command was passed or the passed command was
     * unknown.
     *
     * @param argv
     *            The received command line.
     * @throws Exception In case of errors.
     */
    protected void defaultCmd( String[] argv )
            throws Exception
    {
        err( usage() );
    }

    /**
     * Perform the launch of the cli instance.
     */
    private void launchInstance( String[] argv )
            throws Exception
    {
        if ( argv.length == 0 ) {
            defaultCmd(argv);
            return;
        }

        if ( argv.length == 1 && argv[0].equals("?") ) {
            err(usage());
            return;
        }

        argv = processProperties( argv );

        var cmdName = argv[0].toLowerCase();

        CommandHolder selectedCommand = _commandMap.get(
            cmdName,
            Integer.valueOf(argv.length - 1) );

        if ( selectedCommand != null )
        {
            // We found a matching command.
            _currentCommand =
                    selectedCommand.getName() ;
            selectedCommand.execute(
                    Arrays.copyOfRange( argv, 1, argv.length ) );
            return;
        }

        // No command matched, so we check if there are commands
        // where at least the command name matches.
        Map<Integer, CommandHolder> possibleCommands =
                _commandMap.getAll( cmdName );
        if ( possibleCommands.size() > 0 )
        {
            err( "%s%n",
                    "Parameter count does not match. Available alternatives:" );
            err( "%s%n",
                    getCommandsUsage(possibleCommands, argv));
            return;
        }

        // Check if we got an unnamed command.
        selectedCommand = _commandMap.get(
                UNNAMED,
                argv.length );
        if ( selectedCommand != null )
        {
            _currentCommand =
                    selectedCommand.getName() ;
            selectedCommand.execute(
                    argv );
            return;
        }

        // No match.
        err( "Unknown command '%s'.%n", cmdName );
    }

    private void processProperty( String property )
        throws Exception
    {
        String value = null;
        var equals =
                property.indexOf( "=" );
        if ( equals > 0 )
        {
            value = property.substring( equals+1 );
            property = property.substring( 0, equals );
        }

        var setter = _propertyMap.get(
                property );
        if ( setter == null )
            throw new Exception( "Unknown property: " + property );

        if ( setter.isBooleanType() && value == null )
            value = "true";

        setter.set( value );
    }

    /**
     * Check if the argument is a possible property name.
     * This filters arguments like '-313' which is *not*
     * an allowed property name.
     *
     * @param candidate A property name candidate.
     * @return A valid property name or null if the
     * passed candidate was not an allowed property name.
     */
    private String getNameIfProperty( String candidate )
    {
        if ( StringUtil.isEmpty( candidate ) )
            return null;

        if ( ! candidate.startsWith( "-" ) )
            return null;

        candidate = candidate.substring( 1 );

        var propertyName = candidate.split( "=" )[0];

        return SourceVersion.isIdentifier( propertyName ) ?
                candidate :
                null;
    }

    /**
     * Processes the properties in the passed command line arguments.
     * Properties are qualified by a '-' or '--' prefix.
     *
     * @param argv The command line arguments.
     * @return A newly allocated set of command line arguments with
     * properties removed.
     * @throws Exception In case the conversion fails.
     */
    private String[] processProperties( String[] argv )
        throws Exception
    {
        var result = new ArrayList<String>();

        for ( var c : argv )
        {
            var propertyName = getNameIfProperty( c );

            if ( propertyName != null )
                processProperty( propertyName );
            else
                result.add( c );
        }

        return result.toArray( new String[result.size()] );
    }

    /**
     * Start execution of the console command. This implicitly parses
     * the parameters and dispatches the call to the matching operation.
     * <p>
     * The main operation of an application using {@link #CliApplication()}
     * usually looks like:
     * </p>
     *
     * <pre>
     * <code>
     * public class Foo extends CliApplication
     * {
     *     ...
     *
     *     public static void main( String[] argv )
     *     {
     *         execute( Foo.class, argv );
     *     }
     * }
     * </code>
     * </pre>
     *
     * @param cl The implementation class of the console command.
     * @param argv The unmodified parameter array.
     */
    static public void launch( Class<? extends CliApplication> cl, String[] argv )
    {
        launch(
                new DefaultCtorReflection<>( cl ),
                argv );
    }

    /**
     * Start execution of the console command. This implicitly parses
     * the parameters and dispatches the call to the matching operation.
     * <p>
     * The main operation of an application using {@link #CliApplication()} usually looks like:
     * </p>
     *
     * <pre>
     * <code>
     * public class Duck extends CliApplication
     * {
     *     ...
     *
     *     public static void main( String[] argv )
     *     {
     *         execute( Duck::new, argv );
     *     }
     * }
     * </code>
     * </pre>
     *
     * @param cl The implementation class of the console command.
     * @param argv The unmodified parameter array.
     */
    static public void launch( Supplier<CliApplication> cl, String[] argv )
    {
        try
        {
            cl.get().launchInstance( argv );
        }
        catch (RuntimeException e)
        {
            String msg = e.getMessage();
            if (msg == null)
                msg = e.getClass().getName();

            LOG.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            String msg = e.getMessage();
            if (msg == null)
                msg = e.getClass().getName();

            LOG.log(Level.FINE, msg, e);
            System.err.println("Failed: " + msg);
        }
    }

    /**
     * Get String for error handling with correct function calls.
     *
     * @param commands
     *            Map with all possible commands.
     * @param argv
     *            Argument list as String.
     * @return Usage message for error handling.
     */
    private String getCommandsUsage(Map<Integer, CommandHolder> commands, String[] argv)
    {
        StringBuilder result = new StringBuilder();

        commands.values().forEach(
                c -> result.append( c.usage() ));

        return result.toString();
    }

    /**
     * Usage function to get a dynamic help text with all available commands.
     *
     * @return Usage text.
     */
    protected String usage()
    {
        StringBuilder result =
                new StringBuilder( getApplicationName() );
        {
            String desc =
                    getApplicationDescription();
            if ( StringUtil.hasContent( desc ) )
            {
                result.append( " -- " );
                result.append(desc);
            }
        }
        result.append( StringUtil.EOL );
        result.append( "The following commands are supported:" );
        result.append( StringUtil.EOL );

        for ( CommandHolder command : sort( _commandMap.getValues() ) )
            result.append( command.usage() );

        if ( ! _propertyMap.isEmpty() )
        {
            result.append( StringUtil.EOL );
            result.append( "Properties:" );
            result.append( StringUtil.EOL );
            _propertyMap.values().stream().sorted().forEach( c ->
            {
                result.append( c.usage() );
                result.append( StringUtil.EOL );
            } );
        }

        return result.toString();
    }

    /**
     * Helper operation to sort a collection of methods.
     *
     * @return A newly allocated list.
     */
    private List<CommandHolder> sort( Collection<CommandHolder> methods )
    {
        var result = new ArrayList<>( methods );

        Collections.sort( result, null );

        return result;
    }

    /**
     * Get a map of all commands that allows to access a single command based on
     * its name and argument list.
     */
    private MultiMap<String, Integer, CommandHolder> getCommandMap(
            Class<?> targetClass )
    {
        MultiMap<String,Integer,CommandHolder> result =
                new MultiMap<>();

        ReflectionUtil.processAnnotation(
                Command.class,
                targetClass::getDeclaredMethods,
                (c,a) -> {
                    String name = a.name();
                    if ( StringUtil.isEmpty( name ) )
                        name = c.getName();

                    for ( Class<?> current : c.getParameterTypes() )
                    {
                        Objects.requireNonNull(
                                _converters.getConverter( current ),
                                "No mapper for " + current );
                    }

                    Integer numberOfArgs =
                            Integer.valueOf( c.getParameterTypes().length );

                    var keyName = name.toLowerCase();
                    // Check if we already have this command with the same parameter
                    // list length. This is an implementation error.
                    if (result.get(keyName, numberOfArgs) != null) {
                        throw new InternalError(
                                "Implementation error. Operation " +
                                name +
                                " with " +
                                numberOfArgs +
                                " parameters is not unique.");
                    }

                    result.put(
                            keyName,
                            numberOfArgs,
                            new CommandHolder( c ) );
                } );

        return result;
    }

    /**
     * Get a map of all commands that allows to access a single command based on
     * its name and argument list.
     */
    private Map<String, PropertyHolder> getPropertyMap(
            Object targetInstance )
    {
        var targetClass =
                targetInstance.getClass();
        var result =
                new HashMap<String, PropertyHolder>();

        ReflectionUtil.processAnnotation(
                Property.class,
                targetClass::getDeclaredFields,
                (f,a) -> {
                    var p = new PropertyHolder( f );
                    result.put(
                            p.getName(),
                            p );
                } );

        return result;
    }

    /**
     * Handle an exception thrown from a command. This default implementation
     * prints the exception message or, if this is empty, the exception name.
     * <p>In addition it tries to differentiate between implementation errors
     * and logical errors. RuntimeExceptions and Errors are handled as
     * implementation errors and printed including their stack trace.</p>
     *
     * @param e The exception to handle.
     * @param commandName The name of the failing command.
     */
    protected void processCommandException( String commandName, Throwable e )
    {
        String msg = e.getMessage();

        if ( StringUtil.isEmpty( msg ) )
            msg = e.getClass().getName();

        if ( e instanceof RuntimeException || e instanceof Error )
        {
            // Special handling of implementation or VM errors.
            err( "%s failed.%n",
                    commandName );
            e.printStackTrace();
        }
        else
        {
            err( "%s failed: %s%n",
                    commandName,
                    msg );
        }
    }

    /**
     * Transform function for File. This ensures that the file exists.
     *
     * @param fileName The name of the file.
     * @throws Exception If the file does not exist.
     * @return A reference to a file instance if one exists.
     */
    private static File stringToFile(String fileName) throws Exception {

        File file = new File(fileName);

        if (!file.exists())
            throw new Exception("File not found: " + file);

        return file;
    }

    private String getEnumDocumentation( Class<?> c )
    {
        List<String> enumNames = new ArrayList<>();

        for ( Object o : c.getEnumConstants() )
            enumNames.add( o.toString() );

        if ( enumNames.isEmpty() )
            return StringUtil.EMPTY_STRING;

        Collections.sort( enumNames );

        return
                "[" +
                StringUtil.concatenate( ", ", enumNames ) +
                "]";
    }

    /**
     * Get the application name. That is the name printed in the headline
     * of generated documentation. If the application is running from
     * CliConsole this is the name that has to specified on the command
     * line.  If the application is run via java -jar application.jar
     * then the returned value has only impact on the generated docs.
     * Add this information by applying the Named annotation on your
     * implementation class.
     *
     * @return The application name.
     */
    public String getApplicationName()
    {
        Named annotation =
                getClass().getAnnotation( Named.class );

        if ( annotation != null && StringUtil.hasContent( annotation.value() ) )
            return annotation.value();

        return getClass().getSimpleName();
    }

    /**
     * Get textual information on the overall console application.
     * Add this information by applying the Named annotation on your
     * implementation class.
     *
     * @return Overall application documentation.
     * @see #getApplicationName()
     */
    protected String getApplicationDescription()
    {
        Named annotation =
                getClass().getAnnotation( Named.class );

        if ( annotation != null && StringUtil.hasContent( annotation.description() ) )
            return annotation.description();

        return StringUtil.EMPTY_STRING;
    }

    /**
     * Convert an argument string to a typed object. Uses
     * a special mapping for enums and the type map
     * for all other types.
     */
    private static final Object transformArgument(
            Class<?> targetType,
            String argument )
        throws Exception
    {
        targetType =
                Objects.requireNonNull( targetType );
        argument =
                Objects.requireNonNull( argument );

        var transformer =
                Objects.requireNonNull(
                        _converters.getConverter( targetType ),
                        "No mapper for " + targetType.getSimpleName() );

        return transformer.convert( argument );
    }

    /**
     * Format the parameters to the standard error stream.
     *
     * @param fmt The format string.
     * @param argv Format parameters.
     */
    protected final void err( String fmt, Object ... argv )
    {
        System.err.printf( fmt, argv );
    }

    /**
     * Print to the standard error stream. Note that no
     * line feed is added.
     *
     * @param msg The message to print.
     */
    protected final void err( String msg )
    {
        System.err.print( msg );
    }

    /**
     * Format the parameters to the standard output stream.
     *
     * @param fmt The format string.
     * @param argv Format parameters.
     */
    protected final void out( String fmt, Object ... argv )
    {
        System.out.printf( fmt, argv );
    }

    /**
     * Print to the standard output stream. Note that no
     * line feed is added.
     *
     * @param msg The message to print.
     */
    protected final void out( String msg )
    {
        System.out.print( msg );
    }

    /**
     * Shorthand for System.in.
     *
     * @return The standard input stream.
     */
    protected final InputStream in()
    {
        return System.in;
    }

    private static class DefaultCtorReflection<T extends CliApplication>
        implements Supplier<CliApplication>
    {
        private final Class<T> _class;

        public DefaultCtorReflection( Class<T> claß )
        {
            _class = claß;
        }

        @Override
        public CliApplication get()
        {
            try {
                Constructor<T> c = _class.getDeclaredConstructor();
                return c.newInstance();
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Encapsulates a property.
     */
    private class PropertyHolder implements Comparable<PropertyHolder>
    {
        private final Field _field;
        private final Property _property;

        PropertyHolder( Field field )
        {
            _field = field;
            _property = Objects.requireNonNull(
                    field.getAnnotation( Property.class ) );
        }

        String getName()
        {
            String name = _property.name();

            if ( StringUtil.hasContent( name ) )
                return name;

            return _field.getName();
        }

        void set( String value )
                throws Exception
        {
            var self = CliApplication.this;

            _field.set(
                    self,
                    transformArgument(
                            _field.getType(),
                            value ) );
        }

        boolean isBooleanType()
        {
            return
                    ReflectionUtil.normalizePrimitives( _field.getType() ) == Boolean.class;
        }

        String usage()
        {
            var type =
                    _field.getType();
            var typeDoc = type.isEnum() ?
                    getEnumDocumentation( type ) :
                    type.getSimpleName();

            var result = String.format(
                    "-%s=(%s)",
                    getName(),
                    typeDoc );

            var description =
                    _property.description();
            if ( StringUtil.isEmpty( description ) )
                return result;

            return result + " : " + description;
        }

        @Override
        public int compareTo( PropertyHolder o )
        {
            int result =
                    getName().compareTo( o.getName() );

            return result;
        }
    }

    /**
     * Encapsulates a command.
     */
    private class CommandHolder implements Comparable<CommandHolder>
    {
        private final Method _op;
        private final Command _commandAnnotation;

        CommandHolder( Method operation )
        {
            _op =
                    operation;
            _commandAnnotation =
                    Objects.requireNonNull(
                            _op.getAnnotation( Command.class ),
                            "@Command missing." );
        }

        String getName()
        {
            var result = _commandAnnotation.name();

            if ( StringUtil.hasContent( result ))
                return result;

            return _op.getName();
        }

        int getParameterCount()
        {
            return _op.getParameterCount();
        }

        private String getDescription()
        {
            String result = _commandAnnotation.description();

            if ( StringUtil.hasContent( result ) )
                return result;

            return _commandAnnotation.shortDescription();
        }

        /**
         * Execute the passed command with the given passed arguments. Each parameter
         * is transformed to the expected type.
         *
         * @param command
         *            Command to execute.
         * @param argv
         *            List of arguments.
         */
        private void execute( String ... argv )
        {
            Object[] arguments =
                    new Object[argv.length];
            Class<?>[] params =
                    _op.getParameterTypes();

            if ( argv.length != params.length )
                throw new AssertionError();

            for (int j = 0; j < params.length; j++) try {
                arguments[j] = transformArgument(
                        params[j],
                        argv[j] );
            } catch ( Exception e ) {
                err("Parameter %s : ", argv[j]);

                String msg = e.getMessage();

                if ( StringUtil.isEmpty( msg ) )
                    msg = e.getClass().getSimpleName();

                err( "%s%n", msg );

                return;
            }

            try {
                final var self = CliApplication.this;

                if ( ! _op.canAccess( self ) )
                    _op.setAccessible( true );

                _op.invoke(
                        self,
                        arguments);
            }
            catch ( InvocationTargetException e )
            {
                processCommandException( _op.getName(), e.getCause() );
            }
            catch ( Exception e )
            {
                // A raw exception must come from our implementation,
                // so we present a less user friendly stacktrace.
                e.printStackTrace();
            }
            finally
            {
                // In case a parameter conversion operation created
                // 'closeable' objects, ensure that these get freed.
                for ( Object c : arguments )
                {
                    if ( c instanceof AutoCloseable )
                        JavaUtil.force( ((AutoCloseable)c)::close );
                }
            }
        }

        private String getParameterList()
        {
            String[] list = getCommandParameterListExt();

            if ( list.length == 0 )
                return StringUtil.EMPTY_STRING;

            return StringUtil.concatenate( ", ", list );
        }

        private String[] getCommandParameterListExt()
        {
            Class<?>[] parameterTypes =
                    _op.getParameterTypes();

            // The old-style command parameter documentation has priority.
            if ( _commandAnnotation.argumentNames().length > 0 )
            {
                if ( _commandAnnotation.argumentNames().length != parameterTypes.length )
                    LOG.warning( "Command.argumentNames inconsistent with " + _op );

                return _commandAnnotation.argumentNames();
            }

            // The strategic way of defining parameter documentation.
            String[] result = new String[ getParameterCount() ];
            int idx = 0;
            for ( Parameter c : _op.getParameters() )
            {
                Named named = c.getDeclaredAnnotation( Named.class );

                if ( named != null && StringUtil.hasContent( named.value() ) )
                    result[idx] = named.value();
                else if ( c.getType().isEnum() )
                    result[idx] = getEnumDocumentation( c.getType() );
                else
                    result[idx] = c.getType().getSimpleName();

                idx++;
            }

            return result;
        }

        /**
         * Generate help text for a method.
         */
        private String usage()
        {
            StringBuilder info = new StringBuilder();

            info.append( getName() );

            String optional =
                    getParameterList();
            if ( StringUtil.hasContent( optional ) )
            {
                info.append( ": " );
                info.append( optional );
            }
            info.append( StringUtil.EOL );

            optional =
                    getDescription();
            if ( StringUtil.hasContent( optional ) )
            {
                info.append( "    " );
                info.append( optional );
                info.append( StringUtil.EOL );
            }

            return info.toString();
        }

        @Override
        public int compareTo( CommandHolder o )
        {
            int result =
                    getName().compareTo( o.getName() );

            if ( result != 0 )
                return result;

            return
                    getParameterCount() -
                    o.getParameterCount();
        }
    }
}
