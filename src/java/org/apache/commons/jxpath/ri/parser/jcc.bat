@echo off

set JAVACC_HOME=c:\tools\javacc2.0

%JAVACC_HOME%\bin\javacc.bat XPath.jj
%JAVACC_HOME%\bin\jjdoc.bat