package com.codepath.contact;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parse.ParseUser;

public class QRCodeWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_qr_code);

            String username = ParseUser.getCurrentUser().getUsername();
            if (username.length() == 0) {
                username = "test2";
            }
            remoteViews.setTextViewText(R.id.tvUserName, username);

            // TODO(emily) refactor this, copied from SearchUsernameFragment.java
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix qrBitMatrix = qrCodeWriter.encode(username, BarcodeFormat.QR_CODE, 400, 400);
                Bitmap qrBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);
                for (int x = 0; x < 400; x++){
                    for (int y = 0; y < 400; y++){
                        qrBitmap.setPixel(x, y, qrBitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }
                remoteViews.setImageViewBitmap(R.id.ivQRCode, qrBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

    }
}
