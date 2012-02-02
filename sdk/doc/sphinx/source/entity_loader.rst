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

There is only one Entity Loader per application and it can be set on the Socialize instance:

.. include:: snippets/entity_loader.txt

The **loadEntity** method will be called by Socialize when the user selects an item rendered by Socialize which corresponds directly to 
content in your app.

For example you may want to start an activity in the entity loader:

.. include:: snippets/entity_loader_example0.txt

The entity loader would usually be specified when you instantiate the ActionBar, for example:

.. include:: snippets/entity_loader_example1.txt

.. include:: footer.inc