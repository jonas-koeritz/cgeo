package cgeo.geocaching.maps.mapsforge.v6.caches;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import org.mapsforge.map.layer.Layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LoadFlags;
import cgeo.geocaching.maps.MapUtils;
import cgeo.geocaching.maps.mapsforge.v6.MapHandlers;
import cgeo.geocaching.models.Geocache;
import cgeo.geocaching.models.Waypoint;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.storage.DataStore;

public class LiveDataWaypointsOverlay extends AbstractCachesOverlay {
    private LiveData<List<cgeo.geocaching.persistence.entities.Waypoint>> waypoints;
    private LifecycleOwner owner;

    public LiveDataWaypointsOverlay(final LiveData<List<cgeo.geocaching.persistence.entities.Waypoint>> waypoints, final LifecycleOwner owner, final int overlayId, final Set<GeoEntry> geoEntries, final CachesBundle bundle, final Layer anchorLayer, final MapHandlers mapHandlers) {
        super(overlayId, geoEntries, bundle, anchorLayer, mapHandlers);
        this.waypoints = waypoints;
        this.owner = owner;

        this.waypoints.observe(this.owner, this::showWaypoints);
    }

    void hideWaypoints() {
        final Collection<String> removeCodes = getGeocodes();
        final Collection<String> newCodes = new HashSet<>();

        syncLayers(removeCodes, newCodes);
    }

    void showWaypoints(final List<cgeo.geocaching.persistence.entities.Waypoint> waypoints) {
        final Collection<String> removeCodes = getGeocodes();
        final Collection<String> newCodes = new HashSet<>();

        final boolean isDotMode = Settings.isDotMode();

        for (final cgeo.geocaching.persistence.entities.Waypoint waypoint : waypoints) {
            if (waypoint == null || waypoint.getCoordinates() == null) {
                continue;
            }
            if (removeCodes.contains(waypoint.getGpxId())) {
                removeCodes.remove(waypoint.getGpxId());
            } else {
                if (addItem(waypoint, isDotMode)) {
                    newCodes.add(waypoint.getGpxId());
                }
            }
        }

        syncLayers(removeCodes, newCodes);
    }

    public void invalidateWaypoints(final Collection<String> geocodes) {
        // Nothing to invalidate
    }

}
