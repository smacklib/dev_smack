package org.smack.application;

import java.io.IOException;

import org.junit.Test;
import org.smack.util.ServiceManager;

public class ApplicationContextTest
{
    private static ApplicationContext _ac;

    static public class UnderTestAc extends CliApplication
    {
        @Command
        public void add( int a, int b )
        {
            out( "%s%n", a + b );
        }
        public static void main( String[] argv )
        {
            _ac = ServiceManager.initApplicationService(
                    new ApplicationContext( UnderTestAc.class ) );

            launch( UnderTestAc::new, argv );
        }
    }

    @Test
    public void testHelpDeprecated() throws IOException
    {
        CliApplicationTest. execCli( UnderTestAc::main,
            CliApplicationTest.EMPTY_STRING_ARRAY,
            CliApplicationTest.EMPTY_STRING_ARRAY,
            CliApplicationTest.s(
                UnderTestAc.class.getSimpleName(),
                "The following commands are supported:",
                "add: int, int" )
            );
    }
}
