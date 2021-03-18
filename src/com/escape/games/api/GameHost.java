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
 * Represents services outside the GL/AGE environment.
 * @author escape-llc
 *
 */
public interface GameHost {
	/**
	 * Display static text.
	 * This is invoke-safe.
	 * @param resid Resource ID of text location.
	 * @param text The text to display.
	 */
	void setText(int resid, String text);
	/**
	 * Display dynamically-located text.
	 * This is invoke-safe.
	 * @param x Screen location X.
	 * @param y Screen location y.
	 * @param layout Resource ID of text layout.
	 * @param color Text color.
	 * @param text The text to display.
	 */
	void fadeInOutText(int x, int y, int layout, int color, String text);
	/**
	 * Instruct game host to force quit application.
	 */
	void forceQuit();
	/**
	 * Instruct game host to enable the "Start Game" UI.
	 * This include start and play/pause commands.
	 * @param enabled true: enable; false: disable.
	 */
	void enableStartGame(boolean enabled);
	/**
	 * Instruct game host to enable the "Loading" UI.
	 * @param enabled true: enable; false: disable.
	 */
	void enableLoading(boolean enabled);
	/**
	 * Instruct game host that the game has ended.
	 * This notification is in addition to any calls to enableXXX() APIs.
	 */
	void gameIsOver();
}
