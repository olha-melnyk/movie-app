package ws.bilka.movieapp.model;

public class NowPlayingItem {
    private String nowPlayingTitle;
    private String nowPlayingRating;
    private String nowPlayingImage;
    private String nowPlayingOverview;
    private String nowPlayingYear;
    private String nowPlayingPoster;
    private String movieId;

    public NowPlayingItem() {
    }


    public String getNowPlayingTitle() {
        return nowPlayingTitle;
    }

    public void setNowPlayingTitle(String nowPlayingTitle) {
        this.nowPlayingTitle = nowPlayingTitle;
    }

    public String getNowPlayingRating() {
        return nowPlayingRating;
    }

    public void setNowPlayingRating(String nowPlayingRating) {
        this.nowPlayingRating = nowPlayingRating;
    }

    public String getNowPlayingImage() {
        return nowPlayingImage;
    }

    public void setNowPlayingImage(String nowPlayingImage) {
        this.nowPlayingImage = nowPlayingImage;
    }

    public String getNowPlayingOverview() {
        return nowPlayingOverview;
    }

    public void setNowPlayingOverview(String nowPlayingOverview) {
        this.nowPlayingOverview = nowPlayingOverview;
    }

    public String getNowPlayingYear() {
        return nowPlayingYear;
    }

    public void setNowPlayingYear(String nowPlayingYear) {
        this.nowPlayingYear = nowPlayingYear;
    }

    public String getNowPlayingPoster() {
        return nowPlayingPoster;
    }

    public void setNowPlayingPoster(String nowPlayingPoster) {
        this.nowPlayingPoster = nowPlayingPoster;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
