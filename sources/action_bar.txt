.. include:: header.inc

====================
Socialize Action Bar
====================

The Socialize Action Bar is a single all inclusive control that can be easily added to any Android application.

.. image:: images/action_bar.png

The Action Bar provides immediate access to all Socialize features including

- Commenting
- Sharing via Facebook, Twitter, Email and SMS
- Liking
- Recording Views
- User Profile screens with recent user activity 	
- User Settings to allow users to customize their experience
- Subscribe/Unsubscribe to SmartAlerts for users

Displaying the Action Bar
~~~~~~~~~~~~~~~~~~~~~~~~~
The Action Bar is designed to automatically "pin" itself to the bottom of your view.
  
Adding the Action Bar to your app is done with a simple call to **showActionBar** from ActionBarUtils

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarSample.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
(Refer to the :doc:`getting_started` section for details on configuring your **socialize.properties** file.)

Disabling the ScrollView
~~~~~~~~~~~~~~~~~~~~~~~~
By default the ActionBar will create a ScrollView to house your existing content.  
This is typically necessary so that the ActionBar doesn't impede the use of your existing content.

If you don't want your content to be scrollable however, you can disable this feature by using **ActionBarOptions**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarSampleNoScroll.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

ActionBar Listener
~~~~~~~~~~~~~~~~~~
If you need or want to obtain a reference to the ActionBar view at runtime, you can use a creation listener to listen 
for the "onCreate" event of the ActionBar.

If you want to attach your own events to user operations on the ActionBar you can bind an **OnActionBarEventListener** 
to capture these.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarSampleListener.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
ActionBar Comment Listener
~~~~~~~~~~~~~~~~~~~~~~~~~~
In addition to ActionBar events you can also register a listener to receive events triggered by the Comment List View

If you need or want to obtain a reference to the ActionBar view at runtime, you can use a creation listener to listen 
for the "onCreate" event of the ActionBar.

If you want to attach your own events to user operations on the ActionBar you can bind an **OnCommentViewActionListener** 
to capture these.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarCommentListener.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
ActionBar Share & Social Listener
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
You can also specify a more complete listener interface to capture both action bar and sharing events.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarShareDialogListener.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
Making Hyerlinks Clickable
--------------------------	
The Socialize SDK includes a pre-built OnCommentViewActionListener instance that automatically makes hyperlinks appearing in comments clickable

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarLinkifyCommentListener.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

Reloading the Action Bar
~~~~~~~~~~~~~~~~~~~~~~~~
If you want to maintain a single action bar instance shared across multiple entities you can simply use the **refresh** method 
to instruct the ActionBar to reload after you change the entity key.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarReload.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarReload.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
.. _custom_actionbar:	
	
Customizing the Action Bar
~~~~~~~~~~~~~~~~~~~~~~~~~~
The Action Bar can be customized to suite the look-and-feel of your app.  You can alter colors, font colors and images on the ActionBar to give it a unique appearance

Anything from a simply accent change

.. image:: images/action_bar_red.png

To a complete re-imagining of the appearance

.. image:: images/action_bar_autumn.png

To change the appearance of the Action Bar simply provide an **ActionBarOptions** instance when you display the bar

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarCustom.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0


Adding/Removing Buttons
-----------------------

You can also programmatically remove entire functions from the action bar.  For example, the following code will remove
the comment button:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarNoComments.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	:emphasize-lines: 22

Action Bar Icon Assets
~~~~~~~~~~~~~~~~~~~~~~
To make customization simple we have provided Photoshop PSD templates for the standard Action Bar icons used in the default Action Bar.

These can be found in the **/icons** folder of your SDK download.

XML Based Layout
~~~~~~~~~~~~~~~~
If the "auto-pin" feature of the Action Bar is not to your liking, or doesn't play well with your existing layout 
you can always just add the view manually.

.. note:: There are some fairly important things the Action Bar expects that you should be aware of:

1. It MUST be included inside a RelativeLayout.  This is because several of the UI features need to be able to slide "over" your existing content.
2. It MUST be positioned at the bottom of your view. 
3. It MUST be included as the LAST element in your XML layout, otherwise you may get some strange behavior with layers sliding over/under content.

Here is the recommended way to include the Action Bar in your XML layout:

.. literalinclude:: snippets/action_bar_layout_xml.txt
   :language: xml

In this case you will still need to set the entity in code:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarSampleLayout.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
.. include:: footer.inc	
