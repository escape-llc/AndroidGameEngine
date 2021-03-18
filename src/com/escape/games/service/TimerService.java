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
package com.escape.games.service;

import java.util.ArrayList;

import com.escape.games.api.Timer;
import com.escape.games.api.TimerCallback;
import com.escape.games.api.TimerCancel;
import com.escape.games.api.TimerConfig;
import com.escape.games.core.GameTaskWithChannel;
import com.escape.games.core.TaskChannel;
import com.escape.games.core.TaskMessage;
import com.escape.games.message.AggregateNotifyTimer;
import com.escape.games.message.NotifyTimer;
import com.escape.games.message.Constants;
import com.escape.games.message.EmptyMessage;

/**
 * Core implementation of the timer service.
 * This service is free-threaded.  There is one locked object, <b>timers</b>.
 * Responds to Game Control messages to pause and resume timers.
 * Timers do not start until the first GAME_START is received.
 * @author escape-llc
 *
 */
public class TimerService extends GameTaskWithChannel implements Timer {
	/**
	 * Extend the TimerConfig with bookkeeping data.
	 * @author escape-llc
	 *
	 */
	static final class Holder extends TimerConfig implements TimerCancel {
		long remaining;
		boolean first;
		boolean cancelled;
		final TimerCallback cb;
		final TaskChannel tc;
		Object tokenLock;
		public Holder(TimerCallback cb, TaskChannel tc, Object tokenLock) {
			this.cb = cb;
			this.tc = tc;
			this.tokenLock = tokenLock;
		}
		public void reset() {
			this.remaining = this.durationMS;
		}
		/**
		 * Should be holding the token lock when calling this.
		 */
		public void release() {
			tokenLock = null;
		}
		/**
		 * Release the token upon expiration/cancellation of your timer, in the <b>TimerCallback</b>.
		 */
		@Override
		public TimerCancel obtainToken() {
			if(tokenLock == null) return null;
			return (TimerCancel)this;
		}
		public boolean cancel() {
			if(tokenLock == null) return false;
			synchronized(tokenLock) {
				cancelled = true;
				return cancelled;
			}
		}
		public boolean cancelled() {
			if(tokenLock == null) return cancelled;
			synchronized(tokenLock) { return cancelled; }
		}
	}
	final int timebaseMS;
	/**
	 * Also used as synchronization object for accessing Holder objects!
	 */
	final ArrayList<Holder> timers;
	/**
	 * Reused during the Timer Tick loop to purge events.
	 */
	final ArrayList<Holder> remove;
	final ArrayList<AggregateNotifyTimer> recycle_atn;
	long lastTime;
	final int siz;
	Thread tx;
	/**
	 * Ctor.
	 * Uses default preallocate (32).
	 * @param supervisor Target for notifications.
	 * @param tbt Time Base Tick in MS.
	 */
	public TimerService(TaskChannel supervisor, int tbt) {
		this(supervisor, tbt, 32);
	}
	/**
	 * Ctor.
	 * @param supervisor Target for notifications.
	 * @param tbt Time Base Tick in MS.
	 * @param siz Preallocate size for lists.
	 */
	public TimerService(TaskChannel supervisor, int tbt, int siz) {
		super("Timer", supervisor);
		timers = new ArrayList<Holder>(siz);
		remove = new ArrayList<Holder>(siz);
		recycle_atn = new ArrayList<AggregateNotifyTimer>(siz);
		timebaseMS = tbt;
		this.siz = siz;
	}

	Thread createTimer() {
		return new Thread() {
			public void run() {
				while(!interrupted()) {
					try {
						Thread.sleep(timebaseMS);
						send(new EmptyMessage(Constants.Message.TIME_BASE_TICK));
					} catch (InterruptedException e) {
						break;
					} catch (Exception e) {
						break;
					}
				}
			}
		};
	}
	@Override
	protected void startup() throws Exception {
	}

