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

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.escape.games.message.Constants;

import android.util.Log;

/**
 * Base class for a task with an input channel for receiving messages.
 * A Blocking Queue is waited on, then a batch of up to CAP messages are drained and then processed,
 * before the next BQ operation.
 * @author escape-llc
 *
 */
public abstract class GameTaskWithChannel extends GameTask implements TaskChannel {
	/**
	 * Default Maximum number of messages to read at one time.
	 */
	protected static final int MESSAGE_CAP = 16;
	/**
	 * Default Blocking Queue Capacity.
	 */
	protected static final int QUEUE_CAP = 256;
	/**
	 * List of incoming messages.
	 */
	protected final BlockingQueue<TaskMessage> bq;
	/**
	 * Maximum number of messages to read at one time.
	 */
	protected final int cap;
	/**
	 * Ctor.
	 * Uses default Queue Capacity.
	 * @param name Task/thread name.
	 * @param supervisor Target for notifications.
	 */
	protected GameTaskWithChannel(String name, TaskChannel supervisor) {
		this(name, supervisor, QUEUE_CAP, MESSAGE_CAP);
	}
	/**
	 * Ctor.
	 * @param name Task/thread name.
	 * @param supervisor Target for notifications.
	 * @param qcap Queue Capacity.
	 */
	protected GameTaskWithChannel(String name, TaskChannel supervisor, int qcap, int cap) {
		super(name, supervisor);
		this.bq = new ArrayBlockingQueue<TaskMessage>(qcap);
		this.cap = cap;
	}
	/**
	 * Perform task startup activities.
	 * Should fail fast.
	 * Called once per lifecycle.
	 * @throws Exception startup error.
	 */
	protected abstract void startup() throws Exception;
	/**
	 * Perform task shutdown activities.
	 * Must Not Throw.
	 * Must handle partial initialization by startup().
	 * Called once per lifecycle.
	 */
	protected abstract void shutdown();
	/**
	 * Process the message.
	 * @param msg Message to process.
	 */
	protected abstract void process(TaskMessage msg);
	protected void loopStart(long tx1) { }
	protected void loopEnd(long tx1, long tx2) { }
	/**
	 * Send a message to the task message queue.
	 */
	public void send(TaskMessage tm) throws Exception {
		if(!isAlive())
			throw new IllegalStateException(name + " Not Running");
		final boolean did = bq.offer(tm);
		if(!did && TraceSwitches.Game.SEND) {
			Log.e(name, "send failed on " + tm.getClass().getName());
		}
	}
	/**
	 * Perform the apartment thread loop.
	 */
	public void run() {
		try {
			startup();
			final ArrayList<TaskMessage> drain = new ArrayList<TaskMessage>(cap);
			drain.ensureCapacity(cap);
			boolean sawshutdown = false;
			while(!isInterrupted() && !sawshutdown) {
				// block until we actually get one
				final TaskMessage firstone = bq.take();
				drain.clear();
				drain.add(firstone);
				// drainTo does not block!
				// get whatever else up to cap - 1
				bq.drainTo(drain, cap - 1);
				//if(drain.get(0) != firstone)
				//	Log.e(name, "WE LOST ONE!");
				final long tx1 = System.currentTimeMillis();
				loopStart(tx1);
				for (int ix = 0; ix < drain.size(); ix++) {
					final TaskMessage msg = drain.get(ix);
					if (msg.cmdcode == Constants.Message.SHUTDOWN) {
						sawshutdown = true;
						break;
					}
					process(msg);
				}
				final long tx2 = System.currentTimeMillis();
				loopEnd(tx1, tx2);
				if (TraceSwitches.Game.TELEMETRY) {
					final long dx = tx2 - tx1;
					if (dx > TraceSwitches.Game.TELEMETRY_THD_1) {
						final StringBuilder sb = new StringBuilder("Cycle ")
								.append(dx).append("ms,bq.size=")
								.append(bq.size()).append(",drain.size=")
								.append(drain.size());
						if (dx > TraceSwitches.Game.TELEMETRY_THD_2) {
							sb.append(",processed");
							for (int ix = 0; ix < drain.size(); ix++) {
								sb.append(" ");
								sb.append(drain.get(ix).toString());
							}
						}
						Log.d(name, sb.toString());
					}
				}
			}
		}
		catch(Exception ex) {
			Log.e(name, "run", ex);
		}
		finally {
			shutdown();
		}
	}
}
