#!/bin/bash

javac -cp lib/sqlite-jdbc-3.51.1.0.jar -d bin AirlineReservationSystem.java
java -cp ".:bin:lib/sqlite-jdbc-3.51.1.0.jar" AirlineReservationSystem



