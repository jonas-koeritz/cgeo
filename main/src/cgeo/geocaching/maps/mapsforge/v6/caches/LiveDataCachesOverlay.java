package cgeo.geocaching.maps.mapsforge.v6.caches;

import cgeo.geocaching.maps.mapsforge.v6.MapHandlers;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.GeocacheWithWaypoints;
import cgeo.geocaching.persistence.entities.Waypoint;
import cgeo.geocaching.persistence.util.DownloadStatus;
import cgeo.geocaching.settings.Settings;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mapsforge.map.layer.Layer;


public class LiveDataCachesOverlay extends AbstractCachesOverlay {
    private LifecycleOwner owner;
    private LiveData<List<Geocache>> caches;

    private LiveData<DownloadStatus> downloadStatus;

    public LiveDataCachesOverlay(final LiveData<List<Geocache>> caches, final LiveData<DownloadStatus> downloadStatus, final LifecycleOwner owner, final int overlayId, final Set<GeoEntry> geoEntries, final CachesBundle bundle, final Layer anchorLayer, final MapHandlers mapHandlers) {
        super(overlayId, geoEntries, bundle, anchorLayer, mapHandlers);

        this.caches = caches;
        this.owner = owner;
        this.downloadStatus = downloadStatus;

        this.caches.observe(this.owner, geocaches -> {
            update(geocaches);
            refreshed();
        });
    }

    @Override
    public void invalidate(final Collection<String> invalidGeocodes) {
        // Nothing to invalidate with LiveData
    }

    public boolean isDownloading() {
        return downloadStatus.getValue() == DownloadStatus.LOADING;
    }

    // TODO implement title updating
}
