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
package com.escape.games.service;

import com.escape.games.api.RenderContext;
import com.escape.games.api.SceneRender;
import com.escape.games.resource.Shader;

import android.graphics.Point;
import android.opengl.GLU;
import android.opengl.Matrix;

/**
 * Render service core.
 * Must run on the GL thread, not the Game thread!
 * This implementation maintains 3 matrices:
 * <ul>
 * <li>View (V): camera position/focal-point.</li>
 * <li>Projection (P): viewport width/height plus z near/far. Creates a frustum.</li>
 * <li>View/Projection (VP): combined matrix.</li>
 * </ul>
 * @author escape-llc
 *
 */
public abstract class RenderServiceImpl implements RenderContext  {
	static final int VEC_X = 0;
	static final int VEC_Y = 1;
	static final int VEC_Z = 2;
	/**
	 * Implementation for Null Object pattern.
	 * @author escape-llc
	 *
	 */
	protected static final class NullScene implements SceneRender {
		public void activateShader(Shader sx) {
		}
		public void render(RenderContext rc) {
		}
	}
	protected static SceneRender EMPTY = new NullScene();
	protected final float[] cameraPosition = new float[] { 0f, 0f, -32f };
	protected final float[] cameraLooksAt = new float[] { 0f, 0f, 0f };
	protected final float[] zPlanes = new float[] { 1f, 60f };
	/** Projection (P) */
	protected final float[] matrixP = new float[16];
	/** View (V) */
	protected final float[] matrixV = new float[16];
	/** Combined View/Projection  (VP) */
	protected final float[] matrixVP = new float[16];
	/* pre-allocated for activateShader() */
	final float[] finalMatrix = new float[16];
	/** Currently displaying */
	protected SceneRender currentScene;
	/** Current shader in frame */
	protected Shader currentShader;
	/** The update lock */
	protected final Object updateLock;
	/** Viewport size */
	protected final Point viewport = new Point(0, 0);
	/* Control drawing */
	volatile boolean suspended;
	/**
	 * Ctor.
	 * @param updateLock Model update lock.
	 */
	public RenderServiceImpl(Object updateLock) {
		if(updateLock == null)
			throw new IllegalArgumentException("updateLock");
		this.updateLock = updateLock;
		Matrix.setIdentityM(matrixP, 0);
		updateViewMatrix();
		currentScene = EMPTY;
	}
	/**
	 * Update V and VP matrices in response to settings.
	 * Must be called after updating camera-position, camera-looks-at, proj-matrix.
	 * Must hold update lock.
	 */
	protected void updateViewMatrix() {
	    Matrix.setLookAtM(matrixV, 0, cameraPosition[VEC_X], cameraPosition[VEC_Y], cameraPosition[VEC_Z],
	    		cameraLooksAt[VEC_X], cameraLooksAt[VEC_Y], cameraLooksAt[VEC_Z], 0f, 1f, 0f);
	    Matrix.multiplyMM(matrixVP, 0, matrixP, 0, matrixV, 0);
	}
	/**
	 * Update P and VP matrix in response to settings.
	 * Must be called after updating viewport, z-planes.
	 * Must hold update lock.
	 */
	protected void updateProjMatrix() { 
	    final float ratio = (float) viewport.x / (float)viewport.y;
	    //Matrix.perspectiveM(mProjMatrix, 0, vangle/zoom, ratio, zPlanes[VEC_X], zPlanes[VEC_Y]);
		Matrix.frustumM(matrixP, 0, -ratio, ratio, -1, 1, zPlanes[VEC_X], zPlanes[VEC_Y]);
	    Matrix.multiplyMM(matrixVP, 0, matrixP, 0, matrixV, 0);
	}
	/**
	 * Stop drawing while GL resources are being reacquired.
	 */
	public void suspend() { suspended = true; }
	/**
	 * Resume drawing; GL resources are reloaded.
	 */
	public void resume() { suspended = false; }
	/**
	 * Render all registered elements.
	 * Must hold updateLock while accessing scene.
	 */
	public abstract void render();
	/**
	 * Set the scene to render.
	 * Obtains update lock.
	 * @param sc New scene.
	 */
	public void setScene(SceneRender sc) { synchronized(updateLock) { currentScene = (sc == null ? EMPTY : sc); } }
	/**
	 * Get the current scene.
	 * Must hold update lock.
	 * @return current scene.
	 */
	public SceneRender getScene() { return currentScene; }
	/**
	 * Init bookkeeping for frame.
	 * Must hold update lock.
	 */
	protected void initFrame() {
		currentShader = null;
	}
	/**
	 * Final bookkeeping for frame.
	 * Must hold update lock.
	 */
	protected void doneFrame() {
		if(currentShader != null) {
			currentShader.teardown();
			currentShader = null;
		}
	}
	/**
	 * Manage switch of shaders.
	 * If we are on the same shader, do nothing.
	 * Must hold update lock.
	 */
	public void activateShader(Shader sx) {
		if(sx == currentShader) return;
		if(currentShader != null) {
			currentShader.teardown();
			currentShader = null;
		}
		sx.setup();
		currentShader = sx;
		currentScene.activateShader(sx);
	}
	/**
	 * Apply matrix uniforms of current shader.
	 * Must hold update lock.
	 */
	public void usingModel(float[] matrixM) {
		if(null == currentShader) return;
		// transforms
		if(currentShader.query(Shader.SV_MATRIX_MV)) {
			Matrix.multiplyMM(finalMatrix, 0, matrixV, 0, matrixM, 0);
			currentShader.matrix4(Shader.SV_MATRIX_MV, finalMatrix);
		}
		if(currentShader.query(Shader.SV_MATRIX_MVP)) {
			Matrix.multiplyMM(finalMatrix, 0, matrixVP, 0, matrixM, 0);
			currentShader.matrix4(Shader.SV_MATRIX_MVP, finalMatrix);
		}
	}
	/**
	 * return the V matrix.
	 * Must hold update lock.
	 */
	public float[] matrixV() {
		return matrixV;
	}
	/**
	 * return the VP matrix.
	 * Must hold update lock.
	 */
	public float[] matrixVP() {
		return matrixVP;
	}
	/**
	 * Reset the projection matrix.
	 * Obtains update lock.
	 * @param width view width.
	 * @param height view height.
	 */
	public void setProjection(int width, int height) {
		synchronized(updateLock) {
			viewport.x = width;
			viewport.y = height;
			updateProjMatrix();
		}
	}
	/**
	 * Set the z-clipping projection planes.
	 * These are relative distances FORWARD from camera-position.
	 * Must call before setProjection() is called.
	 * Obtains update lock.
	 * @param near distance to near clip plane.
	 * @param far distance to far clip plane.
	 */
	public void setNearFar(float near, float far) {
		synchronized(updateLock) {
			zPlanes[VEC_X] = near;
			zPlanes[VEC_Y] = far;
			updateProjMatrix();
		}
	}
	/**
	 * Reset the projection matrix and z-clipping planes together.
	 * Obtains update lock.
	 * @param width view width.
	 * @param height view height.
	 */
	public void setProjection(int width, int height, float zn, float zf) {
		synchronized(updateLock) {
			viewport.x = width;
			viewport.y = height;
			zPlanes[VEC_X] = zn;
			zPlanes[VEC_Y] = zf;
			updateProjMatrix();
		}
	}
	/**
	 * Set new camera position and focal point together and update matrices.
	 * Obtains update lock.
	 * @param px Position x.
	 * @param py
	 * @param pz
	 * @param fx Focal x.
	 * @param fy
	 * @param fz
	 */
	public void setCamera(float px, float py, float pz, float fx, float fy, float fz) {
		synchronized(updateLock) {
			cameraPosition[VEC_X] = px;
			cameraPosition[VEC_Y] = py;
			cameraPosition[VEC_Z] = pz;
			cameraLooksAt[VEC_X] = fx;
			cameraLooksAt[VEC_Y] = fy;
			cameraLooksAt[VEC_Z] = fz;
			updateViewMatrix();
		}
	}
	/**
	 * Set new camera position and update matrices.
	 * Obtains update lock.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setCameraPosition(float x, float y, float z) {
		synchronized(updateLock) {
			cameraPosition[VEC_X] = x;
			cameraPosition[VEC_Y] = y;
			cameraPosition[VEC_Z] = z;
			updateViewMatrix();
		}
	}
	/**
	 * Set new camera focal point and update matrices.
	 * Obtains update lock.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setCameraLooksAt(float x, float y, float z) {
		synchronized(updateLock) {
			cameraLooksAt[VEC_X] = x;
			cameraLooksAt[VEC_Y] = y;
			cameraLooksAt[VEC_Z] = z;
			updateViewMatrix();
		}
	}
	/**
	 * Get the current viewport dimensions.
	 * Not valid before setProjection() is called.
	 * Obtains update lock.
	 * @param vp receives vp width/height.
	 */
	public void getViewportSize(Point vp) {
		synchronized(updateLock) {
			vp.x = viewport.x;
			vp.y = viewport.y;
		}
	}
	/**
	 * Project point in World space to point in Window space.
	 * Model coords should be transformed by M matrix.
	 * Optionally invert Y-axis so it lines up with native screen coordinates.
	 * Obtains update lock.
	 * @param xx World-x
	 * @param yy World-y
	 * @param zz World-z
	 * @param win Output Window space.
	 * @param invertY true: invert Y-axis value by viewport.y
	 */
	public void project(float xx, float yy, float zz, float[] win, boolean invertY) {
		synchronized(updateLock) {
			final int[] view = { 0, 0, viewport.x, viewport.y };
			GLU.gluProject(xx, yy, zz, matrixV, 0, matrixP, 0, view, 0, win, 0);
			if(invertY) {
				win[1] = (float)viewport.y - win[1];
			}
		}
	}
	/**
	 * Unproject a point in Window space to point in World space.
	 * @param xx Window-x
	 * @param yy Window-y
	 * @param zz Window-z
	 * @param world Output World space.
	 */
	public void unproject(float xx, float yy, float zz, float[] world) {
		synchronized(updateLock) {
			final int[] view = { 0, 0, viewport.x, viewport.y };
			GLU.gluUnProject(xx, yy, zz, matrixV, 0, matrixP, 0, view, 0, world, 0);
		}
	}
}
