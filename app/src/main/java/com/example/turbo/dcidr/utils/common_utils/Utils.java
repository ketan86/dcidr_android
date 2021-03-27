package com.example.turbo.dcidr.utils.common_utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import com.example.turbo.dcidr.android_activity.BaseActivity;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Turbo on 2/14/2016.
 */
public class Utils {

    public static int generateRandomColor() {
        Color color = new Color();
        return color.rgb(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
    }
    public static String convertDateToMeaningfulText(long epochTimeinMilliSecs, boolean future) {
        String returnDateStr = "";
        if (future) {
            long millisUntilFinished = epochTimeinMilliSecs - System.currentTimeMillis();
            if (millisUntilFinished <= 0) {
                return "EXPIRED";
            }
            long diffSeconds = millisUntilFinished / 1000 % 60;
            long diffMinutes = millisUntilFinished / (60 * 1000) % 60;
            long diffHours = millisUntilFinished / (60 * 60 * 1000) % 24;
            long diffDays = millisUntilFinished / (24 * 60 * 60 * 1000);

            if (diffSeconds != 0) {
                returnDateStr = diffSeconds + " Secs left";
            }
            if (diffMinutes != 0) {
                returnDateStr = diffMinutes + " Mins left";
            }
            if (diffHours != 0) {
                returnDateStr = diffHours + " Hrs left";
            }
            if (diffDays != 0) {
                returnDateStr = diffDays + " Days left";
            }
            return returnDateStr;
        } else {
            long millisSinceLastUpdated = System.currentTimeMillis() - epochTimeinMilliSecs;
            if (millisSinceLastUpdated < 1000) {
                //"UPDATED IN THE FUTURE !!";
                return "0 Secs ago";
            }
            long diffSeconds = millisSinceLastUpdated / 1000 % 60;
            long diffMinutes = millisSinceLastUpdated / (60 * 1000) % 60;
            long diffHours = millisSinceLastUpdated / (60 * 60 * 1000) % 24;
            long diffDays = millisSinceLastUpdated / (24 * 60 * 60 * 1000);

            if (diffSeconds != 0) {
                returnDateStr = diffSeconds + " Secs ago";
            }
            if (diffMinutes != 0) {
                returnDateStr = diffMinutes + " Mins ago";
            }
            if (diffHours != 0) {
                returnDateStr = diffHours + " Hrs ago";
            }
            if (diffDays != 0) {
                returnDateStr = diffDays + " Days ago";
            }
            return returnDateStr;        
        }
    }

    public static String[] getStringArray(JSONArray jsonArray){
        String[] stringArray = null;
        int length = jsonArray.length();
        if(jsonArray!=null){
            stringArray = new String[length];
            for(int i=0;i<length;i++){
                stringArray[i]= jsonArray.optString(i);
            }
        }
        return stringArray;
    }

    public static String capitalizeFirstLetter(String string){
        return String.format("%s%s", string.substring(0, 1).toUpperCase(), string.substring(1).toLowerCase());
    }

    public static String hashString(String message, String algorithm)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // algorithm can be "MD5", "SHA-1", "SHA-256"
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
        return convertByteArrayToHexString(hashedBytes);

    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 800;
        int targetHeight = 800;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static long convertDateTimeToEpoch(String date, String timeStr, TimeZone tz, String formatStr){
        String str = date + " " + timeStr;
        SimpleDateFormat df = new SimpleDateFormat(formatStr);//"MM/dd/yyyy hh:mm aa");
        df.setTimeZone(tz);
        Date newdate = null;
        try {
            newdate = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newdate.getTime();
    }

    public static final String convertEpochToDateTime(long epoch, TimeZone tz, String formatStr) {
        Date date = new Date(epoch);
        SimpleDateFormat df = new SimpleDateFormat(formatStr);//"MM/dd/yyyy hh:mm aa");
        df.setTimeZone(tz);
        String[] dateTimeArray = new String[3];
        return df.format(date);
    }


    public static String encodeToBase64(Bitmap image)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] b = os.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }






    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeUriAsBitmap(BaseActivity baseActivity, Uri uri, int reqWidth, int reqHeight) {
        try {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(baseActivity.getContentResolver().openInputStream(uri), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(baseActivity.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeBase64(String input, int reqWidth, int reqHeight)
    {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}


