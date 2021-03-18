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
package com.escape.games.api;

import com.escape.games.core.GameObject;

/**
 * Install pipeline continuation.
 * @author escape-llc
 *
 */
public interface LoadedCallback {
	/**
	 * Loading was completed.
	 * @param go Target object.
	 * @param ex NULL: success; !NULL: load failed do not use GO.
	 * @param lc Locator for additional components.
	 * @param pps Access to pipelines.
	 */
	void loaded(GameObject go, Exception ex, Locator lc, Pipelines pps);
}