package com.codepath.contact.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PhotoReader extends AsyncTask<Uri, Void, byte[]>{
    private static final String TAG = "PhotoReader";
    private PhotoReadCompletionListener listener;

    public interface PhotoReadCompletionListener{
        void onPhotoReadCompletion(byte[] photoBytes);
    }

    public PhotoReader(PhotoReadCompletionListener listener){
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
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return bytes;
        //ParseFile file = new ParseFile("resume.txt", photo);
    }
}
