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
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

import com.carmatechnologies.commons.jmx.MBeans.Builder;

public class MBeansBuilderTest {
	@Test
	public void creatingObjectNameFromMBeanShouldUseDefaultPackageAndType() {
		Builder builder = new Builder("A");

		assertThat(builder.objectName(), is("java.lang:type=String"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameFromMBeanAndPackageNameShouldUseProvidedPackageAndDefaultType() {
		Builder builder = new Builder("A").packageName("my.custom.package");

		assertThat(builder.objectName(), is("my.custom.package:type=String"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameFromMBeanAndTypeShouldUseProvidedTypeAndDefaultPackage() {
		Builder builder = new Builder("A").type("SuperString");

		assertThat(builder.objectName(), is("java.lang:type=SuperString"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameFromMBeanAndPackageNameAndTypeShouldUseProvidedValues() {
		Builder builder = new Builder("A").packageName("my.custom.package").type("SuperString");

		assertThat(builder.objectName(), is("my.custom.package:type=SuperString"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameFromMBeanAndPropertyShouldUseDefaultPackageAndTypeAndProvidedProperty() {
		Builder builder = new Builder("A").property("group", "Vowels");

		assertThat(builder.objectName(), is("java.lang:type=String,group=Vowels"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameFromMBeanAndPropertiesShouldUseAllProvidedPropertiesInTheOrderTheyAreAdded() {
		Builder builder = new Builder("A").packageName("my.custom.package").type("SuperString").property("name", "MySuperA").property("group", "Vowels");

		assertThat(builder.objectName(), is("my.custom.package:type=SuperString,name=MySuperA,group=Vowels"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameFromMBeanAndPropertyWithDisabledTypeShouldUseProvidedPropertyAndRemoveType() {
		Builder builder = new Builder("A").packageName("my.custom.package").property("name", "MySuperA").disableType();

		assertThat(builder.objectName(), is("my.custom.package:name=MySuperA"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test
	public void creatingObjectNameShouldUseDefaultPackageAndType() {
		Builder builder = new Builder();

		assertThat(builder.objectName(), is("default:type=Object"));
		assertThat(builder.mbean(), is(nullValue()));
	}

	@Test
	public void creatingObjectNameFromMBeanUsingBuilderSetterShouldUseDefaultPackageAndType() {
		Builder builder = new Builder().mbean("A");

		assertThat(builder.objectName(), is("java.lang:type=String"));
		assertThat((String) builder.mbean(), is("A"));
	}

	@Test(expected = NullPointerException.class)
	public void createObjectNameFromNullMBeanThrowsNullPointerException() {
		new Builder(null);
	}

	@Test(expected = NullPointerException.class)
	public void createObjectNameFromNullMBeanUsingBuilderSetterThrowsNullPointerException() {
		new Builder().mbean(null);
	}

	@Test(expected = NullPointerException.class)
	public void createObjectNameFromMBeanAndNullPackageThrowsNullPointerException() {
		new Builder("A").packageName(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createObjectNameFromMBeanAndEmptyPackageThrowsIllegalArgumentException() {
		new Builder("A").packageName("");
	}

	@Test(expected = NullPointerException.class)
	public void createObjectNameFromMBeanAndNullTypeThrowsNullPointerException() {
		new Builder("A").type(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createObjectNameFromMBeanAndEmptyTypeThrowsIllegalArgumentException() {
		new Builder("A").type("");
	}

	@Test(expected = NullPointerException.class)
	public void createObjectNameFromMBeanAndNullPropertyKeyThrowsNullPointerException() {
		new Builder("A").property(null, "bar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createObjectNameFromMBeanAndEmptyPropertyKeyThrowsIllegalArgumentException() {
		new Builder("A").property("", "bar");
	}

	@Test(expected = NullPointerException.class)
	public void createObjectNameFromMBeanAndNullPropertyValueThrowsNullPointerException() {
		new Builder("A").property("foo", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createObjectNameFromMBeanAndPropertyValueThrowsIllegalArgumentException() {
		new Builder("A").property("foo", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void createObjectNameFromMBeanAndDisabledTypeWithoutAnyOtherPropertyThrowsIllegalArgumentException() {
		new Builder("A").disableType();
	}
}
