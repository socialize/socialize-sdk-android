.. include:: header.inc

.. _ProGuard: http://developer.android.com/guide/developing/tools/proguard.html

.. _proguard_conf:	
	
======================
ProGuard Configuration
======================

If you are using ProGuard_ when building your app with Socialize you will need to add some lines to your **proguard.cfg** file
to preserve class the class names used by Socialize.

.. literalinclude:: snippets/proguard.txt
   :language: properties
   :linenos:

.. include:: footer.inc