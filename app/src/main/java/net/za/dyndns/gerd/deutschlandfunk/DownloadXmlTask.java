package net.za.dyndns.gerd.deutschlandfunk;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hanno on 06.06.14.
 */

// Implementation of AsyncTask used to download XML feed from srv.deutschlandradio.de.
// <String, String, Entries>
// <String, für doInBackground und execute(Params )
//  String,    für Progress
//  Entries>  für onPostExecute(String result)
//
class DownloadXmlTask extends AsyncTask<String, String, Menge> {
  /*
  *   android.os.AsyncTask<Params, Progress, Result>
  * 1   Params, the type of the parameters sent to the task upon execution.
  * 2   Progress, the type of the progress units published during the background computation.
  * 3   Result, the type of the result of the background computation.
   * Menge kann in onPostExecute(Menge result) weiterverarbeitet werden.
   * Menge wird von doInBackground(String... urls) geliefert.
   * String1 wird an doInBackground(String... urls) übergeben.
   * String2 kann in onProgressUpdate(Integer... publishedProgress) weiterverarbeitet werden.
  *
  * der Parameter von execute(suchbegriff)
  * ist das Array String1... urls
  * für doInBackground(String1... urls)
  *
   * Overrides
   * Menge doInBackground(String... urls)
   *         darin void publishProgress
   * UI void onPreExecute()
   * UI void onPostExecute(Menge alleSendungen)
   * UI void onProgressUpdate(String... publishedProgress)
   * noch zu erlernen : cancel, isCancelled, onCancelled
   */
  Activity activity;
  Context context;
  int debug;
  DownloadDlfunk downloadDlfunk;
  private Menge allentries;
  private MediaPlayer mxPlayerV0;
  private TextView ladeFortschrittAlsText;
  private boolean bevorzugeNetz;
/*
  public Menge getEntries() {
    return allentries;
  }
*/
  /*
  * gerufen als .execute() von Serien
  */
  DownloadXmlTask(Activity activity, Context context, int debug,
                  DownloadDlfunk downloadDlfunk, MediaPlayer mxPlayerV0,
                  boolean bevorzugeNetz) {
    this.activity = activity;
    //this.context = context;
    this.context = activity.getApplicationContext();
    this.debug = debug;
    this.downloadDlfunk = downloadDlfunk;
    this.mxPlayerV0 = mxPlayerV0;
    this.bevorzugeNetz = bevorzugeNetz;
    //activity.setContentView(R.layout.activity_auswahl); // Entfernt frühere Eintragungen
    ladeFortschrittAlsText = (TextView) this.activity.findViewById(R.id.zeigeJetzt);
    ladeFortschrittAlsText.setText(String.format("Ladefortschritt in DownloadXmlTask"));
  }
/*
  public void stoppeWiedergabe() {
    Log.i("X998", "Stoppe vielleicht downloadDlfunk.stoppeWiedergabe()");
    if (downloadDlfunk != null) {
      Log.i("X999", "downloadDlfunk.stoppeWiedergabe()");
      downloadDlfunk.stoppeWiedergabe();
    }
  }
*/
  /*
   * Der von doInBackground gelieferte Wert wird von
   * onPostExecute(Menge result) weiterverarbeitet.
   * Der dritte Parameter AsyncTask<String, String, Menge>
   * bestimmt den Typ "Menge".
   */
  @Override
  protected Menge doInBackground(String... urls) { // DownloadXmlTask
    // ruft ladeXmlBeschreibungen
    // wird gerufen in stelle
    // als
    // new DownloadXmlTask(
    //      activity, context, this.debug,
    //      prefSeite, downloadDlfunk, mxPlayerV0
    //  ).execute(suchbegriff);
    // urls[0] ist der Parameter von execute im Auruf
    // downloadXmlTask( ... ).execute(suchbegriff)
    String ServerURL =
        "http://srv.deutschlandradio.de/aodlistaudio.1706.de.rpc";
    String suchbegriff = urls[0];
    String myUrlString = ServerURL
        + "?drau:" + suchbegriff;
    // + "&drau:page=" + prefSeite;
    String seitennummerparameter = "&drau:page=";
    // Der Konstruktor ruft ladeAusSendungsdatei nicht
    Menge menge = new Menge(activity, context, debug, mxPlayerV0);
    menge.setSuchbegriff(suchbegriff);
    if (!bevorzugeNetz) {
      // Lade entries aus aus der Datei zum suchbegriff
      // Der Konstruktor ruft ladeAusSendungsdatei
      menge = new Menge(activity, context, debug, mxPlayerV0, suchbegriff);
      if (debug > 8) menge.logge();
      if (debug > 0)
        Log.i("X070", "ladeAusSendungsdatei fand " + menge.size() + " Adressen mit dem Suchbegriff \"" + suchbegriff + "\" in " + menge.seitenanzahl + " Seiten");
      if (debug > 0) Log.i("X070", "InBack start");
      String debugMeldung = "";
      if (debug > 2) {
        debugMeldung = "X071 ";
      }
      publishProgress(
          ""
              + debugMeldung
              + menge.size()
              + " Mp3-HTML-Links aus der Datei "
              + menge.getFilename()
              + " geladen. "
              + menge.seitenanzahl
              + " Seiten, "
              + suchbegriff
      );
    }
    if (menge.isEmpty()) { // Wenn "ladeAusSendungsdatei" nichts fand
      if (debug > 0) Log.i("X093", "InBack wird aus dem Internet lesen.");

      try {
        //xx menge = ladeXmlBeschreibungen(myUrlString, seitennummerparameter, true);
        if (debug > 0) Log.i("X074", "InBack " + myUrlString + " " + seitennummerparameter);
        menge.addAll(ladeXmlBeschreibungen(myUrlString, seitennummerparameter));
      } catch (IOException e) {
        // Laden aus dem Netz geht nicht.
        if (debug > 0) Log.i("X076", "InBack " + "IOException " + e.toString());
        menge.ladeAusSendungsdatei();
      } catch (XmlPullParserException e) {
        if (debug > 0) Log.i("X078", "InBack " + "XmlPullParserException" + e.toString());
        //return new Sendungen(getResources().getString(R.string.xml_error));
      }
      // Wir wissen nun, wieviele Seiten der Server bereithält und
      // könnten sie alle holen.
      if (debug > 0) Log.i("X080", "InBack fand " + menge.size() + " Einträge");
      //xx menge.retteInSendungsdatei(context, debug, suchbegriff);
      menge.retteInSendungsdatei();
    } else { // Wenn "ladeAusSendungsdatei" etwas fand
      if (debug > 0) Log.i("X095", "InBack hat aus der Datei gelesen.");
    }
    menge.zeigeSendungsdatei();
    if (debug > 0)
      Log.i("X097", "InBack fertig " + menge.size() + " Adressen in " + menge.seitenanzahl + " Seiten");
    return menge; // Argument von onPostExecute
  }

