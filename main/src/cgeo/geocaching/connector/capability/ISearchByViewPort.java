package cgeo.geocaching.connector.capability;

import cgeo.geocaching.SearchResult;
import cgeo.geocaching.connector.IConnector;
import cgeo.geocaching.location.Viewport;
import cgeo.geocaching.models.Geocache;

import androidx.annotation.NonNull;

import java.util.List;

public interface ISearchByViewPort extends IConnector {
    @NonNull
    SearchResult searchByViewport(@NonNull Viewport viewport);

    @NonNull
    List<Geocache> searchGeocachesByViewport(@NonNull Viewport viewport);
}
