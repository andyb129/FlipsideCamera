package com.sixteenplusfour.android.flipsidecamera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;

import com.sixteenplusfour.android.flipsidecamera.R;
import com.sixteenplusfour.android.flipsidecamera.customviews.SwipeImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.VignetteFilterTransformation;

/**
 * Created by Andy on 18/09/13.
 */
public class Utils {

    private static final String TAG = "Utils";

    public static void transformImage(Context ctx, int transformationNumber, SwipeImageView image,
                               File file, Callback loadedCallback) {
        switch (transformationNumber)
        {
            case 0:
                //normal image
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .into(image, loadedCallback);
                break;
            case 1:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new SepiaFilterTransformation(ctx)).into(image, loadedCallback);
                break;
            case 2:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new GrayscaleTransformation()).into(image, loadedCallback);
                break;
            case 3:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new ToonFilterTransformation(ctx)).into(image, loadedCallback);
                break;
            case 4:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new ContrastFilterTransformation(ctx, 2.0f)).into(image, loadedCallback);
                break;
            case 5:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new InvertFilterTransformation(ctx)).into(image, loadedCallback);
                break;
            case 6:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new PixelationFilterTransformation(ctx, 20)).into(image, loadedCallback);
                break;
            case 7:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new SketchFilterTransformation(ctx)).into(image, loadedCallback);
                break;
            case 8:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new SwirlFilterTransformation(ctx, 0.5f, 1.0f, new PointF(0.5f, 0.5f))).into(image, loadedCallback);
                break;
            case 9:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new BrightnessFilterTransformation(ctx, 0.5f)).into(image, loadedCallback);
                break;
            case 10:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new VignetteFilterTransformation(ctx, new PointF(0.5f, 0.5f),
                                new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.75f)).into(image, loadedCallback);
                break;
            case 11:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new ColorFilterTransformation(Color.argb(80, 255, 0, 0))).into(image, loadedCallback);
                break;
            case 12:
                Picasso.with(ctx).load(file).fit().centerCrop().noPlaceholder()
                        .transform(new ColorFilterTransformation(Color.argb(80, 255, 0, 0))).into(image, loadedCallback);
                break;
        }
    }

    public static String halfBitmap(Context ctx, boolean isTop, Bitmap bitmap)
    {
        // resize them first so the image returned is the same size with both images in each half
        Bitmap halfBitmap;
        int halfHeight = bitmap.getHeight() / 2;
        if (isTop)
        {
            halfBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), halfHeight);
        } else {
            halfBitmap = Bitmap.createBitmap(bitmap, 0, halfHeight, bitmap.getWidth(), halfHeight);
        }

        // To write the file out to the SDCard:
        return saveBitmapToStorage(ctx, halfBitmap, null, false);
    }

    public static String combineBitmaps(Context ctx, Bitmap firstImage, Bitmap secondImage) {
        Bitmap bmOverlay = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight()*2, firstImage.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(firstImage, new Matrix(), null);
        canvas.drawBitmap(secondImage, 0, firstImage.getHeight(), null);

        firstImage = null;
        secondImage = null;

        // To write the file out to the SDCard:
        String newFilePath = saveBitmapToStorage(ctx, bmOverlay, null, true);

        //clean up temp dir
        cleanUpTempDir(ctx);

        return newFilePath;
    }

    public static void cleanUpTempDir(Context ctx) {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/" + ctx.getPackageName() + "/tmp/");
        File[] Files = root.listFiles();
        if(Files != null) {
            int j;
            for(j = 0; j < Files.length; j++) {
                //Files[j].getAbsolutePath());
                Files[j].delete();
            }
        }
    }

    /**
     * util method to save a bitmap in the external storage and retrun a path to the file
     *
     * @param ctx
     * @param bitmap
     * @return
     */
    private static String saveBitmapToStorage(Context ctx, Bitmap bitmap, String filename, boolean isShareDir)
    {
        String IMAGE_TEMP_LOCATION = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/" + ctx.getPackageName() + "/tmp/";
        String IMAGE_LOCATION = isShareDir ? getDirPath(ctx) : IMAGE_TEMP_LOCATION;

        String imagePath = "";
        if (filename==null)
        {
            File sdDir = new File(IMAGE_LOCATION);
            if (!sdDir.exists() && !sdDir.mkdirs()) {
                Log.d(TAG, "Can't create directory to save image.");
                return "";
            }
            imagePath = sdDir + "/front_back_camera_app_" + System.currentTimeMillis() + ".png";
        } else {
            imagePath = filename;
        }

        OutputStream fos = null;
        try
        {
            fos = new FileOutputStream(imagePath);
            if (fos != null)
            {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            }
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            // e.printStackTrace();
            Log.w(TAG, "saveBitmapToStorage() - file not found");
        }
        catch (IOException e)
        {
            // e.printStackTrace();
            Log.w(TAG, "saveBitmapToStorage() - IO exception");
        }

        bitmap = null;

        return imagePath;
    }

    private static String getDirPath(Context ctx) {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, ctx.getResources().getString(R.string.app_name)).getPath();
    }

    // A method to find height of the status bar
    public static int getStatusBarHeightNew(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
