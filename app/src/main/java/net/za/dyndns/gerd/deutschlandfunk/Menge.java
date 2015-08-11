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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by hanno on 19.08.14.
 */
public class Menge {

  private SortedSet<EineSendung> geordneteMenge;
  private String filename;
  private Activity activity;
  private Context context;
  private int debug;
  private DownloadDlfunk downloadDlfunk;
  private MediaPlayer mediaPlayer;
  public int seitenanzahl;

  public Menge() {
    this.geordneteMenge = new TreeSet<EineSendung>();
    this.seitenanzahl = 1;
  }

  public Menge(Activity activity, Context context, int debug, MediaPlayer mediaPlayer) {
    this.activity = activity;
    this.context = context;
    this.debug = debug;
    this.mediaPlayer = mediaPlayer;
    this.geordneteMenge = new TreeSet<EineSendung>();
    this.seitenanzahl = 1;
  }

  private String sendungsDateiname(String suchbegriff) {
    return suchbegriff.replaceAll("[^A-Za-z_0-9]", "_") + "-v2.txt";
  }

  public void setSuchbegriff(String suchbegriff) {
    this.filename = sendungsDateiname(suchbegriff);
  }

  public String getFilename() {
    return this.filename;
  }

  public Menge(Activity activity, Context context, int debug, MediaPlayer mediaPlayer, String suchbegriff) {
/*
    this.activity = activity;
    this.context = context;
    this.debug = debug;
    this.mediaPlayer = mediaPlayer;
    this.geordneteMenge = new TreeSet<EineSendung>();
    this.seitenanzahl = 1;
*/
    this(activity, context, debug, mediaPlayer);
    this.filename = sendungsDateiname(suchbegriff);
    ladeAusSendungsdatei();
  }

  public SortedSet<EineSendung> get() {
    return geordneteMenge;
  }

  public int size() {
    return geordneteMenge.size();
  }

  public Iterator<EineSendung> iterator() {
    return geordneteMenge.iterator();
  }

  public boolean isEmpty() {
    return geordneteMenge.isEmpty();
  }

  public EineSendung erst() {
    if (geordneteMenge.size() > 0) {
      return geordneteMenge.first();
    } else {
      return null;
    }
  }

  public void add(EineSendung eineSendung) {
    geordneteMenge.add(eineSendung);
  }

  public void addAll(Menge menge) {
    geordneteMenge.addAll(menge.get());
  }

