# Run locally

./gradlew :frontend:web:clean :frontend:web:webpack-run

# Build 

./gradlew :frontend:web:build

# Firebase hosting deploy

./gradlew :frontend:web:build && (cd frontend/web && firebase deploy)
