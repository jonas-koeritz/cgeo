package cgeo.geocaching.persistence.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;

import cgeo.geocaching.persistence.CGeoDatabase;
import cgeo.geocaching.persistence.dao.ListDao;
import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.ListWithGeocaches;
import cgeo.geocaching.utils.Log;

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
        Log.d(String.format("ListRepository\tcreating new List: %d:%s", listId, name));
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            listDao.createList(new CacheList(listId, name, new Date()));
        });
    }

    public void deleteList(final long listId) {
        CGeoDatabase.databaseWriteExecutor.execute(() -> {
            listDao.deleteList(listId);
        });
    }

    public LiveData<ListWithGeocaches> getListOfCaches(final long listId) {
        return listDao.getListOfCaches(listId);
    }
}
