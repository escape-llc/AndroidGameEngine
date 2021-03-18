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

import com.escape.games.core.TaskMessage;

/**
 * System-wide constants.
 * @author escape-llc
 *
 */
public final class Constants {
	/**
	 * Message constants.
	 * Values [1..999] are reserved for the framework.
	 * @author escape-llc
	 *
	 */
	public static final class Message {
		public static final int SHUTDOWN = 1;
		public static final int STARTUP = 2;
		public static final int DRAW_FRAME = 3;
		public static final int GAME_START = 4;
		public static final int GAME_PAUSE = 5;
		public static final int GAME_RESUME = 6;
		public static final int GAME_TOGGLE = 7;
		public static final int GAME_OVER = 8;
		public static final int LOAD_OBJECT = 9;
		public static final int UNLOAD_OBJECT = 10;
		public static final int SURFACE_READY = 11;
		public static final int SURFACE_CHANGED = 12;
		public static final int TIME_BASE_TICK = 13;
		public static final int NOTIFY_TIMER = 14;
		public static final int AGGREGATE_NOTIFY_TIMER = 15;
		public static final int LOAD_CHECKPOINT = 16;
		public static final int SCENE_START = 17;
		public static final int GAME_EVENT = 18;
		public static final int SURFACE_ATTACH = 19;
		public static final int LOAD_OBJECTS = 20;
		public static final int GAME_EVENTS = 21;
		public static final int USER_DEFINED_START = 1000;
		public static final TaskMessage MSG_SHUTDOWN = new EmptyMessage(SHUTDOWN);
		public static final EmptyMessage MSG_START = new EmptyMessage(GAME_START);
		public static final EmptyMessage MSG_PAUSE = new EmptyMessage(GAME_PAUSE);
		public static final EmptyMessage MSG_RESUME = new EmptyMessage(GAME_RESUME);
		public static final EmptyMessage MSG_TOGGLE = new EmptyMessage(GAME_TOGGLE);
		/**
		 * Return whether the command code requires the update lock.
		 * @param cmd command code.
		 * @return true: requires lock; false: no lock.
		 */
		public static boolean requiresLock(int cmd) {
			return cmd == NOTIFY_TIMER || cmd == AGGREGATE_NOTIFY_TIMER || cmd == LOAD_OBJECT || cmd == LOAD_OBJECTS || cmd == UNLOAD_OBJECT || cmd == GAME_EVENT;
		}
	}
	/**
	 * Service constants.
	 * Values [1..999] are reserved for the framework.
	 * @author escape-llc
	 *
	 */
	public static final class Service {
		public static final int TIMER = 1;
		public static final int LOADER = 2;
		public static final int RESOURCES = 3;
		//public static final int LOCATOR = 4;
		public static final int HOST = 5;
		public static final int INSTALLER = 6;
		public static final int USER_DEFINED_START = 1000;
	}
	/**
	 * Property constants.
	 * Values [1..999] are reserved for the framework.
	 * @author escape-llc
	 *
	 */
	public static final class Property {
		public static final int COLOR = 1;
		public static final int TRANSFORM = 2;
		public static final int MATERIAL = 3;
		public static final int VELOCITY = 4;
		public static final int USER_DEFINED_START = 1000;
	}
}
