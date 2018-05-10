#! /bin/sh
COUNTER=0
MAX=7

javac Java_Thread.java

> result.txt

echo "Cold Running tests"
nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 16 >> /dev/null

echo "Running tests for no multithreading..."
echo "### No Multithreading" >> result.txt
while [ $COUNTER -lt $MAX ]; do
    ((COUNTER++))
    echo "Running test #$COUNTER"
    nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 0 >> result.txt
done

COUNTER=0
echo "Running tests for 1 thread..."
echo "### Threading with 1 thread" >> result.txt
while [ $COUNTER -lt $MAX ]; do
    ((COUNTER++))
    echo "Running test #$COUNTER"
    nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 1 >> result.txt
done

COUNTER=0
echo "Running tests for 2 threads..."
echo "### Multithreading with 2 threads" >> result.txt
while [ $COUNTER -lt $MAX ]; do
    ((COUNTER++))
    echo "Running test #$COUNTER"
    nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 2 >> result.txt
done

COUNTER=0
echo "Running tests for 4 threads..."
echo "### Multithreading with 4 threads" >> result.txt
while [ $COUNTER -lt $MAX ]; do
    ((COUNTER++))
    echo "Running test #$COUNTER"
    nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 4 >> result.txt
done

COUNTER=0
echo "Running tests for 8 threads..."
echo "### Multithreading with 8 threads" >> result.txt
while [ $COUNTER -lt $MAX ]; do
    ((COUNTER++))
    echo "Running test #$COUNTER"
    nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 8 >> result.txt
done

COUNTER=0
echo "Running tests for 16 threads..."
echo "### Multithreading with 16 threads" >> result.txt
while [ $COUNTER -lt $MAX ]; do
    ((COUNTER++))
    echo "Running test #$COUNTER"
    nice -n -20 java -Xms1g -Xmx1g Java_Thread 4096.in 16 >> result.txt
done

echo "Done";
