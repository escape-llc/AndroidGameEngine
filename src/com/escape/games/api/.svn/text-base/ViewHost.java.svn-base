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

import android.graphics.Point;

import com.escape.games.core.TaskChannel;
import com.escape.games.service.RenderServiceImpl;

/**
 * SurfaceView's connection to the game engine.
 * @author escape-llc
 *
 */
public interface ViewHost {
	/**
	 * Send callback notifications to given target.
	 * @param supervisor Message target.
	 */
	void connect(TaskChannel supervisor);
	/**
	 * Set the render service for drawing.
	 * @param rr
	 */
	void setRender(RenderServiceImpl rr);
	/**
	 * Disconnect from game engine.
	 */
	void disconnect();
	/**
	 * Request a redraw cycle on the surface view's thread.
	 */
	void postRenderRequest();
	/**
	 * Return whether the surfaceCreated callback was seen.
	 * @return true: surfaceCreated seen; false: not seen.
	 */
	boolean isSurfaceActive();
	/**
	 * Obtain the current viewport size.
	 * @param vp Target to accept viewport.
	 */
	void getViewport(Point vp);
}
