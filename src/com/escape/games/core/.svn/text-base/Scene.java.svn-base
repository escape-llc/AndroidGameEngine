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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.escape.games.api.LoadedCallback;
import com.escape.games.api.Locator;
import com.escape.games.api.Pipelines;
import com.escape.games.api.RenderContext;
import com.escape.games.api.RequireRender;
import com.escape.games.api.SceneRender;
import com.escape.games.api.UnloadedCallback;
import com.escape.games.message.Constants;
import com.escape.games.model.Material;
import com.escape.games.resource.Shader;

/**
 * Core implementation for rendering a collection of game objects.
 * Uses property MATERIAL for scene-level shader uniforms.
 * Install pipeline: add accepted component to the scene.
 * Uninstall pipeline: remove accepted component from the scene.
 * @author escape-llc
 *
 */
public class Scene extends GameObjectWithProperties implements SceneRender, LoadedCallback, UnloadedCallback {
	/**
	 * Compare for rendering order (getDepth).
	 * @author escape-llc
	 *
	 */
	static final class CompareRender implements Comparator<RequireRender> {
		public int compare(RequireRender lhs, RequireRender rhs) {
			if (lhs.getDepth() == rhs.getDepth()) {
				return 0;
			} else if (lhs.getDepth() < rhs.getDepth()) {
				//reversing 1 and -1 to get low numbered drawables to get drawn last
				return 1;
			} else {
				return -1;
			}
		}
	}
	// everything
	protected final ArrayList<GameObject> dobjs;
	// subset of renderable ones
	protected final ArrayList<RequireRender> rrs;
	// sort by layer
	protected static final Comparator<RequireRender> cmp = new CompareRender();
	/**
	 * Ctor.
	 * @param name GO name.
	 * @param cap Initial capacity for size.
	 */
	public Scene(String name, int cap) {
		super(name, true);
		dobjs = new ArrayList<GameObject>(cap);
		rrs = new ArrayList<RequireRender>(cap);
	}

	public void loaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		if(ex == null && go != this) {
			if (!dobjs.contains(go)) {
				dobjs.add(go);
				if (go instanceof RequireRender) {
					rrs.add((RequireRender) go);
					Collections.sort(rrs, cmp);
				}
			}
		}
	}
	public void unloaded(GameObject go, Exception ex, Locator lc, Pipelines pps) {
		if (ex == null && go != this) {
			if (dobjs.contains(go)) {
				if (go instanceof RequireRender) {
					rrs.remove((RequireRender) go);
					Collections.sort(rrs, cmp);
				}
				dobjs.remove(go);
			}
		}
	}
	/**
	 * Render the scene.
	 */
	public void render(RenderContext rc) {
		for(int ix = 0; ix < rrs.size(); ix++) {
			final RequireRender rr = rrs.get(ix);
			rr.render(rc);
		}
	}
	/**
	 * Apply Scene.material if present
	 * 
	 */
	public void activateShader(Shader sx) {
		final Material mx = getAs(Constants.Property.MATERIAL);
		if(mx != null) {
			mx.setup(sx);
		}
	}
}
