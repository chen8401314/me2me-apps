mvn clean package -Pproduct
cp -r ./activity/target/jsw/app-activity-service C:/Users/pc308/Desktop/apps
cp -r ./content/target/jsw/app-content-service C:/Users/pc308/Desktop/apps
cp -r ./live/target/jsw/app-live-service C:/Users/pc308/Desktop/apps
cp -r ./search/target/jsw/app-search-service C:/Users/pc308/Desktop/apps
cp -r ./io/target/jsw/app-io-service C:/Users/pc308/Desktop/apps
cp -r ./user/target/jsw/app-user-service C:/Users/pc308/Desktop/apps

echo '##############################################################'
echo '#                            success                         #'
echo '##############################################################'


