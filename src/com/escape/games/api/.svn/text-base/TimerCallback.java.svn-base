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
 * Ability to process timer callbacks.
 * @author escape-llc
 *
 */
public interface TimerCallback {
	/**
	 * Setup the timer callback configuration.
	 * Use TimerConfig.obtainToken() to get a cancel token for this timer.
	 * @param tc Target configuration to setup.
	 * @param tbt Time Base Tick in MS.
	 */
	void setConfig(TimerConfig tc, int tbt);
	/**
	 * Whether to automatically register during install pipeline.
	 * @return true: auto-register; false: manual register.
	 */
	boolean getRegisterOnInstall();
	/**
	 * Execute the timer update logic.
	 * Release any <b>TimerCancel</b> token upon expiration/cancellation of timer.
	 * @param delta Number of MS since last callback
	 * @param elapsed Number of MS elapsed so far
	 * @param last true: this is the last call for the interval
	 * @param lc Source of components
	 * @param in Component services.
	 */
	void execute(long delta, long elapsed, boolean last, Locator lc, Pipelines in);
}
