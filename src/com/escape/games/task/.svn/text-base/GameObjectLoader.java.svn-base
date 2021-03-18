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

import com.escape.games.api.RequireResourceLoader;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.core.GameObject;
import com.escape.games.core.GameTaskWithChannel;
import com.escape.games.core.TaskChannel;
import com.escape.games.core.TaskMessage;
import com.escape.games.core.TraceSwitches;
import com.escape.games.message.Constants;
import com.escape.games.message.LoadGameObject;
import com.escape.games.message.LoadGameObjects;

/**
 * Dedicated GO loader task.
 * @author escape-llc
 *
 */
public class GameObjectLoader extends GameTaskWithChannel {
	final ResourceLoader rl;
	final Services svc;
	/**
	 * Ctor.
	 * @param supervisor Target for notifications.
	 * @param rl Resource loader to pass to GOs.
	 * @param svc Services to pass to GOs.
	 * @param qcap Queue capacity.
	 * @param mcap Message capacity.
	 */
	public GameObjectLoader(TaskChannel supervisor, ResourceLoader rl, Services svc, int qcap, int mcap) {
		super("Loader", supervisor, qcap, mcap);
		this.rl = rl;
		this.svc = svc;
	}
	/**
	 * Ctor.
	 * @param supervisor Target for notifications.
	 * @param rl Resource loader to pass to GOs.
	 * @param svc Services to pass to GOs.
	 */
	public GameObjectLoader(TaskChannel supervisor, ResourceLoader rl, Services svc) {
		this(supervisor, rl, svc, QUEUE_CAP, MESSAGE_CAP);
	}

	@Override
	protected void startup() throws Exception {
	}

	@Override
	protected void shutdown() {
	}

	@Override
	protected void process(TaskMessage msg) {
		if(msg.cmdcode == Constants.Message.LOAD_OBJECT) {
			final LoadGameObject lgo = (LoadGameObject)msg;
			try {
				if(lgo.ggo instanceof RequireResourceLoader) {
					if(TraceSwitches.Loader.LOADED){ 
						Log.d(name, new StringBuilder("Loading ").append(lgo.ggo.name).toString());
					}
					((RequireResourceLoader)lgo.ggo).load(rl, svc);
				}
			}
			catch(Exception ex) {
				Log.e(name, "load", ex);
				lgo.error = ex;
			}
			finally {
				try {
					lgo.replyTo.send(msg);
				} catch (Exception e) {
					Log.e(name, "Loading.replyTo " + lgo.ggo.name, e);
				}
			}
		}
		else if(msg.cmdcode == Constants.Message.LOAD_OBJECTS) {
			final LoadGameObjects lgo = (LoadGameObjects)msg;
			for(int kx = 0; kx < lgo.ggo.length; kx++) {
				final GameObject go = lgo.ggo[kx];
				if(go == null) continue;
				try {
					if(go instanceof RequireResourceLoader) {
						if(TraceSwitches.Loader.LOADED){ 
							Log.d(name, new StringBuilder("Loading.Batch ").append(go.name).toString());
						}
						((RequireResourceLoader)go).load(rl, svc);
					}
				}
				catch(Exception ex) {
					Log.e(name, new StringBuilder("load.").append(go.name).toString(), ex);
					lgo.error[kx] = ex;
				}
			}
			try {
				lgo.replyTo.send(msg);
			} catch (Exception e) {
				Log.e(name, "Loading.replyTo.batch", e);
			}
		}
	}
}
