package cgeo.geocaching.persistence.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.ListWithGeocaches;

@Dao
public abstract class ListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createList(CacheList list);

    @Transaction
    @Query("SELECT * FROM lists where listId = :listId")
    public abstract LiveData<ListWithGeocaches> getListOfCaches(long listId);

    @Query("DELETE FROM lists WHERE listId = :listId")
    public abstract void deleteList(long listId);
}
