package com.example.jatinm.activitytracker;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class IncrementActivityTimeAsyncTask extends AsyncTask<Void, Void, Void> {
    @NonNull private StorageDatabaseHelper mStorageDbHelper;
    private int mActivityId;
    private int mIncrement;

    public IncrementActivityTimeAsyncTask(
            @NonNull StorageDatabaseHelper storageDatabaseHelper,
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
