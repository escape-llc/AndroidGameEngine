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
package com.escape.games.view;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Utility methods for host application support.
 * Invoke-safe means the method may be called from any thread, because the method posts a Runnable
 * to the view or calls Activity.runOnUiThread() to perform the operation.
 * @author escape-llc
 *
 */
public class HostSupport {
	/**
	 * Start an animation, and remove the View from its container when it completes.
	 * @param vx Target view. Should be VISIBLE before calling.
	 * @param root View container of VX.
	 * @param resid Animation resource id.
	 */
	public static void startAnim(final View vx, final ViewGroup root, int resid) {
		final Runnable remove = new Runnable() {
			public void run() {
				root.removeView(vx);
			}
		};
		final Animation.AnimationListener al = new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				vx.setVisibility(View.VISIBLE);
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationEnd(Animation animation) {
				vx.setVisibility(View.GONE);
				vx.postDelayed(remove, 200);
			}
		};
		final Animation fi = AnimationUtils.loadAnimation(vx.getContext(), resid);
		fi.setAnimationListener(al);
		vx.startAnimation(fi);
	}
	/**
	 * Bind a runnable to animate display of text at given location.
	 * A new layout is created to contain the text.
	 * @param li Layout inflater.
	 * @param root View to add the animated text into. Should be RelativeLayout.
	 * @param x x location
	 * @param y y location
	 * @param layout layout resource id. Must be TextView.
	 * @param color text color. Pass Color.TRANSPARENT to use the layout's value.
	 * @param text text to display
	 * @param resid animation resource id
	 * @return new instance bound to parameters.
	 */
	public static Runnable animTextAt(final LayoutInflater li, final ViewGroup root, final int x, final int y, final int layout, final int color, final CharSequence text, final int resid) {
		final Runnable rx = new Runnable() {
			public void run() {
				if(root != null) {
					final TextView tv = (TextView)li.inflate(layout, null);
					tv.setVisibility(View.GONE);
					tv.setText(text);
					if(Color.TRANSPARENT != color) {
						tv.setTextColor(color);
					}
					final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					lp.leftMargin = x;
					lp.topMargin = y;
					root.addView(tv, lp);
					startAnim(tv, root, resid);
				}
			}
		};
		return rx;
	}
	/**
	 * Invoke-safe post a runnable to set the text of the indicated root view + view id.
	 * @param vx Root view.
	 * @param resid Target view id.
	 * @param text Text to display.
	 */
	public static void setText(final View vx, final int resid, final CharSequence text) {
		if (vx != null) {
			final Runnable rx = new Runnable() {
				public void run() {
					if (vx != null) {
						final TextView tv = (TextView) vx.findViewById(resid);
						if (tv != null) {
							tv.setText(text);
						}
					}
				}
			};
			vx.post(rx);
		}
	}
	/**
	 * Standard invoke-safe handling of GameHost.fadeInOutText.
	 * @param fx Fragment must be !NULL.
	 * @param x
	 * @param y
	 * @param layout
	 * @param color
	 * @param text
	 * @param animid
	 */
	public static void fadeInOutText(Fragment fx, int x, int y, int layout, int color, CharSequence text, int animid) {
		final Activity ax = fx.getActivity();
		if(ax == null) return;
		final ViewGroup vg = (ViewGroup)fx.getView();
		if(vg == null) return;
		final Runnable rx = HostSupport.animTextAt(ax.getLayoutInflater(), vg, x, y, layout, color, text, animid);
		ax.runOnUiThread(rx);
	}
	/**
	 * Finish the activity if it still exists.
	 * @param fx Fragment must be !NULL.
	 */
	public static void forceQuit(Fragment fx) {
		final Activity ax = fx.getActivity();
		if(ax == null) return;
		ax.finish();
	}
	/**
	 * Standard invoke-safe handling of GameHost.setText.
	 * @param fx Fragment must be !NULL.
	 * @param resid
	 * @param text
	 */
	public static void setText(Fragment fx, final int resid, final String text) {
		final Activity ax = fx.getActivity();
		if(ax == null) return;
		final Runnable rx = new Runnable() {
			public void run() {
				final TextView tv = (TextView)ax.findViewById(resid);
				if(tv != null) {
					tv.setText(text);
				}
			}
		};
		ax.runOnUiThread(rx);
	}
	/**
	 * Invoke-safe perform animation on fragment's view according to enabled flag.
	 * If anything in the chain Fragment->Activity->View is NULL, nothing occurs.
	 * @param fx Fragment must be !NULL.
	 * @param vid View id.
	 * @param animin Animation for enabled.
	 * @param animout Animation for !enabled.
	 * @param enabled Enabled flag.
	 */
	public static void enableUi(final Fragment fx, final int vid, final int animin, final int animout, final boolean enabled) {
		final Activity ax = fx.getActivity();
		if(ax == null) return;
		final Runnable rx = new Runnable() {
			public void run() {
				final View vx = fx.getView();
				if(vx == null) return;
				final View vvx = vx.findViewById(vid);
				if(vvx == null) return;
				final int vz = vvx.getVisibility();
				if(enabled && vz == View.VISIBLE) return;
				if(!enabled && vz != View.VISIBLE) return;
				final Animation.AnimationListener al = new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
						vvx.setVisibility(enabled ? View.INVISIBLE : View.VISIBLE);
					}
					public void onAnimationRepeat(Animation animation) {
					}
					public void onAnimationEnd(Animation animation) {
						vvx.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
					}
				};
				final Animation fi = AnimationUtils.loadAnimation(ax, enabled ? animin : animout);
				fi.setAnimationListener(al);
				vvx.startAnimation(fi);
			}
		};
		ax.runOnUiThread(rx);
	}
}
