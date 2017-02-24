package com.example.jatinm.activitytracker;

import android.database.Cursor;
import android.os.AsyncTask;

public class FetchDashboardContentAsyncTask extends AsyncTask<Void, Void, Cursor> {
    MainActivity mMainActivity;
    StorageDatabaseHelper mStorageDatabaseHelper;

    FetchDashboardContentAsyncTask(MainActivity mainActivity, StorageDatabaseHelper storageDatabaseHelper) {
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
