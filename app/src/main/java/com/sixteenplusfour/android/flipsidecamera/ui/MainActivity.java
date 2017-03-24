package com.sixteenplusfour.android.flipsidecamera.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sixteenplusfour.android.flipsidecamera.R;
import com.sixteenplusfour.android.flipsidecamera.customviews.SwipeImageView;
import com.sixteenplusfour.android.flipsidecamera.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.camera_preview) CameraView cameraPreview;

    @BindView(R.id.top_pic_image_layout) FrameLayout topPicImageLayout;
    @BindView(R.id.top_pic) SwipeImageView topPic;
    @BindView(R.id.top_pic_progress) ProgressBar topPicProgress;
    @BindView(R.id.top_bottom_pic_image_layout) FrameLayout topBottomPicImageLayout;
    @BindView(R.id.top_bottom_pic) SwipeImageView topBottomPic;
    @BindView(R.id.top_bottom_pic_progress) ProgressBar topBottomPicProgess;
    @BindView(R.id.top_empty_pic) View topEmptyPic;
    @BindView(R.id.top_pic_layout) LinearLayout topPicLayout;

    @BindView(R.id.bottom_empty_pic) View bottomEmptyPic;
    @BindView(R.id.bottom_top_pic) ImageView bottomTopPic;
    @BindView(R.id.bottom_pic) ImageView bottomPic;
    @BindView(R.id.bottom_pic_layout) LinearLayout bottomPicLayout;

    @BindView(R.id.capture_photo_button) FloatingActionButton capturePhotoButton;
    @BindView(R.id.circle_menu) CircleMenu circleMenu;

    private File fileTopImage, fileBottomImage;
    private MenuItem helpActionItem, flashActionItem;
    private boolean isTopPictureTaken;
    private boolean isReverseImageOrder;
    private boolean hasPermissiongranted;
    private int transformNumberTop, transformNumberBottom;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
        if (hasPermissiongranted) {
            cameraPreview.start();
        }
    }

    @Override
    protected void onPause() {
        cameraPreview.stop();
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        helpActionItem = menu.findItem(R.id.help_item);
        helpActionItem.setVisible(false);
        flashActionItem = menu.findItem(R.id.flash_item);
        toggleFlashAuto();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.about_item) {
            startActivity(AboutActivity.createIntent(this));
        } else if (item.getItemId() == R.id.help_item) {
            showImageEffectsHelp();
        } else if (item.getItemId() == R.id.flash_item) {
            toggleFlash();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.capture_photo_button)
    void capturePhoto() {
        cameraPreview.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

                //show pictures and change view state
                if (isTopPictureTaken) {

                    saveBottomImage(bitmap);

                } else {

                    saveTopImage(bitmap);

                }
            }
        });
        cameraPreview.captureImage();
    }

    private void initViews() {
        ButterKnife.bind(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        params.setMargins(0, Utils.getStatusBarHeightNew(this), 0, 0);
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);

        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                switch (menuButton.getId()) {
                    case R.id.clear_images_button:
                        clearImages();
                        break;
                    case R.id.swap_images_button:
                        swapImages();
                        break;
                    case R.id.share_button:
                        shareImage();
                        break;
                }
            }
        });

        topPic.setSwipeListener(new SwipeImageView.OnSwipeListener() {
            @Override
            public void onSwipeRight() {
                imageEffectRight(topPic, isReverseImageOrder ? fileBottomImage : fileTopImage, true);
            }

            @Override
            public void onSwipeLeft() {
                imageEffectLeft(topPic, isReverseImageOrder ? fileBottomImage : fileTopImage, true);
            }
        });
        topPic.setIsSwipeEnabled(false);
        topBottomPic.setSwipeListener(new SwipeImageView.OnSwipeListener() {
            @Override
            public void onSwipeRight() {
                imageEffectRight(topBottomPic, isReverseImageOrder ? fileTopImage : fileBottomImage, false);
            }

            @Override
            public void onSwipeLeft() {
                imageEffectLeft(topBottomPic, isReverseImageOrder ? fileTopImage : fileBottomImage, false);
            }
        });
        topBottomPic.setIsSwipeEnabled(false);
    }

    private void imageEffectLeft(SwipeImageView swipeImageView, File imageFile, final boolean isTop) {
        if (isTop) {
            if (transformNumberTop >= 1) {
                transformNumberTop -= 1;
            }
        } else {
            if (transformNumberBottom >= 1) {
                transformNumberBottom -= 1;
            }
        }
        setProgressVisibility(isTop, true);
        Utils.transformImage(getApplicationContext(), isTop ? transformNumberTop : transformNumberBottom,
                swipeImageView, imageFile, new Callback() {
            @Override
            public void onSuccess() {
                setProgressVisibility(isTop, false);
            }

            @Override
            public void onError() {
                setProgressVisibility(isTop, false);
            }
        });
    }

    private void imageEffectRight(SwipeImageView swipeImageView, File imageFile, final boolean isTop) {
        if (isTop) {
            if (transformNumberTop <= 9) {
                transformNumberTop += 1;
            }
        } else {
            if (transformNumberBottom <= 11) {
                transformNumberBottom += 1;
            }
        }
        setProgressVisibility(isTop, true);
        Utils.transformImage(getApplicationContext(), isTop ? transformNumberTop : transformNumberBottom,
                swipeImageView, imageFile, new Callback() {
            @Override
            public void onSuccess() {
                setProgressVisibility(isTop, false);
            }

            @Override
            public void onError() {
                setProgressVisibility(isTop, false);
            }
        });
    }

    private void setProgressVisibility(boolean isTop, boolean isVisible) {
        if (isTop) {
            topPicProgress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        } else {
            topBottomPicProgess.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void saveBottomImage(Bitmap bitmap) {
        String pathToBottomImage = Utils.halfBitmap(this, false, bitmap);
        fileBottomImage = new File(pathToBottomImage);
        bitmap.recycle();

        Picasso.with(this).load(fileBottomImage).fit().centerCrop().noPlaceholder().into(topBottomPic);

        setTopLayoutImageVisibility(true);

        showActionButtonsVisibility(true);

        topPic.setIsSwipeEnabled(true);
        topBottomPic.setIsSwipeEnabled(true);

        //only show help the first time the user takes pic and then they can access from
        //action bar button
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (!(sharedPref.getBoolean(getString(R.string.has_help_shown_key), false))) {
            showImageEffectsHelp();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.has_help_shown_key), true);
            editor.apply();
        }
    }

    private void saveTopImage(Bitmap bitmap) {
        String pathToTopImage = Utils.halfBitmap(this, true, bitmap);
        bitmap.recycle();
        fileTopImage = new File(pathToTopImage);

        Picasso.with(this).load(fileTopImage).fit().centerCrop().noPlaceholder().into(topPic);

        switchCameraViews(false);
    }

    /**
     * used to show the image effect action help screen
     */
    private void showImageEffectsHelp() {
        final Display display = getWindowManager().getDefaultDisplay();
        final Drawable arrows = ContextCompat.getDrawable(this, R.drawable.ic_arrows);
        final Rect arrowsTargetTop = new Rect(0, 0, arrows.getIntrinsicWidth() * 2, arrows.getIntrinsicHeight() * 2);
        arrowsTargetTop.offset(display.getWidth() / 2, display.getHeight() / 4);
        final Rect arrowsTargetBottom = new Rect(0, 0, arrows.getIntrinsicWidth() * 2, arrows.getIntrinsicHeight() * 2);
        arrowsTargetBottom.offset(display.getWidth() / 2, ((display.getHeight() / 4)*3));
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.circle_menu), getString(R.string.action_button_help_title),
                                getString(R.string.action_button_help_text)).transparentTarget(true),
                        TapTarget.forBounds(arrowsTargetTop, getString(R.string.top_image_help_title),
                                getString(R.string.top_image_help_text)).icon(arrows),
                        TapTarget.forBounds(arrowsTargetBottom, getString(R.string.bottom_image_help_title),
                                getString(R.string.bottom_image_help_text)).icon(arrows)).start();
    }

    private void toggleFlash() {
        switch (cameraPreview.toggleFlash()) {
            case CameraKit.Constants.FLASH_ON:
                if (flashActionItem!=null) {
                    flashActionItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_flash_on));
                }
                break;
            case CameraKit.Constants.FLASH_OFF:
                if (flashActionItem!=null) {
                    flashActionItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_flash_off));
                }
                break;
            case CameraKit.Constants.FLASH_AUTO:
                if (flashActionItem!=null) {
                    flashActionItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_flash_auto));
                }
                break;
        }
    }

    private void toggleFlashAuto() {
        cameraPreview.setFlash(CameraKit.Constants.FLASH_AUTO);
        if (flashActionItem!=null) {
            flashActionItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_flash_auto));
        }
    }

    /**
     * check permissions
     */
    private void checkPermissions() {
        MultiplePermissionsListener dialogOnAnyDeniedMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("Flipside Camera permissions")
                        .withMessage("All requested permissions are required for this app to work.  Please re-open the app and try again.")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_launcher)
                        .build();

        MultiplePermissionsListener multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.areAllPermissionsGranted()) {
                    hasPermissiongranted = false;
                    finish();
                } else {
                    //cameraPreview.start();
                    hasPermissiongranted = true;
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        };

        MultiplePermissionsListener compositePermissionListener =
                new CompositeMultiplePermissionsListener(multiplePermissionsListener, dialogOnAnyDeniedMultiplePermissionsListener);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(compositePermissionListener)
                .check();
    }

    /**
     * change visibility of top image images in top layout
     * @param hasBothImagesVisible
     */
    private void setTopLayoutImageVisibility(boolean hasBothImagesVisible) {
        topEmptyPic.setVisibility(hasBothImagesVisible ? View.GONE : View.VISIBLE);
        topBottomPicImageLayout.setVisibility(hasBothImagesVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * set second image and show image acton buttons
     */
    private void showActionButtonsVisibility(boolean isActionButtonsVisible) {
        capturePhotoButton.setVisibility(isActionButtonsVisible ? View.GONE : View.VISIBLE);
        circleMenu.setVisibility(isActionButtonsVisible ? View.VISIBLE : View.GONE);
        helpActionItem.setVisible(isActionButtonsVisible);
    }

    /**
     * method to switch between the top and bottom camera views
     */
    public void switchCameraViews(boolean isTopCameraView) {
        this.isTopPictureTaken = !isTopCameraView;
        topPicLayout.setVisibility(isTopCameraView ? View.GONE : View.VISIBLE);
        bottomPicLayout.setVisibility(isTopCameraView ? View.VISIBLE : View.GONE);
        flashActionItem.setVisible(isTopCameraView);
        //set or remove images as required
        if (isTopCameraView) {
            //remove images as resetting view to top
            topPic.setImageBitmap(null);
            topBottomPic.setImageBitmap(null);
            //change visibility of views
            capturePhotoButton.setVisibility(View.VISIBLE);
            setTopLayoutImageVisibility(false);
            // switch flash back on to auto
            toggleFlashAuto();
        }

        //change to back camera
        cameraPreview.toggleFacing();
    }

    /**
     * circular fab action button methods
     */

    private void clearImages() {
        transformNumberTop = 0;
        transformNumberBottom = 0;
        topPic.setIsSwipeEnabled(false);
        topBottomPic.setIsSwipeEnabled(false);
        Utils.cleanUpTempDir(this);
        showActionButtonsVisibility(false);
        switchCameraViews(true);
    }

    private void swapImages() {
        if (isReverseImageOrder) {
            Utils.transformImage(getApplicationContext(), transformNumberBottom,
                    topPic, fileTopImage, null);
            Utils.transformImage(getApplicationContext(), transformNumberTop,
                    topBottomPic, fileBottomImage, null);

            isReverseImageOrder = false;
        } else {
            Utils.transformImage(getApplicationContext(), transformNumberBottom,
                    topBottomPic, fileTopImage, null);
            Utils.transformImage(getApplicationContext(), transformNumberTop,
                    topPic, fileBottomImage, null);

            isReverseImageOrder = true;
        }
    }

    private void shareImage() {
        topPic.buildDrawingCache();
        Bitmap topBitmap = topPic.getDrawingCache();
        topBottomPic.buildDrawingCache();
        Bitmap bottomBitmap = topBottomPic.getDrawingCache();
        String pathToCombinedImage = Utils.combineBitmaps(this, topBitmap, bottomBitmap);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(new File(pathToCombinedImage));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
