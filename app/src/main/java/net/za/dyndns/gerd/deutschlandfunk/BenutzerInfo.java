package net.za.dyndns.gerd.deutschlandfunk;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hanno on 24.08.14.
 */
public class BenutzerInfo extends DialogFragment {
    private Context context;
    private String suchbegriff;
    private int debug;
    private TextView textView;

    public BenutzerInfo() {
    }

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        context = mActivity.getApplicationContext();
        debug = getArguments().getInt("DEBUG");
        suchbegriff = getArguments().getString("SUCHBEGRIFF");

        //...

        //final Context context = this.context;
        View rootView = inflater.inflate(R.layout.anzeige_mit_buttons, container,
                false);
        getDialog().setTitle("Benutzer-Info");

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        String verbindung = "Keine Verbindung";
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                verbindung = "Mit WIFI verbunden";
            } else {
                verbindung = "Keine WIFI-Verbindung";
            }
            verbindung += " - " + networkInfo.getTypeName();
            //verbindung += " - " + networkInfo.getSubtypeName();
            verbindung += " - " + networkInfo.getExtraInfo();
            //verbindung += "\n" + networkInfo;
        }


        // Fülle "textView" mit Informationen
        if (rootView != null) {
            textView = (TextView) rootView.findViewById(R.id.anzeigefragment_textview);
            if (textView != null) {
                textView.setText("");
                textView.append(getResources().getString(R.string.abspielgerät) + " " + PreferenceManager.getDefaultSharedPreferences(context).
                        getString(getResources().getString(R.string.abspielgerät), "470816") + "\n");
                textView.append("Verbindung: " + verbindung + "\n");
                textView.append("Debug-Niveau " + debug + "\n");
                textView.append("suchbegriff=\"" + suchbegriff + "\"\n");
                textView.append("Verzeichnis=\"" + context.getFilesDir().toString() + "\"\n");
                textView.append("Dateien darin:\n");
                for (String dateiname : context.fileList()) {
                    textView.append(dateiname + "\n");
                }
                // nun ist "textView" mit Informationen gefüllt

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, context.getString(R.string.berührenTunix), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            Button speichern = (Button) rootView.findViewById(R.id.speichern);
            speichern.setText(R.string.speichern);
            speichern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, R.string.speichern, Toast.LENGTH_SHORT).show();
                    if (((AuswahlActivity) getActivity()) != null) {
                        ((AuswahlActivity) getActivity()).doPositiverKlick();
                    }
                }
            });

            Button löschen = (Button) rootView.findViewById(R.id.löschen);
            löschen.setText(R.string.löschen);
            löschen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, R.string.löschen, Toast.LENGTH_SHORT).show();
                    if (((AuswahlActivity) getActivity()) != null) {
                        ((AuswahlActivity) getActivity()).doNegativerKlick();
                    }
                }
            });
        }
        return rootView;
    }

}
/*
AuswahlActivity.java definiert:  public void doNegativerKlick
AuswahlActivity.java definiert:  public void doPositiverKlick
 */