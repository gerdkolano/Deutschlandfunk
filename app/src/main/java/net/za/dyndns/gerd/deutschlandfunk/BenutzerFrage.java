package net.za.dyndns.gerd.deutschlandfunk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hanno on 24.08.14.
 */
public class BenutzerFrage extends DialogFragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialogfragment, container,
        false);
    getDialog().setTitle("BenutzerFrage.java");
    // Do something else
    return rootView;
  }
/*
Das folgende provoziert
RuntimeException: requestFeature() must be called before adding content
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
        // Set Dialog Icon
        .setIcon(R.drawable.androidhappy)
            // Set Dialog Title
        .setTitle("Alert DialogFragment")
            // Set Dialog Message
        .setMessage("Alert DialogFragment Tutorial")

            // Positive button
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            // Do something else
          }
        })

            // Negative Button
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            // Do something else
          }
        }).create();
  }
*/
}

