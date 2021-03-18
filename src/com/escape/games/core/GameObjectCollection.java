/*
 * Copyright 2013-4 eScape Technology LLC.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.util.Log;

import com.escape.games.api.LoadedCallback;
import com.escape.games.api.Locator;
import com.escape.games.api.Pipelines;
import com.escape.games.api.UnloadedCallback;

/**
 * Container for component collection.
 * Install pipeline: add incoming component to collection.
 * Uninstall pipeline: remove incoming component from collection.
 * The implementation uses both a list for sequential access, and a map for keyed access/membership-test.
 * @author escape-llc
 *
 * @param <GO> Target class for collection.
 */
public class GameObjectCollection<GO extends GameObject> extends GameObject implements LoadedCallback, UnloadedCallback {
	/**
	 * Ability to traverse the collection.
	 * @author escape-llc
	 *
	 * @param <GO> Target class for collection
	 */
	public static interface Visitor<GO extends GameObject> {
		/**
		 * Visit the given item.
		 * @param obj Source item.
		 * @return true: continue; false: break.
		 */
		boolean visit(GO obj);
	}
	// map targets (for fast contains)
	final HashSet<GO> map;
	// list targets (for sequential/random access)
	final ArrayList<GO> list;
	/**
	 * Ctor.
	 * @param name Instance name.
	 * @param lc true: locatable.
	 * @param cap initial capacity.
	 */
	public GameObjectCollection(String name, boolean lc, int cap) {
		super(name, lc);
		map = new HashSet<GO>(cap);
		list = new ArrayList<GO>(cap);
	}
	protected void add(GO obj) {
		if(!map.contains(obj)) {
			list.add(obj);
			map.add(obj);
		}
	}
	public void remove(GO obj) {
		list.remove(obj);
		map.remove(obj);
	}
	/**
	 * Clear the contents of the collection.
	 */
	public void clear() {
		list.clear();
		map.clear();
	}
	/**
	 * Traverse the collection.
	 * @param vx Visitor.
	 */
	public void traverse(Visitor<GO> vx) {
		for(int ix = 0; ix < list.size(); ix++) {
			if(!vx.visit(list.get(ix))) break;
		}
	}
	/**
	 * Return number of elements in collection.
	 * @return number of elements.
	 */
	public int size() { return list.size(); }
	/**
	 * Access item by positional index.
	 * @param idx 0-relative index.
	 * @return Item at indicated position.
	 */
	public GO item(int idx) { return list.get(idx); }
	/**
	 * Access item name by positional index.
	 * @param idx 0-relative index.
	 * @return Name of item at indicated position.
	 */
	public String itemName(int idx) { return list.get(idx).name; }
	/**
	 * Return an iterator for sequential access.
	 * Garbage warning! Use traverse() instead if possible.
	 * @return New iterator.
	 */
	public Iterator<GO> iterator() { return list.iterator(); }
	/**
	 * Return whether the collection contains this component.
	 * @param obj Component to check membership.
	 * @return true: in colleciton; false: not in collection.
	 */
	public boolean contains(GO obj) { return map.contains(obj); }
	@SuppressWarnings("unchecked")
	public void loaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		if(TraceSwitches.GameObjects.COLLECTIONS) {
			Log.d(name, "load.Accepting " + go.name);
		}
		if(ex == null) {
			if(go != this)
				add((GO)go);
		}
	}
	@SuppressWarnings("unchecked")
	public void unloaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		if(TraceSwitches.GameObjects.COLLECTIONS) {
			Log.d(name, "unload.Accepting " + go.name);
		}
		if(go != this)
			remove((GO)go);
	}
}
