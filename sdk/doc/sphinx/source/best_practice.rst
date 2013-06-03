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
UI elements and replace the default ones provided by Socialize.  This is a 3 step process:

.. note:: The following applies to **Android** implementations.  Differences may apply for other platforms.

Step 1 - Declare your UI in a layout XML file
#############################################

All of the UI default components in the Sociaize SDK for Android are created programmatically [1]_, however it is not necessary to
define your own UI views in this way.

Simply define your view as an XML layout file and you can use this view in place of the Socialize view.

See the :ref:`example` section at the bottom of this page for an example.


Step 2 - Override the default behaviour for user actions (where required)
#########################################################################

If you are not planning to override *all* of the Socialize UI (i.e. only part of it) but you want to insert your own UI
elements into the normal flow (e.g. you want to keep the action bar, but change the comment list) you can simply use the
callback listeners provided in the SDK to override the default *click* action triggered by the Socialize UI components.

See the :ref:`example` section at the bottom of this page for an example.

Step 3 - Implement the user actions taken from your UI using the Core SDK
#########################################################################

Once you have your UI and have bound the user actions you can then just call the Socialize Core SDK in the same way the
Socialize UI components do.

See the :ref:`example` section at the bottom of this page for an example, and/or refer to the :doc:`sdk_user_guide` section for details on
calling the Socialize Core SDK.



Generic SDK Enhancement
~~~~~~~~~~~~~~~~~~~~~~~


Things to avoid
---------------


.. _example:

Customizing a View - Working Example
------------------------------------
asdad



------

.. [1] This is somewhat of a legacy left over from when Socialize was distributed as a single JAR file and may change in future releases





.. include:: footer.inc
