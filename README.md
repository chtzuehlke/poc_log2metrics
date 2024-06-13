# What

This tool looks for patterns in lines coming from system in and provides metrics via an openmetrics-like endpoint with count statistics for pattern occurrences. 

This is pre-alpha code and should not really be used at the moment ;-)

# Sample

```
LOAD_TEST_CSV_FILE_PATH=prod-test-synthethic.csv java -jar target/imap-canary-0.0.1-SNAPSHOT.jar | ADDR=users1 PORT=8585 java -jar log2metrics-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
