.. include:: header.inc

.. _best_practice:
	
===================
Extending Socialize
===================

.. raw:: html
   :file: snippets/expert_warning.html

Architecture
--------------

The Socializd SDK is designed around a tiered structure so that developers can easily alter or enhance how their users
interact with the Socialize system.

    .. image:: images/architecture.png

The Socialize UI Components provided in the SDK call the Socialize Core SDK in **exactly** the same manner as any external developer would
so you can easily replace the UI components shipped with the SDK with your own variations.

In addition each layer of the design includes a set of **callbacks** (listeners) to allow developers to integrate their features
and/or intercept calls made within the SDK.

Use Cases & Best Practices
--------------------------

There are several legitimate reasons why a developer might want to extend the Socialize framework:

 #. The developer wants to implement their own UI controls to create a completely custom look-and-feel
 #. The developer wants to make *slight* adjustments to the existing UI for which the default configuration options do not allow
 #. The developer wants to provide completely new functionality or integrate other services into the framework

By far the most common use case is the first in this list.  Creating a completely custom UI is one of the most requested
features of the Socialize SDK.

Although some customization is provided within the ActionBar (Refer to :ref:`custom_actionbar`) a complete re-imagining of the UI experience
requires a slightly more comprehensive approach.

Best Practice for UI Customization
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If the configuration options provided in the standard Socialize SDK are insufficient for your needs we recommend you create your own
UI elements and replace the default ones provided by Socialize.  This is a 4 step process:

.. note:: The following applies to **Android** implementations.  Differences may apply for other platforms.

See the :ref:`example` section at the bottom of this page for an example.

Step 1 - Declare your UI in a layout XML file
#############################################

All of the UI default components in the Sociaize SDK for Android are created programmatically [1]_, however it is not necessary to
define your own UI views in this way.

Simply define your view as an XML layout file and you can use this view in place of the Socialize view.

Step 2 - Create an Activity to house the layout
###############################################

All *Views* on Android must be housed in an Activity (or another View).  In the most basic implementation the user action that launches the UI view
will be changed to launch your activity.

Step 3 - Override the default behaviour for user actions (where required)
#########################################################################

If you are not planning to override *all* of the Socialize UI (i.e. only part of it) but you want to insert your own UI
elements into the normal flow (e.g. you want to keep the action bar, but change the comment list) you can simply use the
callback listeners provided in the SDK to override the default *click* action triggered by the Socialize UI components.

Step 4 - Implement the user actions taken from your UI using the Core SDK
#########################################################################

Once you have your UI and have bound the user actions you can then just call the Socialize Core SDK in the same way the
Socialize UI components do.

See the :ref:`example` section at the bottom of this page for an example, and/or refer to the :doc:`sdk_user_guide` section for details on
calling the Socialize Core SDK.


Generic SDK Enhancement
~~~~~~~~~~~~~~~~~~~~~~~
If there is an area of the SDK that you need/want to behave differently, and you feel that the changes you need are not specific
to your app, we **strongly** encourage you to follow the commonly adopted approach to open source contributions:

#. Fork the socialize-sdk-android repository on github (https://github.com/socialize/socialize-sdk-android)
#. Make the changes you require on your forked repo, being careful to ensure the changes are done in a generic manner
#. Submit a *Pull Request* so we can review the changes and look to incorporate them into the product

The open source community depends largely on contributions to make it better. If you feel there is something missing from the
product, don't be afraid to add it and let us know!


Things to avoid
---------------
*"With great power comes great responsibility"* [2]_.  When working with the Socialize SDK at a source code level there are some
common pitfalls of which one should be mindful:

Avoid changing Socialize code in your local copy
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
When you have the source code in front of you it's tempting to simply make whatever changes you believe are required to suit your
needs directly on the source tree of your local copy of Socialize.  This should be avoided in preference to forking the repository,
making changes in a **generic** way and submitting a pull request.  As Socialize releases new versions of the SDK with bug
fixes, enhancements and new features it will become increasingly difficult to merge these changes into your modified version of the
source tree.

Avoid partial changes to Socialize UI elements
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Customizing the UI is best done as an "all or nothing" affair.  That is, even if you want a look-and-feel that is only *slightly*
different to that provided by Socialize, we recommend you re-implement the entire UI in your own XML layout where possible as opposed
to extending the Socialize classes and making intimite modifications to how UI components are created.

Where possible Socialize will always retain the public method signatures of a class, and deprecate methods long before they are removed, however
we make no guarantees about the consistency of implementation *within* methods.  Altering the behavior of a sub-set of methods on a class that extends
a Socialize class may lead to unexpected errors as new versions of the SDK are released.

.. _example:

Customizing a View - Working Example
------------------------------------
The Socialize Demo app contains an example of a complete replacement of the Profile View accessed from the comment list.

Following the steps decsribed above:

Step 1 - Declare your UI in a layout XML file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. literalinclude:: ../../../../demo/res/layout/custom_profile_view.xml

Step 2 - Create an Activity to house the layout
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. literalinclude:: ../../../../demo/src/com/socialize/demo/CustomProfileViewActivity.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

Step 3 - Override the default behaviour for user actions (where required)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. literalinclude:: ../../../../demo/src/com/socialize/demo/implementations/actionbar/ActionBarWithCustomUserProfileActivity.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

Step 4 - Implement the user actions taken from your UI using the Core SDK
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(taken from CustomProfileViewActivity)

.. literalinclude:: ../../../../demo/src/com/socialize/demo/CustomProfileViewActivity.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1

This example results in the custom profile view being displayed when the user selects "Settings" from the menu

============================= =============================
.. image:: images/custom0.png .. image:: images/custom1.png
============================= =============================

Expert Level Customization
--------------------------

.. note:: The following is an **expert level** approach and may result in unexpected behavior

If none of the above approaches suits your needs and/or the changes you want to make are not with UI compoents, **AND** you cannot
make the changes in a generic way to contribute back to the project, then you can utilize a more drastic approach.

The Socialize SDK for Android uses a *Dependency Injection* (DI) framework to create and manage all the objects (beans) that make up Socialize.
Class and Object definitions are made *declaratively* in XML files that are loaded when Socialize is initialized.

This DI framework allows for the overriding of beans so that you can effectively replace entire implementations of objects.

In the **src** path of the Socialize SDK you will find several XML files that define the beans used by Socialize, these include:

 - socialize_core_beans.xml
 - socialize_notification_beans.xml
 - socialize_ui_beans.xml

Each of these files defines one or more *beans* which corresponds to Socialize objects.

For example the following defines the "DeviceUtils" object:

.. literalinclude:: ../../../../sdk/src/socialize_core_beans.xml
	:start-after: Begin Doc Snippet
	:end-before: End Doc Snippet

To replace this implementation, simply create a **separate** XML file that redefines this bean and place it in your classpath

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/bean_overrides.xml
	:start-after: Begin Doc Snippet
	:end-before: End Doc Snippet

If you are simply extending the Socialize class, be sure to include **ALL OBJECT INITIALIZATION PARAMETERS** in the bean declaration.

Finally add a line to your **socialize.properties** file:

.. literalinclude:: snippets/bean_overrides.txt
   :language: properties

This will cause any beans defined in **bean_overrides.xml** to override the declarations in the default Socialize implementation.

------

.. [1] This is somewhat of a legacy left over from when Socialize was distributed as a single JAR file and may change in future releases
.. [2] Spiderman (via Stan Lee)




.. include:: footer.inc
