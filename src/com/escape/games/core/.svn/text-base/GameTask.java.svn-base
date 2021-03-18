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
package com.escape.games.core;

/**
 * Core implementation for game task apartment thread.
 * @author escape-llc
 *
 */
public abstract class GameTask extends Thread {
	protected final TaskChannel supervisor;
	protected final String name;
	/**
	 * Ctor.
	 * @param name Task name and Thread name.
	 * @param supervisor Target for notifications.
	 */
	protected GameTask(String name, TaskChannel supervisor) {
		this.name = name;
		this.supervisor = supervisor;
		setName(name);
		setDaemon(true);
	}
}
