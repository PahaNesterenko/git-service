# Git Service

[![Build Status](https://travis-ci.org/your-username/git-service.svg?branch=main)](https://travis-ci.org/your-username/git-service) <!-- Replace 'your-username' with your GitHub username -->

Git Service is a microservice that provides a REST API for fetching Git information. It allows users to interact with Git repositories and retrieve various details such as repository information, commits, branches, and more.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
    - [Installation](#installation)
    - [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Deployment](#deployment-on-aws-fargate)
- [Testing](#testing)

## Introduction

Git Service is a Java 17 microservice built with Gradle and uses Feign as the HTTP client to interact with Git repositories. It provides a simple and efficient way to fetch Git-related information using RESTful API endpoints.

## Features

- Retrieve repository information (name, owner, description, etc.).
- Fetch commits for a given repository.
- Get a list of branches for a repository.
- View details of a specific commit.

## Prerequisites

- Java 17 (or later) JDK installed.
- Gradle build tool installed.

## Technology Stack

- Java 17
- Spring Boot
- Gradle
- Feign (for API calls)
- AWS Fargate
- AWS CloudFormation for infrastructure

## Getting Started

Follow the steps below to get started with Git Service.

### Installation

1. Clone the Git Service repository:

   ```bash
   git clone https://github.com/PahaNesterenko/git-service.git
   cd git-service

2. Build the microservice using Gradle:

    ```bash
   ./gradlew build

### Configuration

Before running the microservice, make sure to configure the Feign client to interact with your Git API. Update the configuration in the application.properties or application.yml file with the appropriate Git API base URL and any required authentication tokens.

## Usage

Git Service exposes various API endpoints that can be accessed to fetch Git-related information. Please refer to the API Documentation section for details on available endpoints and how to use them.

## API Documentation

The detailed API documentation for Git Service is available at http://localhost:8080/swagger-ui.html once the microservice is up and running. The Swagger UI allows you to explore and test the API endpoints interactively.

## Deployment on AWS Fargate

### Prerequisites

- You should have an AWS account with appropriate permissions to deploy AWS Fargate services.
- Ensure that you have the AWS CLI installed and configured with the correct credentials.

### Docker

- Build a Docker image for your microservice

    ```bash
    docker build -t test/git-service .

- and push it to AWS Elastic Container Registry

    ```bash
    docker tag test/git-service:latest public.ecr.aws/e9q2h4z1/test/git-service:latest
    docker push public.ecr.aws/e9q2h4z1/test/git-service:latest

### Infrastructure

After image was pushed to container registry we can ren service on AWS Fargate
Use AWS CLI or AWS console to execute CloudFormation templates from cf_templates directory to setup and run application on AWS cloud. 

Default region - eu-north-1

## Testing

You can run the tests for Git Service using the following Gradle command:

    ```bash
    ./gradlew build
