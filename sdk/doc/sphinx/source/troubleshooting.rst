.. include:: header.inc

	
.. _support:	
	
=========================
Troubleshooting & Support
=========================


.. _debug_logs:	

Displaying Debug logs in LogCat
-------------------------------
If you're having problems with loading the Action Bar, or errors at runtime the simplest way to diagnose these problems is via the 
debug logs in the Android LogCat display.

By default Socialize will only render logs of level WARN to logcat.  To override this, simply add an entry to your socialize.properties file:

.. include:: snippets/props_log.txt

Socialize will now render more details logs to the logcat console.  All Socialize logs will be tagged with the word "Socialize" so you can 
filter them in the logcat display:

.. image:: images/logcat.png

Common Problems
---------------

Action Bar is does not display
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This is most often a result of mis-configuration in either the AndroidManifest.xml, or in your socialize.properties file.
In most cases the debug logs should give you an indication of the problem.  Refer to the above :ref:`debug_logs` section for details on displaying 
debug logs.

Action Bar is pushed up by Keyboard
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you are already using a ScrollView in your layout you may have noticed that when entering text the 
ActionBar is "pushed up" by the soft keyboard like so:

.. image:: images/action_bar_above.png

You can correct this by setting **android:isScrollContainer="false"** in the declaration of your ScrollView

.. raw:: html

	<pre class="brush: xml;">

	&lt;ScrollView 
		android:layout_width="fill_parent"
		android:layout_height="fill_parent"
		android:isScrollContainer="false">
		
		...
		
	&lt;/ScrollView>
	
	</pre>
	
Support and Feedback
--------------------

For direct access to our support team you can post questions on our support system here:

http://support.getsocialize.com/

.. include:: footer.inc	