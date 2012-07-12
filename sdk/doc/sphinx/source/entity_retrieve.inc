An existing entity can be retrieved via the **getEntity** method.  Entities obtained in this way will also 
provide aggregate data on comments, likes, shares and views.  Refer to the :ref:`entity_stats` section for more detail on these aggregate values.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/EntitySnippets.java
	:start-after: begin-snippet-3
	:end-before: end-snippet-3
	
You can also retrieve several entities in one call:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/EntitySnippets.java
	:start-after: begin-snippet-4
	:end-before: end-snippet-4