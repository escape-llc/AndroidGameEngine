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
package com.escape.games.model;

import com.escape.games.resource.Shader;

/**
 * Material for lighting model.
 * @author escape-llc
 *
 */
public class LightingMaterial extends Material {
	final float[] position;
	public LightingMaterial(float[] position) {
		this.position = position;
	}
	@Override
	public String toString() {
		return new StringBuilder("LightingMaterial.position ").append(position[0]).append(",")
				.append(position[1]).append(",").append(position[2]).toString();
	}
	@Override
	public void setup(Shader sh) {
		sh.uniform3d(Shader.SV_ULIGHTPOSITION, position);
	}

	@Override
	public String getShaderKey() {
		return Shader.COLORPERVERTEX;
	}
}
