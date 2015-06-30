package com.josenaves.spotifystreamer;


import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrackParcelable implements Parcelable {

    private String trackId;
    private String trackName;
    private String trackAudioUrl;
    private String trackArtUrl;

    private String albumName;

    private String artistId;
    private String artistName;

    public SpotifyTrackParcelable(String trackId, String trackName, String trackAudioUrl, String trackArtUrl, String albumName, String artistId, String artistName) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackAudioUrl = trackAudioUrl;
        this.trackArtUrl = trackArtUrl;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackAudioUrl() {
        return trackAudioUrl;
    }

    public void setTrackAudioUrl(String trackAudioUrl) {
        this.trackAudioUrl = trackAudioUrl;
    }

    public String getTrackArtUrl() {
        return trackArtUrl;
    }

    public void setTrackArtUrl(String trackArtUrl) {
        this.trackArtUrl = trackArtUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackId);
        dest.writeString(trackName);
        dest.writeString(trackAudioUrl);
        dest.writeString(trackArtUrl);
        dest.writeString(albumName);
        dest.writeString(artistId);
        dest.writeString(artistName);
    }

    protected SpotifyTrackParcelable(Parcel in) {
        trackId = in.readString();
        trackName = in.readString();
        trackAudioUrl = in.readString();
        trackArtUrl = in.readString();
        albumName = in.readString();
        artistId = in.readString();
        artistName = in.readString();
    }

    @SuppressWarnings("unused")
    public static final
        Parcelable.Creator<SpotifyTrackParcelable> CREATOR =
            new Parcelable.Creator<SpotifyTrackParcelable>() {

        @Override
        public SpotifyTrackParcelable createFromParcel(Parcel in) {
            return new SpotifyTrackParcelable(in);
        }

        @Override
        public SpotifyTrackParcelable[] newArray(int size) {
            return new SpotifyTrackParcelable[size];
        }
    };

    @Override
    public String toString() {
        return "SpotifyTrackParcelable{" +
                "trackId='" + trackId + '\'' +
                ", trackName='" + trackName + '\'' +
                ", trackAudioUrl='" + trackAudioUrl + '\'' +
                ", trackArtUrl='" + trackArtUrl + '\'' +
                ", albumName='" + albumName + '\'' +
                ", artistId='" + artistId + '\'' +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}