  @Override
  protected void onPreExecute() { // DownloadXmlTask
    //activity.setContentView(R.layout.activity_auswahl); // Entfernt frühere Eintragungen
    //myTextView = (TextView) activity.findViewById(R.id.textView1);
  }

  @Override
  protected void onProgressUpdate(String... publishedProgress) { // DownloadXmlTask
    // publishProgress liefert die hier verwendeten publishedProgresses
    // super.onProgressUpdate(publishedProgress); // nichts wird sichtbar
    // ladeOderSpielFortschritt.setText(String.format(
    // "%02d%% Ladefortschritt von %d", result[0], result[1]));
    // myTextView.setText(publishedProgress[0]);
    String fortschritt = publishedProgress[0];
    if (debug > 0) Log.i("X010", fortschritt);
    if (debug > 0) fortschritt = "X010 " + fortschritt;
    ladeFortschrittAlsText.setText(fortschritt);
    //super.onProgressUpdate(publishedProgress);
  }

  @Override
  // DownloadXmlTask.doInBackground liefert Menge
  protected void onPostExecute(Menge alleSendungen) {
    this.allentries = alleSendungen;
    // Erstelle je einen Button für jede Sendung.
    //010Sendungsbuttons(alleSendungen);
    // ToDo : Buttons schon beim Laden herstellen.
    if (alleSendungen != null)
      if (alleSendungen.size() > 0)
        if (debug > 2)
          Log.i("X090", "PostExec " + alleSendungen.size() + " Links, erster: " + alleSendungen.erst().link);

/*
Plan geändert: Nichts wird gelöscht.
Plan:
wifiConnected beachten
Die Sendungsdatei soll erst gelöscht werden,
wenn es gelungen ist,
eine neue aus dem Internet vom Deutschlandfunk zu laden.
Also nicht löschen, sondern umbenennen.
Dann eine neue laden,und wenn
Erfolg      : Umbenannte Datei löschen.
Misserfolg  : Umbenennung zurücknehmen.
"löscheSendungsdatei" ist eine Methode der Klasse "Menge".

*/

    //downloadDlfunk = new MachSendungsButtons(activity, context, debug, mxPlayerV0)
    //    .machSendungsbuttons(alleSendungen);
    if (alleSendungen != null) {
      downloadDlfunk = alleSendungen.machSendungsbuttons();
    } else {
      downloadDlfunk = null;
    }
    if (downloadDlfunk == null) {
      if (debug > 3) Log.i("X091", "PostExec " + "downloadDlfunk==null");
    } else {
      if (debug > 3) Log.i("X091", "PostExec " + "downloadDlfunk!=null");
    }
  }

