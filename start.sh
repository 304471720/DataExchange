#!/bin/bash
HOME_DIR=`pwd`
VERSION="0.0.1"
PROJECT_NAME=${HOME_DIR##*/}
JAR_EXECUTE=$PROJECT_NAME-$VERSION-SNAPSHOT.jar
echo $JAR_EXECUTE
export JAVA_HOME=/www/ljj/jdk7
start()
{
nohup  $JAVA_HOME/bin/java -jar $HOME_DIR/target/$JAR_EXECUTE > /dev/null 2>&1  &
}



logs()
{
tail -f  $HOME_DIR/$PROJECT_NAME.log
}

stop()
{
ps aux | grep $PROJECT_NAME | grep -v grep | awk '{print "sudo  kill "$2}' | sh
}


build()
{
stop
cvs up
mvn clean package
}
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'build')
     build
     ;;
  *)
     logs
esac
exit 0