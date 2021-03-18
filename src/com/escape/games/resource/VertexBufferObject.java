/*
 * Copyright 2014 eScape Technology LLC.
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

import java.nio.FloatBuffer;
import java.util.concurrent.CountDownLatch;

import com.escape.games.model.Geometry;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Implementation of GL Vertex Buffer Object resource.
 * Buffers are 4-byte Floats.
 * @author escape-llc
 *
 */
public class VertexBufferObject implements GLResource {
	/* VBO id*/
	int id;
	volatile boolean released;
	/* actual data */
	final FloatBuffer fb;
	public VertexBufferObject(FloatBuffer fb) {
		if(fb == null) throw new IllegalArgumentException("fb");
		this.fb = fb;
	}
	public Object preload(Context ctx) {
		return null;
	}
	public void load(Object ctx) {
		final int[] handle = new int[1];
		GLES20.glGenBuffers(1, handle, 0);
		if(handle[0] != 0) {
			id = handle[0];
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * Geometry.FLOAT_BYTES, fb, GLES20.GL_STATIC_DRAW);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			released = false;
		}
		else {
			Log.w("TX", "glGenBuffers failed");
			//Shader.checkGlError("glGenBuffers");
		}
	}
	public void unload(Context ctx) {
		if(released) return;
		final int handle[] = { id };
		GLES20.glDeleteBuffers(1, handle, 0);
		id = 0;
		released = true;
	}
	public void release() {
		if(released) return;
		id = 0;
		released = true;
	}
	public void setup() {
		if(released) return;
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);
	}
	public void teardown() {
		if(released) return;
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	/**
	 * Dispatch an operation to the GL thread to create a VBO.
	 * Waits for the operation to complete.
	 * @param ctx Source of resources.
	 * @param fb Source data buffer.  Should be InterleavedVertexGeometry.
	 * @param sv GL Surface view.
	 * @return New VBO or NULL.
	 */
	public static VertexBufferObject create(Context ctx, FloatBuffer fb, GLSurfaceView sv) {
		if(sv != null) {
			final CountDownLatch cl = new CountDownLatch(1);
			final VertexBufferObject[] output = new VertexBufferObject[1];
			final VertexBufferObject vbo = new VertexBufferObject(fb);
			final Object ox = vbo.preload(ctx);
			final Runnable rx = new Runnable() {
				public void run() {
					try {
						vbo.load(ox);
						output[0] = vbo;
					} catch(Exception ex) {
						// eat it
						Log.e("VBO", "VertexBufferObject.create", ex);
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
