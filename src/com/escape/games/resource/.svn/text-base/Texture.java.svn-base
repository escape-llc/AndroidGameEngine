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
package com.escape.games.resource;

import java.util.concurrent.CountDownLatch;
import com.escape.games.core.TraceSwitches;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Base implementation for OpenGL 2D texture from application resource.
 * @author escape-llc
 *
 */
public class Texture implements GLResource {
	static final String TAG = "Texture";
	int id;
	public final int resid;
	final int wraps;
	final int wrapt;
	final int minf;
	final int magf;
	volatile boolean released;
	/**
	 * Ctor.
	 * Sets wrap s/t to REPEAT, min/mag to NEAREST.
	 * @param resid drawable ID.
	 */
	public Texture(int resid) {
		this(resid, GLES20.GL_REPEAT, GLES20.GL_REPEAT, GLES20.GL_NEAREST, GLES20.GL_NEAREST);
	}
	/**
	 * Ctor.
	 * Sets min/mag to NEAREST.
	 * @param resid drawable ID.
	 * @param wraps TEXTURE_WRAP_S value.
	 * @param wrapt TEXTURE_WRAP_T value.
	 */
	public Texture(int resid, int wraps, int wrapt) {
		this(resid, wraps, wrapt, GLES20.GL_NEAREST, GLES20.GL_NEAREST);
	}
	/**
	 * Ctor.
	 * @param resid drawable ID.
	 * @param wraps TEXTURE_WRAP_S value.
	 * @param wrapt TEXTURE_WRAP_T value.
	 * @param minf TEXTURE_MIN_FILTER value.
	 * @param magf TEXTURE_MAG_FILTER value.
	 */
	public Texture(int resid, int wraps, int wrapt, int minf, int magf) {
		if(resid == 0) throw new IllegalArgumentException("resid");
		this.resid = resid;
		this.wraps = wraps;
		this.wrapt = wrapt;
		this.minf = minf;
		this.magf = magf;
	}
	public void unload(Context ctx) {
		if(released) return;
		final int[] handle = { id };
		GLES20.glDeleteTextures(1, handle, 0);
		id = 0;
		released = true;
	}
	public void release() {
		if(released) return;
		id = 0;
		released = true;
	}
	public Object preload(Context ctx) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), resid, options);
		return bitmap;
	}
	public void load(Object preload) {
		if(!(preload instanceof Bitmap)) {
			release();
			Log.w("TX", "preload was not a Bitmap");
			return;
		}
		final Bitmap bitmap = (Bitmap)preload;
		try {
			final int[] handle = new int[1];
			GLES20.glGenTextures(1, handle, 0);
			if (handle[0] != 0) {
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle[0]);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minf);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magf);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wraps);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapt);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
				id = handle[0];
				released = false;
			} else {
				Log.w("TX", "glGenTextures failed");
				// Shader.checkGlError("glGenTextures");
			}
		} finally {
			bitmap.recycle();
		}
	}
	/**
	 * Configure texturing into shader.
	 * @param sx Target shader.
	 * @param tex value for glActiveTexure().
	 * @param tuniform value for shader sampler2d uniform.
	 * @param locname shader uniform location name.
	 */
	public void setup(Shader sx, int tex, int tuniform, String locname) {
		if(released) return;
		GLES20.glActiveTexture(tex);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
		sx.texture(locname, tuniform);
	}
	/**
	 * Create a texture by resource id from non-GL thread.
	 * Waits for the operation on GL thread to complete.
	 * @param ctx Source of services.
	 * @param sv Target GL Surface view.
	 * @param output Target output list; length of this array controls iteration count.
	 * @param resid list of resource ids
	 * @return true: some textures were created, check array; false: GL thread not invoked.
	 */
	public static boolean create(Context ctx, GLSurfaceView sv, final Texture[] output, int... resid) {
		if(sv != null) {
			final CountDownLatch cl = new CountDownLatch(output.length);
			final Texture[] local = new Texture[output.length];
			final Object[] preload = new Object[output.length];
			for(int ix = 0; ix < resid.length; ix++) {
				if(resid[ix] <= 0) continue;
				local[ix] = new Texture(resid[ix]);
				preload[ix] = local[ix].preload(ctx);
				if (TraceSwitches.Loader.GL_RESOURCES) {
					Log.d(TAG, "preload." + ix);
				}
			}
			final Runnable rx = new Runnable() {
				public void run() {
					if (TraceSwitches.Loader.GL_RESOURCES) {
						Log.d(TAG, "GL.run");
					}
					Shader.GLthreadResource(cl, local, preload, output);
				}
			};
			sv.queueEvent(rx);
			try {
				cl.await();
				return true;
			} catch (InterruptedException e) {
			}
		}
		return false;
	}
}
