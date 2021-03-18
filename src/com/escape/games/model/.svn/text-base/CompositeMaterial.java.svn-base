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

import com.escape.games.resource.Shader;

/**
 * Composite material.
 * @author escape-llc
 *
 */
public class CompositeMaterial extends Material {
	final Material[] mats;
	final String key;
	/**
	 * Ctor.
	 * @param key Shader key.
	 * @param mats Array of materials.
	 */
	public CompositeMaterial(String key, Material[] mats) {
		super();
		if(key == null || key.length() == 0) throw new IllegalArgumentException("key");
		if(mats == null || mats.length == 0) throw new IllegalArgumentException("mats");
		this.mats = mats;
		this.key = key;
	}

	@Override
	public void setup(Shader sh) {
		for(int ix = 0; ix < mats.length; ix++) {
			mats[ix].setup(sh);
		}
	}

	@Override
	public String getShaderKey() {
		return key;
	}
}
