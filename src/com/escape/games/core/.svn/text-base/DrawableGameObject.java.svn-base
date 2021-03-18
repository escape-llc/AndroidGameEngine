/*
 * Copyright 2013-4 eScape Technology LLC.
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
package com.escape.games.core;

import android.opengl.Matrix;

import com.escape.games.api.LoadedCallback;
import com.escape.games.api.Locator;
import com.escape.games.api.Pipelines;
import com.escape.games.api.Properties;
import com.escape.games.api.RenderContext;
import com.escape.games.api.RequireRender;
import com.escape.games.api.RequireResourceLoader;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.api.UnloadedCallback;
import com.escape.games.message.Constants;
import com.escape.games.model.Effect;
import com.escape.games.model.Geometry;
import com.escape.games.model.Material;
import com.escape.games.model.PerVertexMaterial;
import com.escape.games.model.Transform;
import com.escape.games.resource.Shader;

/**
 * Base implementation for game object with visual representation.
 * Uses properties TRANSFORM and MATERIAL.
 * Delegates RequireResourceLoader, LoadedCallback, UnloadedCallback.
 * @author escape-llc
 *
 */
public class DrawableGameObject extends GameObjectWithProperties implements RequireResourceLoader, LoadedCallback, UnloadedCallback, RequireRender {
	final int depth;
	protected final Geometry model;
	protected Shader sx;
	protected Effect efx;
	boolean visible;
	/* current model matrix with Transform applied */
	final float[] modelMatrix = new float[16];
	/**
	 * Override to hook into RequireResourceLoader chain.
	 * Default implementation initializes model, material, and shader.
	 * @param rl source of resources.
	 * @param svc source of services.
	 */
	protected void internalLoad(ResourceLoader rl, Services svc) {
		// initialize model
		model.load(rl, svc);
		// initialize material
		final Material mx = this.getAs(Constants.Property.MATERIAL);
		if(mx == null)
			throw new IllegalArgumentException(name + ": No material was defined");
		if(mx instanceof PerVertexMaterial) {
			((PerVertexMaterial)mx).setVertexCount(model.getVertexCount());
		}
		if(mx instanceof RequireResourceLoader) {
			((RequireResourceLoader)mx).load(rl, svc);
		}
		//Log.d("DGO", "shader=" + mx.getShaderKey());
		sx = rl.createShader(mx.getShaderKey());
		if(sx == null)
			throw new IllegalArgumentException(name + ": could not locate shader: " + mx.getShaderKey());
	}
	/**
	 * Hook into LoadedCallback.
	 * Must be called by subclasses implementing LoadedCallback.
	 * @param go
	 * @param ex
	 * @param lc
	 */
	protected void internalLoaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		if(null == ex && go instanceof Effect) {
			efx = (Effect)go;
		}
	}
	/**
	 * Hook into UnloadedCallback.
	 * Must be called by subclasses implementing UnloadedCallback.
	 * @param go
	 * @param ex
	 * @param lc
	 */
	protected void internalUnloaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		if(go instanceof Effect) {
			efx = null;
		}
	}
	/**
	 * Recompute model transform (Translate/Scale/RotateXYZ).
	 * @param modelMatrix Target model transform matrix.
	 * @param px Source of values.
	 */
	protected void transform(float[] modelMatrix, Properties px) {
		Matrix.setIdentityM(modelMatrix, 0);
		final Transform tf = px.getAs(Constants.Property.TRANSFORM, null);
		if(tf == null) return;
		Matrix.translateM(modelMatrix, 0, tf.tx, tf.ty, tf.tz);
		Matrix.scaleM(modelMatrix, 0, tf.sx, tf.sy, tf.sz);
		if(tf.rx != 0f)
			Matrix.rotateM(modelMatrix, 0, tf.rx, 1f, 0f, 0f);
		if(tf.ry != 0f)
			Matrix.rotateM(modelMatrix, 0, tf.ry, 0f, 1f, 0f);
		if(tf.rz != 0f)
			Matrix.rotateM(modelMatrix, 0, tf.rz, 0f, 0f, 1f);
	}
	/**
	 * Recompute model matrix when TRANSFORM is changed.
	 */
	@Override
	protected void notifyPropertyChanged(int propertyId) {
		if(propertyId == Constants.Property.TRANSFORM) {
			transform(modelMatrix, this);
		}
		else {
			super.notifyPropertyChanged(propertyId);
		}
	}
	/**
	 * Ctor.
	 * @param name GO name.
	 * @param lc true: locatable.
	 * @param model Vertex attributes.
	 * @param depth Drawing depth.
	 */
	public DrawableGameObject(String name, boolean lc, Geometry model, int depth) {
		super(name, lc);
		if(model == null)
			throw new IllegalArgumentException("model");
		this.model = model;
		this.depth = depth;
		Matrix.setIdentityM(modelMatrix, 0);
	}
	public int getDepth() { return depth; }
	public boolean getVisible() { return visible; }
	public void setVisible(boolean vis) { visible = vis; }
	public void render(RenderContext rc) {
		if(!visible) return;
		final Shader sfx = efx != null ? efx.getShader() : sx;
		if(sfx == null) return;
		rc.activateShader(sfx);
		rc.usingModel(modelMatrix);
		if (efx == null) {
			// material
			final Material mx = this.getAs(Constants.Property.MATERIAL);
			if (mx != null) {
				mx.setup(sfx);
			}
		}
		else {
			efx.setup();
		}
		// draw
		model.render(sfx, this);
	}
	public void load(ResourceLoader rl, Services svc) { internalLoad(rl, svc); }
	public void unloaded(GameObject go, Exception ex, Locator lc, Pipelines pps) { internalUnloaded(go, ex, lc, pps); }
	public void loaded(GameObject go, Exception ex, Locator lc, Pipelines pps) { internalLoaded(go, ex, lc, pps); }
}
