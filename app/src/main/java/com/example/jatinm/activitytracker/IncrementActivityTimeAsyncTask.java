package com.example.jatinm.activitytracker;

import android.os.AsyncTask;

public class IncrementActivityTimeAsyncTask extends AsyncTask<Void, Void, Void> {
    StorageDatabaseHelper mStorageDbHelper;
    int mActivityId;
    int mIncrement;

    IncrementActivityTimeAsyncTask(
            StorageDatabaseHelper storageDatabaseHelper,
            int activityId,
            int increment) {
        mStorageDbHelper = storageDatabaseHelper;
        mActivityId = activityId;
        mIncrement = increment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mStorageDbHelper.runIncrementDbQuery(mActivityId, mIncrement);
        return null;
    }
}
