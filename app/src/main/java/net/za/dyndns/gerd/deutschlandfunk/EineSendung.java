package net.za.dyndns.gerd.deutschlandfunk;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hanno on 06.06.14.
 * This class represents a single entry (post) in the XML feed.
 * It includes the data members "title," "link," and "zeitstempel."
 */
public class EineSendung implements Comparable<EineSendung> { // benötigt compareTo(EineSendung entry)
  public String title;
  public String autor;
  public int seitenanzahl;
  public int seitennummer;
  public String link;
  public String duration;
  public String zeitstempel;
  public String htmlDarstellung;
  public Button taste;
  public int debug;
  public static final int siebenZeilen = 7;
  private static Pattern sixpart = Pattern.compile("(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)");
  private String[] tagesName = {"Sa", "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};

  public EineSendung(String title, String autor,
                     int seitenanzahl, int seitennummer,
                     String zeitstempel, String link, String duration, int debug) {
    int sek = 0;
    try {
      sek = Integer.parseInt(duration);
    } catch (NumberFormatException e) {
      sek = 0;
    }
    String minSek = String.format("%d:%02d", sek / 60, sek % 60);
    this.title = title;
    this.autor = autor;
    this.seitenanzahl = seitenanzahl;
    this.seitennummer = seitennummer;
    this.zeitstempel = zeitstempel;
    this.link = link;
    this.duration = minSek;
    this.htmlDarstellung = "";
    this.debug = debug;
  }

  public EineSendung(int debug) {
    this.title = null;
    this.autor = null;
    this.seitenanzahl = 0;
    this.seitennummer = 0;
    this.zeitstempel = null;
    this.link = null;
    this.duration = null;
    this.htmlDarstellung = "";
    this.taste = null;
    this.debug = debug;
  }

  /*
  Die Monatsnummer im zeitstempel liegt in 1..12, aber
  GregorianCalendar erwartet sie in 0..11.
   */
  private String wochentag() {
    Matcher m = sixpart.matcher(zeitstempel);
    if (m.matches()) {
      int Jahr = Integer.parseInt(m.group(1));
      int Monat = Integer.parseInt(m.group(2)) - 1;
      int Tag = Integer.parseInt(m.group(3));
/*
      int Stunde = Integer.parseInt(m.group(4));
      int Minute = Integer.parseInt(m.group(5));
      int Sekunde = Integer.parseInt(m.group(6));
*/
      GregorianCalendar dieserTag = new GregorianCalendar(Jahr, Monat, Tag);
      int tagDerWoche = dieserTag.get(Calendar.DAY_OF_WEEK);
      return tagesName[tagDerWoche];
    }
    return "";
  }

  @Override
  public int compareTo(@NonNull EineSendung eineSendung) {  // für "implements Comparable<EineSendung>"
    //return this.link.compareTo(eineSendung.link);
    int lastCmp = eineSendung.zeitstempel.compareTo(this.zeitstempel);
    return (lastCmp != 0 ? lastCmp : eineSendung.link.compareTo(this.link));
    //return eineSendung.link.compareTo(this.link);
  }

  public void setTaste(Button taste) {
    this.taste = taste;
  }

  /*
    public void machHtml(
        boolean verweisPref,
        boolean zeitstempelPref,
        boolean autorPref) {
      StringBuilder htmlInhalt = new StringBuilder("");
      htmlInhalt.append("<p>");
  //      htmlInhalt.append(this.abspielgerät + "/");
  //      htmlInhalt.append(this.seitenanzahl + " ");
      if (verweisPref) htmlInhalt.append(link + " ");
      htmlInhalt.append("<a href='" + link + "'>");
      htmlInhalt.append(title + "</a></p>");
      // If the user set the preference to include zeitstempel text,
      // adds it to the display.
      if (zeitstempelPref) htmlInhalt.append("Sendezeit: " + zeitstempel);
      if (autorPref) htmlInhalt.append(" Autor: " + autor);
      if (durationPref) htmlInhalt.append(" Dauer: " + duration);
      htmlDarstellung = htmlInhalt.toString();
    }
  */
  public String machDateiname() {
    String zwerg = link;
    zwerg = zwerg.replaceFirst(".*/.*?_", "");
    zwerg = zwerg.replaceFirst("([0-9_]*)_.*(\\..*)", "$1$2");
    return zwerg;
  }

  public CharSequence buttontext(boolean autor, boolean zeit, boolean duration) {
    if (debug > 1)
      return ""
          + this.wochentag() + " "
          + this.seitennummer + "/"
          + this.seitenanzahl + " "
          + this.autor + " "
          + this.duration + " "
          + this.zeitstempel + " "
          + this.title + " -> "
          + this.machDateiname();
    else
      return ""
          + this.wochentag() + " "
          + (duration ? (this.duration + " \u2014 ") : "") // em-dash
          + (zeit ? (this.zeitstempel + " \u2014 ") : "") // em-dash
          + (autor ? (this.autor + " \u2014 ") : "") // em-dash
          + this.title;
  }

  public String zuRetten() {
    // Schreibe siebenZeilen Zeilen
    return String.format("%s\n%s\n%d\n%d\n%s\n%s\n%s\n",
        this.title,
        this.autor,
        this.seitenanzahl,
        this.seitennummer,
        this.zeitstempel,
        this.link,
        this.duration
    );
  }

  public boolean allesGesammelt(String strLine, int zeile) {
    switch (zeile % siebenZeilen) {
      case 0:
        title = strLine;
        return false;
      case 1:
        autor = strLine;
        return false;
      case 2:
        try {
          seitenanzahl = Integer.parseInt(strLine);
        } catch (NumberFormatException e) {
          seitenanzahl = 0;
        }
        return false;
      case 3:
        try {
          seitennummer = Integer.parseInt(strLine);
        } catch (NumberFormatException e) {
          seitennummer = 0;
        }
        return false;
      case 4:
        zeitstempel = strLine;
        return false;
      case 5:
        link = strLine;
        return false;
      case 6:
        duration = strLine;
      default:
        return true;
    }
  }
/*
  public void ladeNach(String strLine, int zeile) {
    // Print the content on the console
    if (debug > 8) Log.i("Lade", strLine);
    //System.out.println(strLine);
    switch (zeile % siebenZeilen) {
      case 0:
        title = strLine;
        break;
      case 1:
        autor = strLine;
        break;
      case 2:
        try {
          seitenanzahl = Integer.parseInt(strLine);
        } catch (NumberFormatException e) {
          seitenanzahl = 0;
        }
        break;
      case 3:
        try {
          seitennummer = Integer.parseInt(strLine);
        } catch (NumberFormatException e) {
          seitennummer = 0;
        }
        break;
      case 4:
        zeitstempel = strLine;
        break;
      case 5:
        link = strLine;
        break;
      case 6:
        duration = strLine;
        break;
      default:
        break;
    }
  }
*/
}

