package com.sixteenplusfour.android.flipsidecamera;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by andy.barber on 21/03/2017.
 */

public class FlipsideCameraApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //font from here -> http://ndiscovered.com/exo-2/

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Exo2-Medium.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
