# UiAutomator Test



## Test instructions
Firstly run, 
```
./gradlew assembleDebug
```
Install via adb, 
```
adb install -r -g app-debug.apk
```
Execute test command, 
```
adb shell am instrument -w -e class 'com.app.automatorjava.YoutubeTest' com.app.automatorjava/androidx.test.runner.AndroidJUnitRunner
```