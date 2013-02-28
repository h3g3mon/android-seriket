package de.claudia.myfirstapp;

public class Series {
    private final String seriesName;
    private final String seriesId;
    private final String overview;

    public String getSeriesName() {
		return seriesName;
	}

	public String getSerieId() {
		return seriesId;
	}

	public String getOverview() {
		return overview;
	}

	Series(String seriesName, String overview, String seriesId) {
        this.seriesName = seriesName;
        this.overview = overview;
        this.seriesId = seriesId;
    }
}
