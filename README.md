# MQTT demo project with Spring Boot

## Feature
- Connect to MQTT Broker through docker container
- Publishes temperature data JSON messages every 5 seconds on topic `temperature`
- Uses Eclipse Paho MQTT Client for MQTT communication
- Uses Spring Boot scheduling to send messages periodically

## How to run
1. Clone the project:
```bash
git clone https://github.com/Pyro9803/mqtt-demo.git
```
2. Open the project.
3. Run the docker container:
```bash
docker-compose up -d
```
4. Run the application.