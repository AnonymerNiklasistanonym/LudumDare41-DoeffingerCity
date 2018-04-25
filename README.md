# LudumDare41-DoeffingerCity

Racing-TowerDefense

## Setup

1. Clone files/git repository
2. Open Eclipse
3. Select `Import` > `Gradle` > `Existing Gradle Project` (then `Next` and `Next`)
4. Then click `Browse` and select the project directory (`td-racing`) and click `Finish`

---

If there are resource load errors you need to link the assets directory from `core` in the `desktop` project over the `Build path`

---

## Run

### Java Desktop (Windows, MacOs, Linux)

To run the program you need to do this:

1. Open the command line in the directory of the project (`td-racing`)
2. Run the command `./gradlew desktop:run`
3. Wait till a window pops up either in the foreground or background

### Java Html (Chrome, Firefox, ...)

To run the program in the web browser you need to this:

1. Open the command line in the directory of the project (`td-racing`)
2. Run the command `./gradlew html:clean` 
3. After the command is finished run the command `./gradlew html:superDev`
4. When the command prints to the console `...: http://localhost:9876` open the address [127.0.0.1:8080/html](127.0.0.1:8080/html) in the browser and wait till everything is loaded

## Export

### Java Desktop (Windows, MacOs, Linux)

To export the program as a *fat* `.jar` you need to do this:

1. Open the command line in the directory of the project (`td-racing`)
2. Run the command `./gradlew desktop:dist`
3. If gradle is finished go into the subdirectory `desktop`, then `build`, then `libs` and there is the `.jar` file that can be execued on any system that has a Java Runtime Engine (JRE) installed of version 1.6 or higher

### Java Html (Chrome, Firefox, ...)

To export the program as ? `war` web app you need to this:

1. Open the command line in the directory of the project (`td-racing`)
2. Run the command `./gradlew html:clean` 
3. After the command is finished run the command `./gradlew html:dist`
4. Then go into the directory `html` and then `build`
5. Now copy everything in this directory into an directory on you server or into `htdocs` if you use XAMPP and open the page (XAMPP: `127.0.0.1`) 

## Used Software

- To create the LibGDX project the original [LibGDX Setup App](https://libgdx.badlogicgames.com/download.html) was used
- To edit the Java code and run/debug it [Eclipse Java Oxygen](https://www.eclipse.org/oxygen/) was used in combination with Java 6 (because of LibGdx the version number is this low)
- To create the bounding maps for the box2D physics the [box2d-editor](https://code.google.com/archive/p/box2d-editor/downloads) was used
- To create the Bitmap fonts the program [Hiero](https://libgdx.badlogicgames.com/tools.html) was used