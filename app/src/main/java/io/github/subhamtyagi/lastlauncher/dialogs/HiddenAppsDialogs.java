/*
 * Last Launcher
 * Copyright (C) 2019,2020 Shubham Tyagi
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.adapters.UniversalAdapter;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;

public class HiddenAppsDialogs extends Dialog {

    private final List<Apps> mAppsList;
    private final Context context;
    ArrayList<Apps> hiddenApps = new ArrayList<>();

    public HiddenAppsDialogs(Context context, List<Apps> appsList) {
        super(context);
        this.context = context;
        mAppsList = appsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hidden_apps);
        ListView listView = findViewById(R.id.hidden_app_list);

        listView.setLongClickable(true);
        UniversalAdapter adapter = new UniversalAdapter(context, hiddenApps);
        listView.setAdapter(adapter);
        adapter.setOnLongClickListener(this::confirmationAndRemove);
        adapter.setOnClickListener(this::openApp);

    }

    private void openApp(Apps apps, View view) {

        if (!apps.isShortcut()) {
            String[] strings = apps.getActivityName().split("/");
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.setClassName(strings[0], strings[1]);
            intent.setComponent(new ComponentName(strings[0], strings[1]));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void showAppInfo(Apps apps, View view) {
        if (!apps.isShortcut()) {
            final Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + apps.getActivityName().split("/")[0]));
            context.startActivity(intent);
        }
    }

    private boolean confirmationAndRemove(Apps apps, View view) {

        Context ctx;
        if (DbUtils.getTheme() == R.style.Wallpaper)
            ctx = new ContextThemeWrapper(context, R.style.AppTheme);
        else
            ctx = new ContextThemeWrapper(context, DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.getMenuInflater().inflate(R.menu.hidden_app_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            if (menuItem.getItemId() == R.id.menu_unhide_this) {
                apps.setAppHidden(false);
                updateHiddenList();
            } else if (menuItem.getItemId() == R.id.menu_run_this_app) {
                openApp(apps, view);
            } else if (menuItem.getItemId() == R.id.menu_app_info) {
                showAppInfo(apps, view);
            }
            return true;

        });
        popupMenu.show();
        return true;

    }

    public int updateHiddenList() {
        synchronized (mAppsList) {
            for (Apps apps : mAppsList) {
                if (apps.isHidden()) {
                    hiddenApps.add(apps);
                }
            }
        }
        Collections.sort(hiddenApps, (o1, o2) -> o1.getAppName().compareToIgnoreCase(o2.getAppName()));
        return hiddenApps.size();
    }

}
