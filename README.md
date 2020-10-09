# schedule-psi

schedule-psi is a software designed to help the Junior-Entreprise Physique Strasbourg Ingenierie (PSI) to schedule the meetings 
to interview their members. The software has been especially designed for their needs. Only a French version is supported for now.

## Description

The software is built in Kotlin with the [TornadoFX framework](https://github.com/edvin/tornadofx). Maeven was used to compile the project.

The software use the information from the Excel file exported by the [Doodle tool](https://doodle.com/). Only the tool french version is handled.
Members must specify their availabilities on the website and then the corresponding Excel file is used by the software to start planning.

The software stores JSON file as historics. The corresponding folder is called `MyPlannings`.

## Get started

### User mode

Just clone the repository on your desktop.
```
git clone https://github.com/lcaruso27/schedule-psi.git
```

Then open the executable file:`schedule-psi.exe`. This file was produced with two steps:
* Computes the JAR file of the project from my IDE (IntelliJ IDEA 2020 - Artifacts)
* Convert JAR file into EXE file thanks to [Launch4J](http://launch4j.sourceforge.net/)

NOTE: The historic folder will be written in the home repository.

### Developer mode

For any contribution or modification:
* Set up the proper environment to run any Kotlin project: Java JDK + IDE. See [Getting Started with Kotlin](https://kotlinlang.org/docs/tutorials/getting-started.html) tutorial.
* Install [JavaFX](https://openjfx.io/openjfx-docs/#maven) in your environment with Gradle, Maven or manually.
* Install the [TornadoFX framework](https://github.com/edvin/tornadofx).
* Clone or fork this repository:
	```
	git clone https://github.com/lcaruso27/schedule-psi.git

	git fork https://github.com/lcaruso27/schedule-psi.git
	```
* Open `schedule-psi` project from your favorite IDE. Feel free to use either Maven or Gradle as compiler for the project.

NOTE: The historic folder will be written at the root of the project directory.

## Repository organisation
````
schedule-psi
├── MyPlannings 					- Folder with historic JSON files
├── package                                             - Folder with executables
│	├── jre						- Folder with all java ressources to make working the application
│	├── psi_icon.ico				- Icon file for the exe
│	├── README.md					- Some pretty good information about the software are written there...  
│	└── schedule-psi.jar			        - JAR file build by IntelliJ Idea IDE 
├── src 						- Source code of the whole project
├── LICENSE						- Project license
├── pom.xml 						- XML file to compile the project with Maeven
├── README						- Readme file
└── schedule-psi.exe					- Executable file to use SchedulePSI application based on the JAR file. Launch4J software was used to convert JAR file into EXE one.
````

## Screenshots

## License

schedule-psi is available under the MIT license. See LICENSE file.

## Additional information

Any thoughtful and helpful comments are welcome to improve the software. :)
