#!/bin/bash

nohup sudo java -jar /home/ubuntu/ROOT*.war server.port=8080 > /home/ubuntu/application-execution.out 2>&1 &
# mkdir /home/ubuntu/applicationStart