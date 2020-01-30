package com.mtk.util;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class ImageLoadTask extends AsyncTask<Void, Void, Drawable> {
    private String url;

    public abstract void onRecived(Drawable drawable);

    public ImageLoadTask(String url) {
        this.url = url;
    }

    protected Drawable doInBackground(Void... params) {
        MalformedURLException e;
        IOException e2;
        Drawable drawable = null;
        try {
            InputStream in = (InputStream) new URL(this.url).openConnection().getContent();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            while (true) {
                int bytesRead = in.read(buf);
                if (bytesRead != -1) {
                    bos.write(buf, 0, bytesRead);
                } else {
                    byte[] byteArray = bos.toByteArray();
                    Drawable drawable2 = new BitmapDrawable(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                    try {
                        in.close();
                        bos.close();
                        return drawable2;
                    } catch (MalformedURLException e3) {
                        e = e3;
                        drawable = drawable2;
                        e.printStackTrace();
                        return drawable;
                    } catch (IOException e4) {
                        e2 = e4;
                        drawable = drawable2;
                        e2.printStackTrace();
                        return drawable;
                    }
                }
            }
        } catch (MalformedURLException e5) {
            e = e5;
            e.printStackTrace();
            return drawable;
        } catch (IOException e6) {
            e2 = e6;
            e2.printStackTrace();
            return drawable;
        }
    }

    protected void onPostExecute(Drawable result) {
        super.onPostExecute(result);
        onRecived(result);
    }
}
