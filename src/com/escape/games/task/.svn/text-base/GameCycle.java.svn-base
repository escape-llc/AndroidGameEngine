/*
 * Copyright 2013-14 eScape Technology LLC.
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Point;
import android.util.Log;

import com.escape.games.api.Configure;
import com.escape.games.api.EventHooks;
import com.escape.games.api.GameControl;
import com.escape.games.api.GameHost;
import com.escape.games.api.Pipelines;
import com.escape.games.api.LoadedCallback;
import com.escape.games.api.Locator;
import com.escape.games.api.RequireLocatable;
import com.escape.games.api.RequireTimer;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.SceneRender;
import com.escape.games.api.Services;
import com.escape.games.api.TimerCallback;
import com.escape.games.api.UnloadedCallback;
import com.escape.games.api.ViewHost;
import com.escape.games.core.GameObject;
import com.escape.games.core.GameTaskWithChannel;
import com.escape.games.core.TaskChannel;
import com.escape.games.core.TaskMessage;
import com.escape.games.core.TraceSwitches;
import com.escape.games.message.AggregateNotifyTimer;
import com.escape.games.message.EmptyMessage;
import com.escape.games.message.GameEvent;
import com.escape.games.message.GameEvents;
import com.escape.games.message.LoadGameObjects;
import com.escape.games.message.NotifyTimer;
import com.escape.games.message.Constants;
import com.escape.games.message.LoadGameObject;
import com.escape.games.message.SceneMessage;
import com.escape.games.message.SurfaceChanged;
import com.escape.games.message.UnloadGameObject;
import com.escape.games.service.RenderService;
import com.escape.games.service.RenderServiceImpl;
import com.escape.games.service.TimerService;

/**
 * Core implementation for games.
 * Make a subclass of this and implement the abstract methods.
 * @author escape-llc
 *
 */
