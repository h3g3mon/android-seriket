package de.claudia.myfirstapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlToOutputConverter {
	private static final String ns = null;

	public List<Series> getXmlFromTask(String seriesId) throws XmlPullParserException {
		// Search
		Search searchTvDB = new Search();
		String result = null;
		InputStream is = null;
		List<Series> series = null;
		try {
			result = searchTvDB.searchTvDB(seriesId);
			is = new ByteArrayInputStream(result.getBytes());
			series = parse(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return series;
	}

	private List<Series> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readData(parser);
		} finally {
			in.close();
		}
	}

	private List<Series> readData(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Series> entries = new ArrayList<Series>();

		parser.require(XmlPullParser.START_TAG, ns, "Data");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("Series")) {
				entries.add(readSeries(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private Series readSeries(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "Series");
		String seriesName = null;
		String overview = null;
		String seriesId = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("SeriesName")) {
				seriesName = readSeriesName(parser);
			} else if (name.equals("Overview")) {
				overview = readOverview(parser);
			} else if (name.equals("seriesid")) {
				seriesId = readSeriesId(parser);
			} else {
				skip(parser);
			}
		}
		return new Series(seriesName, overview, seriesId);
	}

	// Processes title tags in the feed.
	private String readSeriesName(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "SeriesName");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "SeriesName");
		return title;
	}

	// Processes link tags in the feed.
	private String readSeriesId(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String link = "";
		parser.require(XmlPullParser.START_TAG, ns, "seriesid");
		link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "seriesid");
		return link;
	}

	// Processes summary tags in the feed.
	private String readOverview(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "Overview");
		String summary = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "Overview");
		return summary;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
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
}