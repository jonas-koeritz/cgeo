package cgeo.geocaching.persistence.util;

public class DownloadStatus {
    public enum Status {
        IDLE,
        SUCCESS,
        ERROR,
        LOADING
    }

    public Status status;
    public String message;
    public Exception error;

    public DownloadStatus(final Status status, final String message) {
        this.status = status;
        this.message = message;
    }

    public DownloadStatus() {
        this.status = Status.IDLE;
        this.message = "";
        this.error = null;
    }

    public static DownloadStatus Loading(final String message) {
        return new DownloadStatus(Status.LOADING, message);
    }

    public static DownloadStatus Success(final String message) {
        return new DownloadStatus(Status.SUCCESS, message);
    }

    public static DownloadStatus Error(final String message, final Exception error) {
        final DownloadStatus status = new DownloadStatus(Status.ERROR, message);
        status.error = error;
        return status;
    }
}