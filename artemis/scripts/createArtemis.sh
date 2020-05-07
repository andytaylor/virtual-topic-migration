rm -rf test
$ARTEMIS_HOME/bin/artemis create test --user admin --password password --allow-anonymous --addresses topicA --host localhost --port-offset 100 --user admin --password admin
cp broker.xml test/etc