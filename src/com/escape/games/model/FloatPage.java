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

/**
 * Staging area for a single vertex attribute.
 * Attribute can be 2-tuple, 3-tuple, or 4-tuple, enforced on ctor.
 * You must use the corresponding putXd() to add data.
 * @author escape-llc
 *
 */
public class FloatPage extends FloatPageImpl {
	/**
	 * Ctor.
	 * @param stride Number of floats per element.  Must be in [2..4].
	 * @param cap Initial number of elements. Must be GT zero.
	 * @param xby Expand by elements.  Must be GT zero.
	 */
	public FloatPage(int stride, int cap, int xby) {
		super(stride, cap, xby);
	}
	/**
	 * Add entry of stride 2.
	 * @param f0
	 * @param f1
	 * @throws IllegalArgumentException stride must be 2.
	 */
	public void put2d(float f0, float f1) {
		if(2 != stride) throw new IllegalArgumentException("stride must be 2");
		final int ofx = current*stride;
		if(ofx + 1 >= buffer.length) resize(expandBy);
		buffer[ofx + 0] = f0;
		buffer[ofx + 1] = f1;
		current++;
	}
	/**
	 * Add entry of stride 3.
	 * @param f0
	 * @param f1
	 * @param f2
	 * @throws IllegalArgumentException stride must be 3.
	 */
	public void put3d(float f0, float f1, float f2) {
		if(3 != stride) throw new IllegalArgumentException("stride must be 3");
		final int ofx = current*stride;
		if(ofx + 2 >= buffer.length) resize(expandBy);
		buffer[ofx + 0] = f0;
		buffer[ofx + 1] = f1;
		buffer[ofx + 2] = f2;
		current++;
	}
	/**
	 * Add entry of stride 4.
	 * @param f0
	 * @param f1
	 * @param f2
	 * @param f3
	 * @throws IllegalArgumentException stride must be 4.
	 */
	public void put4d(float f0, float f1, float f2, float f3) {
		if(4 != stride) throw new IllegalArgumentException("stride must be 4");
		final int ofx = current*stride;
		if(ofx + 3 >= buffer.length) resize(expandBy);
		buffer[ofx + 0] = f0;
		buffer[ofx + 1] = f1;
		buffer[ofx + 2] = f2;
		buffer[ofx + 3] = f3;
		current++;
	}
	/**
	 * Overwrite entry of stride 3.  The index must already be allocated.
	 * @param idx zero-relative index.
	 * @param f0
	 * @param f1
	 * @param f2
	 * @throws IllegalArgumentException index IDX not allocated.
	 */
	public void update3d(int idx, float f0, float f1, float f2) {
		if(3 != stride) throw new IllegalArgumentException("stride must be 3");
		final int ofx = idx*stride;
		if(ofx + 2 >= buffer.length) throw new IllegalArgumentException("Index not allocated: " + idx);
		buffer[ofx + 0] = f0;
		buffer[ofx + 1] = f1;
		buffer[ofx + 2] = f2;
		current++;
	}
}
