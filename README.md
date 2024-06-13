# What

This tool scans for patterns in SYSIN and provides openmetrics-like metrics with counts for pattern occurrences. 

This is pre-aplha code and should not really be used at the moment ;-)

# Sample

```
LOAD_TEST_CSV_FILE_PATH=prod-test-synthethic.csv java -jar target/imap-canary-0.0.1-SNAPSHOT.jar | ADDR=users1 PORT=8585 java -jar log2metrics-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
