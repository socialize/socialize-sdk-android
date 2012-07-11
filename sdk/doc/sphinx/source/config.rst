.. include:: header.inc

.. _config:	
	
======================
Configuration Settings
======================

Socialize can be easily configured via a configuration file placed in the **assets** path of your project called **socialize.properties**

.. image:: images/socialize.properties.png

The following table lists the configuration options that can be specified in your **socialize.properties** file.

+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| Property                        | Type    | Default  | Example                                | Description                                                      |
+=================================+=========+==========+========================================+==================================================================+
| socialize.consumer.key          | String  | None     | 12a05e3e-e522-4c81-b4bb-89d3be94d122   | Your Socialize consumer key                                      |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.consumer.secret       | String  | None     | 9c313d12-f34c-4172-9909-180384c724fd   | Your Socialize consumer secret                                   |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.entity.loader         | String  | None     | com.mypackage.MyEntityLoader           | Fully qualified class name of your entity loader implementation  |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.comments.on.notify    | Boolean | false    |                                        | Users are directed to the comment list for comment notifications |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.require.auth          | Boolean | true     |                                        | Require users to authenticate with a 3rd party for social actions|
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.allow.anon            | Boolean | false    |                                        | Allow users to authenticate anonymously for social actions       |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.c2dm.sender.id        | String  | None     | ``yoursender@gmail.com``               | Optional.  Use if you have your own C2DM sender ID               |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| socialize.location.enabled      | Boolean | true     |                                        | Enable/Disable app-wide location services                        |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| facebook.app.id                 | Integer | None     | 1234567890                             | Your facebook app ID                                             |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| facebook.sso.enabled            | Boolean | true     |                                        | Enable/Disable Single Sign on for Facebook                       |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| twitter.consumer.key            | String  | None     | U18LUnVjULkkpGoJ6                      | Your Twitter consumer key                                        |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| twitter.consumer.secret         | String  | None     | RiIljnFq4RWV9LEaCM1ZLsAHf053vX2K       | Your Twitter consumer secret                                     |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| http.connection.timeout         | Long    | 10000    | 10000                                  | Time in ms for an http connection to be established              |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| http.socket.timeout             | Long    | 10000    | 10000                                  | Time in ms for an http request to return                         |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| log.level                       | String  | WARN     | DEBUG or INFO or WARN or ERROR         | Application log level                                            |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+
| redirect.app.store              | String  | android  | Only 'amazon' currently supported      | Provides for creation of non Android Market urls.                |
+---------------------------------+---------+----------+----------------------------------------+------------------------------------------------------------------+

.. include:: footer.inc