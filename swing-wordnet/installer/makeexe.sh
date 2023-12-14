#!/bin/bash
# bbou@ac-toulouse
# dim 10 nov 2002 09:39:38 CET 

#version=3.9.0
#stamp=`date +%Y%m%d`
toexehome=${IZPACK_HOME}/utils/wrappers/izpack2exe
file=$1

#izpack2exe.py [options]
#options:
#-h, --help show this help message and exit
#--file=FILE The installer JAR file
#--output=OUTPUT The executable file
#--with-7z=P7Z Path to the 7-Zip executable
#--with-upx=UPX Path to the UPX executable
#--no-upx Do not use UPX to further compress the output

echo "Make exe ${EXE_HOME}"
python ${toexehome}/izpack2exe.py --no-upx --file=${file}.jar --output=${file}.exe
