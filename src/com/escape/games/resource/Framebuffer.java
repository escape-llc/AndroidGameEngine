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


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Support for GL Framebuffer
 * Uses 16-bit depth attachment and RGBA color attachment.
 * @author escape-llc
 *
 */
public class Framebuffer implements GLResource {
	/* framebuffer id*/
	int id;
	/* renderbuffer id */
	int rbid;
	/* texture id */
	int txid;
	final int width;
	final int height;
	volatile boolean released;
	/**
	 * Ctor.
	 * @param width FB width.
	 * @param height FB height.
	 */
	public Framebuffer(int width, int height) {
		if(width == 0) throw new IllegalArgumentException("width");
		if(height == 0) throw new IllegalArgumentException("height");
		this.width = width;
		this.height = height;
	}
	public Object preload(Context ctx) {
		return null;
	}
	public void load(Object ctx) {
		final int[] handle = new int[3];
		GLES20.glGenFramebuffers(1, handle, 0);
		GLES20.glGenRenderbuffers(1, handle, 1);
		GLES20.glGenTextures(1, handle, 2);
		if (handle[0] != 0 && handle[1] != 0 && handle[2] != 0) {
			// select
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, handle[0]);
			GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, handle[1]);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle[2]);
			// configure
			GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
			GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, handle[1]);
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, handle[2], 0);
			// check status
			final int fbs = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
			if(fbs == GLES20.GL_FRAMEBUFFER_COMPLETE) {
				// ok to make it
				id = handle[0];
				rbid = handle[1];
				txid = handle[2];
			}
			else {
				Log.w("FB", "failed status check: " + Integer.toHexString(fbs));
			}
			// unselect
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
			released = false;
		}
		else {
			// failed either no handles or not complete status
			Log.w("FB", "failed to create: resources: " + handle[0] + "/" + handle[1] + "/" + handle[2]);
			if(handle[0] != 0)
				GLES20.glDeleteFramebuffers(1, handle, 0);
			if(handle[1] != 0)
				GLES20.glDeleteRenderbuffers(1, handle, 1);
			if(handle[2] != 0)
				GLES20.glDeleteTextures(1, handle, 2);
			id = 0;
			rbid = 0;
			txid = 0;
		}
	}
	public void unload(Context ctx) {
		if(released) return;
		final int handle[] = { id, rbid, txid };
		GLES20.glDeleteFramebuffers(1, handle, 0);
		GLES20.glDeleteRenderbuffers(1, handle, 1);
		GLES20.glDeleteTextures(1, handle, 2);
		id = 0;
		rbid = 0;
		txid = 0;
		released = true;
	}
	public void release() {
		if(released) return;
		id = 0;
		rbid = 0;
		txid = 0;
		released = true;
	}
	public void setup() {
		if(released) return;
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
	}
	public void teardown() {
		if(released) return;
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	/**
	 * Create a frame buffer with 16-bit depth attachment and RGBA color attachment.
	 * @param ctx Source of resources.
	 * @param width Target width.
	 * @param height Target height.
	 * @param sv Target surface view.
	 * @return new instance.
	 */
	public static Framebuffer create(Context ctx, int width, int height, GLSurfaceView sv) {
		if(sv != null) {
			final CountDownLatch cl = new CountDownLatch(1);
			final Framebuffer[] output = new Framebuffer[1];
			final Framebuffer fb = new Framebuffer(width, height);
			final Object ox = fb.preload(ctx);
			final Runnable rx = new Runnable() {
				public void run() {
					try {
						fb.load(ox);
						output[0] = fb;
					} catch(Exception ex) {
						// eat it
						Log.e("Framebuffer", "Framebuffer.create", ex);
					} finally {
						cl.countDown();
					}
				}
			};
			sv.queueEvent(rx);
			try {
				cl.await();
				return output[0];
			} catch (InterruptedException e) {
			}
		}
		return null;
	}
}
