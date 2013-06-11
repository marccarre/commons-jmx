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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public final class MBeans {
	private static final String DEFAULT_TYPE = "Object";

	private MBeans() {
		// Pure utility class, do NOT instantiate.
	}

	private static final String DEFAULT_PACKAGE = "default";
	private static final Logger LOGGER = LoggerFactory.getLogger(MBeans.class);
	private static final MBeanServer MBEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
	private static final Collection<ObjectName> OBJECT_NAMES = new ConcurrentLinkedQueue<ObjectName>();
	private static final Joiner.MapJoiner JOINER = Joiner.on(",").withKeyValueSeparator("=");

	public static String getJmxPort() {
		final String port = System.getProperty("com.sun.management.jmxremote.port");
		return (port == null || port.isEmpty()) ? "N/A" : port;
	}

	public static String getJmxIpPort() {
		return getIpAddress() + ":" + getJmxPort();
	}

	private static String getIpAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Failed to get current host's IP address.", e);
		}
	}

	public static synchronized ObjectName register(final Object mbean) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		checkNotNull(mbean, "MBean must NOT be null.");
		return register(new Builder(mbean));
	}

	public static synchronized ObjectName register(final Builder builder) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		checkNotNull(builder, "MBean builder must NOT be null.");
		final ObjectName objectName = new ObjectName(builder.objectName());
		MBEAN_SERVER.registerMBean(builder.mbean(), objectName);
		OBJECT_NAMES.add(objectName);
		LOGGER.info("Registered MBean '" + objectName + "'");
		return objectName;
	}

	private static String getType(final Object mbean) {
		return (mbean == null) ? DEFAULT_TYPE : mbean.getClass().getSimpleName();
	}

	private static String getPackageName(final Object mbean) {
		if (mbean == null)
			return DEFAULT_PACKAGE;

		final Package mbeanPackage = mbean.getClass().getPackage();
		return (mbeanPackage == null) ? DEFAULT_PACKAGE : mbeanPackage.getName();
	}

	private static String getObjectName(final String packageName, final Map<String, String> properties) {
		final StringBuilder builder = new StringBuilder(packageName);
		builder.append(":");
		builder.append(JOINER.join(properties));
		return builder.toString();
	}

	/**
	 * Unregisters all registered MBeans. WARNING: If not called, may cause troubles on re-deployment or when trying to re-register the same MBean.
	 */
	public static synchronized void unregisterAll() {
		final Iterator<ObjectName> iterator = OBJECT_NAMES.iterator();
		while (iterator.hasNext()) {
			tryUnregisterMBean(iterator.next());
		}

		OBJECT_NAMES.clear();
	}

	/**
	 * Unregisters the specified MBean. NOTE: Consider calling {@link #unregisterAll()} before terminating your application.
	 */
	public static synchronized void unregister(final ObjectName objectName) {
		tryUnregisterMBean(objectName);
		OBJECT_NAMES.remove(objectName);
	}

	private static void tryUnregisterMBean(final ObjectName objectName) {
		try {
			MBEAN_SERVER.unregisterMBean(objectName);
			LOGGER.info("Unregistered MBean '" + objectName + "'");
		} catch (InstanceNotFoundException e) {
			LOGGER.info("MBean '" + objectName + "' doesn't exist or has been already unregistered.");
		} catch (Exception e) {
			LOGGER.error("Failed to unregister MBean '" + objectName + "'");
		}
	}

	public static final class Builder {
		private static final String TYPE = "type";
		private final Map<String, String> properties = new LinkedHashMap<String, String>();
		private Object mbean;
		private String mbeanType;
		private String packageName;
		private boolean disabledType = false;

		public Builder() {
			properties.put(TYPE, DEFAULT_TYPE);
		}

		public Builder(final Object mbean) {
			mbean(mbean);
		}

		public Builder mbean(final Object mbean) {
			checkNotNull(mbean, "MBean must NOT be null.");
			setMBean(mbean);
			return this;
		}

		private void setMBean(final Object mbean) {
			this.mbean = mbean;
			if (!disabledType) {
				mbeanType = getType(mbean);
				properties.put(TYPE, mbeanType);
			}
		}

		public Builder packageName(final String packageName) {
			checkNotNull(packageName, "Package name for '" + mbeanType + "' must NOT be null.");
			checkArgument(!packageName.isEmpty(), "Package name for '" + mbeanType + "' must NOT be empty.");
			this.packageName = packageName;
			return this;
		}

		public Builder type(final String type) {
			checkNotNull(type, "Type for '" + mbeanType + "' must NOT be null.");
			checkArgument(type.isEmpty() == false, "Type for '" + mbeanType + "' must NOT be empty.");

			if (disabledType)
				throw new UnsupportedOperationException("Type can't be set once it has been disabled.");

			properties.put(TYPE, type);
			return this;
		}

		public Builder property(final String key, final String value) {
			checkNotNull(key, "Custom property for '" + mbeanType + "' must NOT be null.");
			checkArgument(!key.isEmpty(), "Custom property for '" + mbeanType + "' must NOT be empty.");
			checkNotNull(value, "Value for '" + key + "' on '" + mbeanType + "' must NOT be null.");
			checkArgument(!value.isEmpty(), "Value for '" + key + "' on '" + mbeanType + "' must NOT be empty.");

			if (disabledType && key.equals(TYPE))
				throw new UnsupportedOperationException("Type can't be set once it has been disabled.");

			properties.put(key, value);
			return this;
		}

		public Builder disableType() {
			properties.remove(TYPE);
			checkArgument(!properties.isEmpty(), "Make sure you add other properties before you disable 'type' on '" + mbeanType + "'.");
			disabledType = true;
			return this;
		}

		public String objectName() {
			return getObjectName((packageName == null ? getPackageName(mbean) : packageName), properties);
		}

		public Object mbean() {
			return mbean;
		}
	}
}