public abstract class GameCycle extends GameTaskWithChannel implements GameControl, Configure, Services, Locator, Pipelines, LoadedCallback, UnloadedCallback, EventHooks {
	final Framerate fr;
	final GameObjectLoader gol;
	final ConcurrentHashMap<Integer, Object> services;
	final HashMap<String, GameObject> model;
	final WeakReference<ViewHost> view;
	protected final RenderServiceImpl rr;
	protected final TimerService timer;
	protected final ResourceLoader rl;
	protected final GameHost host;
	protected final Object updateLock;
	int freshNameCounter;
	boolean gotsurfaceready;
	long lastDrawFrame;
	long fpsMS;
	volatile boolean paused = true;
	volatile boolean gameOver = true;
	volatile boolean gameStarted;
	/**
	 * Ctor.
	 * @param name Component name.
	 * @param fps Framerate in FPS.
	 * @param tb Timer tick time base in MS.
	 * @param qcap Blocking queue capacity.
	 * @param mcap Message pull capacity.
	 * @param supervisor Postback interface; may be NULL.
	 * @param host Game Host interface.
	 * @param rl Resource Loader interface.
	 * @param glgv Game view host interface.
	 */
	public GameCycle(String name, int fps, int tb, int qcap, int mcap, TaskChannel supervisor, GameHost host, ResourceLoader rl, ViewHost glgv) {
		super(name, supervisor, qcap, mcap);
		if(rl == null)
			throw new IllegalArgumentException("rl");
		if(glgv == null)
			throw new IllegalArgumentException("glgv");
		if(host == null)
			throw new IllegalArgumentException("host");
		this.rl = rl;
		this.host = host;
		this.updateLock = new Object();
		rr = new RenderService(updateLock);
		fpsMS = 1000/fps;
		fr = new Framerate(this, fps);
		gol = new GameObjectLoader(this, rl, this, qcap, mcap);
		timer = new TimerService(this, tb);
		services = new ConcurrentHashMap<Integer, Object>();
		model = new HashMap<String, GameObject>();
		view = new WeakReference<ViewHost>(glgv);
		glgv.connect(this);
		addService(Constants.Service.TIMER, timer);
		addService(Constants.Service.RESOURCES, rl);
		addService(Constants.Service.HOST, host);
		addService(Constants.Service.INSTALLER, (Pipelines)this);
	}
	/**
	 * Ctor.
	 * Uses default BQ capacity.
	 * @param name Component name.
	 * @param fps Framerate in FPS.
	 * @param tb Timer tick time base in MS.
	 * @param supervisor Postback interface; may be NULL.
	 * @param host Game Host interface.
	 * @param rl Resource Loader interface.
	 * @param glgv Game view host interface.
	 */
	public GameCycle(String name, int fps, int tb, TaskChannel supervisor, GameHost host, ResourceLoader rl, ViewHost glgv) {
		this(name, fps, tb, QUEUE_CAP, MESSAGE_CAP, supervisor, host, rl, glgv);
	}
	/**
	 * Callback for surface is initialized and ready for loading.
	 * @param isreload false: first time; true: subsequent time.
	 */
	protected abstract void startLoading(boolean isreload);
	/**
	 * Callback for Game object loaded.
	 * Failed objects are sent here for notification only, they DO NOT install!
	 * This callback occurs BEFORE the GO is passed through the Install Pipeline.
	 * Must Not Throw!
	 * @param go Object that was loaded.
	 * @param ex !NULL: load error; NULL: no error.
	 */
	protected abstract void objectLoadedPre(GameObject go, Exception ex);
	/**
	 * Callback for Game object loaded.
	 * Failed objects are sent here for notification only, they DO NOT install!
	 * This callback occurs AFTER the GO is passed through the Install Pipeline.
	 * If anything in the pipeline throws, this method is not called.
	 * Must Not Throw!
	 * @param go Object that was loaded.
	 * @param ex !NULL: load error; NULL: no error.
	 */
	protected abstract void objectLoadedPost(GameObject go, Exception ex);
	/**
	 * Callback for Game object loaded.
	 * This callback occurs BEFORE the GO is passed through the Uninstall Pipeline.
	 * Must Not Throw!
	 * @param go Object that was unloaded.
	 * @param ex !NULL: load error; NULL: no error.
	 */
	protected abstract void objectUnloadedPre(GameObject go, Exception ex);
	/**
	 * Callback for Game object loaded.
	 * This callback occurs AFTER the GO is passed through the Uninstall Pipeline.
	 * If anything in the pipeline throws, this method is not called.
	 * Must Not Throw!
	 * @param go Object that was unloaded.
	 * @param ex !NULL: load error; NULL: no error.
	 */
	protected abstract void objectUnloadedPost(GameObject go, Exception ex);
	/**
	 * Callback for Game events.
	 * This callback occurs BEFORE the GO is passed through the Event Pipeline.
	 * Must Not Throw!
	 * @param go Object that is the event.
	 */
	protected abstract void objectEventPre(GameObject go);
	/**
	 * Callback for Game events.
	 * This callback occurs AFTER the GO is passed through the Event Pipeline.
	 * If anything in the pipeline throws, this method is not called.
	 * Must Not Throw!
	 * @param go Object that is the event.
	 */
	protected abstract void objectEventPost(GameObject go);
	/**
	 * Callback for Start game-control message received.
	 * Must Not Throw!
	 */
	protected abstract void gameStarting();
	/**
	 * Callback for Game Over message received.
	 * Must Not Throw!
	 */
	protected abstract void gameOver();
	/**
	 * Return whether this message index requires the update lock.
	 * @param cmd
	 * @return true: requires lock; false: no lock.
	 */
	protected boolean requiresLock(int cmd) { return Constants.Message.requiresLock(cmd); }
	/**
	 * Request a redraw if we are running late.
	 * @param tx1 start time
	 * @param tx2 end time
	 */
	@Override
	protected void loopEnd(long tx1, long tx2) {
		if(tx2 - lastDrawFrame >= fpsMS - 5) {
			// we are over and didn't see a DRAW_FRAME; tell it to draw
			final ViewHost glgv2 = view.get();
			if(glgv2 == null) return;
			//Log.w(name, new StringBuilder("redraw gsr=").append(gotsurfaceready).append(",tx2=").append(tx2).append(",ldf=").append(lastDrawFrame).toString());
			if(gotsurfaceready && rr.getScene() != null) {
				glgv2.postRenderRequest();
				lastDrawFrame = tx2;
			}
		}
	}
	/**
	 * Notify the supervisor channel load is complete and any pending GAME_START may be sent.
	 */
	protected void notifyLoadCompleted() {
		if(supervisor != null) {
			try {
				supervisor.send(new EmptyMessage(Constants.Message.GAME_START));
			} catch (Exception e) {
				Log.e(name, "notifyLoadCompleted", e);
			}
		}
	}
	/**
	 * Send the game-is-over event.
	 */
	protected void gameIsOver() {
		try {
			send(new EmptyMessage(Constants.Message.GAME_OVER));
		} catch (Exception e) {
			Log.e(name, "gameIsOver", e);
		}
	}
	/**
	 * Send the scene-start event.
	 * @param sc Scene to start.
	 */
	protected void startScene(SceneRender sc) {
		try {
			send(new SceneMessage(Constants.Message.SCENE_START, sc));
		} catch (Exception e) {
			Log.e(name, "startScene", e);
		}
	}
	/**
	 * Start the install pipeline for given object with reply-to target and load-complete callback.
	 * @param go install this.
	 * @param tc reply to this.
	 * @param cb continuation callback.
	 * @throws Exception
	 */
	protected void install(GameObject go, TaskChannel tc, LoadedCallback cb) throws Exception {
		gol.send(new LoadGameObject(go, tc, cb));
	}
	protected void install(GameObject[] gos, TaskChannel tc, LoadedCallback cb) throws Exception {
		gol.send(new LoadGameObjects(gos, tc, cb));
	}
	/**
	 * Overload with BNL.
	 * @param go install this.
	 * @param tc reply to this.
	 * @param cb continuation callback.
	 * @param bnl Binding name list.
	 * @throws Exception
	 */
	protected void install(GameObject go, TaskChannel tc, LoadedCallback cb, String[] bnl) throws Exception {
		gol.send(new LoadGameObject(go, tc, cb, bnl));
	}
	protected void install(GameObject[] gos, TaskChannel tc, LoadedCallback cb, String[] bnl) throws Exception {
		gol.send(new LoadGameObjects(gos, tc, cb, bnl));
	}
	public void install(GameObject go) throws Exception {
		install(go, this, this);
	}
	public void install(GameObject[] gos) throws Exception {
		install(gos, this, this);
	}
	public void install(GameObject go, String[] bnl) throws Exception {
		install(go, this, this, bnl);
	}
	public void install(String[] bnl, GameObject... gos) throws Exception {
		install(gos, this, this, bnl);
	}
	public void install(GameObject go, LoadedCallback cb) throws Exception {
		install(go, this, cb);
	}
	public void install(LoadedCallback cb, GameObject... gos) throws Exception {
		install(gos, this, cb);
	}
	public String freshName(String base) {
		return new StringBuilder(base).append(freshNameCounter++).toString();
	}
	/**
	 * Start the uninstall pipeline for given object with reply-to target and load-complete callback.
	 * @param go uninstall this.
	 * @param tc reply to this.
	 * @param cb continuation callback.
	 * @throws Exception
	 */
	protected void uninstall(String go, TaskChannel tc, UnloadedCallback cb) throws Exception {
		send(new UnloadGameObject(go, tc, cb));
	}
	/**
	 * Overload with BNL.
	 * @param go install this.
	 * @param tc reply to this.
	 * @param cb continuation callback.
	 * @param bnl Binding name list.
	 * @throws Exception
	 */
	protected void uninstall(String go, TaskChannel tc, UnloadedCallback cb, String[] bnl) throws Exception {
		send(new UnloadGameObject(go, tc, cb, bnl));
	}
	public void uninstall(String name) throws Exception {
		uninstall(name, this, this);
	}
	public void uninstall(String name, String[] bnl) throws Exception {
		uninstall(name, this, this, bnl);
	}
	public void event(GameObject go, String[] bnl) throws Exception {
		send(new GameEvent(go, bnl));
	}
	public void event(String[] bnl, GameObject... go) throws Exception {
		send(new GameEvents(go, bnl));
	}
	public void eventHookPre(GameObject go) {
		objectEventPre(go);
	}
	public void eventHookPost(GameObject go) {
		objectEventPost(go);
	}
	/**
	 * Install pipeline LoadedCallback.
	 * Perform the loaded protocol for each marker interface recognized.
	 */
	public void loaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		objectLoadedPre(go, ex);
		if (ex == null) {
			if(go.locatable) {
				// register GO in locator
				model.put(go.name, go);
			}
			if (go instanceof RequireLocatable) {
				// register additional names
				final HashMap<String, GameObject> names = new HashMap<String, GameObject>();
				((RequireLocatable) go).getLocatable(names);
				for (final Entry<String, GameObject> me : names.entrySet()) {
					model.put(me.getKey(), me.getValue());
				}
			}
			if (go instanceof RequireTimer) {
				// register additional timers
				final ArrayList<TimerCallback> timers = new ArrayList<TimerCallback>();
				((RequireTimer) go).getTimers(timers);
				for(int ix = 0; ix < timers.size(); ix++) {
					final TimerCallback tc = timers.get(ix);
					if (tc.getRegisterOnInstall()) {
						// add to timer service now
						timer.register(tc);
					}
				}
			} else if (go instanceof TimerCallback) {
				// GO is a timer itself
				final TimerCallback rt = (TimerCallback) go;
				if (rt.getRegisterOnInstall()) {
					// add to timer service now
					timer.register(rt);
				}
			}
		}
		objectLoadedPost(go, ex);
	}
	/**
	 * Uninstall pipeline UnloadedCallback.
	 * Perform the unloaded protocol for each marker interface recognized (in reverse order of loaded).
	 */
	public void unloaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		objectUnloadedPre(go, ex);
		if (go instanceof RequireTimer) {
			// register additional timers
			final ArrayList<TimerCallback> timers = new ArrayList<TimerCallback>();
			((RequireTimer) go).getTimers(timers);
			for(int ix = 0; ix < timers.size(); ix++) {
				final TimerCallback tc = timers.get(ix);
				if (tc.getRegisterOnInstall()) {
					// remove from timer service now
					timer.unregister(tc);
				}
			}
		} else if (go instanceof TimerCallback) {
			// GO is a timer itself
			final TimerCallback rt = (TimerCallback) go;
			if (rt.getRegisterOnInstall()) {
				// remove from timer service now
				timer.unregister(rt);
			}
		}
		if (go instanceof RequireLocatable) {
			// register additional names
			final HashMap<String, GameObject> names = new HashMap<String, GameObject>();
			((RequireLocatable) go).getLocatable(names);
			for (final Entry<String, GameObject> me : names.entrySet()) {
				model.remove(me.getKey());
			}
		}
		if(go.locatable) {
			model.remove(go.name);
		}
		objectUnloadedPost(go, ex);
	}
	/**
	 * Task startup activities.
	 * Call once per lifecycle.
	 */
	@Override
	protected void startup() throws Exception {
		gol.start();
		timer.start();
		final ViewHost vh = view.get();
		if(vh != null) {
			if(TraceSwitches.Game.SURFACE_READY) {
				Log.d(name, "startup surfaceActive=" + vh.isSurfaceActive());
			}
			if(vh.isSurfaceActive()) {
				// trigger loading now we wont get a SURFACE_READY message
				if(TraceSwitches.Game.SURFACE_READY) {
					Log.d(name, "start loading now surface active");
				}
				send(new EmptyMessage(Constants.Message.SURFACE_ATTACH));
			}
		}
	}
	/**
	 * Task shutdown activities.
	 * Call once per lifecycle.
	 */
	@Override
	protected void shutdown() {
		Log.d(name, "shutdown start");
		if(timer != null) {
			try {
				timer.send(Constants.Message.MSG_SHUTDOWN);
				timer.join(100);
			} catch (Exception e) {
			}
		}
		if(fr != null) {
			try {
				if(fr.isAlive()) {
					fr.interrupt();
					fr.join(100);
				}
			} catch (InterruptedException e) {
			}
		}
		if(gol != null) {
			try {
				gol.send(Constants.Message.MSG_SHUTDOWN);
				gol.join(100);
			} catch (Exception e) {
			}
		}
		if(view != null) {
			final ViewHost glgv = view.get();
			if(glgv != null) {
				glgv.disconnect();
			}
		}
		Log.d(name, "shutdown complete");
	}
	@Override
	protected void process(TaskMessage msg) {
		// process control messages only
		switch(msg.cmdcode) {
		case Constants.Message.SURFACE_READY:
			if(TraceSwitches.Game.SURFACE_READY) {
				Log.d(name, new StringBuilder("SurfaceReady got:").append(gotsurfaceready).toString());
			}
			if(gotsurfaceready) {
				Log.w(name, "Duplicate surface ready");
				//return;
			}
			try {
				if(gotsurfaceready) {
					// reload any cached resources
					rl.reload();
					// tell it to start again
					rr.resume();
				}
				else {
					final ViewHost glgv3 = view.get();
					if (glgv3 != null) {
						glgv3.setRender(rr);
						// start generating frame messages
						fr.start();
					} else {
						Log.w(name, "No View anymore");
					}
				}
				synchronized(updateLock) {
					startLoading(gotsurfaceready);
				}
			} finally {
				gotsurfaceready = true;
			}
			break;
		case Constants.Message.SURFACE_ATTACH:
			if(TraceSwitches.Game.SURFACE_READY) {
				Log.d(name, new StringBuilder("SurfaceAttach got:").append(gotsurfaceready).toString());
			}
			try {
				final ViewHost glgv3 = view.get();
				if (glgv3 != null) {
					final Point vp = new Point();
					glgv3.getViewport(vp);
					rr.setProjection(vp.x, vp.y);
					glgv3.setRender(rr);
					// start generating frame messages
					fr.start();
				} else {
					Log.w(name, "No View anymore");
				}
				synchronized(updateLock) {
					startLoading(false);
				}
			} finally {
				gotsurfaceready = true;
			}
			break;
		case Constants.Message.SURFACE_CHANGED:
			final SurfaceChanged sc = (SurfaceChanged)msg;
			if(TraceSwitches.Game.SURFACE_CHANGED) {
				Log.d(name, new StringBuilder("SurfaceChanged ").append(sc.width).append("x").append(sc.height).toString());
			}
			rr.setProjection(sc.width, sc.height);
			break;
		case Constants.Message.DRAW_FRAME:
			final ViewHost glgv2 = view.get();
			if(glgv2 != null) {
				// ok to draw something
				//Log.d(name, "drawFrame");
				glgv2.postRenderRequest();
			}
			lastDrawFrame = System.currentTimeMillis();
			break;
		case Constants.Message.LOAD_OBJECT:
			// load object completed; continue pipeline
			final LoadGameObject lgo = (LoadGameObject) msg;
			synchronized (updateLock) {
				lgo.callback(this, this);
			}
			break;
		case Constants.Message.LOAD_OBJECTS:
			// batch load objects completed; continue pipeline
			final LoadGameObjects lgos = (LoadGameObjects) msg;
			synchronized (updateLock) {
				lgos.callback(this, this);
			}
			break;
		case Constants.Message.UNLOAD_OBJECT:
			// unload object completed; continue pipeline
			final UnloadGameObject ugo = (UnloadGameObject) msg;
			synchronized (updateLock) {
				ugo.callback(this, this);
			}
			break;
		case Constants.Message.GAME_EVENT:
			// game event trigger; continue pipeline
			final GameEvent ge = (GameEvent) msg;
			synchronized (updateLock) {
				ge.callback(this, this, this);
			}
			break;
		case Constants.Message.GAME_EVENTS:
			// game event batch trigger; continue pipeline
			final GameEvents ges = (GameEvents) msg;
			synchronized (updateLock) {
				ges.callback(this, this, this);
			}
			break;
		case Constants.Message.SCENE_START:
			// install scene
			final SceneMessage sm = (SceneMessage)msg;
			rr.setScene(sm.scene);
			break;
		case Constants.Message.GAME_START:
			gameOver = false;
			gameStarted = true;
			synchronized(updateLock) {
				gameStarting();
			}
		case Constants.Message.GAME_RESUME:
			paused = false;
			try {
				timer.send(Constants.Message.MSG_START);
			} catch (Exception e) {
				Log.e(name, "timer.send(start)", e);
			}
			break;
		case Constants.Message.GAME_PAUSE:
			paused = true;
			try {
				timer.send(Constants.Message.MSG_PAUSE);
			} catch (Exception e) {
				Log.e(name, "timer.send(pause)", e);
			}
			break;
		case Constants.Message.GAME_TOGGLE:
			paused = !paused;
			try {
				timer.send(paused ? Constants.Message.MSG_PAUSE : Constants.Message.MSG_START);
			} catch (Exception e) {
				Log.e(name, "timer.send(toggle)", e);
			}
			break;
		case Constants.Message.GAME_OVER:
			gameOver = true;
			synchronized(updateLock) {
				gameOver();
			}
			break;
		}
		if(!paused) {
			// process messages for gameplay
			switch (msg.cmdcode) {
			case Constants.Message.NOTIFY_TIMER:
				final NotifyTimer at = (NotifyTimer) msg;
				synchronized (updateLock) {
					at.execute(this, this);
				}
				break;
			case Constants.Message.AGGREGATE_NOTIFY_TIMER:
				final AggregateNotifyTimer ant = (AggregateNotifyTimer) msg;
				synchronized (updateLock) {
					ant.execute(this, this);
				}
				try {
					// recycle message back to timer service
					timer.send(ant);
				}
				catch(Exception ex) {
					Log.e(name, "timer.send(ant)", ex);
				}
				break;
			}
		}
	}
	/**
	 * Send the Start game-control message.
	 */
	public void startGame() {
		try {
			send(Constants.Message.MSG_START);
		} catch (Exception e) {
			Log.e(name, "startGame", e);
		}
	}
	/**
	 * Send the Pause game-control message.
	 */
	public void pauseGame() {
		try {
			send(Constants.Message.MSG_PAUSE);
		} catch (Exception e) {
			Log.e(name, "pauseGame", e);
		}
	}
	/**
	 * Send the Resume game-control message.
	 */
	public void resumeGame() {
		try {
			send(Constants.Message.MSG_RESUME);
		} catch (Exception e) {
			Log.e(name, "resumeGame", e);
		}
	}
	/**
	 * Send the Toggle game-control message.
	 */
	public void toggleGame() {
		try {
			send(Constants.Message.MSG_TOGGLE);
		} catch (Exception e) {
			Log.e(name, "toggleGame", e);
		}
	}
	/**
	 * Return whether the game is paused.
	 */
	public boolean isPaused() { return paused; }
	/**
	 * Return whether the game is over.
	 */
	public boolean isOver() { return gameOver; }
	public boolean isStarted() { return gameStarted; }
	@SuppressWarnings("unchecked")
	public <T> T get(int serviceId) {
		return (T)services.get(Integer.valueOf(serviceId));
	}
	public <T> void addService(int serviceId, T service) {
		services.put(Integer.valueOf(serviceId), service);
	}
	@SuppressWarnings("unchecked")
	public <T extends GameObject> T locate(String path) {
		return (T) model.get(path);
	}
}
