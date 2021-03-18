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
package com.escape.games.message;

import com.escape.games.api.Pipelines;
import com.escape.games.api.Locator;
import com.escape.games.api.TimerCallback;
import com.escape.games.core.TaskMessage;

/**
 * Notify timer message.
 * Contains the information for a call to TimerCallback.
 * @author escape-llc
 *
 */
public class NotifyTimer extends TaskMessage {
	final boolean last;
	final long elapsed;
	final long delta;
	final TimerCallback rap;
	/**
	 * Ctor.
	 * @param rap Callback.
	 * @param delta MS since last callback.
	 * @param elapsed MS elapsed since start.
	 * @param last true: last call (expired); false: not last call.
	 */
	public NotifyTimer(TimerCallback rap, long delta, long elapsed, boolean last) {
		super(Constants.Message.NOTIFY_TIMER);
		this.rap = rap;
		this.delta = delta;
		this.elapsed = elapsed;
		this.last = last;
	}
	/**
	 * Execute timer callback.
	 * @param loc Source of components.
	 * @param in Component services.
	 */
	public void execute(Locator loc, Pipelines in) {
		rap.execute(delta, elapsed, last, loc, in);
	}
}
