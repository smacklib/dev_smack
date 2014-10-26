/* $Id: MackAppEditor.java 676 2013-06-05 23:04:04Z Michael $
 *
 * Mack II -- Michael's Application Construction Kit.
 *
 * Released under Gnu Public License
 * Copyright © 2008-2010 Michael G. Binz
 */
package de.michab.mack;

import java.awt.Component;



/**
 * A great but undocumented thing.
 *
 * @param FT This application's file type. (document type)
 * @param MC The main component.
 *
 * @version $Revision: 676 $
 * @author Michael Binz
 */
// TODO base on MackApplication
public abstract class MackAppEditor<FT, MC extends Component>
  extends
    MackAppViewer<FT, MC>
{
    // TODO AppSave
    // private AppOpen<FT, MC> _actAppOpen = null;


    public MackAppEditor( Class<FT> documentClass )
    {
        super( documentClass );
    }
}
