package cgeo.geocaching.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class GeocacheWithWaypoints {
    @Embedded public Geocache geocache;

    @Relation(
            parentColumn = "geocode",
            entityColumn = "geocache"
    )
    public List<Waypoint> waypoints;
}