	@Override
	protected void shutdown() {
		if(tx != null) {
			try {
				tx.interrupt();
				tx.join(100);
			} catch (InterruptedException e) {
			}
		}
	}
	/**
	 * Either obtain one from recycle or create a new one.
	 * @param ant
	 * @return new or recycled instance.
	 */
	AggregateNotifyTimer obtain(ArrayList<NotifyTimer> ant) {
		if(recycle_atn.size() > 0) {
			//Log.d(name, "obtain " + recycle_atn.size());
			final AggregateNotifyTimer aant = recycle_atn.remove(0);
			aant.recycle(ant);
			return aant;
		}
		return new AggregateNotifyTimer(ant);
	}

	@Override
	protected void process(TaskMessage msg) {
		if(msg.cmdcode == Constants.Message.TIME_BASE_TICK) {
			final long ct = System.currentTimeMillis();
			final long delta = ct - lastTime;
			ArrayList<NotifyTimer> nt = null;
			synchronized(timers) {
				remove.clear();
				for(int ix = 0; ix < timers.size(); ix++) {
					final Holder hx = timers.get(ix);
					if(!hx.first) {
						hx.remaining -= delta;
					}
					final boolean expired = hx.remaining <= 0 || hx.cancelled;
					if (expired || hx.continuous || hx.first) {
						// send a notification
						final long elapsed = hx.durationMS - hx.remaining;
						if(nt == null) {
							nt = new ArrayList<NotifyTimer>(siz);
						}
						nt.add(new NotifyTimer(hx.cb, hx.first ? 0L : delta, elapsed, expired));
					}
					hx.first = false;
					if(hx.cancelled) {
						hx.release();
						remove.add(hx);
					}
					else if(expired) {
						// expired
						if(hx.autoRepeat) {
							// reset for next cycle
							hx.reset();
						}
						else {
							hx.release();
							remove.add(hx);
						}
					}
				}
				if(remove.size() > 0)
					timers.removeAll(remove);
			}
			lastTime = ct;
			if (nt != null && nt.size() > 0) {
				/*
				if (false) {
					if (nt.size() > 1) {
						Log.d(name, "Sending " + nt.size() + " timers");
					}
				}
				*/
				try {
					supervisor.send(obtain(nt));
				} catch (Exception e) {
				}
			}
		}
		else if(msg.cmdcode == Constants.Message.AGGREGATE_NOTIFY_TIMER) {
			// got a message to recycle
			final AggregateNotifyTimer atn = (AggregateNotifyTimer)msg;
			atn.release();
			recycle_atn.add(atn);
		}
		else if(msg.cmdcode == Constants.Message.NOTIFY_TIMER) {
			// got a message to recycle
		}
		else if(msg.cmdcode == Constants.Message.GAME_START) {
			tx = createTimer();
			lastTime = System.currentTimeMillis();
			tx.start();
		}
		else if(msg.cmdcode == Constants.Message.GAME_PAUSE) {
			if(tx != null) {
				try {
					tx.interrupt();
					tx.join(100);
				} catch (InterruptedException e) {
				}
				finally {
					tx = null;
				}
			}
		}
	}
	public void register(TimerCallback cb) {
		synchronized(timers) {
			final Holder hx = new Holder(cb, this, timers);
			cb.setConfig(hx, timebaseMS);
			hx.first = true;
			hx.cancelled = false;
			hx.reset();
			timers.add(hx);
		}
	}
	public void unregister(TimerCallback cb) {
		synchronized(timers) {
			Holder target = null;
			for(int ix = 0; ix < timers.size(); ix++) {
				final Holder hx = timers.get(ix);
				if(hx.cb == cb) {
					target = hx;
					break;
				}
			}
			if(target != null) {
				timers.remove(target);
			}
		}
	}
	/**
	 * Use TimerConfig.obtainToken() if possible.
	 */
	public void cancel(TimerCallback cb) {
		synchronized(timers) {
			for(int ix = 0; ix < timers.size(); ix++) {
				final Holder hx = timers.get(ix);
				if(hx.cb == cb) {
					hx.cancelled = true;
					break;
				}
			}
		}
	}
	public void reset() {
		if(tx != null) throw new IllegalStateException("Cannot reset while running");
		synchronized(timers) {
			for(int ix = 0; ix < timers.size(); ix++) {
				final Holder hx = timers.get(ix);
				hx.reset();
			}
		}
	}
}
