.. include:: header.inc
	
=================
Socialize Sharing
=================

.. include:: snippets/sharing.txt

.. _sharing_adv:

Display Share Dialog with Custom Facebook Sharing
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To customize the content of shares posted to facebook, you can access the *ExtraParams* object in the share options

.. literalinclude:: snippets/2.0/share_options.txt
   :language: java
   :linenos:

Explicitly Sharing to Facebook (No Share Dialog)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
To share directly to Facebook simply call the extended version of the share method:

.. literalinclude:: snippets/2.0/share_facebook.txt
   :language: java
   :linenos:
   :emphasize-lines: 5

.. include:: footer.inc	