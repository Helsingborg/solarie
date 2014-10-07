#!/bin/bash

pid=$(cat server.pid)

echo Skickar TERM-signal till pid $pid
kill -15 $pid

echo Väntar
sleep 20

if [ ! -f /proc/$pid ]; then
  echo Skickar KILL-signal till pid $pid
  kill -9 $pid
fi

while [ ! -f /proc/$pid ];
sleep 1

#kill -9 $pid
rm server.pid
