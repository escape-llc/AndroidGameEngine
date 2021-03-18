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
package com.escape.games.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.escape.games.api.ResourceLoader;
import com.escape.games.core.TraceSwitches;
import com.escape.games.model.Geometry;
import com.escape.games.model.InterleavedVertexGeometry;
import com.escape.games.resource.Shader;
import com.escape.games.resource.Texture;
import com.escape.games.resource.VertexBufferObject;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Default implementation for resource loader.
 * Holds weak references to Android components.
 * @author escape-llc
 *
 */
public class ContextResourceLoader implements ResourceLoader {
	static final String TAG = "CRL";
	final WeakReference<Context> wrc;
	final WeakReference<GLSurfaceView> glsv;
	final Point sdim;
	final DisplayMetrics dm;
	/* update lock: protects cached resources */
	final Object updateLock = new Object();
	/* shader factory map */
	final HashMap<String, ShaderFactory> sfmap = new HashMap<String, ShaderFactory>();
	/* cached shaders */
	final HashMap<String, Shader> smap = new HashMap<String, Shader>();
	/* cached textures */
	final HashMap<String, Texture> tmap = new HashMap<String, Texture>();
	/* owner-supplied resource map */
	final HashMap<String, Integer> resmap;
	/**
	 * Helper for creating shaders.
	 * @author escape-llc
	 *
	 */
	static class ShaderFactory {
		final String key;
		final String vsource;
		final String fsource;
		/**
		 * Ctor.
		 * @param key Shader key.
		 * @param vs Vertex shader source.
		 * @param fs Fragment shader source.
		 */
		public ShaderFactory(String key, String vs, String fs) {
			this.key = key;
			this.vsource = vs;
			this.fsource = fs;
		}
	}
	/**
	 * Ctor.
	 * @param ctx Source context.
	 * @param sv Source GLSurfaceView
	 * @param resmap Resource ID map.
	 */
	public ContextResourceLoader(Context ctx, GLSurfaceView sv, HashMap<String, Integer> resmap) {
		if(ctx == null) throw new IllegalArgumentException("ctx");
		if(sv == null) throw new IllegalArgumentException("sv");
		if(resmap == null) throw new IllegalArgumentException("resmap");
		wrc = new WeakReference<Context>(ctx);
		glsv = new WeakReference<GLSurfaceView>(sv);
		this.resmap = resmap;
		this.sdim = new Point();
		final WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
		// API 13
		wm.getDefaultDisplay().getSize(sdim);
		this.dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		registerBuiltinShaders();
	}
	/**
	 * Register the built-in shaders.
	 */
	private void registerBuiltinShaders() {
		sfmap.put(Shader.BASIC, new ShaderFactory(Shader.BASIC, BuiltinShaders.solidVertexShader, BuiltinShaders.solidFragmentShader));
		sfmap.put(Shader.COLORPERVERTEX, new ShaderFactory(Shader.COLORPERVERTEX, BuiltinShaders.cpvVertexShader, BuiltinShaders.cpvFragmentShader));
		sfmap.put(Shader.TEXTURE, new ShaderFactory(Shader.TEXTURE, BuiltinShaders.texVertexShader, BuiltinShaders.texFragmentShader));
		sfmap.put(Shader.TEXTURETEXTURE, new ShaderFactory(Shader.TEXTURE, BuiltinShaders.texVertexShader, BuiltinShaders.textexFragmentShader));
		sfmap.put(Shader.LIGHTPERVERTEX, new ShaderFactory(Shader.LIGHTPERVERTEX, BuiltinShaders.lpvVertexShader, BuiltinShaders.lpvFragmentShader));
		sfmap.put(Shader.LIGHTPERFRAGMENT, new ShaderFactory(Shader.LIGHTPERFRAGMENT, BuiltinShaders.lpfVertexShader, BuiltinShaders.lpfFragmentShader));
	}
	/**
	 * Map string key to Android resource ID.
	 * @param key String key.
	 * @return -1: no key; else: resource ID.
	 */
	protected int mapResourceKey(String key) {
		if(resmap.containsKey(key)) return resmap.get(key).intValue();
		return -1;
	}
	/**
	 * Return the screen dimensions.
	 */
	public Point getScreenDimensions() { return sdim; }
	/**
	 * Return display metrics for default screen.
	 */
	public DisplayMetrics getDisplayMetrics() { return dm; }
	/**
	 * Open a raw input stream from resources.
	 */
	public InputStream open(int resid) {
		final Context ctx = wrc.get();
		if(ctx != null) {
			return ctx.getResources().openRawResource(resid);
		}
		return null;
	}
	/**
	 * Create a new vertex/fragment shader for the key.
	 * Must hold update lock.
	 * @param key Shader key.
	 * @param sv Target GL surface view.
	 * @return New shader or NULL.
	 */
//	Shader create(String key, Context ctx, GLSurfaceView sv) {
//		if(sfmap.containsKey(key)) {
//			return sfmap.get(key).create(ctx, sv);
//		}
//		return null;
//	}
	/**
	 * Register a custom shader.
	 * Obtains update lock.
	 * Shader is not compiled/linked until it is called for (if at all).
	 * Should call before this instance is installed.
	 * @param key Shader key.
	 * @param vsource Vertex shader source.
	 * @param fsource Fragment shader source.
	 */
	public void registerShader(String key, String vsource, String fsource) {
		if(key == null || key.length() == 0) throw new IllegalArgumentException("key");
		if(vsource == null || vsource.length() == 0) throw new IllegalArgumentException("vsource");
		if(fsource == null || fsource.length() == 0) throw new IllegalArgumentException("fsource");
		synchronized(updateLock) {
			sfmap.put(key, new ShaderFactory(key, vsource, fsource));
		}
	}
	/**
	 * Return a cached shader program or NULL if failed.
	 * On cache miss dispatch GL operation to create resource.
	 * Holds update lock until complete.
	 * Synchronizes with GL thread.
	 */
	public Shader createShader(String key) {
		synchronized(updateLock) {
			if(smap.containsKey(key)) return smap.get(key);
		}
		if(TraceSwitches.Loader.GL_RESOURCES) {
			Log.d(TAG, "createShader " + key);
		}
		final GLSurfaceView sv = glsv.get();
		final Context ctx = wrc.get();
		if(ctx != null && sv != null) {
			final Shader[] shs = new Shader[1];
			final boolean did = internalCreateShaders(ctx, sv, shs, key);
			if(did)
				return shs[0];
		}
		return null;
	}
	/**
	 * Common create shaders in batch.
	 * Adds shaders to the cache no overwrite.
	 * Synchronizes with GL thread (via Shader.create).
	 * @param ctx Source of services. Must be non-NULL.
	 * @param sv Dispatch to GL thread. Must be non-NULL.
	 * @param shs Target list of created shaders.
	 * @param keys Source list of keys.
	 * @return true: invoked on GL; false: not.
	 */
	protected boolean internalCreateShaders(Context ctx, GLSurfaceView sv, Shader[] shs, String... keys) {
		final String[] vs = new String[keys.length];
		final String[] fs = new String[keys.length];
		synchronized (updateLock) {
			for (int ix = 0; ix < keys.length; ix++) {
				final String key = keys[ix];
				if (TraceSwitches.Loader.GL_RESOURCES) {
					Log.d(TAG, "internalCreateShaders " + key);
				}
				if (smap.containsKey(key))
					// leave this entry NULL; it will be skipped
					continue;
				if (sfmap.containsKey(key)) {
					// fill in entry; it will be created
					final ShaderFactory sf = sfmap.get(key);
					vs[ix] = sf.vsource;
					fs[ix] = sf.fsource;
				}
			}
		}
		final boolean did = Shader.create(ctx, sv, shs, vs, fs);
		if (did) {
			synchronized (updateLock) {
				for (int jx = 0; jx < shs.length; jx++) {
					if (shs[jx] == null) continue;
					smap.put(keys[jx], shs[jx]);
					if (TraceSwitches.Loader.GL_RESOURCES) {
						Log.d(TAG, "internalCreateShaders.add " + keys[jx]);
					}
				}
			}
		} else {
			Log.w(TAG, "internalCreateShaders Shader.create failed");
		}
		return did;
	}
	protected boolean internalCreateTextures(Context ctx, GLSurfaceView sv, Texture[] output, String... keys) {
		final int[] resids = new int[keys.length];
		synchronized (updateLock) {
			for (int ix = 0; ix < keys.length; ix++) {
				final String key = keys[ix];
				if (TraceSwitches.Loader.GL_RESOURCES) {
					Log.d(TAG, "internalCreateTextures " + key);
				}
				if (tmap.containsKey(key))
				// leave this entry 0; it will be skipped
					continue;
				resids[ix] = mapResourceKey(key);
			}
		}
		final boolean did = Texture.create(ctx, sv, output, resids);
		if (did) {
			synchronized (updateLock) {
				for (int jx = 0; jx < output.length; jx++) {
					if (output[jx] == null) continue;
					tmap.put(keys[jx], output[jx]);
					if (TraceSwitches.Loader.GL_RESOURCES) {
						Log.d(TAG, "internalCreateTextures.add " + keys[jx]);
					}
				}
			}
		}
		return did;
	}
	/**
	 * Preload the list of shaders.
	 * If key is already cached, it is overwritten.
	 * Subsequent call to createShader() returns instance loaded here.
	 * Synchronizes with GL thread.
	 * Entire list runs under update lock.
	 * @param keys list of shader keys.
	 */
	public void preloadShaders(String... keys) {
		final GLSurfaceView sv = glsv.get();
		final Context ctx = wrc.get();
		if (ctx != null && sv != null) {
			final Shader[] shs = new Shader[keys.length];
			internalCreateShaders(ctx, sv, shs, keys);
		}
	}

