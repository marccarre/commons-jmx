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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.ObjectName;
import javax.management.openmbean.TabularDataSupport;

import org.junit.After;
import org.junit.Test;

import com.carmatechnologies.commons.jmx.MBeans.Builder;
import com.carmatechnologies.commons.jmx.utils.AbstractJmxTest;

public class JmxConcurrentMapTest extends AbstractJmxTest {
	private ObjectName objectName;

	@Test
	public void jmxMapShouldBehaveLikeTheMapItWrapsAndExposeMapSizeViaJmxAndDefaultPackageNameAndType() throws Exception {
		ConcurrentMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		JmxConcurrentMap<String, Integer> jmxMap = new JmxConcurrentMap<String, Integer>(map);
		jmxMap.put("A", 1);
		jmxMap.put("B", 2);
		jmxMap.put("C", 3);

		assertThat(jmxMap.get("A"), is(map.get("A")));
		assertThat(jmxMap.get("B"), is(map.get("B")));
		assertThat(jmxMap.get("C"), is(map.get("C")));

		objectName = jmxMap.objectName();
		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx:type=JmxConcurrentMap"));

		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(mbeanServer.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(jmxClient.getAttribute(objectName, "Size").toString()), is(3));
	}

	@Test
	public void jmxMapShouldBehaveLikeTheMapItWrapsAndExposeMapSizeViaJmxAndDefaultPackageNameAndTypeEvenIfEmptyBuilderIsProvided() throws Exception {
		ConcurrentMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		JmxConcurrentMap<String, Integer> jmxMap = new JmxConcurrentMap<String, Integer>(map, new Builder());
		jmxMap.put("A", 1);
		jmxMap.put("B", 2);
		jmxMap.put("C", 3);

		assertThat(jmxMap.get("A"), is(map.get("A")));
		assertThat(jmxMap.get("B"), is(map.get("B")));
		assertThat(jmxMap.get("C"), is(map.get("C")));

		objectName = jmxMap.objectName();
		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx:type=JmxConcurrentMap"));

		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(mbeanServer.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(jmxClient.getAttribute(objectName, "Size").toString()), is(3));
	}

	@Test
	public void jmxMapBuiltUsingBuilderWithCustomPackageNameShouldUseProvidedPackageAndDefaultType() throws Exception {
		ConcurrentMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		JmxConcurrentMap<String, Integer> jmxMap = new JmxConcurrentMap<String, Integer>(map, new Builder().packageName("my.custom.package"));
		jmxMap.put("A", 1);
		jmxMap.put("B", 2);
		jmxMap.put("C", 3);

		assertThat(jmxMap.get("A"), is(map.get("A")));
		assertThat(jmxMap.get("B"), is(map.get("B")));
		assertThat(jmxMap.get("C"), is(map.get("C")));

		objectName = jmxMap.objectName();
		assertThat(objectName.toString(), is("my.custom.package:type=JmxConcurrentMap"));

		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(mbeanServer.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(jmxClient.getAttribute(objectName, "Size").toString()), is(3));
	}

	@Test
	public void getItemsOnAJmxMapShouldExposeTheContentOfTheDecoratedMapAsTabularData() throws Exception {
		ConcurrentMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		JmxConcurrentMap<String, Integer> jmxMap = new JmxConcurrentMap<String, Integer>(map, new Builder().packageName("my.custom.package"));
		jmxMap.put("A", 1);
		jmxMap.put("B", 2);
		jmxMap.put("C", 3);

		objectName = jmxMap.objectName();
		assertThat(mbeanServer.isRegistered(objectName), is(true));

		TabularDataSupport serverItems = (TabularDataSupport) mbeanServer.getAttribute(objectName, "Items");
		assertThat(serverItems.size(), is(3));

		String keys = serverItems.keySet().toString();
		assertThat(keys, containsString("A"));
		assertThat(keys, containsString("B"));
		assertThat(keys, containsString("C"));

		String values = serverItems.values().toString();
		assertThat(values, containsString("{key=A, value=1}"));
		assertThat(values, containsString("{key=B, value=2}"));
		assertThat(values, containsString("{key=C, value=3}"));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		TabularDataSupport clientItems = (TabularDataSupport) jmxClient.getAttribute(objectName, "Items");
		assertThat(clientItems, is(serverItems));
	}

	@Test(expected = NullPointerException.class)
	public void jmxDecoratingNullMapShouldThrowNullPointerException() throws Exception {
		new JmxConcurrentMap<String, Integer>(null);
	}

	@Test(expected = NullPointerException.class)
	public void jmxDecoratingMapWithNullBuilderShouldThrowNullPointerException() throws Exception {
		ConcurrentMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
		new JmxConcurrentMap<String, Integer>(map, null);
	}

	@After
	public void tearDown() throws Exception {
		if (objectName != null)
			mbeanServer.unregisterMBean(objectName);
	}
}
