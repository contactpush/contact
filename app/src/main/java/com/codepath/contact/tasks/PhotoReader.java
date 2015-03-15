package com.codepath.contact.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PhotoReader extends AsyncTask<Uri, Void, byte[]>{
    private static final String TAG = "PhotoReader";
    private PhotoReadCompletionListener listener;
    private Activity activity;

    public interface PhotoReadCompletionListener{
        void onPhotoReadCompletion(byte[] photoBytes);
    }

    public PhotoReader(Activity activity, PhotoReadCompletionListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected byte[] doInBackground(Uri... params) {
        if (params == null || params.length == 0){
            throw new IllegalArgumentException("You must past the uri of the file.");
        }
        Uri photoUri = params[0];
        return getPhotoBytes(photoUri);
    }

    @Override
    protected void onPostExecute(byte[] photoBytes){
        listener.onPhotoReadCompletion(photoBytes);
    }

    private byte[] getPhotoBytes(Uri uri){
        File photo = new File(uri.getPath());
        int size = (int) photo.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photo));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return alternatePhotoByteGetter(uri);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return alternatePhotoByteGetter(uri);
        }
        return bytes;
    }

    // some files can't be found by the method above.  This seems to work as an alternative.
    // http://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
    private byte[] alternatePhotoByteGetter(Uri uri){
        Bitmap selectedImage = null;
        byte[] bytes = null;
        try {
            selectedImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            if (selectedImage != null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bytes = stream.toByteArray();
            } else {
                Log.e(TAG, "Could not find selected image.");
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return bytes;
    }
}
