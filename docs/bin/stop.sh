#!/bin/bash
basePath=$(cd `dirname $0`; pwd)
targetJarPath=$(echo $basePath/../*.jar)
serverName=$(echo $targetJarPath |awk -F '/../' '{print $2}' | awk -F '.jar' '{print $1}')
getPid=$(ps -ef | grep $serverName | grep -v grep | grep -v sh | awk '{print $2}')
echo "stop [$serverName], getPid=[$getPid]."
if  [ "$getPid" == "" ]
then
    echo "[$serverName]当前状态：未启动/已关闭. 不操作."
else
        echo "[$serverName]正在运行/未关闭. 即将关闭..."
        kill -9 "$getPid"
        echo "[$serverName]已关闭."
fi