@echo off

set JAVACC_HOME=c:\tools\javacc2.1

call %JAVACC_HOME%\bin\javacc.bat XPath.jj
call %JAVACC_HOME%\bin\jjdoc.bat  XPath.jj