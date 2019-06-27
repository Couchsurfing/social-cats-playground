# Test Locally

# Stage

./gradlew :backend:socialcats-search-api:build :backend:socialcats-search-api:appengineStage

# Deploy App

./gradlew :backend:socialcats-search-api:build  :backend:socialcats-search-api:appengineDeploy

# Test Deployed app

curl  https://searchapi-dot-sweat-monkey.appspot.com