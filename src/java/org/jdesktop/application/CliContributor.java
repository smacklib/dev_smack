package org.jdesktop.application;

import java.util.Set;

/**
 * Allows to discover command line interface programs.
 *
 * @see CliApplication
 * @author michab
 */
public abstract class CliContributor
{
    /**
     * @return The list of command line programs contributed by this module.
     */
    public abstract Set<Class<? extends CliApplication>> getClis();
}
