# Airline Reservation System

A desktop-based **Airline Reservation System** developed using **Java Swing** and **SQLite**, designed to simulate core airline booking operations through an intuitive graphical user interface.

---

## Overview

This project demonstrates the design and implementation of a **standalone airline reservation application**.  
It supports:

- User authentication
- Customer management
- Flight booking
- Journey tracking
- Boarding pass generation
- Ticket cancellation

All data is persisted using a **local SQLite database**.

The application is built **without external frameworks** to emphasise core Java concepts such as:
- Swing-based UI development
- JDBC database connectivity
- File-based persistence

---

## Features

- User authentication (Login & Registration)
- Customer information management
- Flight details viewing
- Flight booking with automatic **PNR generation**
- Journey details lookup using PNR
- Boarding pass generation
- Ticket cancellation
- Local SQLite database integration
- Desktop GUI built using Java Swing

---

## Technology Stack

- **Programming Language:** Java  
- **UI Framework:** Java Swing  
- **Database:** SQLite  
- **Database Connectivity:** JDBC (`sqlite-jdbc`)  
- **Java Version:** Java 17+ (tested with **Java 21 LTS**)

---

## Project Structure

        airline-reservation-system/
        │
        ├── AirlineReservationSystem.java
        ├── run.sh
        ├── lib/
        │   └── sqlite-jdbc-3.51.1.0.jar
        ├── bin/
        ├── database/
        │   └── schema.sql
        ├── graphics/
        │   └── (UI assets)
        ├── .gitignore
        ├── LICENSE
        └── README.md

---

## Prerequisites

Ensure the following are installed on your system:
	•	Java JDK 17 or later
	•	Git
	•	macOS or Linux environment

Verify Java installation:

    java -version

How to Run (macOS / Linux)
Option 1: Using run.sh (Recommended)
    
    chmod +x run.sh
    ./run.sh
        
Option 2: Manual Compilation & Execution

    javac -cp lib/sqlite-jdbc-3.51.1.0.jar -d bin AirlineReservationSystem.java
    java -cp ".:bin:lib/sqlite-jdbc-3.51.1.0.jar" AirlineReservationSystem

---
    
## Default User Authentication Credentials

For initial access and testing purposes, the system includes a predefined administrator account:

- **Username:** `admin`
- **Password:** `admin`

These credentials allow immediate login without requiring prior registration.

---

Platform Support
	•	✅ macOS (tested)
	•	✅ Linux (compatible)
	•	❌ Windows not tested
Classpath separators may differ

---

Disclaimer

This project is developed for educational purposes only and is intended to demonstrate desktop application development concepts using Java Swing and SQLite.
