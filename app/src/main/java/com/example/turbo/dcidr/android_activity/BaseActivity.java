package com.example.turbo.dcidr.android_activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.user_activity_helper.UserActivityHelper;
import com.example.turbo.dcidr.utils.common_utils.LocationEnabler;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;

/**
 * Created by Borat on 2/26/2016.
 */
public class BaseActivity extends AppCompatActivity {
    // intent request codes
    public static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
    public static final int CROP_IMAGE_REQUEST_CODE = 2;
    public static final int LOCATION_ENABLE_REQUEST_CODE = 3;

    //Permission request codes
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    public static final int READ_PHONE_CONTACTS_PERMISSIONS_REQUEST_CODE = 200;
    public static final int CALENDAR_PERMISSION_REQUEST_CODE = 300;
    public static final int STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE = 400;

    public static final String TAG = "BaseActivity";
    private ProgressDialog mProgressDialog;
    public File SD_IMAGE_FILE;
    public File CROPPED_IMAGE_FILE;

    private UserFetchListener mUserFetchListener;
    public interface UserFetchListener{
        void onFetchDone();
    }

    public void setUserFetchListener(UserFetchListener userFetchListener){
        this.mUserFetchListener = userFetchListener;
    }

    private LocationUpdateInterface mLocationUpdateInterface;

    public interface LocationUpdateInterface {
        void onLocationChange(Location location);
    }

    public void onLocationUpdateListener(LocationUpdateInterface locationUpdateInterface){
        this.mLocationUpdateInterface = locationUpdateInterface;
    }
    /**
     * Constructor for BaseActivity. Common initialization should happen here
     */
    public BaseActivity() {
        super();
        //SD_IMAGE_FILE = new File(Environment.getExternalStorageDirectory(), "myImageFile.jpg");
        //CROPPED_IMAGE_FILE = new File(Environment.getExternalStorageDirectory(), "croppedImageFile.jpg");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG){Log.i(TAG, "[onCreate] initializing base activity onCreate");}
        // fetch user if not there
        DcidrApplication.getInstance().getUserCache().setSharedPreferences(this);
        // get userId from cache
        String userId = DcidrApplication.getInstance().getUserCache().get("userId");
        if (userId != null) {
            // if user class is not populated, populate it. otherwise finish the activity and return
            if (!DcidrApplication.getInstance().getUser().getIsPopulated()) {
                UserActivityHelper userActivityHelper = new UserActivityHelper(this);
                userActivityHelper.setUserFetchInterface(new UserActivityHelper.UserFetchInterface() {
                    @Override
                    public void onFetchDone() {
                    if(mUserFetchListener != null) {
                        mUserFetchListener.onFetchDone();
                    }
                    }
                });
                userActivityHelper.initUser();
            }else {
                if(mUserFetchListener != null) {
                    mUserFetchListener.onFetchDone();
                }
            }
        }else {
            if(mUserFetchListener != null) {
                mUserFetchListener.onFetchDone();
            }
        }

        SD_IMAGE_FILE = new File(Environment.getExternalStorageDirectory(), "myImageFile.jpg");
        CROPPED_IMAGE_FILE = new File(Environment.getExternalStorageDirectory(), "croppedImageFile.jpg");
    }

    /**
     * show dialog method
     * @param msg : msg to show with ok button only
     */
    public void showAlertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create().show();
    }

    /**
     * show progress message and return progress dialog object back to caller
     * @param context activity context
     * @param msg msg to show
     * @return progress dialog object
     */
    public ProgressDialog getAndShowProgressDialog(Context context, String msg) {
        mProgressDialog = new ProgressDialog(context, R.style.ProgressBarTheme);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        //progressDialog.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.custom_loading));
        //progressDialog.setMessage(msg);
        mProgressDialog.setCanceledOnTouchOutside(false);
        // TODO need to enable later
        //progressDialog.setCancelable(false);
        mProgressDialog.show();
        return mProgressDialog;
    }

    /**
     * method to dismiss progress dialog
     * @param progressDialog progress dialog object used while creating progress dialog
     */
    public void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    /**
     * method to get custom dialog title text view
     * @param title title of the text view
     * @return return textVie
     */
    public TextView getCustomDialogTitleTextView(String title, float textSize) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        textView.setTextSize(textSize);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setPadding(10, 50, 10, 50);
        //textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return textView;
    }


    public int getWindowScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public int getWindowScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public boolean hasPermission(String permissionName) {
        int hasPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasPermission = checkSelfPermission(permissionName);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermission(final String[] permissions, String permissionRationale, final int requestCode) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), permissionRationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(BaseActivity.this, permissions,
                                            requestCode);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    public void notifyDeveloper(String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        String[] emailArray = getResources().getString(R.string.developer_email_list).split(",");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailArray);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(emailIntent);
    }



