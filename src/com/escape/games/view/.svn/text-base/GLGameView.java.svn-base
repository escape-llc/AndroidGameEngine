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

import android.content.Context;
import android.util.AttributeSet;

/**
 * GLES 1.x surface view.
 * @deprecated Use GLES 2.0 version.
 * @author escape-llc
 *
 */
public final class GLGameView extends GLSurfaceViewImpl {
	public GLGameView(Context context) {
		super(context);
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	public GLGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	@Override
	public int getRenderMode() { return 1; }
	@Override
	public void surfaceChanged(GL10 gl, int width, int height) {
	    gl.glViewport(0, 0, width, height);
	    float ratio = (float) width / (float)height;
	    gl.glMatrixMode(GL10.GL_PROJECTION);
	    gl.glLoadIdentity();
	    android.opengl.GLU.gluPerspective(gl, vangle/zoom, ratio, 1f, 60f);
	    //gl.glFrustumf(-ratio, ratio, -1, 1, 1f, 60f);
	    gl.glMatrixMode(GL10.GL_TEXTURE);
	    gl.glLoadIdentity();
	    gl.glScalef(-1f, 1f, 1f);
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glLoadIdentity();
	}
	@Override
	public void surfaceCreated(GL10 gl, EGLConfig arg1) {
		gl.glDisable(GL10.GL_DITHER);
		//Set The Blending Function For Translucency
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glClearColor(clear[0], clear[1], clear[2], clear[3]);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		/*
		try {
			final int[] ib = new int[2];
			gl.glGetIntegerv(GL10.GL_MAX_ELEMENTS_VERTICES, ib, 0);
			Log.d(NAME, "max_vertices=" + ib[0]);
			gl.glGetIntegerv(GL10.GL_MAX_ELEMENTS_INDICES, ib, 1);
			Log.d(NAME, "max_indices=" + ib[1]);
		} catch (Exception ex) {
			Log.e(NAME, "getint failed", ex);
		}
		*/
	}
}
