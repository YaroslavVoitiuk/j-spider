# Leon Betting Data Processor

This project is a service for parsing and reporting betting data from the "Leon" bookmaker. It utilizes a web client to fetch data from the Leon API and processes this data to generate comprehensive reports.

## Overview

The project is composed of several components:
- **`LeonClient`**: A service that interacts with the Leon API to fetch match and event data.
- **`LeonBetsParser`**: A service that parses the betting data for various sports and generates a report.
- **`ReportGenerationService`**: A service that generates CSV reports based on the parsed betting data.
- **`LeonController`**: A REST controller that exposes an endpoint to trigger the parsing and reporting process.

## Features

- **Football Match Analysis**
- **Basketball Match Analysis**
- **Tennis Match Analysis**
- **E-sports Match Analysis**

## Technology Stack

- **Java**: The primary programming language for the service.
- **Jsoup**: Utilized for parsing HTML content and extracting necessary data.
- **SLF4J**: For logging purposes.
- **ExecutorService**: Manages concurrent tasks for parsing different sports pages.


## Dependencies

- Spring Boot
- Spring Web
- Jsoup (for HTML parsing)
- Jackson (for JSON processing)
- OpenCSV (for CSV file handling)
- Project Reactor (for handling asynchronous operations)

## Configuration

### Application Properties

Ensure you have the following properties configured in your `application.properties`:

```properties
# Leon API Paths
web-client.event-path=https://api.leon.com/event
web-client.all-events-path=https://api.leon.com/all-events

# Report file path
report.file-path=./reports/leon-bets-report.csv
```
An example of execution can be viewed in the file `example.csv`
