# Telemetry Server Documentation

This README provides a brief overview of the Telemetry Server setup and functionality.

## Overview

The Telemetry Server is designed to capture, store, and manage telemetry data. It includes endpoints for posting request, response, app data, and connection information. Additionally, it supports downloading the telemetry database from the frontend application.

## Database Setup

To initialize the database, the `DatabaseFactory` object is used. It automatically sets up tables for storing requests, responses, app data, and connection information. The SQLite database `telemetry.db` is used for storage.

## Endpoints

- **POST /request**: Accepts telemetry request data.
- **POST /response**: Accepts telemetry response data.
- **POST /app-data**: Accepts application data.
- **POST /connection**: Accepts connection data.
- **GET /table-info**: Provides counts and unique device identifiers from the tables.
- **GET /download-database**: Allows downloading of the SQLite database file.

## Downloading Database

Ensure to adjust the database file path in the `download-database` route to match your environment.


