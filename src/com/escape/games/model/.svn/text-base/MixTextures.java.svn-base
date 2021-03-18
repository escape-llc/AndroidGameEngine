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

import com.escape.games.api.RequireResourceLoader;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;
import com.escape.games.core.GameObject;
import com.escape.games.resource.Shader;
import com.escape.games.resource.Texture;

/**
 * Mix two textures by given ratio of the destination texture.
 * 0 = 100% Source texture, 1 = 100% Destination texture.
 * @author escape-llc
 *
 */
public class MixTextures extends GameObject implements Effect, RequireResourceLoader {
	Shader sx;
	Texture tex1;
	Texture tex2;
	float ratio;
	boolean loaded;
	final String texname1;
	final String texname2;
	/**
	 * Ctor.
	 * @param name Component name.
	 * @param lc true: locatable.
	 * @param texname1 "Source" texture name.
	 * @param texname2 "Destination" texture name.
	 */
	public MixTextures(String name, boolean lc, String texname1, String texname2) {
		super(name, lc);
		this.texname1 = texname1;
		this.texname2 = texname2;
	}

	public Shader getShader() { return sx; }
	public float getRatio() { return ratio; }
	public void setRatio(float ratio) { this.ratio = ratio; }
	/**
	 * Obtain GL resources.
	 */
	public void load(ResourceLoader rl, Services svc) {
		if(loaded) return;
		sx = rl.createShader(Shader.TEXTURETEXTURE);
		if(sx != null) {
			tex1 = rl.createTexture(texname1);
			if(tex1 != null) {
				tex2 = rl.createTexture(texname2);
				loaded = true;
			}
		}
	}
	/**
	 * Configure texture units and uniforms.
	 */
	public void setup() {
		if(sx == null || tex1 == null || tex2 == null) return;
		tex1.setup(sx, GLES20.GL_TEXTURE0, 0, Shader.SV_UTEXTURE);
		tex2.setup(sx, GLES20.GL_TEXTURE1, 1, Shader.SV_UTEXTURE2);
		sx.uniform(Shader.SV_URATIO, ratio);
	}
}
