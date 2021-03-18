/*
 * Copyright 2013-5 eScape Technology LLC.
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
package com.escape.games.message;

import android.util.Log;

import com.escape.games.api.LoadedCallback;
import com.escape.games.api.Locator;
import com.escape.games.api.Pipelines;
import com.escape.games.core.GameObject;
import com.escape.games.core.TaskChannel;
import com.escape.games.core.TaskMessage;
import com.escape.games.core.TraceSwitches;

/**
 * Load a game object.
 * @author escape-llc
 *
 */
public class LoadGameObjects extends TaskMessage {
	public final GameObject[] ggo;
	public final TaskChannel replyTo;
	public final LoadedCallback cb;
	public final String[] bindTargets;
	public Exception[] error;
	/**
	 * Ctor.
	 * Empty bind target list.
	 * @param go Target GO list to load.
	 * @param replyTo Reply target when complete.
	 * @param cb Callback to invoke at target.
	 */
	public LoadGameObjects(GameObject[] go, TaskChannel replyTo, LoadedCallback cb) {
		this(go, replyTo, cb, null);
	}
	/**
	 * Ctor.
	 * @param go Target GO list to load.
	 * @param replyTo Reply target when complete.
	 * @param cb Callback to invoke at target.
	 * @param bt !NULL: list of bind targets; NULL: no bind targets.  Takes ownership of list.
	 */
	public LoadGameObjects(GameObject[] go, TaskChannel replyTo, LoadedCallback cb, String[] bt) {
		super(Constants.Message.LOAD_OBJECTS);
		this.ggo = go;
		this.replyTo = replyTo;
		this.cb = cb;
		this.bindTargets = bt;
		this.error = new Exception[ggo.length];
	}
	/**
	 * Carry out the callback.
	 * Carry out the binding-name-list if supplied.
	 * @param lc !NULL: locator.
	 */
	public void callback(Locator lc, Pipelines pps) {
		if(lc == null) throw new IllegalArgumentException("lc");
		for (int kx = 0; kx < ggo.length; kx++) {
			final GameObject xgo = ggo[kx];
			if(xgo == null) continue;
			final Exception ex = error[kx];
			if (ex != null) {
				Log.e("LGOs", new StringBuilder("Loading.batch ").append(xgo.name).append(": ").append(ex.getMessage()).toString());
			} else {
				if (TraceSwitches.Message.LOAD_OBJECT) {
					Log.d("LGOs", new StringBuilder("Loading.batch ").append(xgo.name).toString());
				}
			}
			cb.loaded(xgo, ex, lc, pps);
			if (bindTargets != null && bindTargets.length > 0) {
				for (int ix = 0; ix < bindTargets.length; ix++) {
					final GameObject go = lc.locate(bindTargets[ix]);
					if (go != null && go instanceof LoadedCallback) {
						((LoadedCallback) go).loaded(xgo, ex, lc, pps);
					} else {
						if (TraceSwitches.Message.LOAD_OBJECT) {
							Log.w("LGOs", new StringBuilder(xgo.name).append(": cannot locate BNL for callback: ").append(bindTargets[ix]).toString());
						}
					}
				}
			}
		}
	}
}
