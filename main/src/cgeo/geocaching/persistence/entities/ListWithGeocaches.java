package cgeo.geocaching.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class ListWithGeocaches {
    @Embedded
    public CacheList list;

    @Relation(
            parentColumn = "listId",
            entityColumn = "geocode",
            associateBy = @Junction(GeocacheListCrossRef.class)
    )
    public List<Geocache> geocaches;
}
