.. include:: header.inc

.. _entity_loader:	

==============
Entity Loaders
==============

Introduction
------------
The "entity loader" is a class provided by the developer (you) that gives Socialize a way to navigate your application.

When a user views a social action (e.g. a comment) the Entity Loader allows the user to navigate to the entity (content) to 
which the social action pertains.

This is particularly important for SmartAlerts (push notifications) and as such an entity loader is required for SmartAlerts.

Creating the Entity Loader
--------------------------

To create an Entity Loader you need to define a new class in your application that implements the **SocializeEntityLoader** 
interface.  

.. note:: An entity loader MUST define a parameterless constructor

There is only one Entity Loader per application and it can be set on the Socialize instance:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/EntityLoaderSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

The **loadEntity** method will be called by Socialize when the user selects an item rendered by Socialize which corresponds directly to 
content in your app.

For example you may want to start an activity in the entity loader:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/EntityLoaderSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1

Adding the Entity Loader to Socialize
-------------------------------------

To add your Entity Loader to Socialize simply add an entry to **socialize.properties**

.. literalinclude:: snippets/props_entity_loader.txt
   :language: properties

Replace **com.mypackage.MyEntityLoader** with the fully qualified class name of your entity loader.

.. include:: footer.inc