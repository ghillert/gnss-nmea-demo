# GNSS NMEA Demo

## Introduction

This project demonstrates the integration of an external GNSS receiver (Global
Navigation Satellite System) into a [Java](https://en.wikipedia.org/wiki/Java_(programming_language)) application via the reception and processing
of NMEA (National Marine Electronics Association) messages.

This demo application *should* work with any GNSS devices that
emit [NMEA messages](https://gpsd.gitlab.io/gpsd/NMEA.html), e.g. devices using the u-blox [ZED-F9P](https://www.u-blox.com/en/product/zed-f9p-module) module. Technical documentation can be found at the [u-blox ZED-F9P Interface Description](https://www.u-blox.com/en/docs/UBX-18010854) guide.

This application allows you to connect to your GNSS device via one of the following options:

- **Bluetooth** using [JSR-82 / BlueCove](http://bluecove.org/)
- **Serial USB Connection** using [nrjavaserial](https://github.com/NeuronRobotics/nrjavaserial)

For development and testing, the [Ardusimple simpleRTK2B](https://www.ardusimple.com/simplertk2b/) standalone application board was used.

Furthermore, we use [Grafana](https://grafana.com/) to visualize some aspects of the data.
As such, we use [Micrometer](http://micrometer.io/) to collect GNSS data as metrics
and provide the data to [Prometheus](https://prometheus.io/), a monitoring system and time series database. Grafana will then be used as a dashboarding tool.

## Requirements

* Java 11
* [Maven](https://maven.apache.org/)

When using Bluetooth, please make sure that your GNSS-receiver's Bluetooth device is already paired with your operating system.

* Docker to run:
  - Grafana
  - Prometheus

In order to setup Grafana and Prometheus, we will use a docker-compose stack from https://github.com/vegasbrianc/prometheus.

Grafana Dashboard: http://localhost:3000/
Prometheus Dashboard: http://localhost:9090/graph

## Running

Build the [Spring Boot](https://spring.io/projects/spring-boot) project using Maven:

```bash
mvn clean package
```

Execute the app:

```bash
java -jar target/gnss-nmea-demo-1.0.0.BUILD-SNAPSHOT.jar
```

**Note**: The application should work on **MacOS Catalina** (v10.15.x) as well as
**Windows 10**.

By default (without custom configuration), the app will activate the Bluetooth support and start the discovery process of Bluetooth devices/services. If found, you can select the service via the console. The application will quit if no Bluetooth service is found.

You can bi-pass the discovery service by providing the optional argument `demo.settings.bluetooth-address`, e.g.:

```bash
java -jar target/gnss-nmea-demo-1.0.0.BUILD-SNAPSHOT.jar \
--demo.settings.id=btspp://98D351FDB940:1;authenticate=false;encrypt=false;master=false
```

Alternatively, you can activate serial (USB) support by providing the `demo.settings.type=serial` property.

```bash
java -jar target/gnss-nmea-demo-1.0.0.BUILD-SNAPSHOT.jar \
--demo.settings.type=serial
--demo.settings.id=/dev/tty.usbmodem1422401
```

## Result

Once connected, NMEA messages will be received, processed and relevant GNSS information
is shown in the console:

* Longitude + Latitude
* Altitude
* Fix status
* Number of GNSS satellites in view
