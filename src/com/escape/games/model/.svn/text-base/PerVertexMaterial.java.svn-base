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

import com.escape.games.api.RequireResourceLoader;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.resource.Shader;

/**
 * Material having one color per vertex.
 * @author escape-llc
 *
 */
public class PerVertexMaterial extends Material implements RequireResourceLoader {
	final Geometry.Vec4[] colors;
	final float[] table;
	int vc;
	private FloatBuffer mColorBuffer;
	protected PerVertexMaterial() { colors = null; table = null; }
	/**
	 * Rotate through the given list of colors for each vertex specified.
	 * @param colors List of vec4 to cycle through.
	 */
	public PerVertexMaterial(Geometry.Vec4[] colors) {
		if(colors == null)
			throw new IllegalArgumentException("colors");
		this.colors = colors;
		this.table = null;
	}
	/**
	 * The given array is the table of vertex colors.
	 * @param colors one vec4 for each vertex.
	 */
	public PerVertexMaterial(float[] colors) {
		if(colors == null)
			throw new IllegalArgumentException("colors");
		this.colors = null;
		this.table = colors;
	}
	/**
	 * Make every vertex specified the given color.
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 */
	public PerVertexMaterial(float r, float g, float b, float a) {
		this.colors = new Geometry.Vec4[] { new Geometry.Vec4(r,g,b,a) };
		this.table = null;
	}
	public void setVertexCount(int vc) { this.vc = vc; }
	protected FloatBuffer createBuffer(ResourceLoader rl, Services svc) {
		if(colors != null)
			return Geometry.colorVertices(vc, colors);
		else {
			if(table.length != vc*4)
				throw new IllegalArgumentException("Vertex count mismatch with color table: expected " + vc*4 + " elements");
			final ByteBuffer byteBuf = ByteBuffer.allocateDirect(table.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			final FloatBuffer cb = byteBuf.asFloatBuffer();
			cb.put(table);
			cb.position(0);
			return cb;
		}
	}
	@Override
	public void setup(Shader sh) {
		if(mColorBuffer != null)
			sh.color4d(mColorBuffer);
	}
	public void load(ResourceLoader rl, Services svc) {
		if(vc > 0) {
			mColorBuffer = createBuffer(rl, svc);
		}
		else
			throw new IllegalArgumentException("did not set vertex count");
	}
	@Override
	public String getShaderKey() { return Shader.COLORPERVERTEX; }
}
