package cgeo.geocaching.persistence.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cgeo.geocaching.persistence.entities.AssignedLists;
import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.repositories.GeocacheRepository;
import cgeo.geocaching.persistence.repositories.ListRepository;
import cgeo.geocaching.persistence.util.DownloadStatus;

public class CacheDetailsViewModel extends AndroidViewModel {
    private GeocacheRepository geocacheRepository;
    private ListRepository listRepository;

    public CacheDetailsViewModel(final Application application) {
        super(application);

        geocacheRepository = new GeocacheRepository(application);
        listRepository = new ListRepository(application);
    }

    public LiveData<Geocache> getGeocacheByGeocode(final String geocode) {
        return geocacheRepository.getGeocacheByGeocode(geocode);
    }

    public LiveData<Geocache> getGeocacheByGeocode(final String geocode, final Geocache.DetailLevel detailLevel, boolean forceDownload) {
        return geocacheRepository.getGeocacheByGeocode(geocode, detailLevel, forceDownload);
    }

    public LiveData<DownloadStatus> getDownloadStatus(final String geocode) {
        return geocacheRepository.getDownloadStatus(geocode);
    }

    public LiveData<List<CacheList>> getLists(final String geocode) {
        return listRepository.getAssignedLists(geocode);
    }

    public void addGeocacheToList(final String geocode, final long listId) {
        geocacheRepository.addGeocacheToList(geocode, listId);
    }

    public void setListsForGeocache(final String geocode, final List<Long> lists) {
        geocacheRepository.clearLists(geocode);
        for (Long listId : lists) {
            geocacheRepository.addGeocacheToList(geocode, listId);
        }
    }
}
