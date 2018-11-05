package com.example.android.mygarden;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;

public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PLANTS)
                .build();

        if ( mCursor != null ) {
            mCursor.close();
        }

        mCursor = mContext.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if ( mCursor == null ) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if ( mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);

        int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = mCursor.getLong(idIndex);
        int plantType = mCursor.getInt(plantTypeIndex);
        long timeNow = System.currentTimeMillis();
        long wateredAt = mCursor.getLong(waterTimeIndex);
        long createdAt = mCursor.getLong(createTimeIndex);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.plant_widget);

        int imgRes = PlantUtils.getPlantImgRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);
        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        views.setViewVisibility(R.id.widget_water_button, View.GONE);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
