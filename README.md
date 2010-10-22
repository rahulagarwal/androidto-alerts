# AndroidTO Alerts

This is an Android app that demonstrates the use of the Android Cloud to Device Messaging (C2DM) Framework. Its a simple alerting app that receives push alert notifications sent from a server and notifies the user. 

An implementation of a server that pushes alerts to this app can be found [here](http://github.com/rahulagarwal/androidto-alertspusher).

Details around the Android C2DM Framework can be found [here](http://code.google.com/android/c2dm/index.html).

## Device Compatibility
C2DM applications can only run on Android 2.2 devices. (Android SDK Version 8)

## Development/Test Requirements
 - To develop/test this app, you need to run and debug the applications on an Android 2.2 system image that includes the necessary underlying Google services.
 - To develop/debug on an actual device, you need a device running an Android 2.2 system image that includes the Market application.
 - To develop/test on the Android Emulator, you need to download the Android 2.2 version of the Google APIs Add-On into your SDK using the Android SDK and AVD Manager. Specifically, you need to download the component named "Google APIs by Google Inc, Android API 8". Then, you need to set up an AVD that uses that system image.

## Build/Run Instructions
Assuming you are using the [ADT Plugin for Eclipse](http://developer.android.com/sdk/eclipse-adt.html).

 - Create a new Android Project using all the files in the /src and /res folders of this project. 
 - Fill the correct account/server information in Constants.java located in com/rahulagarwal/android/androidtoalerts/
 - Make sure you have an Android Emulator/Device which meets the above requirements
 - Build and Run from Eclipse
