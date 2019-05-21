/*
 * Copyright © 2017 Daimler TSS. All Rights Reserved.
 *
 * Reproduction or transmission in whole or in part, in any form or by any
 * means, is prohibited without the prior written consent of the copyright
 * owner.
 */
package com.daimler.tss.ccc.image;

import java.util.Set;

/**
 * Allows to discover command line interface programs.
 *
 * @see CliApplication
 * @author micbinz
 */
public abstract class CliContributor
{
    /**
     * @return The list of command line programs contributed by this module.
     */
    public abstract Set<Class<? extends CliApplication>> getClis();
}
