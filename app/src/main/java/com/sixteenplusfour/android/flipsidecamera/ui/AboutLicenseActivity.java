package com.sixteenplusfour.android.flipsidecamera.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.sixteenplusfour.android.flipsidecamera.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by andy.barber on 15/03/2017.
 */

public class AboutLicenseActivity extends MaterialAboutActivity {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AboutLicenseActivity.class);
        return intent;
    }

    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {
        return createMaterialAboutLicenseList(this, R.color.primary);
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public MaterialAboutList createMaterialAboutLicenseList(final Context c, int colorIcon) {

        MaterialAboutCard materialAboutLIbraryLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "material-about-library", "2016", "Daniel Stone",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard androidIconicsLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "Android Iconics", "2016", "Mike Penz",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard butterknifeLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "Butterknife", "2013", "Jake Wharton",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard picassoLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "Picasso", "2013", "Square",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard picassoTransformLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "Picasso Transformations", "2015", "Wasabeef",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard cameraKitLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "CameraKit", "2017", "Flurgle",
                OpenSourceLicense.MIT);

        MaterialAboutCard circleMenuCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "CircleMenu", "2016", "ImangazalievM",
                OpenSourceLicense.MIT);

        MaterialAboutCard tapTargetViewCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "TapTargetView", "2016", "KeepSafe",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard dexterViewCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "Dexter", "2016", "Karumi",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard calligraphyViewCard = ConvenienceBuilder.createLicenseCard(c,
                new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(c, colorIcon))
                        .sizeDp(18),
                "Calligraphy", "2013", "Chris Jenkins",
                OpenSourceLicense.APACHE_2);

        return new MaterialAboutList(materialAboutLIbraryLicenseCard,
                androidIconicsLicenseCard, butterknifeLicenseCard, picassoLicenseCard,
                picassoTransformLicenseCard, cameraKitLicenseCard, circleMenuCard,
                tapTargetViewCard, dexterViewCard, calligraphyViewCard);
    }
}
