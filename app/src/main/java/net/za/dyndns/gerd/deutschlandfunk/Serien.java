package net.za.dyndns.gerd.deutschlandfunk;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.TreeSet;

/**
 * Created by hanno on 2014-08-28 10:10.
 */
public class Serien {
  private TreeSet<Serie> serie;
  private TreeSet<Serie> geordneteMenge;
  private String filename;
  private Activity activity;
  private Context context;
  private int debug;
  private DownloadDlfunk downloadDlfunk;
  private MediaPlayer mePlayerV0;

  public Serien(Activity activity, Context context, int debug, MediaPlayer mePlayerV0) {
    this.activity = activity;
    this.context = activity.getApplicationContext(); // context;
    if (debug > 2) Log.i("SE02", "this.context=" + this.context.toString());
    this.context = context;
    if (debug > 2) Log.i("SE04", "this.context=" + this.context.toString());
    this.debug = debug;
    this.mePlayerV0 = mePlayerV0;
    this.filename = "serien.txt";
    serie = new TreeSet<Serie>();
    this.geordneteMenge = new TreeSet<Serie>();
    erzeugeSerien();
  }

  public int size() {
    return serie.size();
  }

  public TreeSet<Serie> getSerie() {
    return serie;
  }

  public Serien erzeugeSerien() {
    //löscheDieSeriendatei();
    if (!ladeAusDerSeriendatei()) {
      this.serie.add(new Serie("Forschung aktuell", "searchterm=forschung+aktuell"));
      this.serie.add(new Serie("Computer und Kommunikation", "searchterm=computer+und+kommunikation"));
      this.serie.add(new Serie("Wissenschaft im Brennpunkt", "broadcast_id=155"));
      this.serie.add(new Serie("Wirtschaft und Gesellschaft einzelne", "broadcast_id=162"));
      this.serie.add(new Serie("Wirtschaft und Gesellschaft komplett", "searchterm=wirtschaft+und+gesellschaft+komplette"));
      this.serie.add(new Serie("Kultur heute", "searchterm=Kultur+Heute"));
      retteInDieSeriendatei();
      if (debug > 2) Log.i("SE10", "Lies \"serien\" aus dem Programmtext Serien.java");
    }
    return this;
  }

  public boolean löscheDieSeriendatei() {
    if (debug > 2) Log.i("SE20", "lösche " + filename);
    boolean erg = context.deleteFile(filename);
    if (erg) {
      if (debug > 0) Log.i("SE30", filename + " gelöscht.");
    } else {
      if (debug > 0) Log.i("SE40", "kann " + filename + " nicht löschen.");
    }
    return erg;
  }

