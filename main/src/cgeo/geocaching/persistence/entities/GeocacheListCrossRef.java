package cgeo.geocaching.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "cache_list",
        primaryKeys = { "listId", "geocode" },
        indices = {
                @Index(value = "listId"),
                @Index(value = "geocode")
        },
        foreignKeys = {
                @ForeignKey(entity = Geocache.class, parentColumns = "geocode", childColumns = "geocode", onDelete = CASCADE),
                @ForeignKey(entity = CacheList.class, parentColumns = "listId", childColumns = "listId", onDelete = CASCADE)
        }
)
public class GeocacheListCrossRef {
    @NonNull
    public String geocode;
    public long listId;

    public GeocacheListCrossRef() {

    }

    @Ignore
    public GeocacheListCrossRef(@NonNull final String geocode, final long listId) {
        this.geocode = geocode;
        this.listId = listId;
    }
}
