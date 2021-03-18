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
 * Core implementation for indexed list of vertices.
 * Each vertex attribute is stored in a separate (parallel) buffer.
 * Index list is 8-bit (256 vertices max).
 * 
 * @author escape-llc
 * 
 */
public abstract class IndexedVertexGeometry extends Geometry {
	final int elemType;
	final float[] vertices;
	final float[] normals;
	final float[] texcoords;
	final int indexType;
	final byte[] indices;
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private FloatBuffer mTexcoordBuffer;
	private ByteBuffer mIndexBuffer;

	/**
	 * Ctor.
	 * 
	 * @param vertices
	 *            array of vertex data (xyz)
	 * @param indices
	 *            array of index data (byte).
	 */
	protected IndexedVertexGeometry(float[] vertices, byte[] indices) {
		this(GLES20.GL_TRIANGLES, vertices, null, null, indices);
	}
	/**
	 * Ctor.
	 * @param vertices array of vertex data (xyz).
	 * @param normals array of normal data (xyz).
	 * @param indices array of index data (byte).
	 */
	protected IndexedVertexGeometry(float[] vertices, float[] normals, byte[] indices) {
		this(GLES20.GL_TRIANGLES, vertices, normals, null, indices);
	}
	/**
	 * Ctor.
	 * @param etype GL element type (e.g. GL_TRIANGLES).
	 * @param vertices array of vertex data (xyz).
	 * @param normals array of normal data (xyz).
	 * @param texcoords array of texture coordinates (xy).
	 * @param indices array of index data (byte).
	 */
	protected IndexedVertexGeometry(int etype, float[] vertices, float[] normals, float[] texcoords, byte[] indices) {
		if(vertices.length/3 > 256)
			throw new IllegalArgumentException("Too many vertices for BYTE index");
		if(normals != null && vertices.length != normals.length)
			throw new IllegalArgumentException("Vertex/Normal length mismatch");
		if(texcoords != null && vertices.length/3 != texcoords.length/2)
			throw new IllegalArgumentException("Vertex/Texcoord length mismatch");
		this.vertices = vertices;
		this.indices = indices;
		this.normals = normals;
		this.texcoords = texcoords;
		this.indexType = GLES20.GL_UNSIGNED_BYTE;
		this.elemType = etype;
	}

	@Override
	protected void internalLoad(ResourceLoader rl, Services svc) {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * FLOAT_BYTES);
		byteBuf.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuf.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		
		if(normals != null) {
			byteBuf = ByteBuffer.allocateDirect(normals.length * FLOAT_BYTES);
			byteBuf.order(ByteOrder.nativeOrder());
			mNormalBuffer = byteBuf.asFloatBuffer();
			mNormalBuffer.put(normals);
			mNormalBuffer.position(0);
		}
		
		if(texcoords != null) {
			byteBuf = ByteBuffer.allocateDirect(texcoords.length * FLOAT_BYTES);
			byteBuf.order(ByteOrder.nativeOrder());
			mTexcoordBuffer = byteBuf.asFloatBuffer();
			mTexcoordBuffer.put(texcoords);
			mTexcoordBuffer.position(0);
		}

		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}
	
	@Override
	public int getVertexCount() { return vertices.length/3; }

	@Override
	public void render(Shader sx, Properties arg1) {
		sx.vertex3d(mVertexBuffer);
		if(mNormalBuffer != null) {
			sx.normal3d(mNormalBuffer);
		}
		if(mTexcoordBuffer != null) {
			sx.texture2d(mTexcoordBuffer);
		}
		GLES20.glDrawElements(elemType, indices.length, indexType, mIndexBuffer);
	}
}
