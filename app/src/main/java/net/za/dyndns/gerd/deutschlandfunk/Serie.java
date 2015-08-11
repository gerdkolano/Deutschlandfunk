package net.za.dyndns.gerd.deutschlandfunk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by hanno on 2014-08-29 18:32.
 */
public class Serie  implements Comparable<Serie> {
  private String menschenlesbarerName;
  private String suchbegriff;

  Serie() {}

  Serie(String menschenlesbarerName, String suchbegriff) {
    this.menschenlesbarerName = menschenlesbarerName;
    this.suchbegriff = suchbegriff;
  }

  public Serie(String inputText) {
    menschenlesbarerName = inputText;
    suchbegriff = "";
    try {
      suchbegriff = URLEncoder.encode(menschenlesbarerName, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    suchbegriff = "searchterm=" + suchbegriff;
  }

  public boolean allesGesammelt(String strLine, int zeile) {
    switch (zeile % 2) {
      case 0:menschenlesbarerName = strLine; return false;
      case 1:suchbegriff = strLine; return true;
      default:return true;
    }
  }

  public String getMenschenlesbarerName() {
    return this.menschenlesbarerName;
  }

  public String getSuchbegriff() {
    return this.suchbegriff;
  }

  public String zuRetten() {
    // Schreibe 2 Zeilen
    String erg = String.format("%s\n%s\n",
        this.menschenlesbarerName,
        this.suchbegriff
    );
    return erg;
  }


  @Override
  public int compareTo(Serie serie) {
    return menschenlesbarerName.compareToIgnoreCase(serie.getMenschenlesbarerName());
  }
}
