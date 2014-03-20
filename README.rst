=====================
Socialize SDK Android
=====================

Integrating Socialize into your App
===================================

Check out the full documentation to learn how to integrate Socialize into your app:

http://socialize.github.io/socialize-sdk-android/

Building the Demo App
=====================

To build and test the demo app from the command line::

    cd demo
    ant -Dsdk.dir=/usr/local/android clean release

Make sure you replace **/usr/local/android** with your local path to the Android SDK

Now you can install the demo app::

    /usr/local/android/platform-tools/adb uninstall com.socialize.demo
    /usr/local/android/platform-tools/adb install bin/socialize-demo-release.apk

The demo app is called, **Socialize Demos**


