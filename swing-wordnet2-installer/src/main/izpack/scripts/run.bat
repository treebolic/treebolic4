@echo off
set HERE=%~dp0
set VERSION=@{appversion}
java -jar "%HERE%\swing-wordnet2-%VERSION%-uber.jar"
