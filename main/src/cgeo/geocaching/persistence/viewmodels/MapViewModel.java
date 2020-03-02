package cgeo.geocaching.persistence.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.Set;

import cgeo.geocaching.location.Viewport;
import cgeo.geocaching.maps.MapMode;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.repositories.GeocacheRepository;
import cgeo.geocaching.persistence.util.DownloadStatus;
import cgeo.geocaching.utils.Log;

public class MapViewModel extends AndroidViewModel {
    private GeocacheRepository geocacheRepository;
    private MediatorLiveData<List<Geocache>> visibleGeocaches;
    private LiveData<DownloadStatus> downloadStatus;

    private LiveData<List<Geocache>> liveMapGeocaches;
    private LiveData<List<Geocache>> listGeocaches;

    private MapMode mapMode = MapMode.LIVE;

    public MapViewModel(final Application application) {
        super(application);

        geocacheRepository = new GeocacheRepository(application);
        visibleGeocaches = new MediatorLiveData<>();
        downloadStatus = geocacheRepository.getDownloadStatus();
    }

    public LiveData<List<Geocache>> getVisibleGeocaches() {
        return visibleGeocaches;
    }

    public LiveData<DownloadStatus> getDownloadStatus() {
        return downloadStatus;
    }

    public void setMapMode(final MapMode mode) {
        Log.d(String.format("Changing MapMode to %s", mode));
        this.mapMode = mode;

        clearCacheSources();
        switch (mapMode) {
            case LIST:
            case SINGLE:
                if (listGeocaches != null) {
                    visibleGeocaches.addSource(listGeocaches, caches -> visibleGeocaches.setValue(caches));
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

    private void clearCacheSources() {
        if (liveMapGeocaches != null) {
            Log.d("Removing LIVE caches");
            visibleGeocaches.removeSource(liveMapGeocaches);
        }

        if (listGeocaches != null) {
            Log.d("Removing LIST caches");
            visibleGeocaches.removeSource(listGeocaches);
        }
    }

    public void setCurrentViewport(final Viewport viewport, final boolean loadLiveCaches, final boolean activeCachesOnly, final boolean excludeOwnedCaches, final boolean excludeFoundCaches) {
        // Replaces the previous LiveData source with the new source created for the new Viewport
        Log.d(String.format("Adjusting Viewport: %s, MapMode: %s", viewport, mapMode));

        clearCacheSources();

        switch (mapMode) {
            case LIST:
            case SINGLE:
                Log.d("Adding LIST caches");
                visibleGeocaches.addSource(listGeocaches, caches -> visibleGeocaches.setValue(caches));
                break;
            case COORDS:
                break;
            default: // Default to LIVE
                Log.d("Adding LIVE caches");
                liveMapGeocaches = geocacheRepository.getCachesInViewport(viewport, loadLiveCaches, activeCachesOnly, excludeOwnedCaches, excludeFoundCaches);
                visibleGeocaches.addSource(liveMapGeocaches, caches -> visibleGeocaches.setValue(caches));
        }
    }

    public void setGeocacheList(final Set<String> geocodes) {
        listGeocaches = geocacheRepository.getCachesByGeocode(geocodes);
    }
}
