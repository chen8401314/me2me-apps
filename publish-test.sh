mvn clean package -Ptest
cp -r ./activity/target/jsw/app-activity-service ~/Desktop/apps
cp -r ./content/target/jsw/app-content-service ~/Desktop/apps
cp -r ./live/target/jsw/app-live-service ~/Desktop/apps
cp -r ./search/target/jsw/app-search-service ~/Desktop/apps
cp -r ./io/target/jsw/app-io-service ~/Desktop/apps
cp -r ./user/target/jsw/app-user-service ~/Desktop/apps
cp -r ./cache/target/jsw/app-cache-service ~/Desktop/apps
cp -r ./sms/target/jsw/app-sms-service ~/Desktop/apps
cp -r ./monitor/target/jsw/app-monitor-service ~/Desktop/apps
cp -r ./sns/target/jsw/app-sns-service ~/Desktop/apps
cp -r ./kafka/target/jsw/app-kafka-service ~/Desktop/apps

echo '##############################################################'
echo '#                            success                         #'
echo '##############################################################'


