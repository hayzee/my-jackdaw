# Clojure / Jackdaw Examples

## Info on docker/kafka setup:

https://hub.docker.com/r/bitnami/kafka/

https://lankydan.dev/running-kafka-locally-with-docker

## To start

### From the services directory, run the following command:

`$ docker-compose.yml up -d`

## To run a shell in the kafka docker container

`$ docker exec -it e3c52ccf6deb sh`     ; Note: replace e3c52ccf6deb with the kafka container id.

## When finished:

`$ docker-compose.yml down --remove-orphans`
