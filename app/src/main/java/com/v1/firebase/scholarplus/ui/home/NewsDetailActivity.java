package com.v1.firebase.scholarplus.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by sekartanjung on 6/12/16.
 */
public class NewsDetailActivity extends BaseActivity {
    private TextView createdByUser,deskripsi;
    private ImageView photoBy;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        Intent i = this.getIntent();
        createdByUser = (TextView) findViewById(R.id.text_view_created_by_user);
        deskripsi = (TextView) findViewById(R.id.text_view_list_name);
        photoBy = (ImageView) findViewById(R.id.ivFeedCenter);
        String nama = "<big><big><b>"+i.getStringExtra(Constants.DETAIL_NAME)+"</b></big></big>";

        String sdeskripsi = i.getStringExtra(Constants.DETAIL_DESC);

        Bitmap b = BitmapFactory.decodeByteArray(
                i.getByteArrayExtra(Constants.DETAIL_PHOTO),0,getIntent().getByteArrayExtra(Constants.DETAIL_PHOTO).length);

        photoBy.setImageBitmap(b);
        deskripsi.setText(Html.fromHtml(nama+"<br><br><br>"+sdeskripsi));
        createdByUser.setText(i.getStringExtra(Constants.DETAIL_BY));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(i.getStringExtra(Constants.DETAIL_BY));

    }

    @Override
    public void onBackPressed() {
        this.finish();
        return;
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
