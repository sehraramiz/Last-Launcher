/*
 * Last Launcher
 * Copyright (C) 2019 Shubham Tyagi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.subhamtyagi.lastlauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;
import io.github.subhamtyagi.lastlauncher.views.colorseekbar.ColorSeekBar;

// choose color Dialog
public class ColorSizePositionDialog extends Dialog {

    private static final int DELAY = 25;
    //private static final String TAG = "ChooseSize";
    private final static int DEFAULT_MIN_TEXT_SIZE = DbUtils.getMinAppSize();
    private final static int DEFAULT_MAX_TEXT_SIZE = DbUtils.getMaxAppSize();

    private final Handler handler = new Handler();
    private final String appPackage;
    private final TextView textView;
    private final LauncherActivity launcherActivity;

    private int appSize;
    private int appPosition;
    private Runnable runnable;
    private int appColor;
    private boolean colorChange = false;

    public ColorSizePositionDialog(Context context, String appPackage, int appColor, TextView textView, int appSize, int appPosition, LauncherActivity launcherActivity) {
        super(context);
        this.appPackage = appPackage;
        this.appColor = appColor;
        this.textView = textView;
        this.appSize = appSize;
        this.appPosition = appPosition;
        this.launcherActivity = launcherActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // no title please
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_color_size_position);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        ColorSeekBar colorSeekBar = findViewById(R.id.colorSlider1);
        colorSeekBar.setMaxPosition(100);
        //colorSeekBar.setColorSeeds(R.array.material_colors);
        colorSeekBar.setShowAlphaBar(true);
        colorSeekBar.setBarHeight(8);


        // todo: is this still correct?
        if (appColor != DbUtils.NULL_TEXT_COLOR)
            colorSeekBar.setColor(appColor);
        // set the color and save this to database
        colorSeekBar.setOnColorChangeListener((colorBarPosition, alphaBarPosition, color) -> {
            textView.setTextColor(color);
            appColor = color;
            colorChange = true;
            // DbUtils.putAppColorImmediately(appPackage, color);
        });


        // size related
        TextView plus = findViewById(R.id.btn_plus);
        TextView minus = findViewById(R.id.btn_minus);
        TextView size = findViewById(R.id.tv_size);

        size.setText(String.valueOf(appSize));

        // position related
        TextView posPlus = findViewById(R.id.btn_position_plus);
        TextView posMinus = findViewById(R.id.btn_position_minus);
        TextView position = findViewById(R.id.tv_position);

        size.setText(String.valueOf(appSize));
        position.setText(String.valueOf(appPosition));

        // app size buttons listeners
        plus.setOnClickListener(view -> {

            appSize++;

            if (appSize >= DEFAULT_MAX_TEXT_SIZE) {
                appSize = DEFAULT_MAX_TEXT_SIZE;
                //   plus.setClickable(false);
            }

            size.setText(String.valueOf(appSize));
            textView.setTextSize(appSize);
            launcherActivity.onAppSizeChange(appPackage, appSize);
        });

        minus.setOnClickListener(view -> {
            --appSize;
            if (appSize < DEFAULT_MIN_TEXT_SIZE) {
                appSize = DEFAULT_MIN_TEXT_SIZE;
            }

            size.setText(String.valueOf(appSize));
            textView.setTextSize(appSize);
            launcherActivity.onAppSizeChange(appPackage, appSize);
        });


        plus.setOnLongClickListener(view -> {
            runnable = () -> {
                if (!plus.isPressed()) {
                    handler.removeCallbacks(runnable);
                    return;
                }
                // increase value
                appSize++;

                if (appSize >= DEFAULT_MAX_TEXT_SIZE) {
                    appSize = DEFAULT_MAX_TEXT_SIZE;
                    //   plus.setClickable(false);
                }

                size.setText(String.valueOf(appSize));
                textView.setTextSize(appSize);
                handler.postDelayed(runnable, DELAY);
                launcherActivity.onAppSizeChange(appPackage, appSize);
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DELAY);
            return true;
        });


        minus.setOnLongClickListener(view -> {
            runnable = () -> {
                if (!minus.isPressed()) {
                    handler.removeCallbacks(runnable);
                    return;
                }
                // decrease value
                --appSize;
                if (appSize < DEFAULT_MIN_TEXT_SIZE) {
                    appSize = DEFAULT_MIN_TEXT_SIZE;
                }

                size.setText(String.valueOf(appSize));
                textView.setTextSize(appSize);
                handler.postDelayed(runnable, DELAY);
                launcherActivity.onAppSizeChange(appPackage, appSize);
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DELAY);
            return true;
        });

        // app position button listeners
        posPlus.setOnClickListener(view -> {

            appPosition++;
            position.setText(String.valueOf(appPosition));
            launcherActivity.onAppPositionChange(appPackage, appPosition);
        });

        posMinus.setOnClickListener(view -> {
            --appPosition;
            position.setText(String.valueOf(appPosition));
            launcherActivity.onAppPositionChange(appPackage, appPosition);
        });

        posPlus.setOnLongClickListener(view -> {
            runnable = () -> {
                if (!posPlus.isPressed()) {
                    handler.removeCallbacks(runnable);
                    return;
                }
                // increase value
                appPosition++;
                position.setText(String.valueOf(appPosition));
                handler.postDelayed(runnable, DELAY);
                launcherActivity.onAppPositionChange(appPackage, appPosition);
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DELAY);
            return true;
        });


        posMinus.setOnLongClickListener(view -> {
            runnable = () -> {
                if (!posMinus.isPressed()) {
                    handler.removeCallbacks(runnable);
                    return;
                }
                // decrease value
                --appPosition;
                position.setText(String.valueOf(appPosition));
                handler.postDelayed(runnable, DELAY);
                launcherActivity.onAppPositionChange(appPackage, appPosition);
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DELAY);
            return true;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        DbUtils.putAppColor(appPackage, appColor);
        DbUtils.putAppSize(appPackage, appSize);
        DbUtils.putAppPosition(appPackage, appPosition);
        if (colorChange)
            launcherActivity.onAppColorChange(appPackage, appColor);

    }
}
