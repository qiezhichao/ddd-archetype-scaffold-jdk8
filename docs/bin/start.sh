#!/bin/bash
runParam="$1"
basePath=$(cd `dirname $0`; pwd)
targetJarPath=$(echo $basePath/../*.jar)
serverName=$(echo $targetJarPath |awk -F '/../' '{print $2}' | awk -F '.jar' '{print $1}')
getPid=$(ps -ef | grep $serverName | grep -v grep | grep -v sh | awk '{print $2}')
echo "start [$targetJarPath], getPid=[$getPid], 入参=[$runParam]."
if  [ "$getPid" == "" ]
then
    echo "[$targetJarPath]目前状态：未启动/已关闭. 即将按入参=[$runParam]配置启动(默认值=prod)..."
        if [ "$runParam" == "" ]
        then
            nohup java -jar -Dspring.profiles.active=prod "$targetJarPath" > /dev/null 2>&1 &
        else
            nohup java -jar -Dspring.profiles.active="$runParam"  "$targetJarPath" > /dev/null 2>&1 &
        fi
else
    echo "[$targetJarPath]目前正在运行/未关闭. 请先关闭再启动."
fi