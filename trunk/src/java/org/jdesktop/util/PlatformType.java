/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 *
 * @author Illya Yalovyy
 */
public enum PlatformType {
    DEFAULT ("Default", ""),
    SOLARIS ("Solaris", "sol", "solaris"),
    FREE_BSD ("FreeBSD", "bsd", "FreeBSD"),
    LINUX ("Linux", "lin", "linux"),
    OS_X ("Mac OS X", "osx", "mac os x"),
    WINDOWS ("Windows", "win", "windows");

    private final String name;
    private final String resourceSuffix;
    private final String[] patterns;

    private PlatformType(String name, String resourcePrefix, String... patterns) {
        this.name = name;
        this.resourceSuffix = resourcePrefix;
        this.patterns = patterns;
    }

    public String getName() {
        return name;
    }

    public String[] getPatterns() {
        return patterns.clone();
    }

    public String getResourceSuffix() {
        return resourceSuffix;
    }

    public static boolean is( PlatformType platformType )
    {
        return getPlatform() == platformType;
    }

    @Override
    public String toString() {
        return name;
    }

    private static PlatformType activePlatformType;

    /**
     * Determines a platform type the application is running on.
     * @return current platform type
     */
    public static PlatformType getPlatform()
    {
        if (activePlatformType != null)
            return activePlatformType;

        activePlatformType = PlatformType.DEFAULT;

        PrivilegedAction<String> doGetOSName = new PrivilegedAction<>() {
            @Override
            public String run() {
                return System.getProperty("os.name");
            }
        };

        String osName = AccessController.doPrivileged(doGetOSName);
        if (osName != null) {
            osName = osName.toLowerCase();
            for (PlatformType platformType : PlatformType.values()) {
                for (String pattern : platformType.getPatterns()) {
                    if (osName.startsWith(pattern)) {
                        return activePlatformType = platformType;
                    }
                }
            }
        }
        return activePlatformType = PlatformType.DEFAULT;
    }
}
