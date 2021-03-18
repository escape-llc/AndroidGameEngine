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
package com.escape.games.api;

/**
 * Ability to get/set named values.
 * @author escape-llc
 *
 */
public interface Properties {
	/**
	 * Return whether a property key has a value associated.
	 * @param propertyId Property key.
	 * @return true: exists; false: not present.
	 */
	boolean query(int propertyId);
	/**
	 * Property get.
	 * @param propertyId Property key.
	 * @return value or NULL.
	 */
	Object get(int propertyId);
	/**
	 * Property get as typed value.
	 * @param propertyId Property key.
	 * @return value or NULL.
	 */
	<T> T getAs(int propertyId);
	/**
	 * Property get as typed value with default.
	 * If the property value is set to NULL that is returned, not default!
	 * @param propertyId Property key.
	 * @param defval Default value if not found.
	 * @return value or default value.
	 */
	<T> T getAs(int propertyId, T defval);
	/**
	 * Property set as typed value.
	 * In order to trigger property change notification, you must call set() with the same value returned by getXXX().
	 * @param propertyId Property key.
	 * @param value New value.
	 */
	<T> void set(int propertyId, T value);
}
