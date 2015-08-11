package net.za.dyndns.gerd.deutschlandfunk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hanno on 2015-07-23 23:20.
 */
public class CheckOnlineStatus extends BroadcastReceiver {
  /**
   * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
   * which indicates a connection change. It checks whether the type is TYPE_WIFI.
   * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
   * main activity accordingly.
   */
  public boolean refreshDisplay;
  private int debug=1;
  private String ANY = "xxx", WIFI = "xxx", sPref = "xxx";
  //public class NetworkReceiver extends BroadcastReceiver

CheckOnlineStatus(int debug){
  this.debug = debug;
}
  @Override
  public void onReceive(Context context, Intent intent) {
    ConnectivityManager connMgr =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    // Checks the user prefs and the network connection. Based on the result, decides
    // whether
    // to refresh the display or keep the current display.
    // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
    String meldung;
    if (debug > 3) Log.i("N010", "onReceive");
    if (WIFI.equals(sPref) && networkInfo != null
        && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
      meldung = context.getResources().getString(R.string.wifi_connected);
      if (debug > 0) Log.i("N020", meldung);
      // If device has its Wi-Fi connection, sets refreshDisplay
      // to true. This causes the display to be refreshed when the user
      // returns to the app.
      refreshDisplay = true;
      Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

      // If the setting is ANY network and there is a network connection
      // (which by process of elimination would be mobile), sets refreshDisplay to true.
    } else if (ANY.equals(sPref) && networkInfo != null) {
      if (debug > 0) Log.i("N030", "Diese Anweisung wird nicht erreicht");
      refreshDisplay = true;

      // Otherwise, the app can't download content--either because there is no network
      // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
      // is no Wi-Fi connection.
      // Sets refreshDisplay to false.
    } else {
      meldung = context.getResources().getString(R.string.lost_connection);
      if (debug > 0) Log.i("N040", meldung);
      refreshDisplay = false;
      Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
    }
  }
}

