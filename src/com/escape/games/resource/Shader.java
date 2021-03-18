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
package com.escape.games.resource;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import com.escape.games.core.TraceSwitches;
import com.escape.games.model.Geometry;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Implementation for GL 2.x shader program.
 * System-defined shaders use a pre-defined set of location names; use these in externally-defined shaders for compatibility
 * with existing materials and geometries.
 * @author escape-llc
 *
 */
public class Shader implements GLResource {
	static final String TAG = "Shader";
	/** solid-color shader */
	public static final String BASIC = "basic";
	/** color-per-vertex shader */
	public static final String COLORPERVERTEX = "cpv";
	/** texture-mapped shader */
	public static final String TEXTURE = "tex";
	/** texture-mapped multi-texture mix shader */
	public static final String TEXTURETEXTURE = "textex";
	/** light model per vertex shader */
	public static final String LIGHTPERVERTEX = "lpv";
	/** light model per fragment shader */
	public static final String LIGHTPERFRAGMENT = "lpf";
	// shader location names
	/** attribute vec3 aPosition (note that shaders want it as VEC4) */
	public static final String SV_POSITION = "aPosition";
	/** attribute vec3 aNormal */
	public static final String SV_NORMAL = "aNormal";
	/** attribute vec4 aColor */
	public static final String SV_COLOR = "aColor";
	/** attribute vec2 aTextureCoord */
	public static final String SV_TEXTURE_COORD = "aTextureCoord";
	/** uniform vec4 uColor */
	public static final String SV_UCOLOR = "uColor";
	/** uniform mat4 uMVPMatrix */
	public static final String SV_MATRIX_MVP = "uMVPMatrix";
	/** uniform mat4 uMVMatrix */
	public static final String SV_MATRIX_MV = "uMVMatrix";
	/** uniform sampler2D uTexture */
	public static final String SV_UTEXTURE = "uTexture";
	/** uniform sampler2D uTexture #2 */
	public static final String SV_UTEXTURE2 = "uTexture2";
	/** uniform vec3 uLightPos */
	public static final String SV_ULIGHTPOSITION = "uLightPos";
	/** uniform float uRatio */
	public static final String SV_URATIO = "uRatio";
	static final String[] ALL_ATTRIBUTES = {
		SV_POSITION, SV_NORMAL, SV_COLOR, SV_TEXTURE_COORD
	};
	static final String[] ALL_UNIFORMS = {
		SV_UCOLOR, SV_MATRIX_MV, SV_MATRIX_MVP, SV_UTEXTURE, SV_UTEXTURE2, SV_ULIGHTPOSITION, SV_URATIO
	};
	/**
	 * Configuration data for active locations.
	 * @author escape-llc
	 *
	 */
	static final class Location {
		public final String name;
		public final int handle;
		public boolean setup;
		public Location(String name, int handle) {
			this.name = name;
			this.handle = handle;
		}
	}
	// Vertex shader source
	final String vertex;
	// Fragment shader source
	final String fragment;
	// GL program id
	int program;
	int vid;
	int fid;
	// use for lookup
	final HashMap<String, Location> locs = new HashMap<String, Location>();
	// use for iteration
	final ArrayList<Location> itlocs = new ArrayList<Location>(10);
	// GL released state
	volatile boolean released;
	/**
	 * Ctor.
	 * @param vertex Vertex shader source.
	 * @param fragment fratment shader source.
	 */
	public Shader(String vertex, String fragment) {
		if(vertex == null)
			throw new IllegalArgumentException("vertex");
		if(fragment == null)
			throw new IllegalArgumentException("fragment");
		this.vertex = vertex;
		this.fragment = fragment;
	}
	/**
	 * Configure the given vertex attribute array.
	 * @param lx location handle.
	 * @param elems number of elements.
	 * @param stride array stride in bytes.
	 * @param fb Source buffer.
	 * @return true: value was set; false: not set.
	 */
	boolean vertexAttribArray(Location lx, int elems, int stride, FloatBuffer fb) {
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glEnableVertexAttribArray(lx.handle);
		GLES20.glVertexAttribPointer(lx.handle, elems, GLES20.GL_FLOAT, false, stride, fb);
		lx.setup = true;
		return true;
	}
	/**
	 * Configure the given VBO attribute.
	 * @param lx location handle.
	 * @param elems number of elements.
	 * @param stride array stride in bytes.
	 * @param offset Element offset.
	 * @return true: value was set; false: not set.
	 */
	boolean vertexAttribArray(Location lx, int elems, int stride, int offset) {
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glEnableVertexAttribArray(lx.handle);
		GLES20.glVertexAttribPointer(lx.handle, elems, GLES20.GL_FLOAT, false, stride, offset*Geometry.FLOAT_BYTES);
		lx.setup = true;
		return true;
	}
	/**
	 * Register "standard" attribute and uniform locations for later reference.
	 * Custom shaders should override this (and call super) if they use custom location names.
	 * @param pgx GL Program ID.
	 */
	protected void registerLocations(int pgx) {
		// attributes
		for(int ix = 0; ix < ALL_ATTRIBUTES.length; ix++) {
			final int lx = GLES20.glGetAttribLocation(pgx, ALL_ATTRIBUTES[ix]);
			if(lx != -1) {
				final Location llx = new Location(ALL_ATTRIBUTES[ix], lx);
				locs.put(llx.name, llx);
				itlocs.add(llx);
			}
		}
		// uniforms
		for (int ix = 0; ix < ALL_UNIFORMS.length; ix++) {
			final int lx = GLES20.glGetUniformLocation(pgx, ALL_UNIFORMS[ix]);
			if (lx != -1) {
				final Location llx = new Location(ALL_UNIFORMS[ix], lx);
				locs.put(llx.name, llx);
				itlocs.add(llx);
			}
		}
	}
	public Object preload(Context ctx) {
		return null;
	}
	/**
	 * Compile Vertex and Fragment shaders, link program, register locations.
	 */
	public void load(Object ctx) {
		final int vsi = loadShader(GLES20.GL_VERTEX_SHADER, vertex);
		if(vsi == 0) {
			Log.w(TAG, "Could not create vertex shader");
			return;
		}
		final int fsi = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment);
		if(fsi == 0) {
			Log.w(TAG, "Could not create fragment shader");
			if(vsi != 0) {
				GLES20.glDeleteShader(vsi);
			}
			return;
		}
		program = createProgram(vsi, fsi);
		if(program != 0) {
			vid = vsi;
			fid = fsi;
			registerLocations(program);
			released = false;
		}
		else {
			// it failed, program was deleted, now clean up shaders
			GLES20.glDeleteShader(vsi);
			GLES20.glDeleteShader(fsi);
			vid = 0;
			fid = 0;
		}
	}
	public void unload(Context ctx) {
		if(released) return;
		locs.clear();
		itlocs.clear();
		GLES20.glDeleteProgram(program);
		GLES20.glDeleteShader(fid);
		GLES20.glDeleteShader(vid);
		checkGlError("Shader.glDeleteProgram");
		program = -1;
		vid = 0;
		fid = 0;
		released = true;
	}
	public void release() {
		if(released) return;
		locs.clear();
		itlocs.clear();
		program = -1;
		vid = 0;
		fid = 0;
		released = true;
	}
	/**
	 * Select the shader program into GL context.
	 * Clear setup flags for glDisableVertexAttribArray() bookkeeping in teardown().
	 */
	public void setup() {
		if(released) return;
		for(int ix = 0; ix < itlocs.size(); ix++) {
			itlocs.get(ix).setup = false;
		}
		GLES20.glUseProgram(program);
		checkGlError("Shader.glUseProgram");
	}
	/**
	 * Clean up shader program after rendering is complete.
	 * Disables all vertex attribute arrays that have setup flag true.
	 */
	public void teardown() {
		if(released) return;
		for(int ix = 0; ix < itlocs.size(); ix++) {
			final Location lx = itlocs.get(ix);
			if(lx.setup) {
				GLES20.glDisableVertexAttribArray(lx.handle);
				lx.setup = false;
			}
		}
	}
	/**
	 * Return whether the given Location exists.
	 * Should be called after load() is successfully completed.
	 * Consults the local map.
	 * @param name location name.
	 * @return true: available; false: not available.
	 */
	public boolean query(String name) {
		if(released) return false;
		return locs.containsKey(name);
	}
	/**
	 * Return whether the given uniform exists.
	 * Queries the shader program directly.
	 * @param name uniform name.
	 * @return true: available; false: not available.
	 */
	public boolean queryUniform(String name) {
		if(released) return false;
		final int handle = GLES20.glGetUniformLocation(program, name);
		return handle != -1;
	}
	/**
	 * Return whether the given attribute exists.
	 * Queries the shader program directly.
	 * @param name attribute name.
	 * @return true: available; false: not available.
	 */
	public boolean queryAttribute(String name) {
		if(released) return false;
		final int handle = GLES20.glGetAttribLocation(program, name);
		return handle != -1;
	}
	/**
	 * Set the given uniform matrix.
	 * @param name Location name.
	 * @param mat4 Source matrix.
	 * @return true: value was set; false: not set.
	 */
	public boolean matrix4(String name, float[] mat4) {
		if(released) return false;
		final Location lx = locs.get(name);
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glUniformMatrix4fv(lx.handle, 1, false, mat4, 0);
		return true;
	}
	/**
	 * Set the given uniform vec3.
	 * @param name Location name.
	 * @param vec3 Source vec3.
	 * @return true: value was set; false: not set.
	 */
	public boolean uniform3d(String name, float[] vec3) {
		if(released) return false;
		final Location lx = locs.get(name);
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glUniform3fv(lx.handle, 1, vec3, 0);
		return true;
	}
	/**
	 * Set the given uniform float.
	 * @param name Location name.
	 * @param vx Source float.
	 * @return true: value was set; false: not set.
	 */
	public boolean uniform(String name, float vx) {
		if(released) return false;
		final Location lx = locs.get(name);
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glUniform1f(lx.handle, vx);
		return true;
	}
	/**
	 * Set the position vertex attribute array (SV_POSITION).
	 * Use with interleaved layout.
	 * @param fb Source buffer; use fb.position() to configure the offset.
	 * @param elems number of elements.
	 * @param stride array stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean vertex(FloatBuffer fb, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_POSITION), elems, stride, fb);
	}
	/**
	 * Set the position VBO attribute (SV_POSITION).
	 * Use with interleaved layout.
	 * @param offset Source buffer element offset.
	 * @param elems number of elements.
	 * @param stride array stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean vertex(int offset, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_POSITION), elems, stride, offset);
	}
	/**
	 * Set the position vertex attribute array (SV_POSITION).
	 * @param fb Source buffer.
	 * @return true: value was set; false: not set.
	 */
	public boolean vertex3d(FloatBuffer fb) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_POSITION), 3, 0, fb);
	}
	/**
	 * Set the vertex-normal vertex attribute array (SV_NORMAL).
	 * Use with interleaved layout.
	 * @param fb Source buffer; use fb.position() to configure the offset.
	 * @param elems number of elements.
	 * @param stride stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean normal(FloatBuffer fb, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_NORMAL), elems, stride, fb);
	}
	/**
	 * Set the vertex-normal VBO attribute (SV_NORMAL).
	 * Use with interleaved layout.
	 * @param offset Source buffer element offset.
	 * @param elems number of elements.
	 * @param stride stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean normal(int offset, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_NORMAL), elems, stride, offset);
	}
	/**
	 * Set the vertex-normal vertex attribute array (SV_NORMAL).
	 * @param fb Source buffer.
	 * @return true: value was set; false: not set.
	 */
	public boolean normal3d(FloatBuffer fb) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_NORMAL), 3, 0, fb);
	}
	/**
	 * Set the texture-coord vertex attribute array (SV_TEXTURE_COORD).
	 * Use with interleaved layout.
	 * @param fb Source buffer; use fb.position() to configure the offset.
	 * @param elems number of elements.
	 * @param stride stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean texture(FloatBuffer fb, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_TEXTURE_COORD), elems, stride, fb);
	}
	/**
	 * Set the texture-coord VBO attribute (SV_TEXTURE_COORD).
	 * Use with interleaved layout.
	 * @param offset Source buffer element offset.
	 * @param elems number of elements.
	 * @param stride stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean texture(int offset, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_TEXTURE_COORD), elems, stride, offset);
	}
	/**
	 * Set the texture-coord attribute array (SV_TEXTURE_COORD).
	 * @param fb Source buffer.
	 * @return true: value was set; false: not set.
	 */
	public boolean texture2d(FloatBuffer fb) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_TEXTURE_COORD), 2, 0, fb);
	}
	/**
	 * Set the color vertex attribute array (SV_COLOR).
	 * Use with interleaved layout.
	 * @param fb Source buffer; use fb.position() to configure the offset.
	 * @param elems number of elements.
	 * @param stride stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean color(FloatBuffer fb, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_COLOR), elems, stride, fb);
	}
	/**
	 * Set the color VBO attribute (SV_COLOR).
	 * Use with interleaved layout.
	 * @param offset Source buffer element offset.
	 * @param elems number of elements.
	 * @param stride stride in bytes.
	 * @return true: value was set; false: not set.
	 */
	public boolean color(int offset, int elems, int stride) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_COLOR), elems, stride, offset);
	}
	/**
	 * Set the 4D vertex-color attribute array (SV_COLOR).
	 * @param fb Source buffer.
	 * @return true: value was set; false: not set.
	 */
	public boolean color4d(FloatBuffer fb) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_COLOR), 4, 0, fb);
	}
	/**
	 * Set the 3D vertex-color attribute array (SV_COLOR).
	 * @param fb Source buffer.
	 * @return true: value was set; false: not set.
	 */
	public boolean color3d(FloatBuffer fb) {
		if(released) return false;
		return vertexAttribArray(locs.get(SV_COLOR), 3, 0, fb);
	}
	/**
	 * Set texture unit ID for given location.
	 * @param name Location name.
	 * @param texid Texture unit id.
	 * @return true: value was set; false: not set.
	 */
	public boolean texture(String name, int texid) {
		if(released) return false;
		final Location lx = locs.get(name);
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glUniform1i(lx.handle, texid);
		return true;
	}
	/**
	 * Set the color uniform (SV_UCOLOR).
	 * @param color vec4 color value.
	 * @return true: value was set; false: not set.
	 */
	public boolean color4d(float[] color) {
		if(released) return false;
		final Location lx = locs.get(SV_UCOLOR);
		if(lx == null) return false;
		if(lx.handle == -1) return false;
		GLES20.glUniform4fv(lx.handle, 1, color, 0);
		return true;
	}
	/**
	 * Dump any GL errors to the log.
	 * @param op error message prefix.
	 */
    public static void checkGlError(String op) {
    	StringBuilder sb = null;
        int error;
        int did = 0;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
        	did++;
        	if(sb == null) {
        		sb = new StringBuilder(op);
            	sb.append(": glErrors: ");
        	}
       		sb.append(Integer.toHexString(error));
       		sb.append(" ");
        }
        if(did > 0 && sb != null)
        	Log.e(TAG, sb.toString());
    }
    /**
     * Create a shader program.
     * If it fails to compile, delete it.
     * @param type Shader type.
     * @param shaderCode Shader source code.
     * @return 0: failed; else: compiled shader ID.
     */
	static int loadShader(int type, String shaderCode) {
	    final int shader = GLES20.glCreateShader(type);
		if (shader != 0) {
			// add the source code to the shader and compile it
			GLES20.glShaderSource(shader, shaderCode);
			GLES20.glCompileShader(shader);
			final int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] != GLES20.GL_TRUE) {
				Log.e(TAG, "Could not compile shader " + type + ": " + GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				return 0;
			}
		}
		else {
			checkGlError("Shader.glCreateShader:" + type);
		}
	    return shader;
	}
	/**
	 * Return program id or zero.
	 * If it fails to link, delete it.
	 * @param vs vertex shader ID.
	 * @param fs fragment shader ID.
	 * @return 0: failed; else: linked program ID.
	 */
	static final int createProgram(int vs, int fs) {
		final int mProgram = GLES20.glCreateProgram();
		if(mProgram != 0) {
			GLES20.glAttachShader(mProgram, vs);
			checkGlError("Shader.glAttachShader(vs)");
			GLES20.glAttachShader(mProgram, fs);
			checkGlError("Shader.glAttachShader(fs)");
			GLES20.glLinkProgram(mProgram);
			checkGlError("Shader.glLinkProgram");
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: " + GLES20.glGetProgramInfoLog(mProgram));
                GLES20.glDeleteProgram(mProgram);
                return 0;
            }
		}
		else {
			checkGlError("Shader.glCreateProgram");
		}
		return mProgram;
	}
	/**
	 * Common logic for processing a list of GL resources on the GL thread.
	 * Must call from GL thread!
	 * @param cl Signal mechanism with waiting thread.
	 * @param local List of source objects.
	 * @param preload List of results from GLResource.preload().
	 * @param output Target list of successful loads.
	 */
	public static <T extends GLResource> void GLthreadResource(CountDownLatch cl, T[] local, Object[] preload, T[] output) {
		for (int ix = 0; ix < local.length; ix++) {
			try {
				if(local[ix] == null) continue;
				local[ix].load(preload[ix]);
				output[ix] = local[ix];
				if (TraceSwitches.Loader.GL_RESOURCES) {
					Log.d(TAG, "load." + ix);
				}
			} catch (Exception ex) {
				// eat it
				Log.e(TAG, "Shader.load." + ix, ex);
			} finally {
				cl.countDown();
			}
		}
	}
	/**
	 * Dispatch an operation to the GL thread to create a batch of shader programs.
	 * Waits for the operation on GL thread to complete.
	 * @param ctx Source of services.
	 * @param sv Target GL Surface view.
	 * @param output Target output list; length of this array controls iteration count.
	 * @param vs Vertex shader sources.
	 * @param fs Fragment shader sources.
	 * @return true: some shaders were created, check array; false: GL thread not invoked.
	 */
	public static boolean create(Context ctx, GLSurfaceView sv, final Shader[] output, final String[] vs, final String[] fs) {
		if(ctx != null && sv != null) {
			final CountDownLatch cl = new CountDownLatch(output.length);
			final Shader[] local = new Shader[output.length];
			final Object[] preload = new Object[output.length];
			for(int ix = 0; ix < output.length; ix++) {
				if(vs[ix] == null || fs[ix] == null) continue;
				local[ix] = new Shader(vs[ix], fs[ix]);
				preload[ix] = local[ix].preload(ctx);
				if (TraceSwitches.Loader.GL_RESOURCES) {
					Log.d(TAG, "preload." + ix);
				}
			}
			final Runnable rx = new Runnable() {
				public void run() {
					if (TraceSwitches.Loader.GL_RESOURCES) {
						Log.d(TAG, "GL.run");
					}
					GLthreadResource(cl, local, preload, output);
				}
			};
			sv.queueEvent(rx);
			try {
				cl.await();
				return true;
			} catch (InterruptedException e) {
			}
		}
		return false;
	}
}