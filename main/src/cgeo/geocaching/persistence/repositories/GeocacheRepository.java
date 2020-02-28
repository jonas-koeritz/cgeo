package cgeo.geocaching.persistence.repositories;

import cgeo.geocaching.persistence.CGeoDatabase;
import cgeo.geocaching.persistence.dao.GeocacheDao;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.utils.Log;

import android.app.Application;

public class GeocacheRepository {
    private GeocacheDao geocacheDao;

    public GeocacheRepository(final Application application) {
        final CGeoDatabase db = CGeoDatabase.getDatabase(application);
        geocacheDao = db.geocacheDao();
    }

    public void upsert(final Geocache geocache) {
        Log.d(String.format("Persistence: upserting geocache: %s", geocache.geocode));
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(geocache);
        });
    }

    public void upsert(final Geocache.LiveCache geocache) {
        Log.d(String.format("Persistence: upserting geocache: %s", geocache.geocode));
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(geocache);
        });
    }
}
