package cgeo.geocaching.persistence.entities;

import androidx.room.Entity;

@Entity(tableName = "lists", primaryKeys = { "listId" })
public class List {
    public long listId;

    // The name of this list
    public String name;
}
