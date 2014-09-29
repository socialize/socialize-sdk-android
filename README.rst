=====================
Socialize SDK Android
=====================

Integrating Socialize into your App
===================================

Check out the full documentation to learn how to integrate Socialize into your app:

http://socialize.github.io/socialize-sdk-android/

Building Socialize from Source
==============================

**Note**: *This is not required if you simply want to integrate Socialize into your app*

Prerequisites
-------------

Make sure you have the following installed on your local machine:

- Android SDK (https://developer.android.com/sdk/)

- Pip::

    sudo easy_install pip
    
- Sphinx 1.2.2::

    sudo pip install sphinx
    
- ANT::
    
    brew install ant

Also make sure you have the following *versions* of the Android SDK installed:

- Android 2.2 (API 8)
- Android 4.4 (API 19)

These are installed using the Android SDK manager:

http://developer.android.com/tools/help/sdk-manager.html

Building Socialize
------------------

First clone **this** repo::

    git clone git@github.com:socialize/socialize-sdk-android.git

Socialize depends on 3 external library projects::

    git clone git@github.com:socialize/android-ioc.git
    git clone git@github.com:socialize/loopy-sdk-android.git
    git clone git@github.com:facebook/facebook-android-sdk.git
    
Setup the Facebook SDK for Build
````````````````````````````````

Switch to the verified (tested) version of Facebook::

    cd facebook-android-sdk
    git checkout sdk-version-3.17.2
    cd ../
    
This version of the Facebook SDK (3.17.2) has some compilation warnings which are treated 
as errors by facebook.  To override this, we need to change the compiler arguments in the
facebook ant.properties::

    vim facebook-android-sdk/facebook/ant.properties
    
Replace the following line::

    java.compilerargs=-Xlint -Werror
    
With::
    
    java.compilerargs=-Xlint
    
Now you can build the SDK distribution::
    
    cd socialize-sdk-android/sdk
    ant -Dsdk.dir=/usr/local/android clean build
    
Make sure you replace **/usr/local/android** with your local path to the Android SDK    

Building the Demo App
---------------------

To build and test the demo app from the command line::

    cd socialize-sdk-android/demo
    ant -Dsdk.dir=/usr/local/android clean release

Make sure you replace **/usr/local/android** with your local path to the Android SDK

Now you can install the demo app::

    /usr/local/android/platform-tools/adb uninstall com.socialize.demo
    /usr/local/android/platform-tools/adb install bin/socialize-demo-release.apk

The demo app is called, **Socialize Demos**

Building the Documentation
--------------------------

Note: Sphinx 1.2.2 is required to generate docs::

    sudo pip install sphinx

To build the html version of the documentation::

    cd socialize-sdk-android/sdk
    ant -Dsdk.dir=/usr/local/android doc

Make sure you replace **/usr/local/android** with your local path to the Android SDK

Now you can browse the documentation::

    open build/docs/user_guide/index.html

Running the Tests
-----------------

In order to run the tests you need *either* an Android 4.4 device or emulator.  We recommend using the 
Genymotion Android virtualization platform available here: http://www.genymotion.com/

Ensure the device/emulator is connected and available::

    /usr/local/android/platform-tools/adb devices

If you do not see any devices listed, try restarting the adb server::

    /usr/local/android/platform-tools/adb kill-server
    /usr/local/android/platform-tools/adb start-server
    
Prior to running the tests you **MUST** run an sdk cleanup so that the stage server has its state reset.  
This is a python script located in the *test* folder::

    cd socialize-sdk-android/test
    
    python sdk-cleanup.py <consumer-key> <consumer-secret> \
    <http://stage.api.socialize.com/v1> \
    [facebook_user_id] [facebook_token]

To run the tests::
    
    ant -propertyfile ant.global.properties -Dsdk.dir=/usr/local/android test-with-results

Make sure you replace **/usr/local/android** with your local path to the Android SDK

Now you can browse the coverage results::

    open coverage-results/coverage.html
    
Building the Distro
-------------------

To build the distributable SDK (zip)::

    cd socialize-sdk-android/sdk
    ant -Dsdk.dir=/usr/local/android clean build

Make sure you replace **/usr/local/android** with your local path to the Android SDK



