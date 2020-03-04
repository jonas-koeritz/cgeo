package cgeo.geocaching.persistence;

import android.content.Context;

import cgeo.geocaching.persistence.dao.GeocacheDao;
import cgeo.geocaching.persistence.dao.ListDao;
import cgeo.geocaching.persistence.dao.WaypointDao;
import cgeo.geocaching.persistence.entities.CacheList;
import cgeo.geocaching.persistence.entities.Geocache;
import cgeo.geocaching.persistence.entities.GeocacheListCrossRef;
import cgeo.geocaching.persistence.entities.LogEntry;
import cgeo.geocaching.persistence.entities.Trackable;
import cgeo.geocaching.persistence.entities.Waypoint;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {
        Geocache.class,
        Trackable.class,
        LogEntry.class,
        CacheList.class,
        Waypoint.class,
        GeocacheListCrossRef.class,
},
        version = 2020030402,
        exportSchema = false
)
@TypeConverters({cgeo.geocaching.persistence.util.TypeConverters.class})
public abstract class CGeoDatabase extends RoomDatabase {
    public abstract GeocacheDao geocacheDao();
    public abstract ListDao listDao();
    public abstract WaypointDao waypointDao();

    private static volatile CGeoDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static CGeoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CGeoDatabase.class) {
                if (INSTANCE == null) {
                    // TODO remove fallback to destructive migration
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CGeoDatabase.class, "cgeo_room")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
