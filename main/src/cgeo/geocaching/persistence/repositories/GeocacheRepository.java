package cgeo.geocaching.persistence.repositories;

import cgeo.geocaching.SearchResult;
import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.connector.IConnector;
import cgeo.geocaching.connector.gc.GCConnector;
import cgeo.geocaching.connector.gc.GCMap;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LoadFlags;
import cgeo.geocaching.location.Viewport;
import cgeo.geocaching.persistence.CGeoDatabase;
import cgeo.geocaching.persistence.dao.GeocacheDao;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.GeocacheListCrossRef;
import cgeo.geocaching.persistence.entities.GeocacheWithWaypoints;
import cgeo.geocaching.persistence.entities.Waypoint;
import cgeo.geocaching.persistence.util.DownloadStatus;
import cgeo.geocaching.utils.Log;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.os.Looper.getMainLooper;

public class GeocacheRepository {
    private GeocacheDao geocacheDao;

    private static final BlockingQueue<Runnable> downloadQueue = new ArrayBlockingQueue<>(1);
    private static final ThreadPoolExecutor downloadExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, downloadQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
    private MutableLiveData<DownloadStatus> downloadStatus;
    private Handler mainHandler;

    public GeocacheRepository(final Application application) {
        final CGeoDatabase db = CGeoDatabase.getDatabase(application);
        geocacheDao = db.geocacheDao();

        downloadStatus = new MutableLiveData<>();
        mainHandler = new Handler(getMainLooper());
    }

    public LiveData<DownloadStatus> getDownloadStatus() {
        return downloadStatus;
    }

    public void upsert(final Geocache geocache) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(geocache);
        });
    }

    public void upsert(final Geocache.GeocodeResult result) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(result);
        });
    }

    public void upsert(final Geocache.LiveCache geocache) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(geocache);
        });
    }


    public void upsert(final Waypoint waypoint) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(waypoint);
        });
    }

    public void upsert(final cgeo.geocaching.models.Geocache geocache) {
        upsert(new Geocache(geocache));

        if (geocache.getWaypoints().size() > 0) {
            for (Waypoint wp : Waypoint.getFromGeocache(geocache)) {
                upsert(wp);
            }
        }
    }

    public void logView(final String geocode) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.setLastViewed(geocode, new Date());
        });
    }

    public void addGeocacheToList(final String geocode, final long listId) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            try {
                geocacheDao.insertGeocacheListCrossRef(new GeocacheListCrossRef(geocode, listId));
            } catch (Exception e) {
                Log.d("Error trying to add cache to list", e);
            }
        });
    }

    public LiveData<List<Geocache>> getCachesInViewport(final Viewport viewport, final boolean loadLiveCaches, final boolean activeCachesOnly, final boolean excludeOwnedCaches, final boolean excludeFoundCaches) {
        if (loadLiveCaches) {
            Log.d("Map Viewport moved, triggering a live caches download.");
            // TODO implement rate limiting and cancellation of previous download task
            loadLiveCachesInViewport(viewport);
        }

        return geocacheDao.getGeocachesInRectangle(
                viewport.getLatitudeMin(),
                viewport.getLongitudeMin(),
                viewport.getLatitudeMax(),
                viewport.getLongitudeMax(),
                activeCachesOnly,
                excludeOwnedCaches,
                excludeFoundCaches
        );
    }

    public LiveData<List<GeocacheWithWaypoints>> getCachesWithWaypointsInViewport(final Viewport viewport, final boolean loadLiveCaches, final boolean activeCachesOnly, final boolean excludeOwnedCaches, final boolean excludeFoundCaches) {
        if (loadLiveCaches) {
            Log.d("Map Viewport moved, triggering a live caches download.");
            // TODO implement rate limiting and cancellation of previous download task
            loadLiveCachesInViewport(viewport);
        }

        return geocacheDao.getGeocachesWithWaypointsInRectangle(
                viewport.getLatitudeMin(),
                viewport.getLongitudeMin(),
                viewport.getLatitudeMax(),
                viewport.getLongitudeMax(),
                activeCachesOnly,
                excludeOwnedCaches,
                excludeFoundCaches
        );
    }

    private void setDownloadStatus(final DownloadStatus status) {
        mainHandler.post(() -> downloadStatus.setValue(status));
    }

    private void loadLiveCachesInViewport(final Viewport viewport) {
        // TODO indicate errors
        downloadExecutor.execute(() -> {
            Log.d(String.format("Downloading Geocaches for Viewport: %s", viewport));
            setDownloadStatus(DownloadStatus.Loading(String.format("Downloading Geocaches for Viewport: %s", viewport)));

            // TODO call the different connectors on our own to get as much information as possible
            // relying on Geocache model Objects to do this hides the fact that OC and GC return different
            // amounts of data
            final List<cgeo.geocaching.models.Geocache> result = ConnectorFactory.liveSearchByViewport(viewport);
            for (cgeo.geocaching.models.Geocache c : result) {
                upsert(new Geocache.LiveCache(c));
            }

            setDownloadStatus(DownloadStatus.Success("Finished downloading Geocaches"));
        });
    }

    public LiveData<Geocache> getGeocacheByGeocode(final String geocode) {
        return geocacheDao.getGeocacheByGeocode(geocode);
    }

    public LiveData<DownloadStatus> loadGeocacheDetails(final String geocode, final boolean forceDownload) {
        final MutableLiveData<DownloadStatus> status = new MutableLiveData<>();
        status.setValue(DownloadStatus.Loading(String.format("Loading cache details for geocache %s", geocode)));
        downloadExecutor.execute(() -> {
            final Geocache c = geocacheDao.getGeocacheByGeocodeSync(geocode);
            if (c.difficulty == null || c.cacheType == null || c.difficulty == 0.0 || c.cacheType == CacheType.UNKNOWN || forceDownload) {
                try {
                    final IConnector connector = ConnectorFactory.getConnector(geocode);
                    if (connector instanceof GCConnector) {
                        final SearchResult result = GCMap.searchByGeocodes(Collections.singleton(geocode));
                        // TODO remove dependency on the cacheCache
                        for (cgeo.geocaching.models.Geocache cache : result.getCachesFromSearchResult(LoadFlags.LOAD_CACHE_ONLY)) {
                            upsert(new Geocache.GeocodeResult(cache));
                        }
                        status.postValue(DownloadStatus.Success("Finished loading cache details"));
                    }
                } catch (Exception e) {
                    status.postValue(DownloadStatus.Error("Failed downloading cache details", e));
                }
            } else {
                // Nothing to do here
                status.postValue(DownloadStatus.Success("Skipped loading cache details"));
            }
        });
        return status;
    }

    public LiveData<List<Geocache>> getCachesByGeocode(final List<String> geocodes) {
        return geocacheDao.getCachesByGeocode(geocodes);
    }
}
