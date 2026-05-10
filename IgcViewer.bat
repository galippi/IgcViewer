::@set START=start
@call config.bat
::@set JAVA=C:\KBApps\AdoptOpenJDK\
::@set JAVA=C:\KBApps\openjdk1105
%START% "%JAVA%\bin\java" -jar bin\%APP% %*

::%START% java -jar bin/igcviewer.jar
::pause
