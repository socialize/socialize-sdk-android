.. include:: header.inc

.. _like_button:

=====================
Socialize Like Button
=====================

In v1.4 of the Socialize SDK for Android is a new stand-alone Like Button.  This is simply the "like" capablity of Socialize 
contained in a self-standing UI element which can be easily customized to suit your app.

Displaying the Like Button
~~~~~~~~~~~~~~~~~~~~~~~~~~
The Like Button can either be included via standard Android XML Layout files, or programmatically in your Activity code.

Using XML layout
################

.. include:: snippets/action_button_xml.txt

If you don't want to (or can't) set the entity key as a fixed value in XML, you can set it at runtime in code:

.. include:: snippets/action_button_xml_code.txt

Customization Options
---------------------

+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| Property                        | Type          | Default  | Example                                | Description                                                      |
+=================================+===============+==========+========================================+==================================================================+
| android:layout_width            | N/A           | None     | http://developer.android.com/reference/android/view/ViewGroup.LayoutParams.html#attr_android:layout_width |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| android:layout_height           | N/A           | None     | http://developer.android.com/reference/android/view/ViewGroup.LayoutParams.html#attr_android:layout_height|
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| android:background              | N/A           | None     | http://developer.android.com/reference/android/view/View.html#attr_android:background                     |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| android:textColor               | N/A           | None     | http://developer.android.com/reference/android/widget/TextView.html#attr_android:textColor                |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:entity_key            | String        | None     | 1234                                   | Your entity key.  Optional.  May be set in code.                 |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:entity_name           | String        | None     | ABCD                                   | Your entity name.  Optional.  May be set in code.                |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:src_active            | Resource ID   | None     |                                        | An image resource in your res/drawable/xxxx folder               |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:src_inactive          | Resource ID   | None     |                                        | An image resource in your res/drawable/xxxx folder               |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:src_disabled          | Resource ID   | None     |                                        | An image resource in your res/drawable/xxxx folder               |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:text_active           | String        | None     | Like                                   | Text to display when entity is liked                             |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:text_inactive         | String        | None     | Unlike                                 | Text to display when entity is NOT liked                         |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:share_location        | Boolean       | true     | true/false                             | If true the user's geo location will be shared in the like       |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:auto_auth             | Boolean       | true     | true/false                             | If true the user will be auto-authed to the chosen social network|
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:share_to              | String        | None     |facebook                                | Comma seperated list of social networks (e.g. facebook)          |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize:show_count            | Boolean       | true     | true/false                             | If true the count of current likes will be displayed             |
+---------------------------------+---------------+----------+----------------------------------------+------------------------------------------------------------------+

Using programmatic configuration
################################

If you prefer to do things the old-fashioned way, you can set all the parameters programmatically.

.. include:: snippets/action_button_programmatic.txt
	
.. include:: footer.inc	
