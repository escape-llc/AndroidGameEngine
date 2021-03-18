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
package com.escape.games.core;

/**
 * Diagnostic trace switches.
 * Diagnostics log at DEBUG level, unless noted.
 * @author escape-llc
 *
 */
public final class TraceSwitches {
	/**
	 * Diagnostics associated with the GameCycle and Task channels.
	 * @author escape-llc
	 *
	 */
	public static final class Game {
		/**
		 * true: GameTaskWithChannel event queue diagnostics.
		 */
		public static boolean TELEMETRY = false;
		/**
		 * Telemetry: threshold for initiating summary dump.
		 */
		public static int TELEMETRY_THD_1 = 4;
		/**
		 * Telemetry: threshold for initiating detailed dump.  Includes summary dump information.
		 */
		public static int TELEMETRY_THD_2 = 15;
		/**
		 * true: GameCycle event diagnostics.
		 */
		public static boolean EVENTS = false;
		/**
		 * true: GameCycle loaded diagnostics.
		 */
		public static boolean LOADED = false;
		/**
		 * true: GameCycle unloaded diagnostics.
		 */
		public static boolean UNLOADED = false;
		/**
		 * true: GameCycle surface_changed.
		 */
		public static boolean SURFACE_CHANGED = false;
		/**
		 * true: GameCycle surface_ready.
		 */
		public static boolean SURFACE_READY = false;
		/**
		 * true: Send failed to queue a message (ERROR).
		 */
		public static boolean SEND = false;
	}
	public static final class Loader {
		/**
		 * true: GameObjectLoader load_object.
		 */
		public static boolean LOADED = false;
		/**
		 * true: ContextResourceLoader GL resources.
		 */
		public static boolean GL_RESOURCES = false;
	}
	/**
	 * Diagnostics associated with message implementations, e.g. LoadGameObject.
	 * @author escape-llc
	 *
	 */
	public static final class Message {
		/**
		 * true: BNL lookup fail diagnostics (WARN).
		 */
		public static boolean LOAD_OBJECT = false;
		/**
		 * true: GO lookup fail, BNL lookup fail diagnostics (WARN).
		 */
		public static boolean UNLOAD_OBJECT = false;
	}
	public static final class GameObjects {
		/**
		 * true: collection load/unload callbacks.
		 */
		public static boolean COLLECTIONS = false;
	}
}
