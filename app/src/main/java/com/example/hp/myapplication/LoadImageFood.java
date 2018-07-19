package com.example.hp.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadImageFood extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;

    public LoadImageFood(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            return showIcon(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap s) {
        imageView.setImageBitmap(s);
    }

    private Bitmap showIcon(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        Bitmap bitmap = null;
        httpConn.connect();
        int resCode = httpConn.getResponseCode();

        if (resCode == HttpURLConnection.HTTP_OK) {
            InputStream in = httpConn.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        return bitmap;
    }
}
