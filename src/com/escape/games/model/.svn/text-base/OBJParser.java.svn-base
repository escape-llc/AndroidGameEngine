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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.opengl.GLES20;

/**
 * OBJ file parser.
 * Creates Interleaved Geometry.
 * OBJ file format:
 * <ul>
 * 		<li>list of vertices: <kbd>v x y z</kbd></li>
 * 		<li>list of tex coords: <kbd>vt u v</kbd></li>
 * 		<li>list of normals: <kbd>vn x y z</kbd></li>
 * 		<li>list of faces: <kbd>f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3</kbd></li>
 * </ul>
 * Parsing options (OPTION_*):
 * <ul>
 * 		<li><kbd>IGNORE_NORMAL</kbd> ignore normals if present</li>
 * 		<li><kbd>IGNORE_TEXTURE</kbd> ignore texture coords if present</li>
 * 		<li><kbd>TEXTURE_INVERT[UV]</kbd>	invert the texture coord (1-uv)</li>
 * </ul>
 * @author escape-llc
 *
 */
public class OBJParser {
	static final String TAG = "OBJ2";
	static final String VERTEX = "v";
	static final String NORMAL = "vn";
	static final String TEXTURE = "vt";
	static final String FACE = "f";
	/** parse option: ignore vertex normals */
	public static final int OPTION_IGNORE_NORMAL = (1<<0);
	/** parse option: ignore texture coords */
	public static final int OPTION_IGNORE_TEXTURE = (1<<1);
	/** texture option: invert U (1-u) */
	public static final int OPTION_TEXTURE_INVERTU = (1<<2);
	/** texture option: invert V (1-v) */
	public static final int OPTION_TEXTURE_INVERTV = (1<<3);
	private InterleavedPage mainBuffer;
	private final int capacity;
	private final int expandBy;
	private final int options;
	/**
	 * Ctor.
	 * @param capacity Initial capacity for buffers.
	 * @param expandBy Expand capacity for buffers.
	 * @param options Parsing options.
	 */
	public OBJParser(int capacity, int expandBy, int options) {
		this.capacity = capacity;
		this.expandBy = expandBy;
		this.options = options;
	}
	
