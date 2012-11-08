.. _localization:

============
Localization
============

Localization in Socialize consists simply of adding various Strings for a specific locale that Socialize will use if the device is set to that locale (language).

To localize a specific installation you simply need to add whichever Strings you want to override to your **res/values-xx/strings.xml** file in your Android project.

Refer to the `Android Localization Tutorial <http://developer.android.com/guide/topics/resources/localization.html>`_ for information on localizing Android projects.

.. note:: Socialize localization does not support changing images, only changing text in your strings.xml file

For example, if you wanted to localize for French, you would create a strings.xml in your **res/values-fr/strings.xml**

.. literalinclude:: snippets/localization.txt
   :language: xml
   :tab-width: 4
   :encoding: utf-8

Localization Table
------------------

The following is the list of configurable Strings and their default values:

.. literalinclude:: ../../../src/i18n.properties
   :language: properties
