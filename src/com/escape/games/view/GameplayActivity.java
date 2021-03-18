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
package com.escape.games.view;

import com.escape.games.message.Constants;
import com.escape.games.task.GameCycle;

import android.app.Activity;
import android.util.Log;

/**
 * Basic gameplay activity.
 * Create subclass and override abstract methods.
 * @deprecated Use GameplayFragment with the Fragment Manager.
 * @author escape-llc
 *
 */
public abstract class GameplayActivity extends Activity {
	protected GameCycle game;
	protected GLSurfaceViewImpl view;
	/**
	 * Create the game instance.
	 * @param glsv surface view.
	 * @return new instance.
	 */
	protected abstract GameCycle createGame(GLSurfaceViewImpl glsv);
    /**
     * Must call this after Activity.setContentView().
     * Calls createGame() to get the game instance.
     * @param viewid GL Surface View ID.
     */
    protected void setupContentView(int viewid) {
        view = (GLSurfaceViewImpl) findViewById(viewid);
        if(view != null) {
			view.setKeepScreenOn(true);
			view.setFocusableInTouchMode(true);
			view.setFocusable(true);
	        game = createGame(view);
			game.start();
        }
    }
	final Runnable start = new Runnable() {
		public void run() {
			if(game != null) {
				game.startGame();
			}
		}
	};
	final Runnable toggle = new Runnable() {
		public void run() {
			if(game != null) {
				game.toggleGame();
			}
		}
	};
	protected void postStartGame(int delay) {
		view.postDelayed(start, delay);
	}
	protected void postToggleGame(int delay) {
		view.postDelayed(toggle, delay);
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(game != null) {
			game.pauseGame();
		}
		if(view != null) {
			view.onPause();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(view != null) {
			view.onResume();
		}
	}
	@Override
	protected void onStop() {
		Log.d("GP", "onStop");
		try {
			if (game != null) {
				game.send(Constants.Message.MSG_SHUTDOWN);
				game.join(2000);
			}
		} catch (Exception e) {
		}
		finally {
			game = null;
			super.onStop();
		}
	}
}