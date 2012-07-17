.. include:: header.inc

.. _amazon:	
	
======================
Amazon AppStore URLs
======================

If you are publishing your Android app on the Amazon AppStore you may need to ensure any links generated from social actions (e.g. share) refer to 
your Amazon App listing rather than the default Android Market url.

To do this, simply add a configuration entry to your **socialize.properties** file

.. literalinclude:: snippets/props_amazon.txt
   :language: properties
   
The value for the Amazon AppStore is "amazon" (lower case)

.. include:: footer.inc