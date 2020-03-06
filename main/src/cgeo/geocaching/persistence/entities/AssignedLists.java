package cgeo.geocaching.persistence.entities;

import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class AssignedLists {
    public String geocode;

    @Relation(
            parentColumn = "geocode",
            entityColumn = "listId",
            associateBy = @Junction(GeocacheListCrossRef.class)
    )
    public List<CacheList> lists;
}
