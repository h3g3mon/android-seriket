package de.claudia.myfirstapp;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Intent intent = getIntent();
		String searchTerm = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		// Search
		List<?> series = null;
		XmlToOutputConverter converter = new XmlToOutputConverter();

		View layout = null;

		try {

			series = converter.getXmlFromTask(searchTerm);
			layout = displaySeries(series);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set the text view as the activity layout
		setContentView(layout);

	}

	public View displaySeries(List<?> series) throws InterruptedException,
			ExecutionException {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.activity_display_message, null);
		ImageView seriesPoster = (ImageView) layout
				.findViewById(R.id.seriesPoster);
		TextView overview = (TextView) layout.findViewById(R.id.overview);
		overview.setTextSize(16);

		// Create the text view
		for (int i = 0; i < series.size(); i++) {

			Series show = (Series) series.get(i);
			seriesPoster.setImageBitmap(getImageBySeriesId(show.getSerieId()));
			overview.setText(show.getOverview());
			setTitle(show.getSeriesName());
		}

		return layout;
	}

	private Bitmap getImageBySeriesId(String seriesId) {

		try {
			
			DownloadImageTask downloadImage = new DownloadImageTask();
			downloadImage.execute("http://www.thetvdb.com/banners/posters/" + seriesId + "-10.jpg");

			Bitmap poster = downloadImage.get();
		
			return poster;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
