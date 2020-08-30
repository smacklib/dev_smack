/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2020 Michael G. Binz
 */
package org.smack.util.xml;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

/**
 * A default implementation of {@link NamespaceContext}.
 *
 * @author micbinz
 */
@SuppressWarnings("serial")
class NamespaceContextImpl
// Deliberate inheritance to inherit the normal iteration operations and a
// decent toString() operation.
    extends HashMap<String, String>
    implements NamespaceContext
{
    public NamespaceContextImpl() {
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return get(prefix);
    }

    @Override
    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<String> getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }
}
