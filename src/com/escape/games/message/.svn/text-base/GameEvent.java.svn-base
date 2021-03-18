/*
 * Copyright 2013-15 eScape Technology LLC.
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

import com.escape.games.api.EventCallback;
import com.escape.games.api.EventHooks;
import com.escape.games.api.Pipelines;
import com.escape.games.api.Locator;
import com.escape.games.core.GameObject;
import com.escape.games.core.TaskMessage;
import com.escape.games.core.TraceSwitches;

/**
 * Game Event.
 * @author escape-llc
 *
 */
public class GameEvent extends TaskMessage {
	public final GameObject rrl;
	public final String[] bindTargets;
	/**
	 * Ctor.
	 * @param rrl Target GO for event.
	 * @param bt !NULL: list of bind targets; NULL: no bind targets.  Takes ownership of list.
	 */
	public GameEvent(GameObject rrl, String[] bt) {
		super(Constants.Message.GAME_EVENT);
		this.rrl = rrl;
		this.bindTargets = bt;
	}
	/**
	 * Carry out the callback.
	 * Carry out the binding-name-list if supplied.
	 * @param lc !NULL: locator.
	 * @param in !NULL: installer.
	 * @param eh Event Hooks.
	 */
	public void callback(Locator lc, Pipelines in, EventHooks eh) {
		if(lc == null) throw new IllegalArgumentException("lc");
		if(in == null) throw new IllegalArgumentException("in");
		if(TraceSwitches.Game.EVENTS) {
			Log.d("GE", new StringBuilder("Event ").append(rrl.getClass().getName()).toString());
		}
		eh.eventHookPre(rrl);
		if(bindTargets != null && bindTargets.length > 0) {
			for(int ix = 0; ix < bindTargets.length; ix++) {
				final GameObject go = lc.locate(bindTargets[ix]);
				if(go != null && go instanceof EventCallback) {
					((EventCallback)go).event(rrl, lc, in);
				}
			}
		}
		eh.eventHookPost(rrl);
	}
}
