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
package com.carmatechnologies.commons.jmx.utils;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractJmxTest {
	private JMXConnectorServer connectorServer;
	private JMXConnector connector;

	protected final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
	protected MBeanServerConnection jmxClient;

	@Before
	public void abstractSetup() throws IOException {
		final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://");
		connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
		connectorServer.start();
		final JMXServiceURL address = connectorServer.getAddress();

		connector = JMXConnectorFactory.connect(address);
		jmxClient = connector.getMBeanServerConnection();
		// Test JMX logic using jmxClient in child classes, e.g. :
		// String attr = (String) mbeanClient.getAttribute(objectName, "MyAttr");
		// or mbeanClient.addNotificationListener(objectName, listener, filter, null);
	}

	private void shutdown() throws IOException {
		shutdownConnector();
		shutdownConnectorServer();
	}

	private void shutdownConnector() throws IOException {
		if (connector != null)
			connector.close();
	}

	private void shutdownConnectorServer() throws IOException {
		if (connectorServer != null)
			connectorServer.stop();
	}

	@After
	public void abstractTearDown() throws IOException {
		shutdown();
	}
}
