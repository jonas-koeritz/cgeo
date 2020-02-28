package cgeo.geocaching.persistence.entities;

import cgeo.geocaching.enumerations.CacheAttribute;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.location.Geopoint;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;

import java.util.Date;
import java.util.Set;


@Entity(tableName = "geocaches", primaryKeys = { "geocacheId", "geocode" })
public class Geocache {
    public long geocacheId;

    // The unique geocode used to identify this cache
    @NonNull
    public String geocode = "";

    // The caches name
    public String name;

    // The cache owner
    @Embedded public User owner;

    // Difficulty rating
    public Double difficulty;

    // Terrain rating
    public Double terrain;

    // The date this cache has been hidden
    public Date hidden;

    // A hint given by the cache owner
    public String hint;

    // The cache type
    public CacheType cacheType;

    // The cache size
    public CacheSize size;

    // The latitude of the caches position
    public double latitude;

    // The longitude of the caches position
    public double longitude;

    /**
     * The region this cache resides in, as defined by the connector.
     * This typically consists of state and country e.g.
     * "Schleswig-Holstein, Germany"
     */
    public String region;

    /**
     * Creates a Geopoint with the coordinates of this geocache
     * @return Geopoint representing the location of this geocache
     */
    public Geopoint getCoordinates() {
        return new Geopoint(latitude, longitude);
    }

    // Notes added by the user
    public String personalNote;

    // The caches description / listing text
    public String description;

    // Is this cache disabled?
    public Boolean disabled;

    // Has this cache been archived?
    public Boolean archived;

    // Is this cache accessible to premium members only?
    public Boolean premiumMembersOnly;

    // Has the user modified the caches coordinates?
    public Boolean userModifiedCoordinates;

    // Does the cache require a password to submit a new log?
    public Boolean logPasswordRequired;

    // All the attributes assigned to this cache
    public Set<CacheAttribute> attributes;

    /**
     * LiveCache is used to update the subset of information gathered during LiveMap
     * usage.
     */
    public static class LiveCache {
        public long geocacheId;

        @NonNull
        public String geocode = "";
        public String name;
        public double latitude;
        public double longitude;
        public CacheType cacheType;
        public CacheSize size;
        public boolean disabled;
        public boolean archived;
        public boolean premiumMembersOnly;

        public LiveCache(final cgeo.geocaching.models.Geocache cache) {
            this.geocode = cache.getGeocode();
            this.name = cache.getName();
            this.latitude = cache.getCoords().getLatitude();
            this.longitude = cache.getCoords().getLongitude();
            this.cacheType = cache.getType();
            this.size = cache.getSize();
            this.disabled = cache.isDisabled();
            this.archived = cache.isArchived();
            this.premiumMembersOnly = cache.isPremiumMembersOnly();
        }
    }
}