	/**
	 * Return a cached texture or NULL if failed.
	 * On cache miss dispatch GL operation to create resource.
	 * Holds update lock until complete.
	 * Synchronizes with GL thread.
	 */
	public Texture createTexture(String key) {
		synchronized(updateLock) {
			if(tmap.containsKey(key)) return tmap.get(key);
		}
		if(TraceSwitches.Loader.GL_RESOURCES) {
			Log.d(TAG, "createTexture " + key);
		}
		final GLSurfaceView sv = glsv.get();
		final Context ctx = wrc.get();
		if(sv != null && ctx != null) {
			final Texture[] output = new Texture[1];
			final boolean did = internalCreateTextures(ctx, sv, output, key);
			if(did)
				return output[0];
		}
		return null;
	}
	/**
	 * Preload the list of textures.
	 * If key is already cached, it is skipped.
	 * Entire list runs under update lock.
	 * @param keys list of texture keys.
	 * @throws IllegalArgumentException no resource found for key
	 */
	public void preloadTextures(String... keys) {
		final GLSurfaceView sv = glsv.get();
		final Context ctx = wrc.get();
		if(sv != null && ctx != null) {
			final Texture[] output = new Texture[keys.length];
			internalCreateTextures(ctx, sv, output, keys);
		}
	}
	/**
	 * Create the VBO.
	 * Synchronizes with GL thread.
	 */
	public VertexBufferObject createBuffer(Geometry geom) {
		if(!(geom instanceof InterleavedVertexGeometry))
			throw new IllegalArgumentException("Geometry not interleaved");
		final GLSurfaceView sv = glsv.get();
		final Context ctx = wrc.get();
		if(ctx != null && sv != null) {
			final InterleavedVertexGeometry ivg = (InterleavedVertexGeometry)geom;
			return VertexBufferObject.create(ctx, ivg.getBuffer(), sv);
		}
		return null;
	}
	/**
	 * Pump the stream into a string.
	 * @param context Source of resources.
	 * @param resourceId Resource stream ID.
	 * @return Contents or Empty String.
	 */
	public static String readRawResource(final Context context, final int resourceId) {
		final InputStream is = context.getResources().openRawResource(resourceId);
		final InputStreamReader isr = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(isr);
		final StringBuilder sb = new StringBuilder();
		try {
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine);
				sb.append('\n');
			}
		} catch (IOException e) {
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}
	/**
	 * Dispatch operation to GL to reload cached resources.
	 * Holds update lock until complete.
	 * Synchronizes with GL thread.
	 * Do not call from GL thread!
	 */
	public void reload() {
		Log.d(TAG, "reload");
		synchronized(updateLock) {
			final Context ctx = wrc.get();
			final GLSurfaceView sv = glsv.get();
			if(sv == null || ctx == null) {
				Log.w(TAG, "Lost weak reference to sv or ctx");
				return;
			}
			final CountDownLatch cl = new CountDownLatch(1);
			// collect values from all the pre-loads for GL thread section
			final HashMap<Texture,Object> txpreload = new HashMap<Texture,Object>();
			final HashMap<Shader,Object> sxpreload = new HashMap<Shader,Object>();
			try {
				for (Map.Entry<String, Texture> me : tmap.entrySet()) {
					final Texture tx = me.getValue();
					tx.release();
					txpreload.put(tx,  tx.preload(ctx));
				}
				for (Map.Entry<String, Shader> me : smap.entrySet()) {
					final Shader sx = me.getValue();
					sx.release();
					sxpreload.put(sx,  sx.preload(ctx));
				}
			} catch(Exception ex) {
				Log.e(TAG, "reload.run", ex);
			}
			final Runnable rx = new Runnable() {
				public void run() {
					try {
						for (Map.Entry<String, Texture> me : tmap.entrySet()) {
							final Texture tx = me.getValue();
							tx.load(txpreload.get(tx));
						}
						for (Map.Entry<String, Shader> me : smap.entrySet()) {
							final Shader sx = me.getValue();
							sx.load(sxpreload.get(sx));
						}
					} catch(Exception ex) {
						Log.e(TAG, "reload.run", ex);
					} finally {
						cl.countDown();
						txpreload.clear();
						sxpreload.clear();
					}
				}
			};
			sv.queueEvent(rx);
			try {
				cl.await();
			} catch (InterruptedException e) {
			}
		}
	}
}