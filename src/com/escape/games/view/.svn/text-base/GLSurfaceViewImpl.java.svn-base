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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.escape.games.api.ViewHost;
import com.escape.games.core.TaskChannel;
import com.escape.games.message.Constants;
import com.escape.games.message.EmptyMessage;
import com.escape.games.message.SurfaceChanged;
import com.escape.games.service.RenderServiceImpl;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Core implementation for any OpenGL surface view.
 * You must set up a TaskChannel to receive GL callbacks for surface.
 * You must set up a RenderServiceImpl subclass to render anything on the surface.
 * @author escape-llc
 *
 */
public abstract class GLSurfaceViewImpl extends GLSurfaceView implements ViewHost, GLSurfaceView.Renderer {
	protected static final String NAME = "GLSVI";
	protected float zoom = 1f;
	protected float vangle = 60f;
	protected final float[] clear = { 0f, 0f, 0f, 1f };
	protected int width;
	protected int height;
	protected volatile RenderServiceImpl rr;
	protected volatile TaskChannel supervisor;
	protected volatile boolean hasSurface;
	/**
	 * Delegated onSurfaceChanged.
	 * @param gl Not Used.
	 * @param width viewport width.
	 * @param height viewport height.
	 */
	protected abstract void surfaceChanged(GL10 gl, int width, int height);
	/**
	 * Delegated onSurfaceCreated.
	 * @param gl Not Used.
	 * @param arg1 GL config.
	 */
	protected abstract void surfaceCreated(GL10 gl, EGLConfig arg1);
	public GLSurfaceViewImpl(Context context) {
		super(context);
	}
	public GLSurfaceViewImpl(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/**
	 * Return whether the surfaceCreated callback was seen.
	 * @return true: surfaceCreated seen; false: not seen.
	 */
	public boolean isSurfaceActive() { return hasSurface; }
	/**
	 * Get the viewport dimensions.
	 * Required when surface is already attached.
	 */
	public void getViewport(Point vp) { vp.x = width; vp.y = height; }
	/**
	 * Connect the TaskChannel to notify of GL callbacks.
	 */
	public void connect(TaskChannel supervisor) {
		this.supervisor = supervisor;
	}
	/**
	 * Connect the render service.
	 */
	public void setRender(RenderServiceImpl rr) {
		this.rr = rr;
	}
	/**
	 * Disconnect components from connect() and setRender().
	 */
	public void disconnect() {
		this.rr = null;
		this.supervisor = null;
	}
	/**
	 * Post request to GL thread to render frame.
	 */
	public void postRenderRequest() {
		this.requestRender();
	}
	/**
	 * Delegate to subclass then send to supervisor.
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;
		surfaceChanged(gl, width, height);
	    final TaskChannel tc = this.supervisor;
		if (tc != null) {
			try {
				tc.send(new SurfaceChanged(width, height));
			} catch (Exception e) {
				Log.e(NAME, "SURFACE_CHANGED", e);
			}
		}
	}
	/**
	 * Delegate to subclass then send to supervisor.
	 * If this is not the initial surface creation, suspend RenderService.
	 * IST: the receiver of SURFACE_READY must call RenderServiceImpl.reload() and RenderServiceImpl.resume()!
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		hasSurface = true;
		final RenderServiceImpl prr = this.rr;
		if(prr != null) {
			// this is a repeat of surface changed, tell it to stop.
			prr.suspend();
		}
		surfaceCreated(gl, arg1);
	    final TaskChannel tc = this.supervisor;
		if (tc != null) {
			try {
				tc.send(new EmptyMessage(Constants.Message.SURFACE_READY));
			} catch (Exception e) {
				Log.e(NAME, "SURFACE_READY", e);
			}
		}
	}
	/**
	 * Pass through to render service.
	 */
	public void onDrawFrame(GL10 gl) {
		final RenderServiceImpl prr = this.rr;
		if (prr != null) {
			prr.render();
		}
	}
}
