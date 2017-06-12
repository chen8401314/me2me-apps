#!/bin/sh

mvn clean package -Pproduct

myPath="D:/apps"

cp -r ./activity/target/jsw/app-activity-service $myPath
cp -r ./content/target/jsw/app-content-service $myPath
cp -r ./live/target/jsw/app-live-service $myPath
cp -r ./search/target/jsw/app-search-service $myPath
cp -r ./io/target/jsw/app-io-service $myPath
cp -r ./user/target/jsw/app-user-service $myPath
cp -r ./cache/target/jsw/app-cache-service $myPath
cp -r ./sms/target/jsw/app-sms-service $myPath
cp -r ./monitor/target/jsw/app-monitor-service $myPath
cp -r ./sns/target/jsw/app-sns-service $myPath
cp -r ./kafka/target/jsw/app-kafka-service $myPath
cp -r ./pay/target/jsw/app-pay-service $myPath

echo '##############################################################'
echo '#                 product build success                      #'
echo '##############################################################'


