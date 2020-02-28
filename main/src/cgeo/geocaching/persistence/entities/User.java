package cgeo.geocaching.persistence.entities;

import androidx.room.Ignore;

public class User {
    // The users user ID
    public String userId;

    // The users display name
    public String displayName;

    public User() {

    }

    @Ignore
    public User(final String userId, final String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
}
