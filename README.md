# Docker Setup for Online Book Store

## Overview

This project uses Docker Compose to run:

* MySQL 8.0 database
* Spring Boot application
* Environment variables loaded from a `.env` file

The application connects to MySQL using the variables defined in the `.env` file.

## Environment Configuration

### `.env.sample`

A template file containing all required environment variables:

```env
MYSQLDB_USER=
MYSQLDB_PASSWORD=
MYSQLDB_ROOT_PASSWORD=
MYSQLDB_DATABASE=

MYSQLDB_LOCAL_PORT=
MYSQLDB_DOCKER_PORT=

SPRING_LOCAL_PORT=
SPRING_DOCKER_PORT=
DEBUG_PORT=
```

Create your local environment file:

```bash
cp .env.sample .env
```

Then fill in all required values.

## Environment Variables Mapping

The project uses the `MYSQLDB_*` prefix.

These variables are mapped in `docker-compose.yml` to the official MySQL environment variables:

```yaml
MYSQL_ROOT_PASSWORD=${MYSQLDB_ROOT_PASSWORD}
MYSQL_USER=${MYSQLDB_USER}
MYSQL_PASSWORD=${MYSQLDB_PASSWORD}
MYSQL_DATABASE=${MYSQLDB_DATABASE}
```

## Running the Application

1. Copy the sample environment file:

```bash
cp .env.sample .env
```

2. Fill in all required values in `.env`.

3. Build and start the containers:

```bash
docker compose up --build
```

## Accessing the Application

The application will be available at:

```text
http://localhost:${SPRING_LOCAL_PORT}
```

Example:

```text
http://localhost:8088
```

