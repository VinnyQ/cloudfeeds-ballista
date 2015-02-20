cloudfeeds-ballista
==========================

A command line utility to migrate data from RDBMS databases to HDFS using SCP.


## How to Build
To build this component, we require:
* Gradle version 2.2 or above
* JDK 7
* Scala 2.10 or above


### Simple build
```
gradle clean build
```

### Build an RPM
```
gradle clean buildRpm
```

## How to run the App
Run a simple build to create an installable app. Run the following command.

```
java -Dconfig.file="/<file path>/cloudfeeds-ballista.conf" -Dlogback.configurationFile="/<file path>/logback.xml"-jar build/libs/cloudfeeds-ballista-<version>.jar
```
## How to run the App after installing the rpm
Run the command to build an RPM. Install rpm. Create the necessary config files in /etc/cloudfeeds-ballista. Execute the below script   

sh /usr/share/cloudfeeds-ballista/bin/cloudfeeds-ballista.sh


## Command line options

  -d <value> | --runDate <value>
         runDate is a date in the format yyyy-MM-dd. Data belonging to this date will be exported. Default value is yesterday.           
  -n <value> | --dbNames <value>
         dbNames is comma separated list of database names to be exported. Default value is all the databases configured.           
  --help
         Use this option to get detailed usage information of this utility.

## Configuration

The Cloud Feeds Ballista app uses the following configuration files:

### cloudfeeds-ballista.conf
This file configures the cloudfeeds-ballista.conf app itself. 

By default, the cloudfeeds-ballista app will try to find this file from classpath. This can be overriden by specifying the Java System properties ```-Dconfig.file=<path_to_file>```.


### logback.xml
This file has the logging related configuration.

By default, the cloudfeeds-ballista app will try to load this file from classpath. This can be overriden by editing the ```cloudfeeds-ballista.conf``` file above.
