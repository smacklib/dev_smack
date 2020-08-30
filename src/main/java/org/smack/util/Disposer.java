/* $Id$
 *
 * Common.
 *
 * Released under Gnu Public License
 * Copyright © 2019 Michael G. Binz
 */
package org.smack.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Supports in managing multiple {@link AutoCloseable}s in a single
 * try-with-resources.
 *
 * @author Michael Binz
 */
public class Disposer implements AutoCloseable
{
    private final List<AutoCloseable> _resources =
            new ArrayList<>();

    /**
     * Create an instance.
     */
    public Disposer()
    {
    }

    /**
     * Register a resource with this Disposer.
     *
     * @param <T> The resource type, this must extend {@link AutoCloseable}.
     * @param resource The resource to register.  Null is not allowed.
     * @return The registered resource, equal to the 'resource' parameter.
     */
    public <T extends AutoCloseable> T register( T resource )
    {
        _resources.add(
                0,
                Objects.requireNonNull(
                        resource ) );
        return resource;
    }

    /**
     * Releases the registered resources in inverse order of registration.
     */
    @Override
    public void close()
    {
        for ( AutoCloseable c : _resources ) {
            JavaUtil.force( c::close );
        }

        _resources.clear();
    }
}
