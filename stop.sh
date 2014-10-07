#!/bin/bash

pid=$(cat server.pid)

echo Skickar TERM-signal till pid $pid
kill -15 $pid