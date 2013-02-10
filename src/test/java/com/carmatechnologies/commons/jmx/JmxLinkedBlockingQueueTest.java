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

import java.util.concurrent.LinkedBlockingQueue;

import javax.management.ObjectName;

import org.junit.After;
import org.junit.Test;

import com.carmatechnologies.commons.jmx.MBeans.Builder;
import com.carmatechnologies.commons.jmx.utils.AbstractJmxTest;

public class JmxLinkedBlockingQueueTest extends AbstractJmxTest {
	private ObjectName objectName;

	@Test
	public void jmxQueueShouldBehaveLikeTheQueueItWrapsAndExposeQueueSizeViaJmxAndDefaultPackageNameAndType() throws Exception {
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		JmxLinkedBlockingQueue<String> jmxQueue = new JmxLinkedBlockingQueue<String>(queue);
		jmxQueue.put("A");
		jmxQueue.put("B");
		jmxQueue.put("C");

		objectName = jmxQueue.objectName();
		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx:type=JmxLinkedBlockingQueue"));

		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(mbeanServer.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(jmxClient.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxQueue.poll(), is("A"));
		assertThat(jmxQueue.poll(), is("B"));
		assertThat(jmxQueue.poll(), is("C"));
	}

	@Test
	public void jmxQueueShouldBehaveLikeTheQueueItWrapsAndExposeQueueSizeViaJmxAndDefaultPackageNameAndTypeEvenIfEmptyBuilderIsProvided() throws Exception {
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		JmxLinkedBlockingQueue<String> jmxQueue = new JmxLinkedBlockingQueue<String>(queue, new Builder());
		jmxQueue.put("A");
		jmxQueue.put("B");
		jmxQueue.put("C");

		objectName = jmxQueue.objectName();
		assertThat(objectName.toString(), is("com.carmatechnologies.commons.jmx:type=JmxLinkedBlockingQueue"));

		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(mbeanServer.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(jmxClient.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxQueue.poll(), is("A"));
		assertThat(jmxQueue.poll(), is("B"));
		assertThat(jmxQueue.poll(), is("C"));
	}

	@Test
	public void jmxQueueBuiltUsingBuilderWithCustomPackageNameShouldUseProvidedPackageAndDefaultType() throws Exception {
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		JmxLinkedBlockingQueue<String> jmxQueue = new JmxLinkedBlockingQueue<String>(queue, new Builder().packageName("my.custom.package"));
		jmxQueue.put("A");
		jmxQueue.put("B");
		jmxQueue.put("C");

		objectName = jmxQueue.objectName();
		assertThat(objectName.toString(), is("my.custom.package:type=JmxLinkedBlockingQueue"));

		assertThat(mbeanServer.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(mbeanServer.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxClient.isRegistered(objectName), is(true));
		assertThat(Integer.parseInt(jmxClient.getAttribute(objectName, "Size").toString()), is(3));

		assertThat(jmxQueue.poll(), is("A"));
		assertThat(jmxQueue.poll(), is("B"));
		assertThat(jmxQueue.poll(), is("C"));
	}

	@Test(expected = NullPointerException.class)
	public void jmxDecoratingNullQueueShouldThrowNullPointerException() throws Exception {
		new JmxLinkedBlockingQueue<String>(null);
	}

	@Test(expected = NullPointerException.class)
	public void jmxDecoratingQueueWithNullBuilderShouldThrowNullPointerException() throws Exception {
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		new JmxLinkedBlockingQueue<String>(queue, null);
	}

	@After
	public void tearDown() throws Exception {
		if (objectName != null)
			mbeanServer.unregisterMBean(objectName);
	}
}
