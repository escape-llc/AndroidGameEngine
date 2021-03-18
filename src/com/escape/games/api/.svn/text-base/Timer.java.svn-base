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
 * Interface to the timer service.
 * Timer callbacks have these guarantees:
 * 1. first call is delta=0,elapsed=0,last=false. occurs on a tick boundary.
 * 2. last call will have last=true.  if autoRepeat=true, this is also the "first" of the next cycle.
 * @author escape-llc
 *
 */
public interface Timer {
	/**
	 * Register the timer.
	 * Calls TimerCallback.setConfig() to initialize settings.
	 * It will receive its first callback on the next TBT with elapsed==0.
	 * @param tc the timer to register.
	 */
	void register(TimerCallback tc);
	/**
	 * Remove the indicated timer.
	 * It does not receive any additional callbacks.
	 * @param tc the timer to unregister.
	 */
	void unregister(TimerCallback tc);
	/**
	 * Cancel the indicated timer.
	 * It will be removed on the next TBT, and receive a callback with last==true.
	 * @param tc the timer to cancel.
	 */
	void cancel(TimerCallback tc);
	/**
	 * Reset all timers to full value.
	 * Must execute while timers are paused.
	 */
	void reset();
}
