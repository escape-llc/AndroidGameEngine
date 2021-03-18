package com.escape.games.view;

import com.escape.games.api.GameHost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Display a dialog fragment to confirm quitting game.
 * Accepting terminates the activity.
 * @author escape-llc
 *
 */
public class QuitGameDialogFragment extends DialogFragment {
	final int resid_title;
	final int resid_message;
	final GameHost gh;
	/**
	 * Ctor.
	 * @param rtitle resource ID of title
	 * @param rmessage resource ID of message
	 */
	public QuitGameDialogFragment(int rtitle, int rmessage, GameHost gh) {
		resid_title = rtitle;
		resid_message = rmessage;
		this.gh = gh;
	}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final Dialog dx = new AlertDialog.Builder(this.getActivity())
    	.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
	            gh.forceQuit();
				dialog.dismiss();
			}})
		.setCancelable(true)
		.setMessage(resid_message)
		.setTitle(resid_title).create();
        return dx;
    }
}
