.. include:: header.inc

.. _lifecycle:		
	
===================
Socialize Lifecycle
===================

Socialize must be bound to the lifecycle of **each Activity** in which you want to include Socialize functionality.

This is to ensure that all resources used by Socialize are effectively cleaned up as well as important tasks like Facebook token refreshing
are performed when they should be.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/SampleActivity.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0


Intercepting Socialize Activity Lifecycle Events
------------------------------------------------

.. raw:: html
   :file: snippets/expert_warning.html

If you want to override the default behavior of an activity that is managed by Socialize you can inject a **SocializeActivityLifecycleListener**
into the Socialize singleton instance.

.. note:: This is a global setting.  The listener will be called for ALL Socialize activities.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/SampleActivityWithListener.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

.. include:: footer.inc	
