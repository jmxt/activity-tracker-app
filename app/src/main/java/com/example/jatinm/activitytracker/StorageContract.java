package com.example.jatinm.activitytracker;

import android.provider.BaseColumns;

/**
 * Defines the structure of the underlying database.
 */
public class StorageContract {
    public static final int VERSION = 2;
    public static final String NAME = "activity.db";

    // Activity and its unique ID
    public static class UserActivityTable implements BaseColumns {
        public static final String UAT = "activity";
        public static final String ACTIVITY_NAME = "name";
    }

    // Journal - per day activity tracker.
    public static class JournalTable implements BaseColumns {
        public static final String JT = "journal";
        // It's a unix timestamp; easier for range checks
        public static final String DATE = "date";
        // It's the ID from UserActivityTable
        public static final String ACTIVITY_ID = "activity_id";
        // It's the minutes you spent in a particular activity.
        // Can be displayed as 'hours' if more than 60 minutes.
        public static final String ACTIVITY_TIME_SPENT = "time_spent";

        // The dynamic coloumn created for total time per day/week.
        public static final String TOTAL = "total_time";
    }

    public static final String INTEGER_TYPE = " INTEGER";
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ACTIVITY_TABLE =
            "CREATE TABLE " + UserActivityTable.UAT + " (" +
                    UserActivityTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserActivityTable.ACTIVITY_NAME + TEXT_TYPE + ")";

    public static final String SQL_CREATE_JOURNAL_TABLE =
            "CREATE TABLE " + JournalTable.JT + " (" +
                    JournalTable.DATE + INTEGER_TYPE + COMMA_SEP +
                    JournalTable.ACTIVITY_ID + INTEGER_TYPE + COMMA_SEP +
                    JournalTable.ACTIVITY_TIME_SPENT + INTEGER_TYPE + ")";

    public static final String SQL_DELETE_JOURNAL_TABLE =
            "DROP TABLE IF EXISTS " + JournalTable.JT;

    public static final String SQL_DELETE_ACTIVITY_TABLE =
            "DROP TABLE IF EXISTS " + UserActivityTable.UAT;
}
