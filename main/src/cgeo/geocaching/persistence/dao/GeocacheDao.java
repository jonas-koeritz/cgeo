package cgeo.geocaching.persistence.dao;

import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.GeocacheListCrossRef;
import cgeo.geocaching.persistence.entities.GeocacheWithWaypoints;
import cgeo.geocaching.persistence.entities.Waypoint;
import cgeo.geocaching.utils.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Dao
public abstract class GeocacheDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(Geocache geocache);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void update(Geocache entity);

    @Query("UPDATE geocaches SET lastViewed = :lastViewed WHERE geocode = :geocode")
    public abstract void setLastViewed(String geocode, Date lastViewed);

    @Transaction
    public void upsert(final Geocache geocache) {
        if (geocache.updated == null) {
            geocache.updated = new Date();
        }
        if (geocache.liveUpdated == null) {
            geocache.liveUpdated = new Date();
        }

        final long id = insert(geocache);
        if (id == -1) { // Already existed
            update(geocache);
        }
    }

    @Insert(entity = Geocache.class, onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(Geocache.LiveCache geocache);

    @Update(entity = Geocache.class, onConflict = OnConflictStrategy.IGNORE)
    abstract void update(Geocache.LiveCache entity);

    @Insert(entity = Geocache.class, onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(Geocache.GeocodeResult geocache);

    @Update(entity = Geocache.class, onConflict = OnConflictStrategy.IGNORE)
    abstract void update(Geocache.GeocodeResult entity);

    @Transaction
    public void upsert(final Geocache.LiveCache geocache) {
        if (geocache.liveUpdated == null) {
            geocache.liveUpdated = new Date();
        }

        final long id = insert(geocache);
        if (id == -1) { // Already existed
            update(geocache);
        }
    }

    @Transaction
    public void upsert(final Geocache.GeocodeResult result) {
        if (result.liveUpdated == null) {
            result.liveUpdated = new Date();
        }

        final long id = insert(result);
        if (id == -1) { // Already existed
            update(result);
        }
    }

    @Query("SELECT * FROM geocaches WHERE geocode = :geocode")
    public abstract Geocache getGeoacheByGeocode(String geocode);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(Waypoint waypoint);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void update(Waypoint waypoint);

    @Transaction
    public void upsert(final Waypoint waypoint) {
        final long id = insert(waypoint);
        if (id == -1) { // Already existed
            update(waypoint);
        }
    }

    @Query("DELETE FROM geocaches")
    abstract void deleteAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createList(CacheList list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGeocacheListCrossRef(GeocacheListCrossRef crossRef);

    @Query("DELETE from cache_list where geocode = :geocode")
    public abstract void clearLists(String geocode);

    @Query("SELECT * FROM geocaches WHERE latitude >= :minLat AND latitude <= :maxLat AND longitude >= :minLon AND longitude <= :maxLon LIMIT 500")
    public abstract LiveData<List<Geocache>> getGeocachesInRectangle(double minLat, double minLon, double maxLat, double maxLon);

    @Query("SELECT * FROM geocaches WHERE geocode = :geocode")
    public abstract LiveData<Geocache> getGeocacheByGeocode(String geocode);

    @Query("SELECT * FROM geocaches WHERE geocode = :geocode")
    public abstract Geocache getGeocacheByGeocodeSync(String geocode);

    @Query("SELECT * FROM geocaches WHERE (latitude >= :minLat AND latitude <= :maxLat) AND (longitude >= :minLon AND longitude <= :maxLon) AND NOT (:activeCachesOnly AND disabled = 1) AND NOT (:activeCachesOnly AND archived = 1) AND NOT (:excludeOwnedCaches AND userIsOwner = 1) AND NOT (:excludeFoundCaches AND (found IS NOT NULL) AND found = 1) LIMIT 500")
    public abstract LiveData<List<Geocache>> getGeocachesInRectangle(double minLat, double minLon, double maxLat, double maxLon, boolean activeCachesOnly, boolean excludeOwnedCaches, boolean excludeFoundCaches);

    @Query("SELECT * FROM geocaches WHERE geocode IN (:geocodes)")
    public abstract LiveData<List<Geocache>> getCachesByGeocode(List<String> geocodes);

    @Transaction
    @Query("SELECT * FROM geocaches WHERE geocode IN (:geocodes)")
    public abstract LiveData<List<GeocacheWithWaypoints>> getCachesWithWaypointsByGeocode(Set<String> geocodes);

    @Transaction
    @Query("SELECT * FROM geocaches WHERE (latitude >= :minLat AND latitude <= :maxLat) AND (longitude >= :minLon AND longitude <= :maxLon) AND NOT (:activeCachesOnly AND disabled) AND NOT (:activeCachesOnly AND archived) AND NOT (:excludeOwnedCaches AND userIsOwner) AND NOT (:excludeFoundCaches AND (found IS NOT NULL) AND found = 1) LIMIT 500")
    public abstract LiveData<List<GeocacheWithWaypoints>> getGeocachesWithWaypointsInRectangle(double minLat, double minLon, double maxLat, double maxLon, boolean activeCachesOnly, boolean excludeOwnedCaches, boolean excludeFoundCaches);

    @Update(entity = Geocache.class)
    public abstract void updateRating(Geocache.RatingUpdate update);
}
