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
import com.escape.games.resource.Shader;
import com.escape.games.resource.Texture;

/**
 * Material implementation for GL texture.
 * @author escape-llc
 *
 */
public class TextureMaterial extends Material implements RequireResourceLoader {
	Texture tex;
	final String locname;
	public final String name;
	public final int ttex;
	/**
	 * Ctor.
	 * @param texname Texture name.
	 * @param ttex Texture Unit ID.
	 * @param locname Shader location name.
	 */
	public TextureMaterial(String texname, int ttex, String locname) {
		this.name = texname;
		this.ttex = ttex;
		this.locname = locname;
	}
	@Override
	public String toString() {
		return new StringBuilder("TextureMaterial.").append(name).append(".loc.").append(locname).toString();
	}
	/**
	 * Ctor.
	 * Binds to TEXTURE0, location SV_UTEXTURE.
	 * @param texname Texture name.
	 */
	public TextureMaterial(String texname) {
		this(texname, GLES20.GL_TEXTURE0, Shader.SV_UTEXTURE);
	}
	/**
	 * Ctor.
	 * Uses existing Texture instance.
	 * RequireResourceLoader.load will skip creating a texture.
	 * @param texname Texture name.
	 * @param tex Texture instance.
	 */
	public TextureMaterial(String texname, Texture tex) {
		this(texname, GLES20.GL_TEXTURE0, Shader.SV_UTEXTURE);
		this.tex = tex;
	}

	@Override
	public void setup(Shader sh) {
		if(tex != null) {
			tex.setup(sh, ttex, 0, locname);
		}
	}

	@Override
	public String getShaderKey() { return Shader.TEXTURE; }

	public void load(ResourceLoader rl, Services svc) {
		if(tex == null) {
			tex = rl.createTexture(name);
		}
	}
}
