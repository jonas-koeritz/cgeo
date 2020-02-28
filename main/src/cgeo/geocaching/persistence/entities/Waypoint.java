package cgeo.geocaching.persistence.entities;

import cgeo.geocaching.enumerations.WaypointType;
import cgeo.geocaching.location.Geopoint;

import androidx.room.Entity;

@Entity(tableName = "waypoints", primaryKeys = { "waypointId" })
public class Waypoint {
    public long waypointId;
    public long geocacheId;

    public String name;

    public WaypointType waypointType;

    public double latitude;
    public double longitude;

    public Geopoint getCoordinates() {
        return new Geopoint(latitude, longitude);
    }

    public String description;
    public String personalNote;

    public boolean visited;
    public boolean userDefined;
}
