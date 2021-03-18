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

import android.opengl.GLES20;

import com.escape.games.api.Properties;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.model.Geometry;
import com.escape.games.model.PerVertexMaterial;
import com.escape.games.resource.Shader;

/**
 * XYZ Axes 100-units long.  Tick at 10 units.  Tick is one unit long either side of axis.
 * X-axis=red, Y-axis=green, Z-axis=blue
 * Positive axis is bright, Negative axis is dim.
 * Same colors used in Blender, however the axes are oriented differently between Blender and GL!
 * Blender uses Z-up and GL uses Y-up, so expect to see everything "rotated" with default transforms.
 * @author escape-llc
 *
 */
public class AxisGeometry extends Geometry {
	private FloatBuffer mVertexBuffer;
	static float TICK_DIST = 10;
	static float TICK_LENGTH = 1;
	static int TICK_COUNT = 8;
	static int elementCount = 12 + TICK_COUNT;
	static float[] data = new float[] {
		-100,0,0, 0,0,0, 100,0,0, 0,0,0,
		0,-100,0, 0,0,0, 0,100,0, 0,0,0,
		0,0,-100, 0,0,0, 0,0,100, 0,0,0,
		-TICK_DIST,TICK_LENGTH,0, -TICK_DIST,-TICK_LENGTH,0, TICK_DIST,TICK_LENGTH,0, TICK_DIST,-TICK_LENGTH,0,
		-TICK_LENGTH,-TICK_DIST,0, TICK_LENGTH,-TICK_DIST,0, -TICK_LENGTH,TICK_DIST,0, TICK_LENGTH,TICK_DIST,0,
	};
	/**
	 * Material that goes with AxisGeometry.
	 * @author escape-llc
	 *
	 */
	public static final class AxisMaterial extends PerVertexMaterial {
		public AxisMaterial() {
			super();
		}
		@Override
		protected FloatBuffer createBuffer(ResourceLoader rl, Services svc) {
			final ByteBuffer cbb = ByteBuffer.allocateDirect((4 * elementCount) * (Integer.SIZE / Byte.SIZE));
			cbb.order(ByteOrder.nativeOrder());
			final FloatBuffer cb = cbb.asFloatBuffer();
			int cx = 11*4;
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cx = 4*4;
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cx = 9*4;
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cx = 2*4;
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cx = 8*4;
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cx = 1*4;
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cb.put(float_colors[cx + 0]);
			cb.put(float_colors[cx + 1]);
			cb.put(float_colors[cx + 2]);
			cb.put(float_colors[cx + 3]);
			cx = 7*4;
			for(int tx = 0; tx < TICK_COUNT; tx++) {
				cb.put(float_colors[cx + 0]);
				cb.put(float_colors[cx + 1]);
				cb.put(float_colors[cx + 2]);
				cb.put(float_colors[cx + 3]);
			}
			cb.position(0);
			return cb;
		}
	}
	@Override
	public int getVertexCount() { return elementCount; }
	@Override
	public void internalLoad(ResourceLoader rl, Services svc) {
		final ByteBuffer vbb = ByteBuffer.allocateDirect(data.length * (Float.SIZE / Byte.SIZE));
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(data);
		mVertexBuffer.position(0);
	}
	@Override
	public void render(Shader sx, Properties px) {
		// position
		sx.vertex3d(mVertexBuffer);
		// draw
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, elementCount);
	}
}
