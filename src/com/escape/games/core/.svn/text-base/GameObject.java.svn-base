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

/**
 * Abstract base of all game objects.
 * @author escape-llc
 *
 */
public abstract class GameObject {
	protected static final boolean DEFAULT_LC = true;
	/**
	 * Component name.
	 * Must be unique among all locatable components.
	 */
	public final String name;
	/**
	 * true: install in Locator; false: not locatable.
	 */
	public final boolean locatable;
	/**
	 * Ctor.
	 * @param name Component name.
	 * @param lc true: locatable; false: not locatable.
	 */
	protected GameObject(String name, boolean lc) {
		this.name = name;
		this.locatable = lc;
	}
}
