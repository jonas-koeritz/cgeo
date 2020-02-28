package cgeo.geocaching.persistence.util;

import cgeo.geocaching.connector.trackable.TrackableBrand;
import cgeo.geocaching.enumerations.CacheAttribute;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.WaypointType;
import cgeo.geocaching.log.LogType;
import cgeo.geocaching.log.ReportProblemType;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TypeConverters {
    @TypeConverter
    public static Date dateFromTimestamp(final Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(final Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static CacheType cacheTypeFromString(final String value) {
        return value == null ? null : CacheType.getById(value);
    }

    @TypeConverter
    public static String cacheTypeToString(final CacheType cacheType) {
        return cacheType == null ? null : cacheType.id;
    }

    @TypeConverter
    public static CacheSize cacheSizeFromInteger(final Integer value) {
        return value == null ? null : CacheSize.getByNumber(value);
    }

    @TypeConverter
    public static Integer cacheSizeToInteger(final CacheSize size) {
        return size == null ? null : size.comparable;
    }

    // TODO Maybe serializing the full rawNames to String isn't the best option

    @TypeConverter
    public static String stringSetToString(final Set<String> strings) {
        if (strings == null) {
            return null;
        }
        final StringBuilder resultBuilder = new StringBuilder();
        for (String s : strings) {
            resultBuilder.append(s);
            resultBuilder.append(",");
        }

        // Cut off the last delimiter
        if (resultBuilder.length() > 0) {
            return resultBuilder.substring(0, resultBuilder.length() - 1);
        }
        return null;
    }

    @TypeConverter
    public static Set<String> stringSetFromString(final String value) {
        if (value == null) {
            return null;
        }
        final String[] strings = value.split(",");
        return new HashSet<>(Arrays.asList(strings));
    }

    @TypeConverter
    public static Integer logTypeToInteger(final LogType logType) {
        return logType == null ? null : logType.id;
    }

    @TypeConverter
    public static LogType logTypeFromInteger(final Integer value) {
        return value == null ? null : LogType.getById(value);
    }

    @TypeConverter
    public static String problemToString(final ReportProblemType problem) {
        return problem == null | problem == ReportProblemType.NO_PROBLEM ? null : problem.code;
    }

    @TypeConverter
    public static ReportProblemType problemFromString(final String value) {
        return value == null ? ReportProblemType.NO_PROBLEM : ReportProblemType.findByCode(value);
    }

    @TypeConverter
    public static String waypointTypeToString(final WaypointType type) {
        return type == null ? null : type.id;
    }

    @TypeConverter
    public static WaypointType waypointTypeFromString(final String value) {
        return value == null ? null : WaypointType.findById(value);
    }

    @TypeConverter
    public static TrackableBrand trackableBrandFromInteger(final Integer value) {
        return value == null ? null : TrackableBrand.getById(value);
    }

    @TypeConverter
    public static Integer trackableBrandToInteger(final TrackableBrand brand) {
        return brand == null ? null : brand.getId();
    }
}
