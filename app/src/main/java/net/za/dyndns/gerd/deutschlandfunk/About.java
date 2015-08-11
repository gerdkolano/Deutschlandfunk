package net.za.dyndns.gerd.deutschlandfunk;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hanno on 2015-07-31 14:57.
 */

public class About extends DialogFragment {
  private Context context;
  private int debug;
  private TextView textView;

  public About(Context context, int debug) {
    this.context = context;
    this.debug = debug;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    //final Context context = this.context;
    View rootView = inflater.inflate(R.layout.anzeige_text, container,
        false);
    getDialog().setTitle(R.string.about);

    // Fülle "textView" mit Informationen
    if (rootView != null) {
      textView = (TextView) rootView.findViewById(R.id.anzeige_textview);
      if (textView != null) {
        textView.setText("");
        textView.append("");
        textView.append("Debug-Niveau " + debug + "\n");
        textView.append(""
                + "Download favourites like \"Forschung aktuell\" or "
                + "\"Wissenschaft im Brennpunkt\" and hear them.\n"
                + "Deutschlandfunk - Lade Favoriten wie \"Forschung aktuell\" "
                + "oder \"Wissenschaft im Brennpunkt\" herunter und höre sie.\n"
                + "Deutschlandfunk - Lade Lieblingssendungen herunter und "
                + "höre sie offline.\n"
                + "Die URLs der von DLF bereitgehaltenen MP3-Dateien werden "
                + "lokal gespeichert und die MP3-Dateien werden "
                + "auf Wunsch des Benutzers heruntergeladen.\n"
                + "Die lokal gespeicherten URLs können gelöscht und "
                + "frisch heruntergeladen werden."
                + "\n"
        );
        // nun ist "textView" mit Informationen gefüllt

        textView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Toast.makeText(context, context.getString(R.string.berührt), Toast.LENGTH_SHORT).show();
          }
        });
      }

    }
    return rootView;
  }
}
