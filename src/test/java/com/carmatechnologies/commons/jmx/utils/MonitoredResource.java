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
package com.carmatechnologies.commons.jmx.utils;

public class MonitoredResource implements IMonitoredResource {
	private final String name;

	public MonitoredResource(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
