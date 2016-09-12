package com.v1.firebase.scholarplus.ui.scholarlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by sekartanjung on 6/12/16.
 */
public class ScholarListDetailActivity extends BaseActivity {
    private ImageView photoScholarship,bgPhotoScholarship;
    private TextView title,nama,deskripsi,web,dd,persyaratan,berkas,ipk,sd,jenis,asal,kategori;
    private String snama,sdeskripsi,sweb,sdd,spersyaratan,sberkas;
    private Scholarship scholarship;
    private Intent i;
    View.OnClickListener mOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_scholarship);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

         i = this.getIntent();
        Bitmap b = BitmapFactory.decodeByteArray(
                i.getByteArrayExtra(Constants.DETAIL_PHOTO),0,getIntent().getByteArrayExtra(Constants.DETAIL_PHOTO).length);

        photoScholarship = (ImageView) findViewById(R.id.photo_scholarship);
        bgPhotoScholarship = (ImageView) findViewById(R.id.foto_beasiswa_gan);
        title = (TextView) findViewById(R.id.details_title);
        photoScholarship.setImageBitmap(b);
        String bgPhoto = i.getStringExtra(Constants.DETAIL_BG_PHOTO);
            if (bgPhoto != null && !bgPhoto.isEmpty())
//                new DownLoadImageTask(bgPhotoScholarship).execute(bgPhoto);
                Picasso.with(getApplicationContext())
                        .load(bgPhoto)
//                        .resize(50,50)
//                        .centerCrop()
                        .into(bgPhotoScholarship);

        scholarship = (Scholarship) i.getSerializableExtra(Constants.FIREBASE_PROPERTY_BEASISWA);
        title.setText(scholarship.getInstansi());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeScreen();

    }

    public void onApplyPressed(View view) {
        confirmDialog();
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Konfirmasi Apply")
                .setMessage("Anda yakin untuk apply ?")
                .setPositiveButton("Ya",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.jijiji), "Berhasil apply beasiswa", Snackbar.LENGTH_LONG)
                                .setAction("Undo", mOnClickListener);
                        snackbar.setActionTextColor(Color.RED);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(Color.DKGRAY);
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_send_black_48dp)
                .show();
    }



    private void initializeScreen() {
        nama = (TextView)findViewById(R.id.tvNama_beasiswa);
        deskripsi = (TextView)findViewById(R.id.tvDesk_beasiswa);
        web = (TextView)findViewById(R.id.tvWeb_beasiswa);
        dd = (TextView)findViewById(R.id.tvDd_beasiswa);
        persyaratan = (TextView)findViewById(R.id.tvPersyaratan);
        berkas = (TextView)findViewById(R.id.tvBerkas);
        ipk = (TextView) findViewById(R.id.ipkkuh);
        sd = (TextView)findViewById(R.id.umurs);
        jenis = (TextView)findViewById(R.id.tvJenis_beasiswa);
        asal = (TextView)findViewById(R.id.tvAsal_beasiswa);
        kategori = (TextView)findViewById(R.id.tvKategori_beasiswa);

        nama.setText(scholarship.getNama());
        deskripsi.setText(scholarship.getDeskripsi());
        web.setText(scholarship.getWeb());
        dd.setText(scholarship.getDuedate());
        ipk.setText("IPK "+scholarship.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString());
        sd.setText(scholarship.getStartdate());
        jenis.setText(scholarship.getJenis());
        kategori.setText(scholarship.getKategori());
        asal.setText(scholarship.getAsal());

        spersyaratan = "";
        spersyaratan = "1. Bidang peminatan " +scholarship.getKuantitatif().get(Constants.FIREBASE_PROPERTY_BIDANG).toString()+"<br>";
        spersyaratan = spersyaratan + "2. IPK minimum "+ scholarship.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString()+"<br>";
        spersyaratan = spersyaratan +"3. Semester minimum "+ scholarship.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString()+"<br>";
        int c = 4;
        for (String temp : scholarship.getPersyaratankualitatif()) {
            spersyaratan = spersyaratan + c+". "+temp+"<br>";
            c++;
        }
        c= 1;
        sberkas = "";
        for (String temp : scholarship.getBerkas()) {
            sberkas = sberkas + c+". "+temp+"<br>";
            c++;
        }
        persyaratan.setText(Html.fromHtml(spersyaratan));
        berkas.setText(Html.fromHtml(sberkas));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
