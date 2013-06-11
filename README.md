commons-jmx
===========

Library of utilities to assist with developing applications monitored via JMX.


---
**Features**:
- `MBeans` helper allow you to easily register/unregister MBeans.
- Use the builder to easily and transparently customize the MBeans' names.
- Decorators to easily expose the state of your collections (maps, queues) via JMX:
  - current size
  - current items (using `toString()` to convert all items, keys, values to something understandable for JMX)

--- 
**Examples**:

1. Register a MBean:

        IYourMXBean mbean = new YourMXBean();
        ObjectName objectName = MBeans.register(mbean); 
        // Object name is: '<YourMXBean's package>:type=YourMXBean'

2. Register a MBean with a custom name:

        IYourMXBean mbean = new YourMXBean();
        ObjectName objectName = MBeans.register(new Builder(mbean)
                .packageName("my.custom.package")
                .type("Cache")
                .property("name", "FirstLevelCache")
                .property("group", "InMemoryCaches"));
        // Object name is: 'my.custom.package:type=Cache,name=FirstLevelCache,group=InMemoryCaches'

3. Decorate your collections to expose them via JMX:

  - **ConcurrentMap**:

        ConcurrentMap<String, Integer> jmxMap = new JmxConcurrentMap<String, Integer>(new ConcurrentHashMap<String, Integer>());
        jmxMap.put("A", 1);
        jmxMap.put("B", 2);
        jmxMap.put("C", 3);
        // Object name is: 'com.carmatechnologies.commons.jmx:type=JmxConcurrentMap'

    or, using a Builder:

        ConcurrentMap<String, Integer> jmxMap = new JmxConcurrentMap<String, Integer>(new ConcurrentHashMap<String, Integer>(), new Builder(mbean)
                .packageName("my.custom.package")
                .type("Cache")
                .property("name", "FirstLevelCache")
                .property("group", "InMemoryCaches"));
        jmxMap.put("A", 1);
        jmxMap.put("B", 2);
        jmxMap.put("C", 3);
        // Object name is: 'my.custom.package:type=Cache,name=FirstLevelCache,group=InMemoryCaches'

  - **LinkedBlockingQueue**:

        LinkedBlockingQueue<String> jmxQueue = new JmxLinkedBlockingQueue<String>(new LinkedBlockingQueue<String>());
        jmxQueue.put("A");
        jmxQueue.put("B");
        jmxQueue.put("C");
        // Object name is: 'com.carmatechnologies.commons.jmx:type=JmxLinkedBlockingQueue'

    or, using a Builder:

        LinkedBlockingQueue<String> jmxQueue = new JmxLinkedBlockingQueue<String>(new LinkedBlockingQueue<String>(), new Builder(mbean)
                .packageName("my.custom.package")
                .type("WorkQueue")
                .property("name", "TweetsNotifications")
                .property("group", "WorkQueues"));
        jmxQueue.put("A");
        jmxQueue.put("B");
        jmxQueue.put("C");
        // Object name is: 'my.custom.package:type=WorkQueue,name=TweetsNotifications,group=WorkQueues'