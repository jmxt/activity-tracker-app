package com.example.jatinm.activitytracker;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.provider.BaseColumns._ID;
import static com.example.jatinm.activitytracker.StorageContract.JournalTable.TOTAL;
import static com.example.jatinm.activitytracker.StorageContract.UserActivityTable.ACTIVITY_NAME;

/**
 * List adapter for listing out all the activities that the user is doing plus
 * the amount of time spent doing these activities that day.
 *
 * The UI should roughly:
 *
 *    ActivityName
 *     TimeSpent
 *     (+)  (-)
 *
 * Clicking on the timespent can open a view that shows a dashboard for that given activity
 * Clicking on + / - can add or subtract a basic time unit ~ 10 minutes for that activity
 * Clicking on Activity name can open a view to edit the property of the activity (right now it is
 * just a name)
 */
public class UserActivityListAdapter extends CursorAdapter {

    private final Handler mMainThreadHandler;
    ScheduledThreadPoolExecutor mBackgroundExecutor;
    StorageDatabaseHelper mDbHelper;

    ScheduledFuture mRefreshFuture;
    Runnable mRefreshCursorRunnable = new Runnable() {
        @Override
        public void run() {
            final Cursor cursor = mDbHelper.runQueryToFetchContentForDashboard();
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    changeCursor(cursor);
                }
            });
        }
    };



    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public UserActivityListAdapter(Context context, Cursor c, int flags, StorageDatabaseHelper dbHelper) {
        super(context, c, flags);
        mMainThreadHandler = new Handler();
        mDbHelper = dbHelper;
        mBackgroundExecutor = new ScheduledThreadPoolExecutor(1);
        mBackgroundExecutor.allowCoreThreadTimeOut(true);
        mBackgroundExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false);
        Button positiveButton = (Button) view.findViewById(R.id.add_time);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o == null || !(o instanceof ViewObjectTag)) {
                    Toast.makeText(context, "Cannot update time", Toast.LENGTH_SHORT).show();
                }

                int id = ((ViewObjectTag) o).activityId;
                new IncrementActivityTimeAsyncTask(mDbHelper, id, 10)
                        .executeOnExecutor(mBackgroundExecutor);
                if (mRefreshFuture == null || mRefreshFuture.isDone()) {
                    mRefreshFuture = mBackgroundExecutor.schedule(
                            mRefreshCursorRunnable, 2000, TimeUnit.MILLISECONDS);
                }
            }
        });

        Button negativebutton = (Button) view.findViewById(R.id.subtract_time);
        negativebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = v.getTag();
                if (o == null || !(o instanceof ViewObjectTag)) {
                    Toast.makeText(context, "Cannot update time", Toast.LENGTH_SHORT).show();
                }
                int id = ((ViewObjectTag) o).activityId;
                new IncrementActivityTimeAsyncTask(mDbHelper, id, -10).executeOnExecutor(
                        mBackgroundExecutor);
                if (mRefreshFuture == null || mRefreshFuture.isDone()) {
                    mRefreshFuture = mBackgroundExecutor.schedule(
                            mRefreshCursorRunnable, 2000, TimeUnit.MILLISECONDS);
                }
            }
        });
        return view;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView activityNameTextView = (TextView) view.findViewById(R.id.activity_name);
        activityNameTextView.setText(cursor.getString(cursor.getColumnIndex(ACTIVITY_NAME)));

        TextView timeSpentTextView = (TextView) view.findViewById(R.id.activity_time_spent);
        timeSpentTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(TOTAL))));

        Button addButton = (Button) view.findViewById(R.id.add_time);
        Button subButton = (Button) view.findViewById(R.id.subtract_time);

        ViewObjectTag tag = new ViewObjectTag();
        tag.activityId = cursor.getInt(cursor.getColumnIndex(_ID));
        addButton.setTag(tag);
        subButton.setTag(tag);
    }

    static class ViewObjectTag {
        int activityId;
    }
}
