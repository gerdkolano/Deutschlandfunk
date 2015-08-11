package net.za.dyndns.gerd.deutschlandfunk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by hanno on 2015-07-27 13:31.
 */
public class Helfer {
  private Context context;
  private int debug;

  Helfer(Context context, int debug) {
    this.context = context;
    this.debug = debug;
  }

  public void rettePräferenz(String speicher, String wort) {
    SharedPreferences mySharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    SharedPreferences.Editor editor = mySharedPrefs.edit();
    editor.putString(speicher, wort);
      logi(2, "SR80", speicher + "=\"" + wort + "\"" + (editor.commit() ? "" : " nicht") + " commit'ed");
  }

  public void logi(int debug, String tag, String msg) {
    if (this.debug > debug) Log.i(tag, msg);
    //Log.i(tag, "" + debug + " > " + this.debug + " " + msg);
  }

}
