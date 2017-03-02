package com.example.jatinm.activitytracker;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class FetchDashboardContentAsyncTask extends AsyncTask<Void, Void, Cursor> {
    @NonNull private MainActivity mMainActivity;
    @NonNull private StorageDatabaseHelper mStorageDatabaseHelper;

    public FetchDashboardContentAsyncTask(
            @NonNull MainActivity mainActivity, @NonNull StorageDatabaseHelper storageDatabaseHelper) {
        mMainActivity = mainActivity;
        mStorageDatabaseHelper = storageDatabaseHelper;
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        return mStorageDatabaseHelper.runQueryToFetchContentForDashboard();
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        mMainActivity.initializeListView(cursor);
    }
}
