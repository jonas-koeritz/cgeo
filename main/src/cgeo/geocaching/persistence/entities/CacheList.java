package cgeo.geocaching.persistence.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(
        tableName = "lists"
)
public class CacheList {
    @PrimaryKey(autoGenerate = true)
    public long listId;

    // The name of this list
    public String name;

    // The last time this list has been updated
    public Date lastUpdated;

    public CacheList() {

    }

    @Ignore
    public CacheList(final String name, final Date lastUpdated) {
        this.name = name;
        this.lastUpdated = lastUpdated;
    }

    @Ignore
    public CacheList(final long listId, final String name, final Date lastUpdated) {
        this.listId = listId;
        this.name = name;
        this.lastUpdated = lastUpdated;
    }
}