//    public Bitmap decodeUriAsBitmap(Uri uri){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return bitmap;
//    }

    public void launchGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), BaseActivity.CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void launchImageCaptureIntent() {
        Uri outputFileUri = Uri.fromFile(SD_IMAGE_FILE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, BaseActivity.CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void initCurrentLocation() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, getResources().getString(R.string.location_permission_rationale), BaseActivity.LOCATION_ENABLE_REQUEST_CODE);
        } else {
            // ask for location enable popup

            LocationEnabler locationEnabler = new LocationEnabler(this);
            locationEnabler.enableLocation(BaseActivity.LOCATION_ENABLE_REQUEST_CODE);
            locationEnabler.onLocationStatusListener(new LocationEnabler.LocationStatusInterface() {
                @Override
                public void getLocationStatus(int statusCode) {
                    if(statusCode == LocationSettingsStatusCodes.SUCCESS) {
                        invokeLocationManager();
                    }
                }
            });


        }
    }

    public void invokeLocationManager(){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(mLocationUpdateInterface != null) {
                    mLocationUpdateInterface.onLocationChange(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("BaseActivity", "Location changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("BaseActivity", "Provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("BaseActivity", "Provider disabled");
            }
        });

    }

//    public double[] getCurrentLocation(){
//        if(mCurrentLocationLat != 0 && mCurrentLocationLong != 0) {
//            double[] latLongArray = new double[2];
//            latLongArray[0] = mCurrentLocationLat;
//            latLongArray[1] = mCurrentLocationLong;
//            return latLongArray;
//        }
//        return null;
//    }


//    public double getDistanceToPlaceFromCurrentLoc(double endLat, double endLong){
//        float[] resultArray = new float[0];
//        Location.distanceBetween(mCurrentLocationLat, mCurrentLocationLong, endLat, endLong, resultArray);
//        return resultArray[0];
//    }
    public void launchCropImageIntent(File bitmapFile, File croppedFileUri){
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(android.net.Uri.parse(bitmapFile.toURI().toString()), "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedFileUri));
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE_REQUEST_CODE);
    }



    public void showImageViewDialog(String base64Str, Bitmap bitmap) {

        Dialog imageViewDialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.fragment_image_view
                , null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);

        imageViewDialog.setContentView(view);
        imageViewDialog.show();

        // Get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;


        int bitmapHeight = 0;
        int bitmapWidth = 0;
        if(base64Str != null) {
            bitmap = Utils.decodeBase64(base64Str, screenWidth, screenHeight);
            bitmapHeight = bitmap.getHeight();
            bitmapWidth = bitmap.getWidth();
        }else if(bitmap != null) {
            // Get target image size
            bitmapHeight = bitmap.getHeight();
            bitmapWidth = bitmap.getWidth();
        }
        // find possible width for scale
        int scaleSize = screenWidth - bitmapWidth;

        // Create resized bitmap image
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, bitmapHeight + scaleSize, false);

        if(scaledBitmap != null) {
            imageView.setImageBitmap(scaledBitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog = null;
        mLocationUpdateInterface = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == BaseActivity.LOCATION_ENABLE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                initCurrentLocation();
            } else {
                // Permission Denied
                // nothing to do
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BaseActivity.LOCATION_ENABLE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                invokeLocationManager();
            }else if (resultCode == RESULT_CANCELED){
                // nothing to do
            }
        }
        if(requestCode == BaseActivity.CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if(!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, getResources().getString(R.string.external_storage_permission_rationale) ,BaseActivity.STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE);
            }else {
            }

        }
    }


}
