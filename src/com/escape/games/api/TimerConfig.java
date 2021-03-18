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
 * Configuration for a timer callback registration.
 * @author escape-llc
 *
 */
public abstract class TimerConfig {
	/**
	 * Timer duration in Milliseconds.
	 */
	public long durationMS;
	/**
	 * true: callback every tick; false: callback at end only.
	 */
	public boolean continuous;
	/**
	 * true: restart timer after expiration; false: remove timer after expiration.
	 */
	public boolean autoRepeat;
	/**
	 * Get a token to call that cancels this timer.
	 * Release the token upon expiration/cancellation of your timer, in the <b>TimerCallback</b>.
	 * @return NULL: token not available, e.g. timer already expired; !NULL: token.
	 */
	public abstract TimerCancel obtainToken();
}
