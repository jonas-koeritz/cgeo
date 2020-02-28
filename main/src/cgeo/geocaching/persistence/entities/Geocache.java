package cgeo.geocaching.persistence.entities;

import cgeo.geocaching.enumerations.CacheAttribute;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.location.Geopoint;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static androidx.room.ForeignKey.CASCADE;


@Entity(
        tableName = "geocaches"
)
public class Geocache {
    // The unique geocode used to identify this cache
    @NonNull
    @PrimaryKey
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

    // The last time the basic cache information has been updated
    public Date liveUpdated;

    // The last time the complete cache data has been updated
    public Date updated;

    // The last time the user viewed this caches details page (can be used for history)
    public Date lastViewed;

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

    // TODO Strings seem to be a clunky solution for this, create a Class to hold type and status
    // All the attributes assigned to this cache
    public Set<String> attributes;

    // Has this cache been marked for offline usage?
    public Boolean offline;

    // Have all available details been downloaded yet?
    public Boolean detailed;

    /**
     * LiveCache is used to update the subset of information gathered
     * during live map usage.
     */
    public static class LiveCache {
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
        public Date liveUpdated;

        // TODO evaluate if more cache data can be updated from the live map
        // Create a new LiveCache object using the data from a legacy geocache object
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

    public Geocache() {

    }

    /**
     * Creates a Geocache Entity based on the given legacy cache object
     * @param cache legacy cache object to copy
     */
    public Geocache(final cgeo.geocaching.models.Geocache cache) {
        this.geocode = cache.getGeocode();
        this.name = cache.getName();
        this.owner = new User(cache.getOwnerUserId(), cache.getOwnerDisplayName());
        this.difficulty = (double) cache.getDifficulty();
        this.terrain = (double) cache.getTerrain();
        this.hidden = cache.getHiddenDate();
        this.liveUpdated = new Date(cache.getUpdated());
        this.updated = new Date(cache.getUpdated());
        this.hint = cache.getHint();
        this.cacheType = cache.getType();
        this.size = cache.getSize();
        this.latitude = cache.getCoords().getLatitude();
        this.longitude = cache.getCoords().getLongitude();
        this.region = cache.getLocation();
        this.personalNote = cache.getPersonalNote();
        this.description = cache.getDescription();
        this.disabled = cache.isDisabled();
        this.archived = cache.isArchived();
        this.premiumMembersOnly = cache.isPremiumMembersOnly();
        this.userModifiedCoordinates = cache.hasUserModifiedCoords();
        this.logPasswordRequired = cache.isLogPasswordRequired();
        this.attributes = new HashSet<>(cache.getAttributes());
        this.offline = cache.isOffline();
        this.detailed = cache.isDetailed();
    }
}
