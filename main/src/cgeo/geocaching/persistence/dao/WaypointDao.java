package cgeo.geocaching.persistence.dao;

import cgeo.geocaching.persistence.entities.Waypoint;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;
import java.util.Set;

@Dao
public abstract class WaypointDao {

    @Query("SELECT * FROM waypoints WHERE (latitude >= :minLat AND latitude <= :maxLat) AND (longitude >= :minLon AND longitude <= :maxLon) LIMIT 500")
    public abstract LiveData<List<Waypoint>> getWaypointsInViewport(double minLat, double minLon, double maxLat, double maxLon);
}
