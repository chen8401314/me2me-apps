#!/bin/sh

myPath="D:/apps-test"

if [ ! -d "$myPath" ]; then  
	mkdir "$myPath"
    echo "created dir:$myPath"
fi
rm -rf $myPath/*

mvn clean package -Ptest

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

#war
cp -r ./root/target/*.war $myPath
cp -r ./operation-mgmt/target/*.war $myPath

#zip -r $myPath/app-test.zip $myPath

echo '##############################################################'
echo '#                 test build success                         #'
echo '##############################################################'


