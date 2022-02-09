/*
 * Copyright (c) 2013-2015 Gabriele Mariotti.
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
 *
 */
package com.weberbox.changelibs.library.internal;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.changelibs.R;
import com.weberbox.changelibs.library.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 * @author James Weber
 */
public class ChangeLogRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROW = 0;
    private static final int TYPE_HEADER = 1;

    private final Context context;
    private int rowLayoutId = Constants.rowLayoutId;
    private int rowHeaderLayoutId = Constants.rowHeaderLayoutId;
    private int colorCurrentVersion = Constants.currentVersionColor;
    private final int stringVersionHeader = Constants.stringVersionHeader;

    private final List<ChangeLogRow> items;

    // -------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------

    public ChangeLogRecyclerViewAdapter(Context context, List<ChangeLogRow> items) {
        this.context = context;
        if (items == null)
            items = new ArrayList<>();
        this.items = items;
    }

    public void add(LinkedList<ChangeLogRow> rows) {
        int originalPosition = items.size();
        items.addAll(rows);
        notifyItemRangeInserted(originalPosition, originalPosition + rows.size());
    }


    // -------------------------------------------------------------
    // ViewHolder
    // -------------------------------------------------------------

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView versionHeader;
        public TextView dateHeader;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            // VersionName text
            versionHeader = itemView.findViewById(R.id.chg_headerVersion);
            // ChangeData text
            dateHeader = itemView.findViewById(R.id.chg_headerDate);
        }
    }

    public static class ViewHolderRow extends RecyclerView.ViewHolder {
        public TextView textRow;
        public TextView bulletRow;
        public ImageView tagRow;

        public ViewHolderRow(View itemView) {
            super(itemView);
            textRow = itemView.findViewById(R.id.chg_text);
            bulletRow = itemView.findViewById(R.id.chg_textbullet);
            tagRow = itemView.findViewById(R.id.chg_tag);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            final View viewHeader = LayoutInflater.from(parent.getContext()).inflate(
                    rowHeaderLayoutId, parent, false);
            return new ViewHolderHeader(viewHeader);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(rowLayoutId, parent, false);
            return new ViewHolderRow(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (isHeader(position)) {
            populateViewHolderHeader((ViewHolderHeader) viewHolder, position);
        } else {
            populateViewHolderRow((ViewHolderRow) viewHolder, position);
        }
    }

    private void populateViewHolderRow(ViewHolderRow viewHolder, int position) {
        ChangeLogRow item = getItem(position);
        if (item != null) {
            if (viewHolder.textRow != null) {
                viewHolder.textRow.setText(Html.fromHtml(item.getChangeText()));
                viewHolder.textRow.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (viewHolder.bulletRow != null) {
                if (item.isBulletedList() && item.getLogType() == ChangeLogRow.DEFAULT) {
                    viewHolder.bulletRow.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.bulletRow.setVisibility(View.GONE);
                }
            }
            if (viewHolder.tagRow != null) {
                switch (item.getLogType()) {
                    case ChangeLogRow.IMPROVEMENT:
                        viewHolder.tagRow.setImageDrawable(
                                ContextCompat.getDrawable(context, R.drawable.ic_imp_tag));
                        viewHolder.tagRow.setVisibility(View.VISIBLE);
                        break;
                    case ChangeLogRow.FIX:
                        viewHolder.tagRow.setImageDrawable(
                                ContextCompat.getDrawable(context, R.drawable.ic_fix_tag));
                        viewHolder.tagRow.setVisibility(View.VISIBLE);
                        break;
                    case ChangeLogRow.NOTE:
                        viewHolder.tagRow.setImageDrawable(
                                ContextCompat.getDrawable(context, R.drawable.ic_note_tag));
                        viewHolder.tagRow.setVisibility(View.VISIBLE);
                        break;
                    case ChangeLogRow.NEW:
                        viewHolder.tagRow.setImageDrawable(
                                ContextCompat.getDrawable(context, R.drawable.ic_new_tag));
                        viewHolder.tagRow.setVisibility(View.VISIBLE);
                        break;
                    case ChangeLogRow.DEFAULT:
                        viewHolder.tagRow.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    private void populateViewHolderHeader(ViewHolderHeader viewHolder, int position) {
        ChangeLogRow item = getItem(position);
        if (item != null) {
            if (viewHolder.versionHeader != null) {
                StringBuilder sb = new StringBuilder();
                // String resource for Version
                String versionHeaderString = context.getString(stringVersionHeader);
                if (versionHeaderString != null)
                    sb.append(versionHeaderString);
                // VersionName text
                sb.append(item.versionName);

                viewHolder.versionHeader.setText(sb.toString());

                if (item.isCurrentVersion()) {
                    viewHolder.versionHeader.setTextColor(ContextCompat.getColor(context,
                            colorCurrentVersion));
                } else {
                    viewHolder.versionHeader.setTextColor(ContextCompat.getColor(context,
                            Constants.previousVersionColor));
                }
            }

            // ChangeData text
            if (viewHolder.dateHeader != null) {
                if (item.changeDate != null) {
                    viewHolder.dateHeader.setText(item.changeDate);
                    viewHolder.dateHeader.setVisibility(View.VISIBLE);
                } else {
                    // If item does not have change date, hide TextView
                    viewHolder.dateHeader.setText("");
                    viewHolder.dateHeader.setVisibility(View.GONE);
                }
            }
        }
    }


    private boolean isHeader(int position) {
        return getItem(position).isHeader();
    }

    private ChangeLogRow getItem(int position) {
        return items.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (isHeader(position))
            return TYPE_HEADER;
        return TYPE_ROW;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    //-----------------------------------------------------------------------------------
    // Getter and Setter
    //-----------------------------------------------------------------------------------

    public void setRowLayoutId(int rowLayoutId) {
        this.rowLayoutId = rowLayoutId;
    }

    public void setRowHeaderLayoutId(int rowHeaderLayoutId) {
        this.rowHeaderLayoutId = rowHeaderLayoutId;
    }

    public void setCurrentVersionColor(int colorCurrentVersion) {
        this.colorCurrentVersion = colorCurrentVersion;
    }
}
