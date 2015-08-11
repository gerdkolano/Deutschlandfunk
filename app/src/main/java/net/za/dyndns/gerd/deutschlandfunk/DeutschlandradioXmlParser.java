package net.za.dyndns.gerd.deutschlandfunk;

import android.util.Xml;
/*
 * cp -auv
 * /home/hanno/androwork/DlfForschungAktuell/forschungaktuell/libs/commons-lang3-3.3.2.jar
 * /zoe-home/zoe-hanno/android/Deutschlandfunk/favoriten/libs/
 * in build.gradle
 * in /zoe-home/zoe-hanno/android/Deutschlandfunk/favoriten/build.gradle
 *     compile files('libs/commons-lang3-3.3.2.jar')
 * Synchronize
*/
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hanno on 06.06.14.
 * This class parses XML feeds from srv.deutschlandradio.de.
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class DeutschlandradioXmlParser {
  private static final String ns = null;
  // We don't use namespaces
  private int seitenanzahl = 1;
  private int seitennummer = 0;
  private int debug = 8;
  
  public DeutschlandradioXmlParser( int debug) {
    this.debug = debug;
  }

  public Menge parseEine(InputStream in) throws XmlPullParserException, IOException {
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(in, null);
      parser.nextTag();
      return mengeReadFromFeed(parser);
    } finally {
      in.close();
    }
  }

  private Menge mengeReadFromFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
    Menge entries = new Menge();

    parser.require(XmlPullParser.START_TAG, ns, "entries");
    String name = parser.getName();
    if (name.equals("entries")) {
      liesseitenanzahl(parser);
    }
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      name = parser.getName();
      // Starts by looking for the item tag
      if (name.equals("item")) {
        EineSendung eineSendung = entrySyntaktischZergliedert(parser);
        entries.seitenanzahl = eineSendung.seitenanzahl;
        entries.add(eineSendung);
      } else {
        skip(parser);
      }
    }
    return entries;
  }

  // Parses the contents of an entry. If it encounters a title, zeitstempel, or link tag, hands them
  // off
  // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
  private EineSendung entrySyntaktischZergliedert(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, ns, "item");
    String title = null;
    String autor = null;
    String zeitstempel = null;
    String link = null;
    String duration = null;
    String station = null;
    String sendung = null;
    String name = parser.getName();
    if (name.equals("item")) {
      Item item = readLink(parser);
      link = item.getLink();
      duration = item.getDuration();
    }
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() != XmlPullParser.START_TAG) {
        continue;
      }
      name = parser.getName();
      if (name.equals("datetime")) zeitstempel = liesEinfachenTag(parser, name);
      else if (name.equals("title")) title     = liesEinfachenTag(parser, name);
      else if (name.equals("author")) autor    = liesEinfachenTag(parser, name);
      else if (name.equals("station")) station = liesEinfachenTag(parser, name);
      else if (name.equals("sendung")) sendung = liesEinfachenTag(parser, name);
      else skip(parser);
    }
    return new EineSendung(title, autor,
        this.seitenanzahl, this.seitennummer,
        zeitstempel, link, duration, debug);
  }

  /*
  <entries pages="3" page="1">
    <item i="0" id="265975" file_id="e8a1ad81" url="http://ondemand-mp3.dradio.de/file/dradio/2014/03/29/dlf_20140329_1630_e8a1ad81.mp3" timestamp="1396107002" duration="1740" station="4">
      <datetime>2014-03-29 16:30:02</datetime>
      <title>Computer und Kommunikation 29.03.2014, komplette Sendung</title>
      <author>Manfred Kloiber</author>
      <station>DLF</station>
      <sendung id="101">Computer und Kommunikation</sendung>
      <article id=""/>
    </item>
    <item i="1" id="264256" file_id="82974c43" url="http://ondemand-mp3.dradio.de/file/dradio/2014/03/22/dlf_20140322_1630_82974c43.mp3" timestamp="1395502200" duration="1785" station="4">
      <datetime>2014-03-22 16:30:00</datetime>
      <title>Computer und Kommunikation 22.03.2014, komplette Sendung</title>
      <author>Kloiber, Manfred</author>
      <station>DLF</station>
      <sendung id="101">Computer und Kommunikation</sendung>
      <article id=""/>
    </item>
  </entries>
  */
  // Processes title tags in the feed.
  private String liesEinfachenTag(XmlPullParser parser, String schlüssel)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, ns, schlüssel);
    String inhalt = readText(parser);
    parser.require(XmlPullParser.END_TAG, ns, schlüssel);
    return inhalt;
  }

  // Hole die Anzahl der Seiten aus dem Feed.
  private void liesseitenanzahl(XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, ns, "entries");
    String anzahltext = parser.getAttributeValue(null, "pages");
    if (anzahltext == null) anzahltext = "1";
    String nummertext = parser.getAttributeValue(null, "page");
    if (nummertext == null) nummertext = "0";
    try {
      seitenanzahl = Integer.parseInt(anzahltext);
    } catch (NumberFormatException e) {
      seitenanzahl = 1;
    }
    try {
      seitennummer = Integer.parseInt(nummertext);
    } catch (NumberFormatException e) {
      seitennummer = 0;
    }
  }

  // Processes link tags in the feed.
  private Item readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
    String link = "leerer Verweis";
    String duration = "";
    String anderes = "";
    parser.require(XmlPullParser.START_TAG, ns, "item");
    anderes = parser.getAttributeValue(null, "i");
    anderes = parser.getAttributeValue(null, "id");
    anderes = parser.getAttributeValue(null, "file_id");
    link = parser.getAttributeValue(null, "url");
    anderes = parser.getAttributeValue(null, "timestamp");
    duration = parser.getAttributeValue(null, "duration");
    anderes = parser.getAttributeValue(null, "station");
    return new Item(link, duration);
  }

  // Für einfache Tags wie title and zeitstempel, extracts their text values.
  private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
    String result = "";
    if (parser.next() == XmlPullParser.TEXT) {
      result = parser.getText();
      parser.nextTag();
    }
    return result;
  }

  // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
  // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
  // finds the matching END_TAG (as indicated by the value of "depth" being 0).
  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }

  private class Item {
    private String link;
    private String duration;

    Item(String link, String duration) {
      this.link = link;
      this.duration = duration;
    }
    String getLink() {return this.link;}
    String getDuration() {return this.duration;}
  }
}