  private boolean ladeAusDerSeriendatei() {
    FileInputStream fstream;
    try {
      fstream = context.openFileInput(filename);
    } catch (FileNotFoundException e) {
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
      if (debug > 2) Log.i("SE50", "Lies \"serien\" aus " + filename);
      String strLine;
      //Read File Line By Line
      int zeile = 0;
      Serie neueSerie = new Serie();
      while ((strLine = br.readLine()) != null) {
        // Print the content on the logcat
        if (debug > 8) Log.i("SE60", String.format("%2d %s", zeile/2, strLine));
        // Sammle die einzelnen Felder
        if (neueSerie.allesGesammelt(strLine, zeile)) {
          this.serie.add(neueSerie);
          neueSerie = new Serie();
        }
        zeile++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //Close the input stream
    try {
      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  public void retteInDieSeriendatei() {
    FileOutputStream outputStream;
    if (debug > 2) Log.i("SE70", "Erzeuge " + filename);
    int nummer = 0;
    try {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
      for (Serie was : this.serie) {
        if (debug > 3) Log.i("SR10", String.format("Serie %02d %s", nummer,
            was.getMenschenlesbarerName()
                + " "
                + was.getSuchbegriff()
        ));
        outputStream.write(was.zuRetten().getBytes());
        nummer++;
      }
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void append(Serie serie) {
    this.serie.add(serie);
    retteInDieSeriendatei();
  }
/*
  public void add(Serie serie) {
    FileOutputStream outputStream;
    try {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE | Context.MODE_APPEND);
      outputStream.write(serie.zuRetten().getBytes());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
*/
  int seitennummer = 1;

  public void stelleSerienauswahlbuttonsHer() { // AuswahlActivity
    String KEINE = "keine Vorwahl";
    Serien serien = new Serien(activity, context, debug, mePlayerV0);
    if (debug > 3)
      Log.i("SR20", " Erstelle " + serien.size() + " Serienauswahlbuttons.");
    /* netz0Serie ist in activity_auswahlahl.xml definiert*/
    LinearLayout netz0layout = (LinearLayout) activity.findViewById(R.id.netz0Serie);
    LinearLayout netz1layout = (LinearLayout) activity.findViewById(R.id.netz1Serie);
    int nummer = 0;
    for (Serie serie : serien.getSerie()) {
      Button netz0Taste = new Button(context);
      netz0Taste.setLayoutParams(
          new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT
          )
      );
      Button netz1Taste = new Button(context);
      netz1Taste.setLayoutParams(
          new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT
          )
      );
      final String menschenlesbarerName = serie.getMenschenlesbarerName();
      String suchwort = serie.getSuchbegriff();
      Netz9ClickHandler n0handler = new Netz9ClickHandler(menschenlesbarerName, suchwort, false);
      Netz9ClickHandler n1handler = new Netz9ClickHandler(menschenlesbarerName, suchwort, true);
      //n0handler.set(menschenlesbarerName, suchbegriff);
      String n0Schild = "Datei " + menschenlesbarerName;
      String n1Schild = "Netz " + menschenlesbarerName;
      if (debug > 1) {
        n0Schild = "Schild-n0 Serie-" + nummer + " " + n0Schild + " : " + suchwort;
        n1Schild = "Schild-n1 Serie-" + nummer + " " + n1Schild + " : " + suchwort;
      }
      netz0Taste.setText(n0Schild);
      netz1Taste.setText(n1Schild);
      netz0Taste.setId(160847 + nummer++);
      if (debug > 8) Log.i("SR30", " " + nummer
          + " \"" + menschenlesbarerName + "\" " + suchwort);
      netz0Taste.setOnClickListener(n0handler);
      netz1Taste.setOnClickListener(n1handler);
      netz0layout.addView(netz0Taste);
      netz1layout.addView(netz1Taste);
    }
    if (debug > 2) Log.i("SR40", serien.size() + " Serienauswahlbuttons hergestellt.");
    String gespeicherterBegriff = PreferenceManager.getDefaultSharedPreferences(context).getString("sendungsnamePreff", KEINE);
    if (!gespeicherterBegriff.equals(KEINE))
      loadPage(gespeicherterBegriff, seitennummer, !bevorzugeNetz);
    if (debug > 2) Log.i("SR45", "angestoßen: loadPage(" + gespeicherterBegriff + ")");
  }

  private final boolean bevorzugeNetz = true;

  class Netz9ClickHandler implements View.OnClickListener {
    String menschenlesbarerName, klickwort;
    boolean bevorzugeNetz;

    Netz9ClickHandler(String menschenlesbarerName, String suchbegriff, boolean bevorzugeNetz) {
      this.menschenlesbarerName = menschenlesbarerName;
      this.klickwort = suchbegriff;
      this.bevorzugeNetz = bevorzugeNetz;
    }
/*
    public void set(String menschenlesbarerName, String suchbegriff) {
      this.menschenlesbarerName = menschenlesbarerName;
      this.klickwort = suchbegriff;
    }
*/
    public void onClick(View v) {
      if (debug > 2)
        Log.i("SR52", "geklickt: " + (bevorzugeNetz ? "Netz" : "Datei") + " Serienauswahl " + klickwort);
      loadPage(klickwort, seitennummer, bevorzugeNetz);
      if (debug > 2) Log.i("SR54", "angestoßen: loadPage(" + klickwort + "," + seitennummer + ")");
    }
  }

  boolean wifiConnected = true, mobileConnected = true;

  // true: bevorzugeNetz
  public void loadPage(String suchbegriff, int seitennummer, boolean bevorzugeNetz) {
    /*
    if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
        || ((sPref.equals(WIFI)) && (wifiConnected))) {
      */
    if (this.wifiConnected || this.mobileConnected) {
      if (debug > 2) Log.i("SR60", "stoße an: loadPage(" + suchbegriff + "," + seitennummer + ")");
      //
      //AsyncTask subclass
      //activity this
      //context AuswahlActivity.this
      //
      //DownloadXmlTask downloadXmlTask = (DownloadXmlTask)
      new DownloadXmlTask(
          activity, context, this.debug,
          downloadDlfunk, mePlayerV0, bevorzugeNetz
      ).execute(suchbegriff); // dort ruft doInBackground ladeXmlBeschreibungen("http...?drau:suchbegriff")
      Helfer h = new Helfer(context, debug);
      h.logi(2, "SR70", "InBack downloadXmlTask.execute(" + suchbegriff + "," + seitennummer + ")");
      h.rettePräferenz("sendungsnamePreff", suchbegriff);
    } else {
      showErrorPage();
    }
  }

  // Displays an error if the app is unable to load content.
  private void showErrorPage() {
    activity.setContentView(R.layout.activity_auswahl);

    // The specified network connection is not available. Displays error message.
    //TextView myTextView = (TextView) this.findViewById(R.id.textView1);
    //myTextView.setText(this.getResources().getString(R.string.connection_error));
  }

}
