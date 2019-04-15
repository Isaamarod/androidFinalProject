package com.isa_t.proyectofinalmasterd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by isa_t on 07/08/2017.
 */

public class Utils {

    private static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };


    public static final void requestPermissions(BaseActivity activity) {
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission);
            }
        }
        if (!permissionsNotGranted.isEmpty())
            ActivityCompat.requestPermissions(activity, permissionsNotGranted.toArray(new String[]{}), BaseActivity.REQUEST_CODE_PERMISSION);
        else {
            activity.onPermissionsGranted();
        }
    }

    public static final <T> T getDatabaseField(Cursor cursor, int index, T defaultValue){
        switch (cursor.getType(index)){
            case Cursor.FIELD_TYPE_BLOB:
                return (T) cursor.getBlob(index);
            case Cursor.FIELD_TYPE_FLOAT:
                return (T) Float.valueOf(cursor.getFloat(index));
            case Cursor.FIELD_TYPE_INTEGER:
                return (T) Integer.valueOf(cursor.getInt(index));
            case Cursor.FIELD_TYPE_STRING:
                return (T) cursor.getString(index);
        }
        return defaultValue;
    }

    public static final byte[] bitmapToByte(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] data = outputStream.toByteArray();
        outputStream.reset();
        return data;
    }

    public static final Bitmap byteToBitmap(byte[] bytes){
        return BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
    }

}
