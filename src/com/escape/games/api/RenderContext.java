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
package com.escape.games.api;

import com.escape.games.resource.Shader;

/**
 * Ability to manage scene-wide bookkeeping and shader setup.
 * @author escape-llc
 *
 */
public interface RenderContext {
	/**
	 * Notify we are using this shader.
	 * If shader isn't changing, return immediately.
	 * Context calls Shader.teardown() on previously-active shader, if any.
	 * Context calls sx.setup() on now-current shader.
	 * Context sets the current shader for subsequent calls.
	 * @param sx Shader to activate and become current shader.
	 */
	void activateShader(Shader sx);
	/**
	 * Notify we are using this model transform on current shader.
	 * Should call after activateShader() is successful.
	 * Context will set any matrix uniforms in effect.
	 * @param matrixM (M) matrix.
	 */
	void usingModel(float[] matrixM);
	/**
	 * Get the current View (V) matrix.
	 * @return (V) matrix.
	 */
	float[] matrixV();
	/**
	 * Get the current View/Projection (VP) matrix.
	 * @return (VP) matrix.
	 */
	float[] matrixVP();
}
