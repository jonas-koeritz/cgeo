package cgeo.geocaching.persistence.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

import cgeo.geocaching.persistence.CGeoDatabase;
import cgeo.geocaching.persistence.dao.ListDao;
import cgeo.geocaching.persistence.entities.AssignedLists;
import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.ListWithGeocaches;

public class ListRepository {
    private ListDao listDao;

    public ListRepository(final Application application) {
        final CGeoDatabase db = CGeoDatabase.getDatabase(application);
        listDao = db.listDao();
    }

    public void createList(final String name) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            listDao.createList(new CacheList(name, new Date()));
        });
    }

    public void createList(final long listId, final String name) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            listDao.createList(new CacheList(listId, name, new Date()));
        });
    }

    public void deleteList(final long listId) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            listDao.deleteList(listId);
        });
    }

    public LiveData<List<Geocache>> getListOfCaches(final long listId) {
        return Transformations.map(listDao.getListOfCaches(listId), list -> list.geocaches);
    }

    public LiveData<List<CacheList>> getAssignedLists(final String geocode) {
        return Transformations.map(listDao.getAssignedLists(geocode), assignedLists -> assignedLists.lists);
    }
}
