# File Storage Application

This is a Spring Boot application designed to store files, generate shared URLs, upload multiple files, delete files, and list all files. 
The application uses Java 21, MinIO for object storage, JPA for database interactions, PostgreSQL as the database, and Docker for containerization.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Docker](#docker)

## Features

- **File Storage**: Store files in MinIO.
- **Generate Shared URL**: Generate a shared URL for a file.
- **Upload Multiple Files**: Upload multiple files at once.
- **Delete File**: Delete a specific file.
- **List All Files**: List all stored files.

## Prerequisites

- Java 21
- Docker
- MinIO
- PostgreSQL

## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/file-storage-app.git
    cd file-storage-app
    ```

2. **Run the application**:

## Configuration

### Application Configuration

The application uses the following configuration files:

- `application.properties`: Configuration for the Spring Boot application, including database and MinIO settings.
