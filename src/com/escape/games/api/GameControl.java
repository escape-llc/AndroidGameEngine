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
 * Interface for controlling gameplay.
 * @author escape-llc
 *
 */
public interface GameControl {
	/**
	 * Asynchronous request to start game.
	 */
	void startGame();
	/**
	 * Asynchronous request to pause game.
	 * No effect if game is already paused.
	 */
	void pauseGame();
	/**
	 * Asynchronous request to resume game.
	 * No effect if game is already resumed.
	 */
	void resumeGame();
	/**
	 * Asynchronous request to toggle pause/resume game.
	 * Always has an effect.
	 */
	void toggleGame();
	/**
	 * Return whether the game is paused.
	 * @return true: paused; false: running.
	 */
	boolean isPaused();
	/**
	 * Return whether the game is over.
	 * @return true: game over; false: game not over.
	 */
	boolean isOver();
}
