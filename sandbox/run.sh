#!/bin/bash

# Criar as docker networks
docker network create catalogo_elastic
docker network create catalogo_kafka

# Cria os docker volumes
docker volume create catalogo_es01
docker volume create catalogo_kafka01
docker volume create catalogo_zoo01

docker compose -f elk/docker-compose.yml up -d elasticsearch
docker compose -f kafka/docker-compose.yml --profile kafka up -d
docker compose -f kafka-connect/docker-compose.yml --profile all up -d