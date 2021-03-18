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

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.escape.games.api.Properties;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.resource.Shader;

/**
 * Geometry based on OBJ file resource.
 * @author escape-llc
 *
 */
public class OBJModelGeometry extends Geometry {
	/* OBJ model resource ID */
	final int resid;
	/* OBJ parse options */
	final int options;
	int capacity;
	int extendBy;
	/* Vertex Attribute Buffer */
	private InterleavedVertexGeometry vertexBuffer;
	/**
	 * Ctor.
	 * Sets capacity=100 and extendBy=20.
	 * @param resid Resource ID of OBJ model.
	 * @param options Parser options.
	 */
	public OBJModelGeometry(int resid, int options) {
		this.resid = resid;
		this.options = options;
		capacity = 100;
		extendBy = 20;
	}
	/**
	 * Ctor.
	 * Uses default parser options.
	 * @param resid Resource ID of OBJ model.
	 */
	public OBJModelGeometry(int resid) {
		this(resid, 0);
	}
	/**
	 * Get the OBJ Parser initial capacity.
	 * Must SET before install pipeline!
	 * @return Current value.
	 */
	public int getCapacity() { return capacity; }
	/**
	 * Set the OBJ Parser initial capacity.
	 * Must call before install pipeline!
	 * @param capacity New value.
	 */
	public void setCapacity(int capacity) { this.capacity = capacity; }
	/**
	 * Get the OBJ parser extend-by.
	 * Must SET before install pipeline!
	 * @return Current value.
	 */
	public int getExtendBy() { return extendBy; }
	/**
	 * Set the OBJ Parser extend-by.
	 * Must call before install pipeline!
	 * @param extendBy New value.
	 */
	public void setExtendBy(int extendBy) { this.extendBy = extendBy; }
	@Override
	protected void internalLoad(ResourceLoader rl, Services svc) {
		final InputStream is = rl.open(resid);
		try {
			final OBJParser op = new OBJParser(capacity, extendBy, options);
			op.parse(is);
			vertexBuffer = op.vertexAttributes();
			// allocate the VBA
			vertexBuffer.load(rl, svc);
		} catch (Exception e) {
			Log.e("OMG", "op.parse", e);
		}
		finally {
			try {
				is.close();
			} catch (IOException e) {
				// eat it
			}
		}
	}
	@Override
	public int getVertexCount() {
		return vertexBuffer != null ? vertexBuffer.getVertexCount() : 0;
	}
	@Override
	public void render(Shader sx, Properties px) {
		if(vertexBuffer != null) {
			vertexBuffer.render(sx, px);
		}
	}
}
