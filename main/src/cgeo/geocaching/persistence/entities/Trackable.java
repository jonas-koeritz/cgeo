package cgeo.geocaching.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Entity;

import java.util.Date;

import cgeo.geocaching.connector.trackable.TrackableBrand;

@Entity(tableName = "trackables", primaryKeys = { "trackableId" })
public class Trackable {
    public long trackableId;

    // The geocode of this trackable
    public String geocode;

    // The name of this trackable
    public String name;

    // The date this trackable has been released by its owner
    public Date released;

    // The owner of this trackable
    @Embedded
    public User owner;

    // The owner defined goal of this trackable
    public String goal;

    // The description of this trackable
    public String description;

    // This trackables provider / brand
    public TrackableBrand brand;

    // Is this trackable marked as missing?
    public boolean missing;
}
