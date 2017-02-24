package com.example.jatinm.activitytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static android.provider.BaseColumns._ID;
import static com.example.jatinm.activitytracker.StorageContract.JournalTable.ACTIVITY_ID;
import static com.example.jatinm.activitytracker.StorageContract.JournalTable.ACTIVITY_TIME_SPENT;
import static com.example.jatinm.activitytracker.StorageContract.JournalTable.DATE;
import static com.example.jatinm.activitytracker.StorageContract.JournalTable.JT;
import static com.example.jatinm.activitytracker.StorageContract.JournalTable.TOTAL;
import static com.example.jatinm.activitytracker.StorageContract.UserActivityTable.ACTIVITY_NAME;
import static com.example.jatinm.activitytracker.StorageContract.UserActivityTable.UAT;

/**
 * Helps manage the creation of the storage database
 */
public class StorageDatabaseHelper extends SQLiteOpenHelper {

    public static final ContentValues[] DUMMY_CONTENT_VALUES_ACTIVITY_TABLE = new ContentValues[4];

    static {
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[0] = new ContentValues();
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[0].put(
                ACTIVITY_NAME, "Piano");
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[1] = new ContentValues();
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[1].put(
                ACTIVITY_NAME, "Soccer");
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[2] = new ContentValues();
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[2].put(
                ACTIVITY_NAME, "Walking");
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[3] = new ContentValues();
        DUMMY_CONTENT_VALUES_ACTIVITY_TABLE[3].put(
                ACTIVITY_NAME, "Hacking");
    }

    public static final ContentValues[] DUMMY_CONTENT_VALUES_JOURNAL_TABLE = new ContentValues[4];

    static {
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[0] = new ContentValues();
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[0].put(
                DATE, System.currentTimeMillis() / 1000);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[0].put(
                ACTIVITY_TIME_SPENT, 100);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[0].put(
                ACTIVITY_ID, 1);

        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[1] = new ContentValues();
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[1].put(
                DATE, System.currentTimeMillis() / 1000 - 90400);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[1].put(
                ACTIVITY_TIME_SPENT, 200);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[1].put(
                ACTIVITY_ID, 2);

        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[2] = new ContentValues();
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[2].put(
                DATE, System.currentTimeMillis() / 1000);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[2].put(
                ACTIVITY_TIME_SPENT, 10);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[2].put(
                ACTIVITY_ID, 3);

        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[3] = new ContentValues();
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[3].put(
                DATE, System.currentTimeMillis() / 1000);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[3].put(
                ACTIVITY_TIME_SPENT, 10);
        DUMMY_CONTENT_VALUES_JOURNAL_TABLE[3].put(
                ACTIVITY_ID, 4);
    }

    private SQLiteDatabase mDatabase;

    public Cursor runQueryToFetchContentForDashboard() {
        long nowInSeconds = System.currentTimeMillis() / 1000;
        long roundOfDate = nowInSeconds - 86400;

        mDatabase = getWritableDatabase();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(String.format("Select %s, %s, sum(%s) as %s " +
                "from %s left join %s " +
                "on " +
                "%s = %s " +
                "and %s > %d " +
                "group by %s " +
                "order by %s ",
                getTC(UAT, _ID), getTC(UAT, ACTIVITY_NAME), getTC(JT, ACTIVITY_TIME_SPENT), TOTAL,
                UAT, JT,
                getTC(UAT, _ID), getTC(JT, ACTIVITY_ID),
                getTC(JT, DATE), roundOfDate,
                getTC(UAT, _ID),
                getTC(UAT, _ID)));

        Cursor cursor = mDatabase.rawQuery(sqlBuilder.toString(), null);
        return cursor;
    }

    public void runIncrementDbQuery(int activityId, int increment) {
        mDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, System.currentTimeMillis() / 1000);
        contentValues.put(ACTIVITY_ID, activityId);
        contentValues.put(ACTIVITY_TIME_SPENT, increment);

        long result = mDatabase.insert(JT, null, contentValues);
        if (result < 0) {
            Log.e("jatin", "error while adding the increment");
        }
    }

    public static String getTC(String tableName, String column) {
        return tableName + "." + column;
    }

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public StorageDatabaseHelper(@NonNull Context context) {
        super(context, StorageContract.NAME /* databaseFileName */, null, StorageContract.VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StorageContract.SQL_CREATE_ACTIVITY_TABLE);
        db.execSQL(StorageContract.SQL_CREATE_JOURNAL_TABLE);

        for (ContentValues contentValues : DUMMY_CONTENT_VALUES_ACTIVITY_TABLE) {
            db.insert(UAT, null, contentValues);
        }
        for (ContentValues contentValues : DUMMY_CONTENT_VALUES_JOURNAL_TABLE) {
            db.insert(JT, null, contentValues);
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(StorageContract.SQL_DELETE_ACTIVITY_TABLE);
        db.execSQL(StorageContract.SQL_DELETE_JOURNAL_TABLE);
        onCreate(db);
    }
}
