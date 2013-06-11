/*******************************************************************************
 * Copyright 2013 Marc CARRE (https://github.com/marccarre/commons-jmx)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.carmatechnologies.commons.jmx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.carmatechnologies.commons.jmx.MBeans.Builder;
import com.carmatechnologies.commons.jmx.mbeans.IJmxMap;

public class JmxConcurrentMap<K, V> implements ConcurrentMap<K, V>, IJmxMap {
	private final ConcurrentMap<K, V> map;
	private final ObjectName objectName;

	public JmxConcurrentMap(final ConcurrentMap<K, V> concurrentMap) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		checkNotNull(concurrentMap, "Concurrent map must NOT be null in order to be JMX-decorated.");
		this.map = concurrentMap;
		final Builder builder = new Builder(this);
		this.objectName = new ObjectName(builder.objectName());
		MBeans.register(builder);
	}

	public JmxConcurrentMap(final ConcurrentMap<K, V> concurrentMap, final Builder builder) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		checkNotNull(concurrentMap, "Concurrent map must NOT be null in order to be JMX-decorated.");
		checkNotNull(builder, "MBean builder must NOT be null.");
		this.map = concurrentMap;
		this.objectName = new ObjectName(builder.mbean(this).objectName());
		MBeans.register(builder);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(final Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(final Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(final K key, final V value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public V remove(final Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public V putIfAbsent(final K key, final V value) {
		return map.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(final Object key, final Object value) {
		return map.remove(key, value);
	}

	@Override
	public V replace(final K key, final V value) {
		return map.replace(key, value);
	}

	@Override
	public boolean replace(final K key, final V oldValue, final V newValue) {
		return map.replace(key, oldValue, newValue);
	}

	@Override
	public int getSize() {
		return size();
	}

	@Override
	public ObjectName objectName() {
		return objectName;
	}

	@Override
	public Map<String, String> getItems() {
		final Map<String, String> items = new LinkedHashMap<String, String>();
		for (final Entry<K, V> kvp : map.entrySet()) {
			items.put(kvp.getKey().toString(), kvp.getValue().toString());
		}
		return items;
	}
}
