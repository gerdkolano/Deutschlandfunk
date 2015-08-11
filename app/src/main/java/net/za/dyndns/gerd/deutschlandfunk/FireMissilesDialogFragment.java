package net.za.dyndns.gerd.deutschlandfunk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class FireMissilesDialogFragment extends DialogFragment {
  private Context context;
  private String suchbegriff;

  public FireMissilesDialogFragment() {
  }

  public FireMissilesDialogFragment(Context context, String suchbegriff) {
    this.suchbegriff = suchbegriff;
    this.context = context;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.dialog_fire_missiles)
        .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // FIRE ZE MISSILES!
            Toast.makeText(context, R.string.fire, Toast.LENGTH_SHORT).show();
            ((AuswahlActivity) getActivity()).doPositiverKlick();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // User cancelled the dialog
            Toast.makeText(context, R.string.cancel, Toast.LENGTH_SHORT).show();
            ((AuswahlActivity) getActivity()).doNegativerKlick();
          }
        });
    // Create the AlertDialog object and return it
    return builder.create();
  }
}
