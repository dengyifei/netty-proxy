#!/bin/sh
# java env
# author efei
# export JAVA_HOME=/usr/local/jdk/jdk1.8.0_101
# export JRE_HOME=$JAVA_HOME/jre
# s1==jar $2 start|stop|restart|run | $3 --pro prams |
#JAVA_OPTS="-server -Xms400m -Xmx400m -Xmn300m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xverify:none -XX:+DisableExplicitGC -Djava.awt.headless=true"
JAVA_OPTS="-server -Xms500m -Xmx500m -Xmn350m -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs/"
# 获取jar名字
JAR_PARAM=$3
JAR_PATH=$1
JAR_NAME=${JAR_PATH##*/}
API_NAME=${JAR_NAME%.jar}

# 获取参数
getpropv() {
  if [ ! -f "run.conf" ]
  then
    echo ''
    return 0
  fi

  if [ "e" != "e${1}" ]
  then
    str=$(grep "${1}" run.conf | cut -d'=' -f2 | sed 's/\r//')
    echo $str
  fi
}

this_dir=$(cd `dirname $0`; pwd)
prov=$(getpropv "logdir")
if [ "X$prov" = "X" ]
then
  log_dir="${this_dir}/logs"
else
  log_dir="${prov}/logs"
fi

if [ ! -d "${log_dir}" ]; then
  mkdir "${log_dir}"
fi

#PID  代表是PID文件
PID=$API_NAME.pid

#使用说明，用来提示输入参数
usage() {
    echo "Usage: sh 执行脚本.sh [xxx.jar] [start|startnolog|stop|restart|status|logshow|run] [springboot参数]"
    exit 1
}
#echo "ps -ef|grep 'java -jar "$JAR_NAME"'|grep -v grep|awk '{print $2}'"
#检查程序是否在运行
is_exist(){
  pid=`ps -ef|grep $JAR_NAME|grep -v grep|grep -v $0|awk '{print $2}' `
  #如果不存在返回1，存在返回0     
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
start(){
  is_exist
  if [ $? -eq "0" ]; then 
    echo ">>> ${JAR_NAME} is already running PID=${pid} <<<" 
  else 
    nohup java -jar $JAVA_OPTS $JAR_PATH $JAR_PARAM > "$log_dir"/catalina_"$API_NAME".out 2>&1 &
    sleep 1
    echo $! > $PID
    echo ">>> start $JAR_NAME successed PID=$! <<<" 
   fi
}

startnolog(){
  is_exist
  if [ $? -eq "0" ]; then 
    echo ">>> ${JAR_NAME} is already running PID=${pid} <<<" 
  else 
    nohup java -jar $JAVA_OPTS $JAR_PATH $JAR_PARAM > "/dev/null" 2>&1 &
    sleep 1
    echo $! > $PID
    echo ">>> start $JAR_NAME successed PID=$! <<<" 
   fi
}

#停止方法
stop(){
  #is_exist
  pidf=$(cat $PID)
  #echo "$pidf"  
  echo ">>> api PID = $pidf begin kill $pidf <<<"
  kill $pidf
  rm -rf $PID
  sleep 2
  is_exist
  if [ $? -eq "0" ]; then 
    echo ">>> api 2 PID = $pid begin kill -9 $pid  <<<"
    kill -9  $pid
    sleep 2
    echo ">>> $JAR_NAME process stopped <<<"  
  else
    echo ">>> ${JAR_NAME} is not running <<<"
  fi  
}

#输出运行状态
status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo ">>> ${JAR_NAME} is running PID is ${pid} <<<"
  else
    echo ">>> ${JAR_NAME} is not running <<<"
  fi
}
# 查看log
logshow(){
  if [ -f "${log_dir}/catalina_${API_NAME}.out" ]
  then
    tail -f "${log_dir}/catalina_${API_NAME}.out"
  else
    echo "${log_dir}/catalina_${API_NAME}.out 文件不存在"
  fi
}
#重启
restart(){
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$2" in
  "start")
    start
    ;;
  "startnolog")
    startnolog
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  "run")
    start
    logshow
    ;;
  "logshow")
    logshow
    ;;
  *)
    usage
    ;;
esac
exit 0
