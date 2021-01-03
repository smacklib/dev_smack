package org.jdesktop.util.converters;

import org.jdesktop.util.ResourceConverter;
import org.jdesktop.util.ResourceMap;

import javafx.scene.input.KeyCombination;

public class KeyCombinationConverter extends ResourceConverter
{
    public KeyCombinationConverter()
    {
        super( KeyCombination.class );
    }

    @Override
    public Object parseString( String s, ResourceMap r ) throws Exception
    {
        return KeyCombination.valueOf( s );
    }
}
