.. include:: header.inc

.. _CompoundButton: http://developer.android.com/reference/android/widget/CompoundButton.html
.. _CheckBox: http://developer.android.com/reference/android/widget/CheckBox.html
.. _RadioButton: http://developer.android.com/reference/android/widget/RadioButton.html
.. _Switch: http://developer.android.com/reference/android/widget/Switch.html
.. _ToggleButton: http://developer.android.com/reference/android/widget/ToggleButton.html
.. _StateListDrawable: http://developer.android.com/guide/topics/resources/drawable-resource.html#StateList

====================
Custom Like Buttons
====================
One of the most commonly used social features is a simple "like" button.  Socialize provides a way to quickly and easily create a customized 
like button that you can include in your app with minimal effort.

The following step-by-step guide will show you how to include a basic like button with custom images and text.

How-To Guide
~~~~~~~~~~~~

Step 1 - Setup Socialize
========================

If you haven't already, make sure you have successfully completed the :doc:`getting_started` process

Step 2 - Create a Button State Drawable
=======================================

If you want your like button to match the look and feel of your app you can do this by creating a StateListDrawable_ as an XML configuration saved to **res/drawable**

.. image:: images/state_list_drawable.png	

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/like_button_layout.xml
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	:language: xml
	
This example is using two images from our demo app called "like" and "unlike"

========================== ============================
.. image:: images/like.png .. image:: images/unlike.png
========================== ============================

Step 3 - Create a Button
========================

The Socialize Like Button behavior can be applied to any type of CompoundButton_.  This includes CheckBox_, RadioButton_, ToggleButton_ and Switch_ (API 11)

To turn one of these button types into a Socialize Like Button, first define a button in your layout.  The following example uses a Checkbox:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/like_button_layout.xml
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	:language: xml	

Step 4 - Transform the Button
=============================

The final step in the process is to transform your button into a Socialize Like Button.

In the **onCreate** method of your Activity simply call the **makeLikeButton** method on the LikeUtils instance.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/LikeButtonSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
.. include:: footer.inc	

This will result in a completely custom Socialize Like Button!

.. image:: images/like_button.png