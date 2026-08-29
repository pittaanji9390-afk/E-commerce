.PHONY: all install build run test dev clean docker-build docker-up docker-down

all: build

install:
	@echo "Installing dependencies..."
	npm install
	cd frontend && npm install

build:
	@echo "Building frontend and backend..."
	cd frontend && npm run build
	cd backend && mvn clean package -DskipTests

run:
	@echo "Starting application services..."
	docker-compose up -d

dev:
	@echo "Starting local development mode..."
	npm run dev

test:
	@echo "Running test suite..."
	npm test
	cd backend && mvn test

coverage:
	@echo "Running test coverage analysis..."
	npm run test:coverage

docker-build:
	@echo "Building Docker image..."
	docker build -t marketplace-platform:latest .

docker-up:
	docker-compose up -d

docker-down:
	docker-compose down

clean:
	@echo "Cleaning build artifacts..."
	rm -rf dist frontend/dist backend/target
