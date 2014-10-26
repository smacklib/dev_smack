/* $Id$
 *
 * Michael's commons.
 *
 * Released under Gnu Public License
 * Copyright (c) 2003-2005 Michael G. Binz
 */
package de.michab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.jar.Attributes;

import javax.swing.JOptionPane;





/**
 * A small application stub used as the starter class for the commons
 * jar file.
 *
 * @author Michael G. Binz
 */
public class CommonVersion
{
  /**
   * Stub main.
   *
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    // Note that this code only works inside a jar file.
    try
    {
      Class<CommonVersion> clazz = CommonVersion.class;

      String classContainer =
        clazz
          .getProtectionDomain()
          .getCodeSource()
          .getLocation().
          toString();

      URL manifestUrl =
        new URL(
            "jar:" +
            classContainer +
            "!/META-INF/MANIFEST.MF" );

      Hashtable<String, String> manifestEntries =
        readManifest( manifestUrl );

      String title =
        manifestEntries.get(
            Attributes.Name.IMPLEMENTATION_TITLE.toString() );
      String version =
        manifestEntries.get(
            Attributes.Name.IMPLEMENTATION_VERSION.toString() );

      if ( title == null )
        title = "none";
      if ( version == null )
        version = "none";

      fullMessage( title, version );
    }
    catch ( Exception e )
    {
      simpleMessage();
    }

    System.exit( 0 );
  }



  /**
   * Shows a short message and terminates the application.
   */
  private static void simpleMessage()
  {
    JOptionPane.showMessageDialog(
        null,
        "Michael's commons." );
  }



  /**
   * Shows a long message and terminates the application.
   *
   * @param title The application title.
   * @param version The application version.
   */
  private static void fullMessage(String title, String version)
  {
    JOptionPane.showMessageDialog(
        null,
        "Michael's " + title + ", " + version );
  }



  /**
   * Reads a manifest file and places the entries in a hashtable.
   *
   * @param is The stream to read from.
   * @return The hash table holding the manifest entries.
   * @throws IOException In case reading failed.
   */
  private static Hashtable<String, String> readManifest( URL manifestUrl )
    throws IOException
  {
    InputStream is = null;
    Hashtable<String, String> result = null;

    try
    {
      is = manifestUrl.openStream();
      result = readManifest( is );
    }
    catch ( IOException e )
    {

    }
    finally
    {
      if ( is != null ) try
      {
        is.close();
      }
      catch ( Exception e )
      {
        is = null;
      }
    }

    return result;
  }



  /**
   * Reads a manifest file and places the entries in a hashtable.
   *
   * @param is The stream to read from.
   * @return The hash table holding the manifest entries.
   * @throws IOException In case reading failed.
   */
  private static Hashtable<String, String> readManifest( InputStream is )
      throws IOException
  {
    LineNumberReader isr = new LineNumberReader( new InputStreamReader( is ) );

    String currentLine = null;

    Hashtable<String, String> result = new Hashtable<String, String>();

    while ( null != (currentLine = isr.readLine()) )
    {
      int colonIdx = currentLine.indexOf( ':' );

      if ( (colonIdx > 0) && (colonIdx < currentLine.length() - 1) )
      {
        String key = currentLine.substring( 0, colonIdx );
        String val = currentLine.substring( colonIdx + 1 );
        key = key.trim();
        val = val.trim();
        result.put( key, val );
      }
    }

    isr.close();

    return result;
  }
}
