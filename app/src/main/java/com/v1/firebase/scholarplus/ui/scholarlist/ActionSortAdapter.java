package com.v1.firebase.scholarplus.ui.scholarlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by MacBook on 9/4/16.
 */
public class ActionSortAdapter extends SortAdapter<Scholarship, ScholarListFragment.ScholarshipViewHolder> {
    private ProgressBar mProgressBar;
    private Activity mActivity;
    public ActionSortAdapter(Activity activity, Class<Scholarship> modelClass, int modelLayout, Class<ScholarListFragment.ScholarshipViewHolder> viewHolderClass, Query ref, ProgressBar mProgressBar,String sort) {
        super(modelClass, modelLayout, viewHolderClass, ref,sort);
        this.mProgressBar = mProgressBar;
        this.mActivity = activity;

    }

    @Override
    protected void populateViewHolder(final ScholarListFragment.ScholarshipViewHolder viewHolder,final Scholarship model, int position) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        viewHolder.createdByUser.setText(model.getNama());
        viewHolder.ipk.setText("min IPK "+model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString());
        viewHolder.website.setText("min Semester "+model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString());
        Picasso.with(this.mActivity)
                .load(model.getBgphotopath())
//                    .resize(50, 50)
//                    .centerCrop()
                .into(viewHolder.rl_bg);

        viewHolder.ipk.setText("IPK "+model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString());
        viewHolder.website.setText(model.getInstansi());

        try{
            String duedate = Constants.getMonth(model.getDuedate());
            viewHolder.duedate.setText(duedate);
        }
        catch(ParseException e){
            viewHolder.duedate.setText("-");

            Log.d("AAA",e.getMessage());


        }

        if (model.getPhotopath() != null)
//        new DownLoadImageTask(viewHolder.photoBy).execute(model.getPhotopath());
            Picasso.with(this.mActivity)
                    .load(model.getPhotopath())
//                    .resize(50, 50)
//                    .centerCrop()
                    .into(viewHolder.photoBy);

        final String itemId = this.getRef(position).getKey();
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity,ScholarListDetailActivity.class);
                viewHolder.photoBy.buildDrawingCache();
                Bitmap bm= viewHolder.photoBy.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
                byte[] b = baos.toByteArray();

                i.putExtra(Constants.FIREBASE_PROPERTY_BEASISWA, model);
                i.putExtra(Constants.DETAIL_PHOTO,b);
                i.putExtra(Constants.DETAIL_BG_PHOTO,model.getBgphotopath());
                i.putExtra(Constants.KEY_ID,itemId);
                mActivity.startActivity(i);
            }
        });

        viewHolder.taptoshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String msg = model.getNama()+"\n\n";
                String kat = "Kategori Beasiswa :\n"+model.getKategori() +" - "+model.getJenis();
                msg = msg + kat;

                String spersyaratan = "Persyaratan : \n";
                spersyaratan = spersyaratan + "1. Bidang peminatan " +model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_BIDANG).toString()+"\n";
                spersyaratan = spersyaratan + "2. IPK minimum "+ model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString()+"\n";
                spersyaratan = spersyaratan + "3. Semester minimum "+ model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString()+"\n";
                int c = 4;
                for (String temp : model.getPersyaratankualitatif()) {
                    spersyaratan = spersyaratan + c+". "+temp+"\n";
                    c++;
                }
                c= 1;
                String sberkas = "Berkas :\n";
                for (String temp : model.getBerkas()) {
                    sberkas = sberkas + c+". "+temp+"\n";
                    c++;
                }
                String cp = "Kontak/Website :\n"+model.getWeb();
                String copyright = "\n\n\nSCHOLARPLUS.CO";
                msg = msg+"\n"+spersyaratan +"\n"+sberkas+"\n"+cp +"\n\n"+copyright;

                shareIntent.putExtra(Intent.EXTRA_TEXT,msg);
                shareIntent.setType("text/plain");
                // Launch sharing dialog for image
                mActivity.startActivity(Intent.createChooser(shareIntent, "Share Image"));
//
//                Target shareTarget = new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        // Get access to the URI for the bitmap
//                        Uri bmpUri = getLocalBitmapUri(bitmap);
//                        if (bmpUri != null) {
//                            // Construct a ShareIntent with link to image
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.putExtra(Intent.EXTRA_TEXT,model.getDeskripsi());
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                            shareIntent.setType("text/*");
//                            // Launch sharing dialog for image
//                            mActivity.startActivity(Intent.createChooser(shareIntent, "Share Image"));
//
//
//                        } else {
//                            // ...sharing failed, handle error
//                        }
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                };
//                Picasso.with(mActivity).load(model.getPhotopath()).into(shareTarget);



            }
        });

    }
    public Uri getLocalBitmapUri(Bitmap bmp) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}

