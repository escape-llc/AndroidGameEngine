package com.escape.games.message;

import com.escape.games.core.TaskMessage;

/**
 * Surface-changed message.
 * Sent by the SurfaceView to GameCycle from the callback.
 * @author escape-llc
 *
 */
public class SurfaceChanged extends TaskMessage {
	public final int width;
	public final int height;
	/**
	 * Ctor.
	 * @param width View width.
	 * @param height View height.
	 */
	public SurfaceChanged(int width, int height) {
		super(Constants.Message.SURFACE_CHANGED);
		this.width = width;
		this.height = height;
	}
}
