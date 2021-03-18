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
package com.escape.games.core;

import com.escape.games.api.RequireResourceLoader;
import com.escape.games.api.ResourceLoader;
import com.escape.games.api.Services;

/**
 * Component that calls ResourceLoader.preloadXXX() from Resource Loader task.
 * @author escape-llc
 *
 */
public class PreloadResources extends GameObject implements RequireResourceLoader {
	final String[] shaders;
	final String[] textures;
	/**
	 * Ctor.
	 * @param shaders !NULL to call preloadShaders()
	 * @param textures !NULL to call preloadTextures()
	 */
	public PreloadResources(String[] shaders, String[] textures) {
		super("PreloadResources", false);
		this.shaders = shaders;
		this.textures = textures;
	}

	public void load(ResourceLoader rl, Services svs) {
		if(shaders != null)
			rl.preloadShaders(shaders);
		if(textures != null)
			rl.preloadTextures(textures);
	}
}
