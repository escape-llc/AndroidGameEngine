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
package com.escape.games.api;

import java.io.InputStream;

import com.escape.games.model.Geometry;
import com.escape.games.resource.Shader;
import com.escape.games.resource.Texture;
import com.escape.games.resource.VertexBufferObject;

import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Ability to obtain resources from the environment.
 * Only be accessible in the Install pipeline.
 * @author escape-llc
 *
 */
public interface ResourceLoader {
	/**
	 * Create a texture if necessary, based on given key.
	 * @param key texture key.
	 * @return Cached texture.
	 */
	Texture createTexture(String key);
	/**
	 * Preload the list of textures.
	 * If key is already cached, it is skipped.
	 * @param keys list of texture keys.
	 */
	void preloadTextures(String... keys);
	/**
	 * Create a shader if necessary, program based on given key.
	 * @param key Shader program name, e.g. "basic".
	 * @return Cached program.
	 */
	Shader createShader(String key);
	/**
	 * Preload the list of shaders.
	 * If key is already cached, it is skipped.
	 * @param keys list of shader keys.
	 */
	void preloadShaders(String... keys);
	/**
	 * Create a VBO for the given geometry.
	 * @param geom Source geometry.
	 * @return New VBO.
	 */
	VertexBufferObject createBuffer(Geometry geom);
	/**
	 * Return the screen dimensions.
	 * @return instance containing dimensions.
	 */
	Point getScreenDimensions();
	/**
	 * Return the display metrics.
	 * @return instance containing metrics.
	 */
	DisplayMetrics getDisplayMetrics();
	/**
	 * Open an input stream based on resource id.
	 * @param resid Resource id.
	 * @return New stream.
	 */
	InputStream open(int resid);
	/**
	 * Flush and reload cached resources.
	 */
	void reload();
}
