/* $Id: MackComponent.java 662 2013-05-25 20:04:49Z Michael $
 *
 * Mack
 *
 * Released under Gnu Public License
 * Copyright © 2010 Michael G. Binz
 */
package de.michab.mack;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.bsaf192.application.Application;



/**
 * A component that offers typed model access.
 *
 * @version $Rev: 662 $
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
