package cgeo.geocaching.persistence.entities;

import cgeo.geocaching.enumerations.WaypointType;
import cgeo.geocaching.location.Geopoint;
import cgeo.geocaching.models.Geocache;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "waypoints")
public class Waypoint {
    @PrimaryKey(autoGenerate = true)
    public long waypointId;

    public String geocache;

    public String name;

    public WaypointType waypointType;

    public Double latitude;
    public Double longitude;

    public Geopoint getCoordinates() {
        if (latitude != null && longitude != null) {
            return new Geopoint(latitude, longitude);
        }
        return null;
    }

    public String description;
    public String personalNote;

    public boolean visited;
    public boolean userDefined;
    public boolean originalCoordinatesEmpty;

    /**
     * Extract the waypoints from a legacy Geocache object
     * @param cache The cache to get the waypoints from
     * @return Extracted waypoints
     */
    public static Set<Waypoint> getFromGeocache(final Geocache cache) {
        final Set<Waypoint> waypoints = new HashSet<>();
        for (cgeo.geocaching.models.Waypoint wp : cache.getWaypoints()) {
            waypoints.add(new Waypoint(wp));
        }
        return waypoints;
    }

    public Waypoint() {

    }

    /**
     * Create a new Waypoint Entity from a legacy Waypoint object
     * @param wp the legacy Waypoint object to copy
     */
    public Waypoint(final cgeo.geocaching.models.Waypoint wp) {
        if (wp.getId() > -1) {
            this.waypointId = wp.getId();
        }
        this.geocache = wp.getGeocode();
        this.name = wp.getName();
        this.waypointType = wp.getWaypointType();
        if (wp.getCoords() != null) {
            this.latitude = wp.getCoords().getLatitude();
            this.longitude = wp.getCoords().getLongitude();
        }
        this.description = wp.getNote();
        this.personalNote = wp.getUserNote();
        this.visited = wp.isVisited();
        this.userDefined = wp.isUserDefined();
        this.originalCoordinatesEmpty = wp.isOriginalCoordsEmpty();
    }
}
