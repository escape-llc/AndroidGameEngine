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
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;

/**
 * GLES 2.x surface view.
 * @author escape-llc
 *
 */
public final class GL2GameView extends GLSurfaceViewImpl {
	public GL2GameView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	public GL2GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}
	/**
	 * Configure GL viewport.
	 */
	@Override
	public void surfaceChanged(GL10 gl, int width, int height) {
	    GLES20.glViewport(0, 0, width, height);
	}
	/**
	 * GL Setup on surface created.
	 * Disable GL_DITHER.
	 * BlendFunc = GL_SRC_ALPHA/GL_ONE
	 * ClearColor = clear[]
	 */
	@Override
	public void surfaceCreated(GL10 gl, EGLConfig arg1) {
		Log.d("GL2GV", "created " + arg1);
		GLES20.glDisable(GLES20.GL_DITHER);
		//Set The Blending Function For Translucency
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		GLES20.glClearColor(clear[0], clear[1], clear[2], clear[3]);
		/*
		try {
			final ByteBuffer byteBuf = ByteBuffer.allocateDirect(4);
			byteBuf.order(ByteOrder.nativeOrder());
			final IntBuffer ib = byteBuf.asIntBuffer();
			GLES20.glGetIntegerv(GL10.GL_MAX_ELEMENTS_VERTICES, ib);
			Log.d(NAME, "max_vertices=" + ib.get(0));
			ib.rewind();
			GLES20.glGetIntegerv(GL10.GL_MAX_ELEMENTS_INDICES, ib);
			Log.d(NAME, "max_indices=" + ib.get(0));
		} catch (Exception ex) {
			Log.e(NAME, "getint failed", ex);
		}
		*/
	}
}
