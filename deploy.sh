#!/bin/bash

# --- Deployment Script for good-service ---

# Configuration
GIT_REPO="https://jihulab.com/chenhhhg-group/good-service.git"
PROJECT_DIR="good-service"
# Use branch from the first argument, or default to 'dev'
BRANCH=${1:-dev}

echo "================================================="
echo "Starting deployment of good-service from branch '$BRANCH'"
echo "================================================="

# Step 1: Clone or update the repository
if [ -d "$PROJECT_DIR" ]; then
  echo "--> Project directory '$PROJECT_DIR' found. Pulling latest changes from branch '$BRANCH'..."
  cd $PROJECT_DIR
  git checkout $BRANCH
  git pull origin $BRANCH
  cd ..
else
  echo "--> Project directory '$PROJECT_DIR' not found. Cloning repository..."
  git clone -b $BRANCH $GIT_REPO
  if [ $? -ne 0 ]; then
    echo "!!! Git clone failed. Aborting deployment."
    exit 1
  fi
fi

# Navigate into the project directory
cd $PROJECT_DIR

echo "--> Current location: $(pwd)"

# Step 2: Build the project using Maven wrapper
echo "--> Building the project with Maven..."
if [ ! -f "mvnw" ]; then
    echo "!!! mvnw not found. Please ensure you are in the correct directory."
    exit 1
fi
chmod +x ./mvnw
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "!!! Maven build failed. Aborting deployment."
    exit 1
fi
echo "--> Build successful."

# Step 3: Stop the currently running application
echo "--> Stopping the running application..."
PID=$(ps -ef | grep "java -jar" | grep "good-service" | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
  echo "--> Found running process with PID: $PID. Killing it..."
  kill -9 $PID
  # Wait a moment to ensure the port is freed
  sleep 5
else
  echo "--> No running application found."
fi

# Step 4: Run the new version of the application
echo "--> Starting the new version of the application..."
JAR_FILE=$(find target -name "good-service-*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "!!! Could not find the JAR file in target directory. Aborting deployment."
    exit 1
fi

echo "--> Found JAR file: $JAR_FILE"
nohup java -jar $JAR_FILE > ../app.log 2>&1 &

echo "--> Waiting for application to start..."
sleep 15 # Give it some time to start

# Step 5: Check if the application started successfully
if grep -q "Started GoodServiceApplication" ../app.log; then
    echo "--> Application started successfully."
    echo "--> You can check the logs with: tail -f ../app.log"
else
    echo "!!! Application may have failed to start. Please check the logs at ../app.log"
fi

echo "================================================="
echo "Deployment finished."
echo "================================================="
