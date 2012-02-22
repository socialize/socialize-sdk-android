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

.. note:: There are NEW configurations required for entity loaders as of v1.4.4

Creating the Entity Loader
--------------------------

**IMPORTANT**: An entity loader **MUST define a parameterless constructor!**

There is only one Entity Loader per application and it can be set on the Socialize instance:

.. include:: snippets/entity_loader.txt

The **loadEntity** method will be called by Socialize when the user selects an item rendered by Socialize which corresponds directly to 
content in your app.

For example you may want to start an activity in the entity loader:

.. include:: snippets/entity_loader_example0.txt

Once you have an entity loader, simply set it on the Socialize instance

.. include:: snippets/entity_loader_example2.txt

The entity loader would usually be specified when you instantiate the ActionBar, for example:

.. include:: snippets/entity_loader_example1.txt

Configuring the Entity Loader for SmartAlerts
---------------------------------------------

When your app receives a SmartAlert, Socialize needs to know how to access your entity loader.  This is done via the 
**socialize.entity.loader** property in your **socialize.properties** configuration file:

.. include:: snippets/props_entity_loader.txt

Replace **com.mypackage.MyEntityLoader** with the fully qualified class name of your entity loader.

.. note:: An entity loader **MUST** define a parameterless constructor!

Refer to the :ref:`notifications` section for detailed information on configuring SmartAlerts

.. include:: footer.inc