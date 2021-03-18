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
package com.escape.games.api;

import com.escape.games.core.GameObject;

/**
 * Access to Install, Uninstall, Event pipelines.
 * @author escape-llc
 *
 */
public interface Pipelines {
	/**
	 * Trigger install pipeline for given component.
	 * @param go Object to install.
	 * @throws Exception
	 */
	void install(GameObject go) throws Exception;
	/**
	 * Trigger install pipeline for given component batch.
	 * @param go array of Objects to install.  May contain NULL entries; these are skipped.
	 * @throws Exception
	 */
	void install(GameObject[] go) throws Exception;
	/**
	 * Trigger install pipeline for given component.
	 * Invoke callback with binding-name-list.
	 * @param go Object to install.
	 * @param bnl Binding name list.
	 * @throws Exception
	 */
	void install(GameObject go, String[] bnl) throws Exception;
	/**
	 * Trigger install pipeline for given component batch.
	 * Invoke callback with binding-name-list.
	 * @param bnl Binding name list.
	 * @param go array of Objects to install.  May contain NULL entries; these are skipped.
	 * @throws Exception
	 */
	void install(String[] bnl, GameObject... go) throws Exception;
	/**
	 * Trigger install pipeline with callback.
	 * @param go Object to install.
	 * @param cb Callback to invoke.
	 * @throws Exception
	 */
	void install(GameObject go, LoadedCallback cb) throws Exception;
	/**
	 * Trigger install pipeline batch with callback.
	 * @param cb Callback to invoke.
	 * @param go array of Objects to install.  May contain NULL entries; these are skipped.
	 * @throws Exception
	 */
	void install(LoadedCallback cb, GameObject... go) throws Exception;
	/**
	 * Trigger uninstall pipeline for given component.
	 * @param name Name of GO to uninstall.
	 * @throws Exception
	 */
	void uninstall(String name) throws Exception;
	/**
	 * Trigger uninstall pipeline for given component.
	 * Invoke callback with binding-name-list.
	 * @param name Name of GO to uninstall.
	 * @param bnl Binding name list.
	 * @throws Exception
	 */
	void uninstall(String name, String[] bnl) throws Exception;
	/**
	 * Trigger event pipeline for given (event) component.
	 * @param go Event object.
	 * @param bnl Binding name list.
	 * @throws Exception
	 */
	void event(GameObject go, String[] bnl) throws Exception;
	/**
	 * Trigger event pipeline for given (event) component batch.
	 * @param bnl Binding name list.
	 * @param go Event object list.  May contain NULL entries; these are skipped.
	 * @throws Exception
	 */
	void event(String[] bnl, GameObject... go) throws Exception;
	/**
	 * Generate a unique name.
	 * @param base name.
	 * @return New name.
	 */
	String freshName(String base);
}