  /**
   * Lädt XML von srv.deutschlandradio.de, parses it,
   * and combines it with HTML markup.
   * Liefert eine Liste "entries" aufgezeichneter Sendungen mit ihren Eigenschaften.
   * gerufen von doInBackground, ruft downloadUrl
   * Parst den von downloadUrl gelieferten Stream und
   * publiziert den Fortschritt mittels "publishProgress".
   */
  private Menge ladeXmlBeschreibungen(
      String urlString,
      String seitennummerparameter)
      throws XmlPullParserException, IOException {

    String nummertext = PreferenceManager.getDefaultSharedPreferences(context)
        .getString("seitennummerPref", "1");
    int gewünschteSeite = 1;
    if (debug > 2) Log.i("X050", "ladeXmlBeschreibungen " + urlString);
    try {
      gewünschteSeite = Integer.parseInt(nummertext);
      if (debug > 2) Log.i("X052", "gewünschte Seite = " + nummertext);
    } catch (NumberFormatException e) {
      //Will Throw exception!
      //do something! anything to handle the exception.
      if (debug > 2) Log.i("X054", nummertext + "!" + e.toString());
    }
    if (debug > 2) Log.i("X050", ((gewünschteSeite == 0) ? "alle" : "eine") + " " + 0 + " Seiten");

    DeutschlandradioXmlParser dlfunkXmlParser = new DeutschlandradioXmlParser(debug);
    Menge entries = null; // Liste aufgezeichneter Sendungen
    Menge einigeSendungen = null; // Liste einiger aufgezeichneter Sendungen
    Calendar rightNow = Calendar.getInstance();
    //DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    int letzteNummer = 1, lfdSeite = 1;
    boolean alleSeiten = (gewünschteSeite == 0);
    if (!alleSeiten) letzteNummer = lfdSeite = gewünschteSeite;
    do {
      // Hole die Meta-Daten aufgezeichneter Sendungen aus dem Internet
      String urlStringMitSeite = urlString + seitennummerparameter + lfdSeite;
      // http://srv.deutschlandradio.de/aodlistaudio.1706.de.rpc?drau:searchterm=forschung+aktuell&drau:page=4
      if (debug > 8) Log.i("X040", urlStringMitSeite);
      InputStream stream = downloadUrl(urlStringMitSeite);
      einigeSendungen = dlfunkXmlParser.parseEine(stream);
      if (entries == null) {
        entries = einigeSendungen;
      } else {
        entries.addAll(einigeSendungen);
      }
      if (stream != null) stream.close();
      if (alleSeiten)
        letzteNummer = entries.seitenanzahl;
      String debugMeldung = "";
      if (debug > 2) {
        debugMeldung = "X048 ";
      }
      publishProgress(  // die Parameter werden von onProgressUpdate verarbeitet
          String.format("%s%02d%% %02d von %02d %s XML-Ladefortschritt",
              debugMeldung,
              lfdSeite * 100 / letzteNummer,
              lfdSeite,
              letzteNummer,
              urlStringMitSeite)
      );
      lfdSeite++;
      if (debug > 8) Log.i("X049", "lfdSeite=" + lfdSeite);
    } while (!entries.isEmpty() && lfdSeite <= letzteNummer);
    if (debug > 2)
      if (entries.isEmpty())
        Log.i("X056", ((gewünschteSeite == 0) ? "alle" : "eine") + " " + 0 + " Seiten");
      else
        Log.i("X058", ((gewünschteSeite == 0) ? "alle" : "eine") + " " + entries.seitenanzahl + " Seiten von " + urlString);
    return entries;
  }

  // Given a string representation of a URL, sets up a connection and gets
  // an input stream.
  private InputStream downloadUrl(String urlString) throws IOException {
    // gerufen von ladeXmlBeschreibungen
    if (debug > 8) Log.i("X042", "streaming " + urlString);
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setReadTimeout(10000 /* milliseconds */);
    conn.setConnectTimeout(15000 /* milliseconds */);
    conn.setRequestMethod("GET");
    conn.setDoInput(true);
    // Starts the query
    conn.connect();
    //this.gelieferteHeader = conn.getHeaderFields().toString();
    InputStream stream;
    stream = conn.getInputStream();
    if (debug > 0) Log.i("X044", "streaming " + urlString);
    if (debug > 8) Log.i("X046", "stream " + stream);
    return stream;
  }
}
