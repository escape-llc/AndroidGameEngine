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

/**
 * Easy place to store model transform.
 * @author escape-llc
 *
 */
public class Transform {
	public float tx;
	public float ty;
	public float tz;
	public float sx;
	public float sy;
	public float sz;
	public float rx;
	public float ry;
	public float rz;
	/**
	 * Ctor.
	 * Scale = 1f, else 0f.
	 */
	public Transform() {
		sx = 1f;
		sy = 1f;
		sz = 1f;
	}
	/**
	 * Ctor.
	 * Given position and uniform scale.
	 * @param x transform x.
	 * @param y transform y.
	 * @param z transform z.
	 * @param scale sx/sy/sz value.
	 */
	public Transform(float x, float y, float z, float scale) {
		tx = x;
		ty = y;
		tz = z;
		sx = scale;
		sy = scale;
		sz = scale;
	}
}
