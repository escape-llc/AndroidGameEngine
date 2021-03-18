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
package com.escape.games.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Staging area for float coordinates; base implementation.
 * "Element" refers to a 2/3/4-tuple.
 * Just Say No to auto-boxing!
 * @author escape-llc
 *
 */
public abstract class FloatPageImpl {
	protected final int stride;
	protected final int expandBy;
	protected float[] buffer;
	protected int current;
	/**
	 * Ctor.
	 * Use for single attribute buffer.
	 * @param stride Number of floats per element.  Must be in [2..4].
	 * @param cap Initial number of elements. Must be GT zero.
	 * @param xby Expand by elements.  Must be GT zero.
	 */
	protected FloatPageImpl(int stride, int cap, int xby) {
		if(stride < 2 || stride > 4) throw new IllegalArgumentException("stride");
		if(cap <= 0) throw new IllegalArgumentException("cap");
		if(xby <= 0) throw new IllegalArgumentException("xby");
		this.stride = stride;
		this.expandBy = xby;
		buffer = new float[cap*stride];
	}
	/**
	 * Ctor.
	 * Use for interleaved attribute buffer.
	 * Computes stride from attribute element array.
	 * @param elems Array of 4 attribute element counts: Position/Normal/Color/Texture.  Each element must be in [2..4].
	 * @param cap Initial number of elements. Must be GT zero.
	 * @param xby Expand by elements.  Must be GT zero.
	 */
	protected FloatPageImpl(int[] elems, int cap, int xby) {
		if(elems.length != 4) throw new IllegalArgumentException("elems.length != 4");
		if(cap <= 0) throw new IllegalArgumentException("cap");
		if(xby <= 0) throw new IllegalArgumentException("xby");
		this.expandBy = xby;
		if(elems[0] < 2 || elems[0] > 4) throw new IllegalArgumentException("elems[0]");
		int stx = elems[0];
		for(int ix = 1; ix < elems.length; ix++) {
			if(elems[ix] != 0 && (elems[ix] < 2 || elems[ix] > 4)) throw new IllegalArgumentException("elems[ix] " + ix);
			stx += elems[ix];
		}
		stride = stx;
		buffer = new float[stride*cap];
	}
	/**
	 * Expand the array.
	 * @param xby Number of additional elements.
	 */
	void resize(int xby) { buffer = Arrays.copyOf(buffer, buffer.length + (xby*stride)); }
	/**
	 * Return the number of populated floats (multiplied by stride).
	 * @return current times stride.
	 */
	public int length() { return current*stride; }
	/**
	 * Return the number of populated elements.
	 * @return current tuple count.
	 */
	public int count() { return current; }
	/**
	 * Return the offset of first component of given index.
	 * @param idx zero-relative index.
	 * @return Array offset.
	 */
	public int offsetFor(int idx) { return idx*stride; }
	/**
	 * Copy the given elements (stride floats) to given buffer.
	 * @param idx Source zero-relative index.
	 * @param target target buffer.
	 * @param offset target offset.
	 */
	public void copyTo(int idx, float[] target, int offset) {
		System.arraycopy(buffer, offsetFor(idx), target, offset, stride);
	}
	/**
	 * Make a direct-IO buffer from the underlying array.
	 * @return new buffer.
	 */
	public FloatBuffer makeBuffer() {
		final FloatBuffer vb = ByteBuffer.allocateDirect(length() * Geometry.FLOAT_BYTES)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		vb.put(buffer, 0, length());
		vb.position(0);
		return vb;
	}
}
