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
4. ...Profit?

## Notes

In here: https://docs.google.com/document/d/1LKWW2drZA4GM1Hnr7rooYiS_hsCx3JeYQyQbryagDJs/edit?usp=sharing