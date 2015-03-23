package com.codepath.contact.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

public class PhotoReader extends AsyncTask<Uri, Void, Bitmap>{
    private static final String TAG = "PhotoReader";
    private PhotoReadCompletionListener listener;
    private Activity activity;

    public interface PhotoReadCompletionListener{
        void onPhotoReadCompletion(byte[] photo);
        void onPhotoReadCompletion(Bitmap photo);
    }

    public PhotoReader(Activity activity, PhotoReadCompletionListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        if (params == null || params.length == 0){
            throw new IllegalArgumentException("You must past the uri of the file.");
        }
        Uri photoUri = params[0];
        return getPhotoBytes(photoUri);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        listener.onPhotoReadCompletion(bitmap);
    }

    private Bitmap getPhotoBytes(Uri uri){
        File photo = new File(uri.getPath());
        if (!photo.exists()){
            return alternatePhotoByteGetter(uri);
        }
        rotateBitmapOrientation(uri.getPath());
        int size = (int) photo.length();
        byte[] bytes = new byte[size];
        Bitmap compressedBitmap = null;
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photo));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            bytes = compress(bytes);
            // send listener bytes to write to parse
            listener.onPhotoReadCompletion(bytes);
            // convert to bitmap for loading into image view
            compressedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return alternatePhotoByteGetter(uri);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return alternatePhotoByteGetter(uri);
        }
        return compressedBitmap;
    }

    // some files can't be found by the method above.  This seems to work as an alternative.
    // http://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
    private Bitmap alternatePhotoByteGetter(Uri uri){
        Log.d(TAG, "using alternatePhotoByteGetter");
        Bitmap selectedImage = null;
        byte[] bytes = null;
        Bitmap compressedBitmap = null;
        try {
            selectedImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            if (selectedImage != null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bytes = compress(stream.toByteArray());
                // send listener bytes to write to parse
                listener.onPhotoReadCompletion(bytes);
                // convert to bitmap for loading into image view
                compressedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } else {
                Log.e(TAG, "Could not find selected image.");
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return compressedBitmap;
    }

    private byte[] compress(byte[] bytes){
        int size = bytes.length;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        //BitmapFactory.decodeStream(new FileInputStream(photo),null,o);

        //test
        BitmapFactory.decodeByteArray(bytes, 0, size, o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=70;

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        //Bitmap b = BitmapFactory.decodeStream(new FileInputStream(photo), null, o2);
        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, size, o2);

        // convert to bytes
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bytes = stream.toByteArray();
        return bytes;
    }

    private Bitmap rotateBitmapOrientation(String file) {
        Log.d(TAG, "rotating image");
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(file, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        Log.d(TAG, "orientString: " + orientString);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        Log.d(TAG, "orientation: " + orientation);
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        Log.d(TAG, "rotationAngle: " + rotationAngle);
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
}