  private void ladeOffeneSendungsdatei(FileInputStream fstream) {
    try {
      //DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
      String strLine;
      //Read File Line By Line
      int zeile = 0;
      EineSendung eineSendung = new EineSendung(debug);
      while ((strLine = br.readLine()) != null) {
        // Print the content on the logcat
        if (debug > 8) Log.i("L010", String.format("%3d %s", zeile, strLine));
        // Sammle die einzelnen Felder
        if (eineSendung.allesGesammelt(strLine, zeile)) {
          this.add(eineSendung);
          seitenanzahl = eineSendung.seitenanzahl;
          eineSendung = new EineSendung(debug);
        }
        zeile++;
/*
        eineSendung.ladeNach(strLine, zeile);
        if (zeile % EineSendung.siebenZeilen == 0) {
          this.add(eineSendung);
          seitenanzahl = eineSendung.seitenanzahl;
          eineSendung = new EineSendung(debug);
        }
*/
      }
      //Close the input stream
      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void ladeAusSendungsdatei() {
    String quelle = filename;
    FileInputStream fstream;
    if (debug > 0) Log.i("M010", "ladeAusSendungsdatei \"" + quelle + "\"");
    try {
      fstream = context.openFileInput(quelle);
    } catch (FileNotFoundException e) {
      if (debug > 0) Log.i("M011", "Keine Datei " + quelle + " gefunden.");
      boolean wifiConnected = true;
      if(!wifiConnected) ladeAusBackupdatei(); // weil X072 meldet : "kein WIFI"
      return;
    } catch (Exception e) {
      if (debug > 0) Log.i("M012", "Kann Datei " + quelle + " nicht öffnen.");
      e.printStackTrace();
      return;
    }
    ladeOffeneSendungsdatei(fstream);
  }

  private void ladeAusBackupdatei() {
    String quelle = "back-" + filename;
    FileInputStream fstream;
    if (debug > 0) Log.i("M015", "Lies Datei " + quelle);
    try {
      fstream = context.openFileInput(quelle);
    } catch (FileNotFoundException e) {
      if (debug > 0) Log.i("M016", "Datei " + quelle + " nicht gefunden.");
      return;
    } catch (Exception e) {
      if (debug > 0) Log.i("M017", "Kann Datei " + quelle + " nicht öffnen.");
      e.printStackTrace();
      return;
    }
    ladeOffeneSendungsdatei(fstream);
  }

  void zeigeSendungsdatei() {
    FileInputStream fstream;
    if (debug > 2) Log.i("M020", "finde Datei " + filename + " im Verzeichnis " + context.getFilesDir());
    try {
      fstream = context.openFileInput(filename);
      //DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
      String strLine;
      int nummer = 0;
      //Read File Line By Line
      while ((strLine = br.readLine()) != null) {
        // Print the content on the console
        if (debug > 8) Log.i("Z010", String.format("%3d %s", nummer, strLine));
        nummer++;
        //System.out.println(strLine);
      }
      //Close the input stream
      fstream.close();
    } catch (FileNotFoundException e) {
      if (debug > 2) Log.i("M022", "Existiert nicht: " + filename);
    } catch (Exception e) {
      if (debug > 2) Log.i("M024", "Nicht lesbar: " + filename);
      e.printStackTrace();
    }
  }
/*
  boolean löscheSendungsdatei() { // gerufen von DownloadXmlTask
    if (debug > 2) Log.i("M030", "lösche " + filename);
    boolean erg = context.deleteFile(filename);
    if (erg) {
      if (debug > 0) Log.i("M031", "gelöscht: " + filename);
    } else {
      if (debug > 0) Log.i("M032", "kann nicht " + filename + " löschen.");
    }
    return erg;
  }

  boolean umtaufeSendungsdatei() {
    String neuerName = "back-" + filename;
    if (debug > 2) Log.i("M035", "umtaufe " + filename);
    File oldFile = context.getFileStreamPath(filename);
    File newFile = new File(oldFile.getParent(), neuerName);
    if (!oldFile.exists()) {
      if (debug > 2) Log.i("M036", "keine " + filename);
      return true;
    }
    if (newFile.exists()) {
      // Or you could throw here.
      context.deleteFile(neuerName);
      if (debug > 2) Log.i("M037", "gelöscht " + neuerName);
    }
    oldFile.renameTo(newFile);
    if (debug > 2) Log.i("M038", "umgetauft " + neuerName);
    return true;
  }
*/
  void logge() {
    int nummer = 0;
    Iterator<EineSendung> it = this.iterator();
    while (it.hasNext()) {
      EineSendung was = (it.next());
      Log.i("M038", String.format("%3d %s", nummer, was.machDateiname()));
      nummer++;
    }
  }

  void retteInSendungsdatei() {
    if (this.size() == 0) {
      if (debug > 2) Log.i("M042", "nicht \"" + filename + "\" antasten.");
      return;
    }
    if (debug > 2) Log.i("M040", "retteInSendungsdatei \"" + filename + "\" " + context);
    int nummer = 0;
    FileOutputStream outputStream;
    try {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
      for (Iterator<EineSendung> it = this.iterator(); it.hasNext(); ) {
        EineSendung was = it.next();
        if (debug > 8) Log.i(String.format("r%3d", nummer), was.machDateiname());
        outputStream.write(was.zuRetten().getBytes());
        nummer++;
      }
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
/*
  void retteInSendungsdatei(Context context, int debug, String suchbegriff) {
    this.context = context;
    this.debug = debug;
    this.filename = sendungsDateiname(suchbegriff);
    FileOutputStream outputStream;
    if (debug > 2) Log.i("M050", filename + " " + context);
    int nummer = 0;
    try {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
      for (Iterator<EineSendung> it = this.iterator(); it.hasNext(); ) {
        EineSendung was = it.next();
        if (debug > 8) Log.i("M052", String.format("%3d %s", nummer, was.machDateiname()));
        outputStream.write(was.zuRetten().getBytes());
        nummer++;
      }
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
*/

  // gerufen von downloadXmlTask.onPostExecute
  public DownloadDlfunk machSendungsbuttons() {
    LinearLayout layout = (LinearLayout) activity.findViewById(R.id.welcherTag);
    layout.removeAllViews();
    int nummer = 0;
    for (Iterator<EineSendung> läufer = this.iterator(); läufer.hasNext(); ) {
      EineSendung eineSendung = (läufer.next());
      if (debug > 8) Log.i("E010", String.format("%3d Datei %s", nummer, eineSendung.machDateiname()));
      Button Taste = new Button(activity);
      Taste.setLayoutParams(
          new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT
          )
      );
      final String quellurl = eineSendung.link;
      final String duration = eineSendung.duration;
      final String zieldateiname = eineSendung.machDateiname();
      if (debug > 1)
        Taste.setText("E012 " + eineSendung.buttontext(true, true));
      else
        Taste.setText(eineSendung.buttontext(
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autorPref", false),
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean("zeitstempelPref", false)));
      Taste.setId(16081947 + nummer);
      if (debug > 8) Log.i("E020", quellurl + " -> " + zieldateiname);
      Taste.setOnClickListener(
          new View.OnClickListener() {
            public void onClick(View v) {
              // Perform action on click
              if (debug > 3) Log.i("E030", "Sendung ausgewählt, hol Datei.mp3");
              try {
                if (downloadDlfunk != null) {
                  if (debug > 3) Log.i("E032", "downloadDlfunk.stoppeWiedergabe()");
                  downloadDlfunk.stoppeWiedergabe();
                }
                downloadDlfunk = (DownloadDlfunk)
                    new DownloadDlfunk(
                        activity, context, debug, mediaPlayer)
                        .execute(quellurl, zieldateiname, duration);
//                      .execute(new String[]{quellurl, zieldateiname, duration});
              } catch (Exception e) {
                e.printStackTrace();
              } finally {
                if (debug > 8) Log.i("E040", downloadDlfunk.toString());
              }
              if (debug > 8) Log.i("E050", "Lade " + zieldateiname + " herunter");
              Toast.makeText(context, "Schild-003 Lade " + zieldateiname + " herunter", Toast.LENGTH_SHORT).show();
            }
          }
      );
      //eineSendung.setTaste(Taste);
      layout.addView(Taste);
      nummer++;
    }
    return downloadDlfunk;
  }
}
