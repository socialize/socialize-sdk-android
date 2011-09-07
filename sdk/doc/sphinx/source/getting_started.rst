=====================
Getting Started Guide
=====================

Installing the SDK 
------------------
The Socialize SDK is delivered as a single JAR file, simply copy the socialize-x.x.x.jar file 
from the **dist** folder to the **libs** path of your Android project.

NOTE: Applications targeting older versions of Android (1.6 and below)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Socialize does not support Android versions below v2.1-update1

Configure your AndroidManifest.xml
----------------------------------
Add the following lines to your AndroidManifest.xml under the <manifest...> element:

.. parsed-literal::

	<manifest...>
	
		<application.../>
		
		**<uses-permission android:name="android.permission.INTERNET"/>
		<uses-permission android:name="android.permission.READ_PHONE_STATE" />**
		
	</manifest>


Initializing the Socialize instance in your application
-------------------------------------------------------
The Socialize SDK should be initialized in the onCreate() method of your Activity, and destroyed in the onDestroy() method

.. parsed-literal::

	**import** com.socialize.Socialize;
	
	**protected** **void** onCreate(Bundle savedInstanceState) {
		**super**.onCreate(savedInstanceState);

		Socialize.init(**this**);
		...
	}
	
	**protected** **void** onDestroy() {
		Socialize.destroy(**this**);
		...
		**super**.onDestroy();
	}

Calling the Socialize API
-------------------------
Every call against the Socialize SDK MUST be authenticated.  

On the first successful call to "authenticate" the credentials are automatically cached in the 
application so subsequent calls are much faster.

ALL calls to the Socialize SDK are *asynchronous*.  
This means they will not interrupt your users' use of your application.  

You are notified of the outcome of calls to the Socialize service via a *SocializeListener* 
passed into each call to the Socialize SDK.

Authentication
~~~~~~~~~~~~~~
To authenticate your application for Socialize, simply call the **authenticate** method:

.. parsed-literal::

	Socialize.getSocialize().authenticate(
		consumerKey,
		consumerSecret,
		**new** SocializeAuthListener() {
		
		**public** **void** onAuthSuccess(SocializeSession session) {
			// Success!
		}
		
		**public** **void** onAuthFail(SocializeException error) {
			// Handle auth fail
		}
		
		**public** **void** onError(SocializeException error) {
			// Handle error
		}
	});

Calling SDK methods
~~~~~~~~~~~~~~~~~~~
