/*
 * Copyright 2013 eScape Technology LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.escape.games.core;

import java.util.HashMap;

import com.escape.games.api.Properties;

/**
 * Base implementation for game object with properties.
 * @author escape-llc
 *
 */
public abstract class GameObjectWithProperties extends GameObject implements Properties {
	protected final HashMap<Integer, Object> props;
	protected GameObjectWithProperties(String name, boolean lc) {
		super(name, lc);
		props = new HashMap<Integer, Object>();
	}
	/**
	 * Override this to be notified when properties are set or removed.
	 * @param propertyId Property key that was set or removed.
	 */
	protected void notifyPropertyChanged(int propertyId) { }
	/**
	 * Remove the property, and return its value.
	 * @param propertyId Property key.
	 * @return value or NULL.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeProperty(int propertyId) {
		final Integer key = Integer.valueOf(propertyId);
		final T rv = (T)props.remove(key);
		notifyPropertyChanged(propertyId);
		return rv;
	}
	public boolean query(int propertyId) {
		final Integer key = Integer.valueOf(propertyId);
		return props.containsKey(key);
	}
	public Object get(int propertyId) {
		final Integer key = Integer.valueOf(propertyId);
		return props.get(key);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAs(int propertyId) {
		final Integer key = Integer.valueOf(propertyId);
		return (T)props.get(key);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAs(int propertyId, T defval) {
		final Integer key = Integer.valueOf(propertyId);
		return props.containsKey(key) ? (T)props.get(key) : defval;
	}
	public <T> void set(int propertyId, T value) {
		final Integer key = Integer.valueOf(propertyId);
		// avoid a put if possible
		if(props.get(key) != value) {
			props.put(key, value);
		}
		notifyPropertyChanged(propertyId);
	}
}
