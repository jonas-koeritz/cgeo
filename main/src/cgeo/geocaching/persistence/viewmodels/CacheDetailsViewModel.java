package cgeo.geocaching.persistence.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.repositories.GeocacheRepository;

public class CacheDetailsViewModel extends AndroidViewModel {
    private GeocacheRepository geocacheRepository;

    public CacheDetailsViewModel(final Application application) {
        super(application);

        geocacheRepository = new GeocacheRepository(application);
    }

    public LiveData<Geocache> getGeocacheByGeocode(final String geocode) {
        return geocacheRepository.getGeocacheByGeocode(geocode);
    }
}
