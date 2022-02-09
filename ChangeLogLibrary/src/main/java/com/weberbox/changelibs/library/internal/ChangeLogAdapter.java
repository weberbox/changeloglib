/*
 * Copyright (c) 2013 Gabriele Mariotti.
 * Copyright (c) 2021 James Weber.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.weberbox.changelibs.library.internal;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.weberbox.changelibs.R;
import com.weberbox.changelibs.library.Constants;

import java.util.List;

/**
 * Adapter for ChangeLog model
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 * @author James Weber
 */
@SuppressWarnings("unused")
public class ChangeLogAdapter extends ArrayAdapter<ChangeLogRow> {

    protected static final int TYPE_ROW = 0;
    protected static final int TYPE_HEADER = 1;

    private int rowLayoutId = Constants.rowLayoutId;
    private int rowHeaderLayoutId = Constants.rowHeaderLayoutId;
    private int colorCurrentVersion = Constants.currentVersionColor;
    private int stringVersionHeader = Constants.stringVersionHeader;

    private final Context context;

    public ChangeLogAdapter(Context context,
                            List<ChangeLogRow> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChangeLogRow item = getItem(position);
        View view = convertView;
        int viewType = this.getItemViewType(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (viewType) {
            case TYPE_HEADER:
                ViewHolderHeader viewHolderHeader = null;

                if (view != null) {
                    Object obj = view.getTag();
                    if (obj instanceof ViewHolderHeader) {
                        viewHolderHeader = (ViewHolderHeader) obj;
                    } else {
                        viewHolderHeader = null;
                    }
                }

                if (view == null || viewHolderHeader == null) {
                    int layout = rowHeaderLayoutId;
                    view = mInflater.inflate(layout, parent, false);

                    // VersionName text
                    TextView textHeader = view.findViewById(R.id.chg_headerVersion);
                    // ChangeData text
                    TextView textDate = view.findViewById(R.id.chg_headerDate);
                    viewHolderHeader = new ViewHolderHeader(textHeader, textDate);

                    view.setTag(viewHolderHeader);
                }

                if (item != null) {
                    if (viewHolderHeader.version != null) {
                        StringBuilder sb = new StringBuilder();
                        // String resource for Version
                        String versionHeaderString = getContext().getString(stringVersionHeader);
                        if (versionHeaderString != null)
                            sb.append(versionHeaderString);
                        // VersionName text
                        sb.append(item.versionName);

                        viewHolderHeader.version.setText(sb.toString());
                    }

                    // Change Date text
                    if (viewHolderHeader.date != null) {
                        if (item.changeDate != null) {
                            viewHolderHeader.date.setText(item.changeDate);
                            viewHolderHeader.date.setVisibility(View.VISIBLE);
                        } else {
                            // If item does not have change date, hide TextView
                            viewHolderHeader.date.setText("");
                            viewHolderHeader.date.setVisibility(View.GONE);
                        }
                    }

                    if (item.isCurrentVersion()) {
                        viewHolderHeader.version.setTextColor(ContextCompat.getColor(context,
                                colorCurrentVersion));
                    } else {
                        viewHolderHeader.version.setTextColor(ContextCompat.getColor(context,
                                Constants.previousVersionColor));
                    }
                }
                break;

            case TYPE_ROW:
                ViewHolderRow viewHolder = null;

                if (view != null) {
                    Object obj = view.getTag();
                    if (obj instanceof ViewHolderRow) {
                        viewHolder = (ViewHolderRow) obj;
                    } else {
                        viewHolder = null;
                    }
                }

                if (view == null || viewHolder == null) {
                    int layout = rowLayoutId;
                    view = mInflater.inflate(layout, parent, false);

                    TextView textText = view.findViewById(R.id.chg_text);
                    TextView bulletText = view.findViewById(R.id.chg_textbullet);
                    ImageView imageTag = view.findViewById(R.id.chg_tag);
                    viewHolder = new ViewHolderRow(textText, bulletText, imageTag);
                    view.setTag(viewHolder);

                }


                if (item != null) {
                    if (viewHolder.text != null) {
                        viewHolder.text.setText(Html.fromHtml(item.getChangeText()));
                        viewHolder.text.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    if (viewHolder.bulletText != null) {
                        if (item.isBulletedList() && item.getLogType() == ChangeLogRow.DEFAULT) {
                            viewHolder.bulletText.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.bulletText.setVisibility(View.GONE);
                        }
                    }
                    if (viewHolder.imageTag != null) {
                        switch (item.getLogType()) {
                            case ChangeLogRow.IMPROVEMENT:
                                viewHolder.imageTag.setImageDrawable(
                                        ContextCompat.getDrawable(context, R.drawable.ic_imp_tag));
                                viewHolder.imageTag.setVisibility(View.VISIBLE);
                                break;
                            case ChangeLogRow.FIX:
                                viewHolder.imageTag.setImageDrawable(
                                        ContextCompat.getDrawable(context, R.drawable.ic_fix_tag));
                                viewHolder.imageTag.setVisibility(View.VISIBLE);
                                break;
                            case ChangeLogRow.NOTE:
                                viewHolder.imageTag.setImageDrawable(
                                        ContextCompat.getDrawable(context, R.drawable.ic_note_tag));
                                viewHolder.imageTag.setVisibility(View.VISIBLE);
                                break;
                            case ChangeLogRow.NEW:
                                viewHolder.imageTag.setImageDrawable(
                                        ContextCompat.getDrawable(context, R.drawable.ic_new_tag));
                                viewHolder.imageTag.setVisibility(View.VISIBLE);
                                break;
                            case ChangeLogRow.DEFAULT:
                                viewHolder.imageTag.setVisibility(View.GONE);
                                break;
                        }
                    }
                }

                break;
            default:
                // Throw exception, unknown data type
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isHeader()) {
            return TYPE_HEADER;
        }
        return TYPE_ROW;
    }

    //-----------------------------------------------------------------------------------
    // View Holder
    //-----------------------------------------------------------------------------------

    static class ViewHolderHeader {
        TextView version;
        TextView date;

        public ViewHolderHeader(TextView version, TextView date) {
            this.version = version;
            this.date = date;
        }
    }

    static class ViewHolderRow {
        TextView text;
        TextView bulletText;
        ImageView imageTag;

        public ViewHolderRow(TextView text, TextView bulletText, ImageView imageTag) {
            this.text = text;
            this.bulletText = bulletText;
            this.imageTag = imageTag;
        }
    }

    //-----------------------------------------------------------------------------------
    // Getter and Setter
    //-----------------------------------------------------------------------------------


    public int getRowLayoutId() {
        return rowLayoutId;
    }

    public void setRowLayoutId(int rowLayoutId) {
        this.rowLayoutId = rowLayoutId;
    }

    public int getRowHeaderLayoutId() {
        return rowHeaderLayoutId;
    }

    public void setRowHeaderLayoutId(int rowHeaderLayoutId) {
        this.rowHeaderLayoutId = rowHeaderLayoutId;
    }

    public int getStringVersionHeader() {
        return stringVersionHeader;
    }

    public void setStringVersionHeader(int stringVersionHeader) {
        this.stringVersionHeader = stringVersionHeader;
    }

    public void setCurrentVersionColor(int colorCurrentVersion) {
        this.colorCurrentVersion = colorCurrentVersion;
    }
}
