package cgeo.geocaching.persistence.entities;

import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.connector.IConnector;
import cgeo.geocaching.connector.gc.GCConnector;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.CoordinatesType;
import cgeo.geocaching.location.Geopoint;
import cgeo.geocaching.maps.mapsforge.v6.caches.GeoitemRef;
import cgeo.geocaching.models.ICoordinates;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(
        tableName = "geocaches"
)
public class Geocache implements ICoordinates {
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

    public Date visited;

    // A hint given by the cache owner
    public String hint;

    // The cache type
    public CacheType cacheType;

    // The cache size
    public CacheSize size;

    // The latitude of the caches position
    public Double latitude;

    // The longitude of the caches position
    public Double longitude;

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
    @Override
    public Geopoint getCoords() {
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

    // Has this cache been found by the user
    public Boolean found;

    // Is the user the owner of this Geocache
    public Boolean userIsOwner;

    // Is this cache accessible to premium members only?
    public Boolean premiumMembersOnly;

    // Has the user modified the caches coordinates?
    public Boolean userModifiedCoordinates;

    public Boolean finalDefined;

    public Integer favoritePoints;

    public Double rating;
    public Integer votes;

    // Does the cache require a password to submit a new log?
    public Boolean logPasswordRequired;

    // TODO Strings seem to be a clunky solution for this, create a Class to hold type and status
    // All the attributes assigned to this cache
    public Set<String> attributes;

    // Has this cache been marked for offline usage?
    public Boolean offline;

    // Have all available details been downloaded yet?
    public Boolean detailed;

    @NonNull
    private IConnector getConnector() {
        return ConnectorFactory.getConnector(geocode);
    }

    public GeoitemRef getGeoitemRef() {
        return new GeoitemRef(geocode, CoordinatesType.CACHE, geocode, 0, name, cacheType.markerId);
    }

    public boolean applyDistanceRule() {
        return (cacheType.applyDistanceRule() || BooleanUtils.isTrue(userModifiedCoordinates)) && getConnector() == GCConnector.getInstance();
    }

    public int getMapMarkerId() {
        return getConnector().getCacheMapMarkerId(BooleanUtils.isTrue(disabled) || BooleanUtils.isTrue(archived));
    }

    // TODO find a better name for this class
    public static class GeocodeResult {
        @NonNull public String geocode;
        public Double difficulty;
        public Double terrain;
        public Integer favoritePoints;
        public Date liveUpdated;

        public GeocodeResult(final cgeo.geocaching.models.Geocache cache) {
            this.geocode = cache.getGeocode();
            this.difficulty = (double) cache.getDifficulty();
            this.terrain = (double) cache.getTerrain();
            this.favoritePoints = cache.getFavoritePoints();
        }
    }

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
        public boolean userIsOwner;
        public double difficulty;
        public double terrain;
        public int favoritePoints;
        public boolean found;

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
            this.userIsOwner = cache.isOwner();
            this.difficulty = cache.getDifficulty();
            this.terrain = cache.getTerrain();
            this.favoritePoints = cache.getFavoritePoints();
            // Only update if marked as found as the map might not show the latest data
            if (cache.isFound()) {
                this.found = true;
            }
        }

        public LiveCache(final cgeo.geocaching.models.Geocache cache, final Date liveUpdated) {
            this(cache);
            this.liveUpdated = liveUpdated;
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
        if (cache.getCoords() != null) {
            this.latitude = cache.getCoords().getLatitude();
            this.longitude = cache.getCoords().getLongitude();
        }
        this.region = cache.getLocation();
        this.personalNote = cache.getPersonalNote();
        this.description = cache.getDescription();
        this.disabled = cache.isDisabled();
        this.archived = cache.isArchived();
        this.premiumMembersOnly = cache.isPremiumMembersOnly();
        this.userModifiedCoordinates = cache.hasUserModifiedCoords();
        this.finalDefined = cache.hasFinalDefined();
        this.logPasswordRequired = cache.isLogPasswordRequired();
        this.attributes = new HashSet<>(cache.getAttributes());
        this.offline = cache.isOffline();
        this.favoritePoints = cache.getFavoritePoints();
        this.detailed = cache.isDetailed();
        this.found = cache.isFound();
        this.userIsOwner = cache.isOwner();
        this.visited = new Date(cache.getVisitedDate());
    }

    public boolean showSize() {
        return !(size == CacheSize.NOT_CHOSEN || cacheType.isEvent() || cacheType.isVirtual());
    }

    public enum DetailLevel {
        MAP,
        POPUP,
        FULL
    }


    public static class RatingUpdate {
        public String geocode;
        public double rating;
        public int votes;

        public RatingUpdate(final String geocode, final double rating, final int votes) {
            this.geocode = geocode;
            this.rating = rating;
            this.votes = votes;
        }
    }
}