	/**
	 * Return the geometry created by the parse.
	 * @return New geometry instance.
	 * @throws IllegalArgumentException buffer was not created by parse().
	 */
	public InterleavedVertexGeometry vertexAttributes() {
		if(mainBuffer == null) throw new IllegalArgumentException("parse() did not create data");
		return mainBuffer.createGeometry();
	}
	public int getElementType() { return GLES20.GL_TRIANGLES; }
	/**
	 * Parse the OBJ stream and populate buffers.
	 * @param is
	 * @throws Exception
	 */
	public void parse(InputStream is) throws Exception {
		final BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			load(in);
		} finally {
			try {
				in.close();
			} catch (IOException ioe) {
			}
		}
	}
	/**
	 * Return the index of whitespace character or one-past end-of-string.
	 * To repeat the call, add one to the return value of previous call, or zero for initial.
	 * @param line Source string.
	 * @param startat Starting index.
	 * @return Ending index. Points to either whitespace or one-past end-of-string.
	 */
	int advance(String line, int startat) {
		int endat = startat;
		while(endat < line.length() && !Character.isWhitespace(line.charAt(endat))) {
			endat++;
		}
		return endat;
	}
	/**
	 * Same as advance() but with specific delimiter.
	 * @param line Source string.
	 * @param delim Delimiter to search for.
	 * @param startat Starting index.
	 * @return Ending index. Points to either DELIM or one-past end-of-string.
	 */
	int advance(String line, char delim, int startat) {
		int endat = startat;
		while(endat < line.length() && delim != line.charAt(endat)) {
			endat++;
		}
		return endat;
	}
	/**
	 * Parse a float using return and parameters from advance().
	 * int endat = advance(line, startat);
	 * final float vx = parse(line, startat, endat);
	 * if(endat >= line.length()) newlinetime();
	 * startat = endat + 1;
	 * endat = advance(line, startat);
	 * thenextone();
	 * ...
	 * @param line Source string.
	 * @param startat Starting index.
	 * @param endat Ending index.
	 * @return
	 */
	float parse(String line, int startat, int endat) {
		return Float.parseFloat(line.substring(startat, endat));
	}
	/**
	 * Parse integer version.
	 * @param line Source string.
	 * @param startat Starting index.
	 * @param endat Ending index.
	 * @return
	 */
	int parseInt(String line, int startat, int endat) {
		return Integer.parseInt(line.substring(startat, endat));
	}
	/**
	 * Parse 3 floats and do put3d().
	 * @param fp Target page.
	 * @param line Source line.
	 * @param start Source start position (first char of first float).
	 */
	void parse3d(FloatPage fp, String line, int start) {
		int startat = start;
		int endat = advance(line, startat);
		final float f0 = parse(line, startat, endat);
		startat = endat + 1;
		endat = advance(line, startat);
		final float f1 = parse(line, startat, endat);
		startat = endat + 1;
		endat = advance(line, startat);
		final float f2 = parse(line, startat, endat);
		fp.put3d(f0, f1, f2);
	}
	/**
	 * @param in Input source.
	 * @throws Exception
	 */
	void load(BufferedReader in) throws Exception {
		final FloatPage vertices = new FloatPage(3, capacity, expandBy);
		final FloatPage normals = (options & OPTION_IGNORE_NORMAL) == 0 ? new FloatPage(3, capacity, expandBy) : null;
		final FloatPage tcs = (options & OPTION_IGNORE_TEXTURE) == 0 ? new FloatPage(2, capacity, expandBy) : null;
		final String facelist[] = new String[4];
		final int[] indices = new int[4];
		// these get established on the first face
		FloatPage[] pages = null;
		int[] elems = null;
		InterleavedPage ip = null;
		String line = in.readLine();
		while(line != null) {
			int endat = advance(line, 0);
			final String type = line.substring(0, endat);
			if (type.equals(VERTEX)) {
				parse3d(vertices, line, endat + 1);
			}
			else if (type.equals(TEXTURE)) {
				if(tcs == null) continue;
				int startat = endat + 1;
				endat = advance(line, startat);
				final float f0 = parse(line, startat, endat);
				startat = endat + 1;
				endat = advance(line, startat);
				final float f1 = parse(line, startat, endat);
				tcs.put2d((options & OPTION_TEXTURE_INVERTU) == 0 ? f0 : 1f - f0, (options & OPTION_TEXTURE_INVERTV) == 0 ? f1 : 1f - f1);
			}
			else if (type.equals(NORMAL)) {
				if(normals == null) continue;
				parse3d(normals, line, endat + 1);
			}
			else if (type.equals(FACE)) {
				if(ip == null) {
					// initialize for final staging
					pages = new FloatPage[] {
						vertices,
						normals != null && normals.count() > 0 ? normals : null,
						null,
						tcs != null && tcs.count() > 0 ? tcs : null
					};
					elems = new int[] {
						3,
						pages[InterleavedVertexGeometry.IX_NORMAL] != null && pages[InterleavedVertexGeometry.IX_NORMAL].count() > 0 ? 3 : 0,
						pages[InterleavedVertexGeometry.IX_COLOR] != null && pages[InterleavedVertexGeometry.IX_COLOR].count() > 0 ? 4 : 0,
						pages[InterleavedVertexGeometry.IX_TEXTURE] != null && pages[InterleavedVertexGeometry.IX_TEXTURE].count() > 0 ? 2 : 0
					};
					ip = new InterleavedPage(elems, vertices.count()*3, vertices.count());
				}
				// read faces
				int facecount = 0;
				int startat = endat + 1;
				while(startat < line.length() && facecount < facelist.length) {
					final int fendat = advance(line, startat);
					final String face0 = line.substring(startat, fendat);
					facelist[facecount++] = face0;
					startat = fendat + 1;
				}
				// Each line: f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
				for (int ix = 0; ix < facecount; ix++) {
					final String face = facelist[ix];
					int fstartat = 0;
					int ixc = 0;
					while(fstartat < face.length()) {
						final int fendat = advance(face, '/', fstartat);
						if (startat != endat) {
							// non-empty string
							switch (ixc) {
							case 0:
								indices[InterleavedVertexGeometry.IX_POSITION] = parseInt(face, fstartat, fendat) - 1;
								break;
							case 1:
								indices[InterleavedVertexGeometry.IX_TEXTURE] = parseInt(face, fstartat, fendat) - 1;
								break;
							case 2:
								indices[InterleavedVertexGeometry.IX_NORMAL] = parseInt(face, fstartat, fendat) - 1;
								break;
							}
						}
						ixc++;
						fstartat = fendat + 1;
					}
					indices[InterleavedVertexGeometry.IX_COLOR] = 0;
					// Add vertex attributes
					ip.put(indices, pages);
				}
			}
			// next line
			line = in.readLine();
		}
		// set the output buffer
		mainBuffer = ip;
	}

	/**
	 * Compute face normal of the I'th face.
	 * 
	 * @param vertices Source List of vertices.
	 * @param normals Target List of normals.
	 * @param ix the index of the face.
	 * @param firstV first vertex of the triangle.
	 * @param secondV second vertex of the triangle.
	 * @param thirdV third vertex of the triangle.
	 */
	@SuppressWarnings("unused")
	private void setFaceNormal(FloatPage vertices, FloatPage normals, int ix, int firstV, int secondV, int thirdV) {
		// get coordinates of all the vertices
		final float[] v1v2v3 = new float[9];
		vertices.copyTo(firstV, v1v2v3, 0);
		vertices.copyTo(secondV, v1v2v3, 3);
		vertices.copyTo(thirdV, v1v2v3, 6);
		
		// calculate the cross product of v1-v2 and v3-v2
		final float v1v2[] = { v1v2v3[0] - v1v2v3[3+0], v1v2v3[1] - v1v2v3[3+1], v1v2v3[2] - v1v2v3[3+2] };
		final float v3v2[] = { v1v2v3[6+0] - v1v2v3[3+0], v1v2v3[6+1] - v1v2v3[3+1], v1v2v3[6+2] - v1v2v3[3+2] };

		// Log.d("V1V2: ", v1v2[0] + "," + v1v2[1] + "," + v1v2[2]);
		// Log.d("V3V2: ", v3v2[0] + "," + v3v2[1] + "," + v3v2[2]);
		final float cp[] = new float[3];
		Geometry.crossProduct(cp, 0, v1v2, 0, v3v2, 0);
		Geometry.normalize(cp, 0);

		// set the face normal
		/*
		_faceNormals[ix * 3] = cp[0];
		_faceNormals[ix * 3 + 1] = cp[1];
		_faceNormals[ix * 3 + 2] = cp[2];
		*/
		// Log.d("NORMAL:", cp[0] + "," + cp[1] + "," + cp[2]);

		// Setup for vertex normal construction;
		/*
		 * _normals[firstV * 3] += _faceNormals[i * 3]; _normals[firstV * 3 + 1]
		 * += _faceNormals[i * 3 + 1]; _normals[firstV * 3 + 2] +=
		 * _faceNormals[i * 3 + 2];
		 * 
		 * _normals[secondV * 3] += _faceNormals[i * 3]; _normals[secondV * 3 +
		 * 1] += _faceNormals[i * 3 + 1]; _normals[secondV * 3 + 2] +=
		 * _faceNormals[i * 3 + 2];
		 * 
		 * _normals[thirdV * 3] += _faceNormals[i * 3]; _normals[thirdV * 3 + 1]
		 * += _faceNormals[i * 3 + 1]; _normals[thirdV * 3 + 2] +=
		 * _faceNormals[i * 3 + 2];
		 */
		normals.update3d(firstV, cp[0], cp[1], cp[2]);
		normals.update3d(secondV, cp[0], cp[1], cp[2]);
		normals.update3d(thirdV, cp[0], cp[1], cp[2]);
	}
}
