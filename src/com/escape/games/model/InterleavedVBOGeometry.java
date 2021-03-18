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

import android.opengl.GLES20;

import com.escape.games.api.Properties;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.resource.Shader;
import com.escape.games.resource.VertexBufferObject;

/**
 * Interleaved Geometry bound to VBO.
 * @author escape-llc
 *
 */
public class InterleavedVBOGeometry extends Geometry {
	VertexBufferObject vbo;
	final InterleavedVertexGeometry ivg;
	public InterleavedVBOGeometry(InterleavedVertexGeometry ivg) {
		this.ivg = ivg;
	}

	@Override
	protected void internalLoad(ResourceLoader rl, Services svc) {
		vbo = rl.createBuffer(ivg);
	}

	@Override
	public void render(Shader sx, Properties px) {
		if(vbo == null) return;
		vbo.setup();
		final int[] elems = ivg.elems;
		int offset = 0;
		if(elems[InterleavedVertexGeometry.IX_POSITION] > 0) {
			sx.vertex(offset, elems[InterleavedVertexGeometry.IX_POSITION], ivg.stride);
			offset += elems[InterleavedVertexGeometry.IX_POSITION];
		}
		if(elems[InterleavedVertexGeometry.IX_NORMAL] > 0) {
			sx.normal(offset, elems[InterleavedVertexGeometry.IX_NORMAL], ivg.stride);
			offset += elems[InterleavedVertexGeometry.IX_NORMAL];
		}
		if(elems[InterleavedVertexGeometry.IX_COLOR] > 0) {
			sx.color(offset, elems[InterleavedVertexGeometry.IX_COLOR], ivg.stride);
			offset += elems[InterleavedVertexGeometry.IX_COLOR];
		}
		if(elems[InterleavedVertexGeometry.IX_TEXTURE] > 0) {
			sx.texture(offset, elems[InterleavedVertexGeometry.IX_TEXTURE], ivg.stride);
		}
		GLES20.glDrawArrays(ivg.elemType, 0, ivg.vc);
		vbo.teardown();
	}

	@Override
	public int getVertexCount() {
		return ivg.getVertexCount();
	}
}
