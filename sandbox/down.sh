#!/bin/bash

PROFILES=$1

COMPOSE_PROFILES=$PROFILES docker compose -f elk/docker-compose.yml down
COMPOSE_PROFILES=$PROFILES docker compose -f kafka/docker-compose.yml down
COMPOSE_PROFILES=$PROFILES docker compose -f kafka-connect/docker-compose.yml down