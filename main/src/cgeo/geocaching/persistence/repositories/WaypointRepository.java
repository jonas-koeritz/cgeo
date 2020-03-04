package cgeo.geocaching.persistence.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import cgeo.geocaching.location.Viewport;
import cgeo.geocaching.persistence.CGeoDatabase;
import cgeo.geocaching.persistence.dao.WaypointDao;
import cgeo.geocaching.persistence.entities.Waypoint;

public class WaypointRepository {
    private WaypointDao waypointDao;

    public WaypointRepository(final Application application) {
        final CGeoDatabase db = CGeoDatabase.getDatabase(application);
        waypointDao = db.waypointDao();
    }

    public LiveData<List<Waypoint>> getWaypointsInViewport(final Viewport viewport) {
        return waypointDao.getWaypointsInViewport(
                viewport.getLatitudeMin(),
                viewport.getLongitudeMin(),
                viewport.getLatitudeMax(),
                viewport.getLongitudeMax()
        );
    }
}
