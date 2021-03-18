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
package com.escape.games.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.escape.games.api.Properties;
import com.escape.games.api.RequireResourceLoader;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.resource.Shader;

/**
 * Core implementation for geometry data and its rendering.
 * Delegates RequireResourceLoader.
 * @author escape-llc
 *
 */
public abstract class Geometry implements RequireResourceLoader {
	public static int FLOAT_BYTES = Float.SIZE / Byte.SIZE;
	public static int SHORT_BYTES = Short.SIZE / Byte.SIZE;
	// fixed-point (1.0)
	protected static final int one = 0x10000;
	// fixed-point (0.5)
	protected static final int half = 0x08000;
	// fixed-point color table
	public static final int fixpoint_colors[] = {
		0, 0, 0, one,
		0, 0, one, one,
		0, one, 0, one,
		0, one, one, one,
		one, 0, 0, one,
		one, 0, one, one,
		one, one, 0, one,
		one, one, one, one,
		0, 0, half, one,
		0, half, 0, one,
		0, half, half, one,
		half, 0, 0, one,
		half, 0, half, one,
		half, half, 0, one,
		half, half, half, one,
	};
	static final float onef = 1f;
	static final float halff = .5f;
	// floating-point color table
	public static final float float_colors[] = {
		0, 0, 0, onef,
		0, 0, onef, onef,
		0, onef, 0, onef,
		0, onef, onef, onef,
		onef, 0, 0, onef,
		onef, 0, onef, onef,
		onef, onef, 0, onef,
		onef, onef, onef, onef,
		0, 0, halff, onef,
		0, halff, 0, onef,
		0, halff, halff, onef,
		halff, 0, 0, onef,
		halff, 0, halff, onef,
		halff, halff, 0, onef,
		halff, halff, halff, onef,
	};
	boolean loaded;
	/**
	 * Acquire resources etc.
	 * @param rl
	 * @param svc
	 */
	protected abstract void internalLoad(ResourceLoader rl, Services svc);
	/**
	 * Render geometry.
	 * All transforms are applied to the shader.
	 * @param sx the shader to render with.
	 * @param px Source of values.
	 */
	public abstract void render(Shader sx, Properties px);
	/**
	 * Return number of vertices.
	 * @return the vertex count.
	 */
	public abstract int getVertexCount();
	/**
	 * 4-component vector.
	 * @author escape-llc
	 *
	 */
	public static final class Vec4 {
		public float x;
		public float y;
		public float z;
		public float w;
		public Vec4(float x, float y, float z, float w) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
	}
	/**
	 * Output GL errors to the log.
	 * @param op message prefix.
	 */
    protected static void checkGlError(String op) {
    	final StringBuilder sb = new StringBuilder(op);
    	sb.append(": glErrors: ");
        int error;
        int did = 0;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
        	did++;
        	sb.append(Integer.toHexString(error));
        	sb.append(" ");
        }
        if(did > 0)
        	Log.e("Geometry", sb.toString());
    }
	/**
	 * Normalize vector in-place.
	 * @param vec Buffer.
	 * @param off array offset.
	 */
	public static void normalize(float[] vec, int off) {
		final float len = Matrix.length(vec[off + 0], vec[off + 1], vec[off + 2]);
		vec[off + 0] /= len;
		vec[off + 1] /= len;
		vec[off + 2] /= len;
	}
	/**
	 * Compute the cross-product of two vectors into third.
	 * @param cp Target for cross-product vector.
	 * @param cpo Offset.
	 * @param v0 Vector 1.
	 * @param v0o V1 offset.
	 * @param v1 Vector 2.
	 * @param v1o V2 offset.
	 */
	public static void crossProduct(float[] cp, int cpo, float[] v0, int v0o, float[] v1, int v1o) {
		cp[cpo + 0] = v0[v0o + 1] * v1[v1o + 2] - v0[v0o + 2] * v1[v1o + 1];
		cp[cpo + 1] = v0[v0o + 2] * v1[v1o + 0] - v0[v0o + 0] * v1[v1o + 2];
		cp[cpo + 2] = v0[v0o + 0] * v1[v1o + 1] - v0[v0o + 1] * v1[v1o + 0];
	}
	public void load(ResourceLoader rl, Services svc) {
		if(loaded) return;
		try {
			internalLoad(rl, svc);
		}
		finally {
			loaded = true;
		}
	}
	/**
	 * Create a vertex color buffer.
	 * One vertex is made from each color in the list, looping around the list.
	 * @param numv Number of vertices.
	 * @param colors Color source array.
	 * @return New buffer.
	 */
	public static FloatBuffer colorVertices(int numv, Vec4[] colors) {
		final ByteBuffer bb = ByteBuffer.allocateDirect(numv*4*FLOAT_BYTES);
		bb.order(ByteOrder.nativeOrder());
		final FloatBuffer fb = bb.asFloatBuffer();
		int cidx = 0;
		for(int vx = 0; vx < numv; vx++) {
			fb.put(colors[cidx].x);
			fb.put(colors[cidx].y);
			fb.put(colors[cidx].z);
			fb.put(colors[cidx].w);
			cidx = (cidx + 1) % colors.length;
		}
		fb.position(0);
		return fb;
	}
}
