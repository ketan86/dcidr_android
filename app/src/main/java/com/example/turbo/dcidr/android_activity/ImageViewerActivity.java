package com.example.turbo.dcidr.android_activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.image_viewer_activity_helper.ImageViewerCustomArrayAdapter;
import com.example.turbo.dcidr.main.common.ImageViewer;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.util.ArrayList;

/**
 * Created by Turbo on 10/4/2016.
 */
public class ImageViewerActivity extends BaseActivity {

    private GridView mImageViewerCustomGridView;
    private ImageViewerCustomArrayAdapter mImageViewerCustomArrayAdapter;
    private ArrayList<ImageViewer> mImageBitmapArrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // make sure user is populated before proceeding, refer to base activity onCreate
        setUserFetchListener(new UserFetchListener() {
            @Override
            public void onFetchDone() {
                initActivity();
            }
        });
        super.onCreate(savedInstanceState);
    }
    public void initActivity(){
        setContentView(R.layout.activity_image_viewer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageBitmapArrayList = new ArrayList<>();
        mImageViewerCustomGridView = (GridView) findViewById(R.id.image_viewer_custom_grid_view);
        mImageViewerCustomArrayAdapter = new ImageViewerCustomArrayAdapter(this, R.id.image_viewer_custom_grid_view, mImageBitmapArrayList);
        mImageViewerCustomGridView.setAdapter(mImageViewerCustomArrayAdapter);
        //mImageViewerCustomGridView.setOnScrollListener();
        mImageViewerCustomGridView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showImageViewDialog(null, Utils.decodeUriAsBitmap(ImageViewerActivity.this, android.net.Uri.parse(SD_IMAGE_FILE.toURI().toString()), 500, 500));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }else if(id == R.id.take_photos){
            if(!hasPermission(Manifest.permission.CAMERA)){
                requestPermission(new String[]{Manifest.permission.CAMERA}, getResources().getString(R.string.camera_permission_rationale) ,BaseActivity.CAMERA_PERMISSION_REQUEST_CODE);
            }else {
                // pre-marshmallow releases. No run-time permission so start camera intent.
                launchImageCaptureIntent();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == BaseActivity.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                launchImageCaptureIntent();
            } else {
                // Permission Denied
                // nothing to do
            }
        }
        if(requestCode == BaseActivity.STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                storeImageToStorageAndRetreive();
            } else {
                // Permission Denied
                // nothing to do
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // profile capture intent result
        if(requestCode == BaseActivity.CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if(!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, getResources().getString(R.string.external_storage_permission_rationale) ,BaseActivity.STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE);
            }else {
                storeImageToStorageAndRetreive();
            }

        }
    }

    public void storeImageToStorageAndRetreive(){
        Bitmap bitmap = Utils.decodeUriAsBitmap(this, android.net.Uri.parse(SD_IMAGE_FILE.toURI().toString()), 100, 100);
        //Bundle extras = data.getExtras();
        //Bitmap bitmap = (Bitmap) extras.get("data");
        ImageViewer imageViewer = new ImageViewer(this);
        imageViewer.setImageBitmap(bitmap);
        imageViewer.setAddedByUserName(DcidrApplication.getInstance().getUser().getUserName());
        for(int i=0 ;i< 20;i++){
            mImageBitmapArrayList.add(imageViewer);
        }
        mImageViewerCustomArrayAdapter.notifyDataSetChanged();
    }
}
