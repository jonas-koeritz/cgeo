package cgeo.geocaching.persistence.repositories;

import cgeo.geocaching.persistence.CGeoDatabase;
import cgeo.geocaching.persistence.dao.GeocacheDao;
import cgeo.geocaching.persistence.dao.ListDao;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.GeocacheListCrossRef;
import cgeo.geocaching.persistence.entities.Waypoint;
import cgeo.geocaching.utils.Log;

import android.app.Application;

import java.util.Date;

public class GeocacheRepository {
    private GeocacheDao geocacheDao;
    private ListDao listDao;

    public GeocacheRepository(final Application application) {
        final CGeoDatabase db = CGeoDatabase.getDatabase(application);
        geocacheDao = db.geocacheDao();
        listDao = db.listDao();
    }

    public void upsert(final Geocache geocache) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            geocacheDao.upsert(geocache);
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
            Log.d(String.format("GeocacheRepository\tUpdating lastViewed for %s", geocode));
        });
    }

    public void addGeocacheToList(final String geocode, final long listId) {
        Log.d(String.format("GeocacheRepository\tAdding %s to List %d", geocode, listId));
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            try {
                geocacheDao.insertGeocacheListCrossRef(new GeocacheListCrossRef(geocode, listId));
            } catch (Exception e) {
                Log.d("Error trying to add cache to list", e);
            }
        });
    }
}
