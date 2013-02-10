/*******************************************************************************
 * Copyright 2013 Marc CARRE
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
import static org.hamcrest.Matchers.is;

import javax.management.InstanceAlreadyExistsException;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Test;

import com.carmatechnologies.commons.jmx.MBeans.Builder;
import com.carmatechnologies.commons.jmx.utils.AbstractJmxTest;
import com.carmatechnologies.commons.jmx.utils.IMonitoredResource;
import com.carmatechnologies.commons.jmx.utils.MonitoredResource;

public class MBeansTest extends AbstractJmxTest {
	private ObjectName objectName;

	@Test
	public void registeringMBeanShouldUseDefaultPackageAndType() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(mbean);

		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx.utils:type=MonitoredResource"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanWithSimpleBuilderShouldUseDefaultPackageAndType() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean));

		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx.utils:type=MonitoredResource"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanAndPackageNameShouldUseProvidedPackageAndDefaultType() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean).packageName("my.custom.package"));

		assertThat(objectName.toString(), is("my.custom.package:type=MonitoredResource"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanAndTypeShouldUseProvidedTypeAndDefaultPackage() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean).type("Cache"));

		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx.utils:type=Cache"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanAndPackageNameAndTypeShouldUseProvidedValues() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean).packageName("my.custom.package").type("Cache"));

		assertThat(objectName.toString(), is("my.custom.package:type=Cache"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanAndPropertyShouldUseDefaultPackageAndTypeAndProvidedProperty() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean).property("group", "InMemoryCaches"));

		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx.utils:type=MonitoredResource,group=InMemoryCaches"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanAndPropertiesShouldUseAllProvidedPropertiesInTheOrderTheyAreAdded() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean).packageName("my.custom.package").type("Cache").property("name", "MyCache")
				.property("group", "InMemoryCaches"));

		assertThat(objectName.toString(), is("my.custom.package:type=Cache,name=MyCache,group=InMemoryCaches"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test
	public void registeringMBeanAndPropertyWithDisabledTypeShouldUseProvidedPropertyAndRemoveType() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean).packageName("my.custom.package").property("name", "MyCache").disableType());

		assertThat(objectName.toString(), is("my.custom.package:name=MyCache"));
		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(mbeanServer.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(jmxClient.getAttribute(objectName, "Name").toString(), is("FirstLevelCache"));
	}

	@Test(expected = InstanceAlreadyExistsException.class)
	public void registeringTheSameMBeanTwiceThrowsInstanceAlreadyExistsException() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean));
		objectName = MBeans.register(new Builder(mbean));
	}

	@Test
	public void registeringTheSameMBeanTwiceCanBeDoneIfMBeanIsFirstUnregistered() throws Exception {
		IMonitoredResource mbean = new MonitoredResource("FirstLevelCache");
		objectName = MBeans.register(new Builder(mbean));
		MBeans.unregister(objectName);
		objectName = MBeans.register(new Builder(mbean));
	}

	@Test
	public void registeringTheSameMBeansTwiceCanBeDoneIfMBeansAreFirstUnregistered() throws Exception {
		IMonitoredResource mbean1 = new MonitoredResource("FirstLevelCache");
		IMonitoredResource mbean2 = new MonitoredResource("SecondLevelCache");
		objectName = MBeans.register(new Builder(mbean1).property("name", "1stLevel"));
		objectName = MBeans.register(new Builder(mbean2).property("name", "2ndLevel"));
		MBeans.unregisterAll();
		objectName = MBeans.register(new Builder(mbean1).property("name", "1stLevel"));
		objectName = MBeans.register(new Builder(mbean2).property("name", "2ndLevel"));
	}

	@After
	public void tearDown() throws Exception {
		if (objectName != null)
			mbeanServer.unregisterMBean(objectName);
	}
}
