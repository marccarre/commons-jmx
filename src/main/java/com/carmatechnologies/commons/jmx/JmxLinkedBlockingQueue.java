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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.carmatechnologies.commons.jmx.MBeans.Builder;
import com.carmatechnologies.commons.jmx.mbeans.IJmxCollection;

public class JmxLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> implements Iterable<E>, Collection<E>, BlockingQueue<E>, Queue<E>, IJmxCollection {
	private static final long serialVersionUID = 2961777229338139054L;

	private final LinkedBlockingQueue<E> queue;
	private final ObjectName objectName;

	public JmxLinkedBlockingQueue(final LinkedBlockingQueue<E> queue) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		checkNotNull(queue, "Linked blocking queue must NOT be null in order to be JMX-decorated.");
		this.queue = queue;
		final Builder builder = new Builder(this);
		this.objectName = new ObjectName(builder.objectName());
		MBeans.register(builder);
	}

	public JmxLinkedBlockingQueue(LinkedBlockingQueue<E> queue, Builder builder) throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		checkNotNull(queue, "Linked blocking queue must NOT be null in order to be JMX-decorated.");
		checkNotNull(builder, "MBean builder must NOT be null.");
		this.queue = queue;
		this.objectName = new ObjectName(builder.mbean(this).objectName());
		MBeans.register(builder);
	}

	@Override
	public E element() {
		return queue.element();
	}

	@Override
	public E peek() {
		return queue.peek();
	}

	@Override
	public E poll() {
		return queue.poll();
	}

	@Override
	public E remove() {
		return queue.remove();
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		return queue.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		return queue.drainTo(c, maxElements);
	}

	@Override
	public boolean offer(E e) {
		return queue.offer(e);
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return queue.offer(e, timeout, unit);
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		return queue.poll(timeout, unit);
	}

	@Override
	public void put(E e) throws InterruptedException {
		queue.put(e);
	}

	@Override
	public int remainingCapacity() {
		return queue.remainingCapacity();
	}

	@Override
	public E take() throws InterruptedException {
		return queue.take();
	}

	@Override
	public boolean add(E e) {
		return queue.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return queue.addAll(c);
	}

	@Override
	public void clear() {
		queue.clear();
	}

	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean remove(Object o) {
		return queue.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return queue.retainAll(c);
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	@Override
	public ObjectName objectName() {
		return objectName;
	}

	@Override
	public int getSize() {
		return queue.size();
	}
}
