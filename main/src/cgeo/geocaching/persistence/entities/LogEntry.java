package cgeo.geocaching.persistence.entities;

import cgeo.geocaching.log.LogType;
import cgeo.geocaching.log.ReportProblemType;

import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "logEntries", primaryKeys = { "logId" })
public class LogEntry {
    public long logId;
    public long geocacheId;

    public LogType logType;

    public String author;
    public String contents;

    public Date logDate;

    public ReportProblemType problem;
}
