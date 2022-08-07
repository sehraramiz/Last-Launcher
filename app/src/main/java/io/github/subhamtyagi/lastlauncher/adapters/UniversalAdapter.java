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

package io.github.subhamtyagi.lastlauncher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.model.Apps;

//
// this adapter class is used for Frozen app list and Hidden app list
//
public class UniversalAdapter extends ArrayAdapter<Apps> {

    private final Context context;
    private final ArrayList<Apps> list;
    private OnClickListener listener;
    private OnLongClickListener longListener;

    public UniversalAdapter(Context context, ArrayList<Apps> list) {
        super(context, R.layout.list_item, list);
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener longListener) {
        this.longListener = longListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item, parent, false);
        row.setTag(position);
        TextView textView = row.findViewById(R.id.app_label);
        textView.setText(list.get(position).getAppName());
        textView.setTag(position);

        row.setOnClickListener(view -> listener.onClick(getItem((int) view.getTag()), view));
        row.setOnLongClickListener(view -> longListener.onLongClick(getItem((int) view.getTag()), view));

        textView.setOnClickListener(view -> listener.onClick(getItem((int) view.getTag()), view));
        textView.setOnLongClickListener(view -> longListener.onLongClick(getItem((int) view.getTag()), view));
        return row;
    }


    public interface OnClickListener {
        void onClick(Apps apps, View view);
    }

    public interface OnLongClickListener {
        boolean onLongClick(Apps apps, View view);
    }

}
