echo "***************************************************"
echo "APPLICATION START BEGINS"
echo "***************************************************"

nohup java -jar /home/ubuntu/ROOT*.war server.port=8080 > /home/ubuntu/application-execution.out 2>&1 &

echo "***************************************************"
echo "APPLICATION START ENDS"
echo "***************************************************"