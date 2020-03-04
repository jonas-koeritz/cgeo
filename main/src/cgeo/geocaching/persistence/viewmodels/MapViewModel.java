package cgeo.geocaching.persistence.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import org.mapsforge.map.datastore.Way;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgeo.geocaching.location.Viewport;
import cgeo.geocaching.maps.MapMode;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.GeocacheWithWaypoints;
import cgeo.geocaching.persistence.entities.Waypoint;
import cgeo.geocaching.persistence.repositories.GeocacheRepository;
import cgeo.geocaching.persistence.repositories.WaypointRepository;
import cgeo.geocaching.persistence.util.DownloadStatus;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.utils.Log;

import static cgeo.geocaching.maps.MapMode.LIST;
import static cgeo.geocaching.maps.MapMode.SINGLE;

public class MapViewModel extends AndroidViewModel {
    private GeocacheRepository geocacheRepository;
    private WaypointRepository waypointRepository;

    private MediatorLiveData<List<Geocache>> visibleGeocaches;
    private MediatorLiveData<List<Waypoint>> visibleWaypoints;
    private LiveData<DownloadStatus> downloadStatus;

    private LiveData<List<Geocache>> liveMapGeocaches;
    private LiveData<List<Geocache>> listGeocaches;
    private LiveData<List<Waypoint>> waypoints;

    private MapMode mapMode = MapMode.LIVE;

    public MapViewModel(final Application application) {
        super(application);

        geocacheRepository = new GeocacheRepository(application);
        waypointRepository = new WaypointRepository(application);

        visibleGeocaches = new MediatorLiveData<>();
        visibleWaypoints = new MediatorLiveData<>();

        downloadStatus = geocacheRepository.getDownloadStatus();
    }

    public LiveData<List<Geocache>> getVisibleGeocaches() {
        return visibleGeocaches;
    }
    public LiveData<List<Waypoint>> getVisibleWaypoints() {
        return visibleWaypoints;
    }

    public LiveData<DownloadStatus> getDownloadStatus() {
        return downloadStatus;
    }

    public void setMapMode(final MapMode mode) {
        Log.d(String.format("Changing MapMode to %s", mode));
        this.mapMode = mode;

        clearSources();
        switch (mapMode) {
            case LIST:
            case SINGLE:
                if (listGeocaches != null) {
                    visibleGeocaches.addSource(listGeocaches, caches -> visibleGeocaches.setValue(caches));
                }
                if (waypoints != null) {
                   visibleWaypoints.addSource(waypoints, w -> visibleWaypoints.setValue(w));
                }
                break;
            case COORDS:
                break;
            default:
                if (liveMapGeocaches != null) {
                    visibleGeocaches.addSource(liveMapGeocaches, caches -> visibleGeocaches.setValue(caches));
                }
        }
    }

    private void clearSources() {
        if (liveMapGeocaches != null) {
            visibleGeocaches.removeSource(liveMapGeocaches);
        }

        if (listGeocaches != null) {
            visibleGeocaches.removeSource(listGeocaches);
        }
    }

    public void setCurrentViewport(final Viewport viewport, final boolean loadLiveCaches, final boolean activeCachesOnly, final boolean excludeOwnedCaches, final boolean excludeFoundCaches) {
        // Replaces the previous LiveData source with the new source created for the new Viewport
        clearSources();

        switch (mapMode) {
            case LIST:
            case SINGLE:
                visibleGeocaches.addSource(listGeocaches, caches -> visibleGeocaches.setValue(caches));
                break;
            case COORDS:
                break;
            default: // Default to LIVE
                liveMapGeocaches = geocacheRepository.getCachesInViewport(viewport, loadLiveCaches, activeCachesOnly, excludeOwnedCaches, excludeFoundCaches);
                visibleGeocaches.addSource(liveMapGeocaches, caches -> visibleGeocaches.setValue(caches));
        }
    }

    public void showWaypoints(final Viewport viewport, final boolean showWaypoints) {
        if (waypoints != null) {
            visibleWaypoints.removeSource(waypoints);
        }

        if (showWaypoints || mapMode == LIST || mapMode == SINGLE) {
            waypoints = waypointRepository.getWaypointsInViewport(viewport);
            visibleWaypoints.addSource(waypoints, w -> visibleWaypoints.setValue(w));
        } else {
            visibleWaypoints.setValue(new ArrayList<>());
        }
    }

    public void setGeocacheList(final Set<String> geocodes) {
        // TODO implement / test list handling
        this.mapMode = LIST;
        listGeocaches = geocacheRepository.getCachesByGeocode(geocodes);
    }
}
