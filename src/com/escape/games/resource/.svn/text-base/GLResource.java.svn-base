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
package com.escape.games.resource;

import android.content.Context;

/**
 * Marker interface for GL resources.
 * @author escape-llc
 *
 */
public interface GLResource {
	/**
	 * (Re)load resources required before entering GL thread.
	 * @param ctx Source of resources.
	 * @return
	 */
	Object preload(Context ctx);
	/**
	 * (Re)load the GL resources.
	 * Must be on GL thread.
	 * @param preload return value from preload().
	 */
	void load(Object preload);
	/**
	 * Unload GL resources, if any.
	 * Delete DL resource, and clear out for subsequent call to preload/load().
	 * Must be on GL thread.
	 * @param ctx Source of resources.
	 */
	void unload(Context ctx);
	/**
	 * Release all resources acquired.
	 * Do not call GL to delete anything; GL context is gone!
	 * Called from any thread.
	 * Prepare for a new call to load().
	 */
	void release();
}
