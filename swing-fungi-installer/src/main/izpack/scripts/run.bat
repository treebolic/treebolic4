@echo off
set HERE=%~dp0
set VERSION=@{appversion}
java -jar "%HERE%\swing-fungi-%VERSION%-uber.jar"
