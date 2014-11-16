/* $Id$
 *
 * Common utils.
 *
 * Released under Gnu Public License
 * Copyright (c) 2002-2008 Michael G. Binz
 */
package de.s.mack.util;

import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * A support class for localisation purposes.
 *
 * @version $Revision$
 */
public class Localiser
{
  /**
   * Forbid instantiation.
   */
  private Localiser()
  {
  }



  /**
   * Return an icon for the given key.
   *
   * @param b The resource bundle to use for resource lookup.
   * @param key The resource name to lookup.
   * @return A reference to the icon or null if the icon was not found.
   */
  static public ImageIcon localiseIcon( ResourceBundle b, String key )
  {
    String iconPath = localise( b, key );

    if ( iconPath == null )
      return null;

    java.net.URL url = Localiser.class.getClassLoader().
      getResource( iconPath );

    if ( url == null )
      return null;

    return new ImageIcon( url );
  }



  /**
   * Read the value for the passed key from the resource bundle.  If the key is
   * not found, <code>null</code> is returned.
   *
   * @param b The resource bundle used to resolve the key.
   * @param key The key used for resource resolution.
   * @return The resolved resource value or <code>null</code> if the key could
   *         not be found.
   * @see Localiser#localise( ResourceBundle, String, String )
   */
  static public String localise( ResourceBundle b, String key )
  {
    return localise( b, key, null );
  }



  /**
   * Read the value for the passed key from the resource bundle.  If the key is
   * not found, the passed default value is returned.
   *
   * @param b The resource bundle used to resolve the key.
   * @param key The key used for resource resolution.
   * @param deflt The value to return in case the key could not be resolved.
   * @return The resolved resource value or the value of the <code>deflt</code>
   *         argument if the key could not be found.
   */
  static public String localise(
    ResourceBundle b,
    String key,
    String deflt )
  {
    try
    {
      return b.getString( key );
    }
    catch ( MissingResourceException e )
    {
      return deflt;
    }
  }



  /**
   * Read the value for the passed key from the resource bundle.  If the key is
   * not found, the passed default value is returned.
   *
   * @param b The resource bundle used to resolve the key.
   * @param key The key used for resource resolution.
   * @param deflt The value to return in case the key could not be resolved.
   * @return The resolved resource value or the value of the <code>deflt</code>
   *         argument if the key could not be found.
   */
  static public String[] localiseList(
    ResourceBundle b,
    String key,
    String[] deflt )
  {
    String list;

    try
    {
      list = b.getString( key );
    }
    catch ( MissingResourceException e )
    {
      return deflt;
    }

    Vector<String> result = new Vector<String>();

    StringTokenizer st = new StringTokenizer( list );

    while ( st.hasMoreTokens() )
      result.add(  st.nextToken() );

    return result.toArray(
        new String[result.size()] );
  }



  /**
   * Load a resource bundle form the specified file.
   *
   * @param name The resource file to load.  An example for a valid resource
   *             bundle name is
   *             <code>"de.michab.apps.mp3tagger.resources.Tagger"</code>
   * @return A reference to the resource bundle or null if not found.
   */
  static public ResourceBundle loadResourceBundle( String name )
  {
    ResourceBundle result;

    try
    {
      result = ResourceBundle.getBundle( name );
    }
    catch ( MissingResourceException e )
    {
      result = null;
    }

    return result;
  }
}
