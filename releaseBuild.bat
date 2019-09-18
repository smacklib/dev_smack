
rem TODO ensure ANT_HOME is set...

Rem clear path.
path ;
set JAVA_HOME=

set JDK_NAME=jdk11.0.4_10
set JAVA_HOME=c:\Progra~1\JavaCoretto\%JDK_NAME%
call %ANT_HOME%\bin\ant.bat clean dist
