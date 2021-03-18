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

import com.escape.games.api.Properties;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.resource.Shader;

/**
 * Represents packed interleaved vertex attributes.
 * Vertex Attributes must be in the order {Position, Normal, Color, Texture} and only Position is required.
 * @author escape-llc
 *
 */
public class InterleavedVertexGeometry extends Geometry {
	protected static final int IX_POSITION = 0;
	protected static final int IX_NORMAL = 1;
	protected static final int IX_COLOR = 2;
	protected static final int IX_TEXTURE = 3;
	protected static final int IX_TOTAL = 4;
	/* GL element type */
	final int elemType;
	/* Vertex attributes */
	final float[] vas;
	/* components per attribute */
	final int[] elems;
	/* total vertex size in bytes */
	final int stride;
	/* vertex count */
	final int vc;
	FloatBuffer mVertexBuffer;
	/**
	 * Ctor.
	 * Vertex Attributes must be in the order {Position, Normal, Color, Texture}
	 * Each position of the elems array indicates the number of elements for that component (P/N/C/T).
	 * Vertex attributes must be packed according to values in elems[].
	 * Example: a fully-populated vertex attribute array would be {x,y,z, nx,ny,nz, cr,cg,cb,ca, tx,ty}
	 * The corresponding elems[] would be { 3, 3, 4, 2 }.
	 * Example: a partially-populated vertex attribute array with (P/T) would be {x,y,z, tx,ty}
	 * The corresponding elems[] would be { 3, 0, 0, 2 }.
	 * @param et GL element type.
	 * @param vas Vertex Attributes in the order {Position, Normal, Color, Texture}
	 * @param elems Array of elements-per-component; use 0 to indicate not-present.
	 */
	protected InterleavedVertexGeometry(int et, float[] vas, int[] elems) {
		if(elems == null || elems.length != IX_TOTAL)
			throw new IllegalArgumentException("elems");
		if(elems[IX_POSITION] < 2)
			throw new IllegalArgumentException("position element size must be GE 2");
		int epv = 0;
		for(int ix = 0; ix < elems.length; ix++) {
			if(elems[ix] != 0 && (elems[ix] < 2 || elems[ix] > 4))
				throw new IllegalArgumentException("invalid element size: " + elems[ix]);
			if(elems[ix] > 0)
				epv += elems[ix];
		}
		if(epv <= 0)
			throw new IllegalArgumentException("elemsPerVertex failed");
		if(vas.length % epv != 0)
			throw new IllegalArgumentException("elemsPerVertex does not match array length");
		this.elemType = et;
		this.vas = vas;
		this.elems = elems;
		this.stride = epv*FLOAT_BYTES;
		this.vc = vas.length/epv;
	}
	protected InterleavedVertexGeometry(int et, FloatBuffer fb, int[] elems) {
		if(elems == null || elems.length != IX_TOTAL)
			throw new IllegalArgumentException("elems");
		if(elems[IX_POSITION] < 2)
			throw new IllegalArgumentException("position element size must be GE 2");
		int epv = 0;
		for(int ix = 0; ix < elems.length; ix++) {
			if(elems[ix] != 0 && (elems[ix] < 2 || elems[ix] > 4))
				throw new IllegalArgumentException("invalid element size: " + elems[ix]);
			if(elems[ix] > 0)
				epv += elems[ix];
		}
		if(epv <= 0)
			throw new IllegalArgumentException("elemsPerVertex failed");
		if(fb.capacity() % epv != 0)
			throw new IllegalArgumentException("elemsPerVertex does not match array length");
		this.elemType = et;
		this.elems = elems;
		this.vas = null;
		this.stride = epv*FLOAT_BYTES;
		this.vc = fb.capacity()/epv;
		this.mVertexBuffer = fb;
	}
	protected InterleavedVertexGeometry(FloatBuffer fb, int[] elems) {
		this(GLES20.GL_TRIANGLES, fb, elems);
	}
	/**
	 * Ctor.
	 * Use when element type is TRIANGLES.
	 * @param vas Vertex Attributes in the order {Position, Normal, Color, Texture}
	 * @param elems Array of elements-per-component; use 0 to indicate not-present.
	 */
	protected InterleavedVertexGeometry(float[] vas, int[] elems) {
		this(GLES20.GL_TRIANGLES, vas, elems);
	}
	/**
	 * Ctor.
	 * Use when element type is TRIANGLES AND all attributes are present with default dimensions {3,3,4,2}.
	 * @param vas Vertex Attributes in the order {Position, Normal, Color, Texture}
	 */
	protected InterleavedVertexGeometry(float[] vas) {
		this(GLES20.GL_TRIANGLES, vas, new int[] { 3,3,4,2 });
	}
	@Override
	public int getVertexCount() { return vc; }
	
	public FloatBuffer getBuffer() {
		if(mVertexBuffer == null)
			throw new IllegalArgumentException("load() was not called");
		return mVertexBuffer;
	}

	@Override
	protected void internalLoad(ResourceLoader rl, Services svc) {
		if (vas != null && mVertexBuffer == null) {
			// did not receive the buffer in ctor, load it.
			final ByteBuffer byteBuf = ByteBuffer.allocateDirect(vas.length*FLOAT_BYTES);
			byteBuf.order(ByteOrder.nativeOrder());
			mVertexBuffer = byteBuf.asFloatBuffer();
			mVertexBuffer.put(vas);
			mVertexBuffer.position(0);
		}
	}

	@Override
	public void render(Shader sx, Properties px) {
		int offset = 0;
		if(elems[IX_POSITION] > 0) {
			mVertexBuffer.position(offset);
			sx.vertex(mVertexBuffer, elems[IX_POSITION], stride);
			offset += elems[IX_POSITION];
		}
		if(elems[IX_NORMAL] > 0) {
			mVertexBuffer.position(offset);
			sx.normal(mVertexBuffer, elems[IX_NORMAL], stride);
			offset += elems[IX_NORMAL];
		}
		if(elems[IX_COLOR] > 0) {
			mVertexBuffer.position(offset);
			sx.color(mVertexBuffer, elems[IX_COLOR], stride);
			offset += elems[IX_COLOR];
		}
		if(elems[IX_TEXTURE] > 0) {
			mVertexBuffer.position(offset);
			sx.texture(mVertexBuffer, elems[IX_TEXTURE], stride);
		}
		mVertexBuffer.position(0);
		GLES20.glDrawArrays(elemType, 0, vc);
	}
}
