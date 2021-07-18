package org.smack.application;

import java.io.IOException;

import org.junit.Test;
import org.smack.application.CliApplication.Named;

public class CliApplicationTestAnnotations
{
    static class UnderTest extends CliApplication
    {
        @Command
        public void add( int a, int b )
        {
            out( "%s%n", a + b );
        }
        public static void main( String[] argv )
        {
            launch( UnderTest::new, argv );
        }
    }

    // Uses the annotation's value property.
    @Named( "NamedName" )
    static class AnnotatedUnderTest extends CliApplication
    {
        @Command( description = "description add" )
        public void add(
                @Named( "firstSummand" ) int a,
                @Named( "secondSummand" )int b )
        {
            out( "%s%n", a + b );
        }
        public static void main( String[] argv )
        {
            launch( AnnotatedUnderTest::new, argv );
        }
    }

    @Named( value = "NamedName", description="NamedName description" )
    static class DeprecatedAnnotatedUnderTest extends CliApplication
    {
        @Command( shortDescription = "description add" )
        public void add( int a, int b )
        {
            out( "%s%n", a + b );
        }
        public static void main( String[] argv )
        {
            launch( DeprecatedAnnotatedUnderTest::new, argv );
        }
    }

    @Named(
            value = "NamedName",
            description="NamedName description" )
    static class DoubleAnnotatedUnderTest extends CliApplication
    {
        @Command(
                description = "description add",
                shortDescription = "xxx" )
        public void add( int a, int b )
        {
            out( "%s%n", a + b );
        }
        public static void main( String[] argv )
        {
            launch( DoubleAnnotatedUnderTest::new, argv );
        }
    }

    @Test
    public void testHelpUnannotated() throws IOException
    {
        CliApplicationTest.execCli(
                UnderTest::main,
                new String[0],
                null,
                new String[] {
                "UnderTest",
                "The following commands are supported:",
                "add: int, int" } );
    }

    @Test
    public void testHelpAnnotated() throws IOException
    {
        CliApplicationTest.execCli(
                AnnotatedUnderTest::main,
                new String[0],
                null,
                new String[] {
                "NamedName",
                "The following commands are supported:",
                "add: firstSummand, secondSummand",
                "    description add" } );
    }

    @Test
    public void testHelpDeprecated() throws IOException
    {
        CliApplicationTest.execCli(
                DeprecatedAnnotatedUnderTest::main,
                new String[0],
                null,
                new String[] {
                "NamedName -- NamedName description",
                "The following commands are supported:",
                "add: int, int",
                "    description add" } );
    }

    @Test
    public void testHelpDouble() throws IOException
    {
        CliApplicationTest.execCli(
                DoubleAnnotatedUnderTest::main,
                new String[0],
                null,
                new String[] {
                "NamedName -- NamedName description",
                "The following commands are supported:",
                "add: int, int",
                "    description add" } );
    }
}
