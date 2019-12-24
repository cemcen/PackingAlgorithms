# Geometric Packing for Convex Polygons

## What is a Packing Algorithm? 

## App for mesh simulation


## Installation

### Requirements

* Java 1.8
* Play Library
* SBT (Scala)
* Node (npm)

### Development

For development you first need to install all the node libraries.

```
cd front/
npm install
```

After installing the nodes libraries needed you can start the application using:

```sbt run``` 


### Production

First enter the Play console:

```sbt```

Then for production we need to compile the project into JAR files using:

```sbt dist```

This produces a ZIP file containing all JAR files needed to
 run the application in the ```targer/universal``` folder. 
 To run the application, unzip the file on the target server,
  and then run the script in the ```bin``` directory. 

```
cd target/universal
unzip packvex-1.1.zip

 ```