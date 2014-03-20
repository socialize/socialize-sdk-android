=====================
Socialize SDK Android
=====================

Integrating Socialize into your App
===================================

Check out the full documentation to learn how to integrate Socialize into your app:

http://socialize.github.io/socialize-sdk-android/

Building Socialize
==================

.. note::

    This is not required if you simply want to integrate Socialize into your app

Socialize depends on 3 external library projects::

    cd ../
    git clone git@github.com:socialize/android-ioc.git
    git clone git@github.com:socialize/loopy-sdk-android.git
    git clone git@github.com:facebook/facebook-android-sdk.git
    cd facebook-android-sdk
    git checkout sdk-version-3.0.1
    cd ../

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


