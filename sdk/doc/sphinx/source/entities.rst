.. include:: header.inc
	
==================
Socialize Entities
==================

An *Entity* is the term Socialize uses to describe any item of content within your app.  This could be anything from a News Article to a Hamburger and 
simply provides a reference point from which your users can perform social actions such as liking, commenting and sharing.

For example, imagine your app is a photography app where users can share photos they take with their device.  
In this scenario each photo would be an *entity* and users can then perform social actions on this entity.

.. note:: It is recommended that where possible an HTTP URL be used for your entity key

Entities in Socialize MUST be associated with a unique key.  It is recommended that where possible an 
HTTP URL be used (i.e. one that corresponds to an active web page).  If your entities do not have web addressable URLs Socialize can automatically 
generate an Entity Page for you.  Refer to the :ref:`entity_no_url` section on this page for more information.

Auto-Creation of Entities
-------------------------
In most cases Socialize will automatically create an entity for you based on the information you provide to a particular call in the SDK.

For example, if you are using the :doc:`action_bar` the entity that you pass to the Action Bar will be automatically created for you if it 
does not already exist.

.. note:: You should always specify a name when creating an entity, however if you have previously created the entity with a name and do not want to specify the name every time you can provide a "null" argument for the name.  This will instruct Socialize to ignore the name attribute and the name of the entity will NOT be updated.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarSimple.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	
Manual Creation of Entities
---------------------------
If you want complete control over the lifecycle of an entity you can also create or edit any entity in the system manually

To create an entity, simply call the **saveEntity** method:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/EntitySnippets.java
	:start-after: begin-snippet-2
	:end-before: end-snippet-2
	
Retrieving Entity Data
-----------------------
.. include:: entity_retrieve.inc	

.. _entity_popular:
	
Retrieving Popular Entities
---------------------------
.. include:: entity_popular.inc	

Entity Meta Data
----------------
.. include:: entity_meta_data.inc	

.. _entity_no_url:	

Entities Without URLs
---------------------
.. raw:: html
   :file: snippets/entity_page_notice.html
   
.. _SmartDownload: http://go.getsocialize.com/SmartDownloads

All entities in Socialize will be given an automatically generated entity page which forms part of the Socialize SmartDownload_ process.  

This default entity page can be **completely customized** to suit the look and feel of your app as well as being able to display specific information 
taken from your entity meta data.

.. note:: If your entity key uses an HTTP URL the contents of your entity page will be automatically parsed from that URL by default

To customize the content displayed on your entity pages simply add a JSON structure to your entity **meta data** that includes the following *szsd_* prefixed attributes:

.. literalinclude:: snippets/entity_page_json.txt
   :language: javascript
   :tab-width: 4
   
In code this would look something like this

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/EntitySnippets.java
	:start-after: begin-snippet-8
	:end-before: end-snippet-8
	
This will display on your entity page like this:

.. image:: images/szsd_entity_page.png

.. include:: footer.inc			
