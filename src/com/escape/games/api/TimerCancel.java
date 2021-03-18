/*
 * Copyright 2014 eScape Technology LLC.
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
 * Represents token for cancelling a timer callback.
 * Release the token upon expiration/cancellation of your timer, in the <b>TimerCallback</b>.
 * @author escape-llc
 *
 */
public interface TimerCancel {
	/**
	 * Signal to cancel the timer.
	 * If accepted, there will be one last callback on next TBT after seeing this call.
	 * This is in a race condition with actual timer expiration.
	 * @return true: cancel accepted; false: not accepted, e.g. timer already expired.
	 */
	boolean cancel();
	/**
	 * Return whether cancel() was called.
	 * Meant to call this within the timer callback to check if you called cancel() on your token.
	 * @return true: cancel() was called; false: not called.
	 */
	boolean cancelled();
}
