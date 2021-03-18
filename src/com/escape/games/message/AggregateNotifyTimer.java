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

import java.util.ArrayList;

import com.escape.games.api.Pipelines;
import com.escape.games.api.Locator;
import com.escape.games.core.TaskMessage;

/**
 * Timer update event consisting of a list of timer callbacks.
 * @author escape-llc
 *
 */
public class AggregateNotifyTimer extends TaskMessage {
	private ArrayList<NotifyTimer> rap;
	/**
	 * Ctor.
	 * @param rap list of timer callbacks to exeucte.  Takes ownership of this list.
	 */
	public AggregateNotifyTimer(ArrayList<NotifyTimer> rap) {
		super(Constants.Message.AGGREGATE_NOTIFY_TIMER);
		this.rap = rap;
	}
	/**
	 * Execute all of the aggregated timer callbacks.
	 * @param loc Source of components.
	 * @param in Component services.
	 */
	public void execute(Locator loc, Pipelines in) {
		for(int ix = 0; ix < rap.size(); ix++) {
			rap.get(ix).execute(loc, in);
		}
	}
	public void release() {
		this.rap.clear();
	}
	/**
	 * Take ownership of new list no copying.
	 * @param ant
	 */
	public void recycle(ArrayList<NotifyTimer> ant) {
		this.rap = ant;
	}
}
