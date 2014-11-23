/* $Id$
 *
 * Mack
 *
 * Released under Gnu Public License
 * Copyright © 2010 Michael G. Binz
 */
package de.s.mack;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.jdesktop.application.Application;



/**
 * A component that offers typed model access.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public abstract class MackComponent<MT>
    extends JComponent
{
    private static final long serialVersionUID = -3353625264373850389L;


    /**
     * This component's model.
     */
    private MT _model;



    /**
     *
     */
    public MackComponent()
    {
    }


    /**
     *
     */
    public MackComponent( MT model )
    {
        _model = model;
    }



    /**
     *
     * @param model
     */
    public MT getModel()
    {
        return _model;
    }



    /**
     *
     * @param model
     * @return
     */
    public MT setModel( MT model )
    {
        MT result = _model;
        _model = model;

        return result;
    }

    /**
     *
     * @param name
     * @return
     */
    final protected Action getComponentAction( String name )
    {
        ActionMap am =
            Application.getInstance().getContext().getActionMap( this );
        return am.get( name );
    }
}
