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
 * Staging area for final interleaved vertex attribute array.
 * Position(3d), Normal(3d), Color(4d), Texture(2d).
 * @author escape-llc
 *
 */
public class InterleavedPage extends FloatPageImpl {
	private final int[] elems;
	/**
	 * Ctor.
	 * Position(3d), Normal(3d), Color(4d), Texture(2d).
	 * @param elems Stride counts (see InterleavedVertexGeometry).
	 * @param cap Initial capacity in elements.
	 */
	public InterleavedPage(int[] elems, int cap, int xby) {
		super(elems, cap, xby);
		this.elems = elems;
	}
	/**
	 * Create the interleaved geometry buffer based on current state.
	 * @return New geometry instance.
	 */
	public InterleavedVertexGeometry createGeometry() {
		return new InterleavedVertexGeometry(makeBuffer(), elems);
	}
	/**
	 * Use the elems[] array to guide population of interleaved data.
	 * Position(3d), Normal(3d), Color(4d), Texture(2d).
	 * Pass NULL for entries not supplied; corresponding elems[] must be ZERO!
	 * @param idxs Array of page source indices.
	 * @param fps Array of float pages.
	 * @throws IllegalArgumentException all parameter lengths must match ELEMS.length.
	 */
	public void put(int[] idxs, FloatPage[] fps) {
		if(idxs.length != elems.length) throw new IllegalArgumentException("idxs.length != elems.length");
		if(fps.length != elems.length) throw new IllegalArgumentException("fps.length != elems.length");
		final int ofx = offsetFor(current);
		if(ofx + stride > buffer.length) resize(expandBy);
		int iofx = ofx;
		for(int ix = 0; ix < elems.length; ix++) {
			if(elems[ix] != 0) {
				fps[ix].copyTo(idxs[ix], buffer, iofx);
				iofx += elems[ix];
			}
		}
		current++;
	}
}
