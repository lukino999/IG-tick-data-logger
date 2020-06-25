### IG data logger

Save realtime price updates on csv.

<hr>


#### Usage
You need an account with IG Trading to create your API key.

In order for the jar to run you need the [JavaFX SDK 11.](https://gluonhq.com/products/javafx/)
Just download it and unzip.

Build the jar file (ie: IntelliJ IDEA Build artifact)
At the command prompt type

`java -jar --module-path path_to_javaFX_SDK/lib/ --add-modules javafx.controls,javafx.fxml ig-tick-data-logger.jar username password apiKey`
 
 The app will create a new directory from its location and will create a csv and a log file at each run.
 
 