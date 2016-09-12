package com.v1.firebase.scholarplus.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.v1.firebase.scholarplus.utils.Utility;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by MacBook on 6/17/16.
 */
public class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;
    public DownLoadImageTask(){

    }

    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    /*
        doInBackground(Params... params)
            Override this method to perform a computation on a background thread.
     */
    public Bitmap doInBackground(String...urls){
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try{
            InputStream is = new URL(urlOfImage.toString()).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
            logo = BitmapFactory.decodeStream(is);
        }catch(Exception e){ // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    /*
        onPostExecute(Result result)
            Runs on the UI thread after doInBackground(Params...).
     */
    public void onPostExecute(Bitmap result){

        imageView.setImageBitmap(Utility.getResizedBitmap(result,1000));
    }
}
