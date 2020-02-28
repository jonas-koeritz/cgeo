package cgeo.geocaching.persistence.dao;

import cgeo.geocaching.persistence.entities.Geocache;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class GeocacheDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(Geocache geocache);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void update(Geocache entity);

    @Transaction
    public void upsert(final Geocache geocache) {
        final long id = insert(geocache);
        if (id == -1) { // Already existed
            update(geocache);
        }
    }

    @Insert(entity = Geocache.class, onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(Geocache.LiveCache geocache);

    @Update(entity = Geocache.class, onConflict = OnConflictStrategy.IGNORE)
    abstract void update(Geocache.LiveCache entity);

    @Transaction
    public void upsert(final Geocache.LiveCache geocache) {
        final long id = insert(geocache);
        if (id == -1) { // Already existed
            update(geocache);
        }
    }

    @Query("DELETE FROM geocaches")
    abstract void deleteAll();
}
