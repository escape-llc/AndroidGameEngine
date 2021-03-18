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
package com.escape.games.view;

import com.escape.games.core.TaskChannel;
import com.escape.games.core.TaskMessage;
import com.escape.games.message.Constants;
import com.escape.games.task.GameCycle;

import android.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * Basic gameplay fragment.
 * Create subclass and override abstract methods.
 * @author escape-llc
 *
 */
public abstract class GameplayFragment extends Fragment implements TaskChannel {
	protected GameCycle game;
	protected GLSurfaceViewImpl view;
	protected volatile boolean startGameRequested;
	protected GameplayFragment() {
	}
	/**
	 * Create the game instance and connect it to this as supervisor.
	 * @param glsv surface view.
	 * @param tc Supervisor task channel; must pass this as the supervisor parameter to your GameCycle ctor!
	 * @return new instance.
	 */
	protected abstract GameCycle createGame(GLSurfaceViewImpl glsv, TaskChannel tc);
    /**
     * Must call this within Fragment.createContentView().
     * Sets these view properties programmatically: KeepScreenOn:true, FocusableInTouchMode:true, Focusable:true.
     * If the GL surface view is found, calls createGame() to get the game instance and start game task.
     * @param root Root view of fragment.
     * @param viewid GL Surface View ID.
     */
    protected void setupContentView(View root, int viewid) {
        view = (GLSurfaceViewImpl) root.findViewById(viewid);
        if(view != null) {
			view.setKeepScreenOn(true);
			view.setFocusableInTouchMode(true);
			view.setFocusable(true);
	        game = createGame(view, this);
	        startGameRequested = false;
			game.start();
        }
    }
    /**
     * Whether the GameCycle instance is restartable for each game, or whether a new instance must be created.
     * @return false: not restartable.
     */
	protected boolean isInstanceRestartable() { return false; }
	
	final Runnable start = new Runnable() {
		public void run() {
			if(game != null) {
				if(game.isStarted() && game.isOver()) {
					if(isInstanceRestartable()) {
						// restartable: send another game start
						Log.d("GF", "start: restarting existing completed game");
						if(view != null) view.postDelayed(startgame, 500);
					}
					else {
						// after first time: shutdown and start a new instance
						try {
							Log.d("GF", "start: terminating completed game");
							game.send(Constants.Message.MSG_SHUTDOWN);
							game.join(200);
						} catch (Exception e) {
						}
						// make a new game instance
						Log.d("GF", "start: creating new game");
						game = createGame(view, GameplayFragment.this);
						startGameRequested = true;
						game.start();
					}
				}
				else {
					// first time: can start directly
					if(view != null) view.postDelayed(startgame, 500);
				}
			}
		}
	};
	final Runnable startgame = new Runnable() {
		public void run() {
			game.startGame();
		}
	};
	final Runnable toggle = new Runnable() {
		public void run() {
			if(isGameRunning() && game.isStarted()) {
				game.toggleGame();
			}
		}
	};
	final Runnable pause = new Runnable() {
		public void run() {
			if(isGamePlaying() && !game.isPaused()) {
				game.pauseGame();
			}
		}
	};
	/**
	 * Process supervisor callbacks from the GameCycle.
	 */
	public void send(TaskMessage tm) throws Exception {
		if(tm.cmdcode == Constants.Message.GAME_START) {
			// ok to send the start game command
			if(startGameRequested) {
				if(view != null) view.postDelayed(startgame, 500);
			}
		}
	}
	/**
	 * Invoke-safe.
	 * Post a runnable to send start-game.
	 * @param delay Post delay in MS.
	 */
	protected void postStartGame(int delay) {
		if(view != null) view.postDelayed(start, delay);
	}
	/**
	 * Invoke-safe.
	 * Post a runnable to send toggle-game.
	 * @param delay Post delay in MS.
	 */
	protected void postToggleGame(int delay) {
		if(view != null) view.postDelayed(toggle, delay);
	}
	/**
	 * Invoke-safe.
	 * Post a runnable to send pause-game.
	 * @param delay Post delay in MS.
	 */
	protected void postPauseGame(int delay) {
		if(view != null) view.postDelayed(pause, delay);
	}
	/**
	 * Is the game engine thread created and alive?
	 * @return true: running; false: not running.
	 */
	public boolean isGameRunning() { return game != null && game.isAlive(); }
	/**
	 * Is the game engine thread created, alive, and playing the game (started and not over)?
	 * @return true: playing; false: not playing.
	 */
	public boolean isGamePlaying() { return game != null && game.isAlive() && game.isStarted() && !game.isOver(); }
	/**
	 * Pause the game and the GL surface view.
	 */
	@Override
	public void onPause() {
		super.onPause();
		if(isGameRunning() && !game.isPaused()) {
			game.pauseGame();
		}
		if(view != null) {
			view.onPause();
		}
	}
	/**
	 * Resume the GL surface view only.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(view != null) {
			view.onResume();
		}
	}
	/**
	 * Shut down the game task.
	 */
	@Override
	public void onStop() {
		Log.d("GPF", "onStop");
		try {
			if (game != null) {
				game.send(Constants.Message.MSG_SHUTDOWN);
				game.join(2000);
			}
		} catch (Exception e) {
		}
		finally {
			super.onStop();
		}
	}
}
