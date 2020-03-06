package cgeo.geocaching.persistence.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import cgeo.geocaching.persistence.entities.AssignedLists;
import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.ListWithGeocaches;

@Dao
public abstract class ListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createList(CacheList list);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(CacheList geocache);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void update(CacheList entity);

    @Transaction
    public void upsert(final CacheList list) {
        final long id = insert(list);
        if (id == -1) { // Already existed
            update(list);
        }
    }

    @Transaction
    @Query("SELECT * FROM lists where listId = :listId")
    public abstract LiveData<ListWithGeocaches> getListOfCaches(long listId);

    @Query("DELETE FROM lists WHERE listId = :listId")
    public abstract void deleteList(long listId);

    @Transaction
    @Query("SELECT * FROM geocaches WHERE geocode = :geocode")
    public abstract LiveData<AssignedLists> getAssignedLists(String geocode);
}
