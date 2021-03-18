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
package com.escape.games.task;

import android.util.Log;

import com.escape.games.api.Configure;
import com.escape.games.api.Lifecycle;
import com.escape.games.core.GameTask;
import com.escape.games.core.TaskChannel;
import com.escape.games.message.DrawFrame;

/**
 * Generate frame draw messages at a regular interval.
 * @author escape-llc
 *
 */
public final class Framerate extends GameTask implements Lifecycle {
	final DrawFrame df = new DrawFrame();
	final int framesPerSecond;
	Thread thx;
	/**
	 * Ctor.
	 * @param supervisor Target for notifications.
	 * @param fps Frames per Second.  Actual interval is truncated to nearest MS.
	 */
	public Framerate(TaskChannel supervisor, int fps) {
		super("framerate", supervisor);
		framesPerSecond = fps;
	}
	public void run() {
		try {
			final long ms = 1000/framesPerSecond;
			while(!isInterrupted()) {
				try {
					Thread.sleep(ms);
					supervisor.send(df);
				}
				catch(InterruptedException ie) {
					// expected; we got interrupted
					break;
				}
				catch(IllegalStateException ise) {
					// expected; send() target is off
					break;
				}
				catch(Exception ex) {
					// other
					Log.e("framerate", "run", ex);
				}
			}
		}
		catch(Exception ex) {
		}
		finally {
			
		}
	}
	public void lfStart(Configure cfg) throws Exception {
		// TODO Auto-generated method stub
	}
	public void lfStop() {
		// TODO Auto-generated method stub
	}
}
