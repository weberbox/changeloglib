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
package com.weberbox.changelibs.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.weberbox.changelibs.R;
import com.weberbox.changelibs.library.async.AsyncTask;
import com.weberbox.changelibs.library.internal.ChangeLogAdapter;
import com.weberbox.changelibs.library.parser.XmlParser;

import com.weberbox.changelibs.library.Constants;
import com.weberbox.changelibs.library.Util;
import com.weberbox.changelibs.library.internal.ChangeLog;

/**
 * ListView for ChangeLog
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 * @author James Weber
 */
public class ChangeLogListView extends ListView implements AdapterView.OnItemClickListener {

    //--------------------------------------------------------------------------
    // Custom Attrs
    //--------------------------------------------------------------------------
    protected int rowLayoutId = Constants.rowLayoutId;
    protected int rowHeaderLayoutId = Constants.rowHeaderLayoutId;
    protected int changeLogFileResourceId = Constants.logFileResourceId;
    protected int colorCurrentVersion = Constants.currentVersionColor;
    protected String changeLogFileResourceUrl = null;

    //--------------------------------------------------------------------------
    protected static String TAG = "ChangeLogListView";
    // Adapter
    protected ChangeLogAdapter adapter;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public ChangeLogListView(Context context) {
        this(context, null);
    }

    public ChangeLogListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeLogListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setNestedScrollingEnabled(true);
        }
        init(attrs, defStyle);
    }

    //--------------------------------------------------------------------------
    // Init
    //--------------------------------------------------------------------------

    /**
     * Initialize
     *
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    protected void init(AttributeSet attrs, int defStyle) {
        //Init attrs
        initAttrs(attrs, defStyle);
        //Init adapter
        initAdapter();

        //Set divider to 0dp
        setDividerHeight(0);
    }

    /**
     * Init custom attrs.
     *
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    protected void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ChangeLogListView, defStyle, defStyle);

        try {
            //Layout for rows and header
            rowLayoutId = a.getResourceId(R.styleable.ChangeLogListView_chglib_row_layout, rowLayoutId);
            rowHeaderLayoutId = a.getResourceId(R.styleable.ChangeLogListView_chglib_row_header_layout,
                    rowHeaderLayoutId);

            //Changelog.xml file
            changeLogFileResourceId = a.getResourceId(R
                    .styleable.ChangeLogListView_chglib_log_file_resource, changeLogFileResourceId);

            changeLogFileResourceUrl = a.getString(
                    R.styleable.ChangeLogListView_chglib_log_file_resource_url);

            colorCurrentVersion = a.getResourceId(
                    R.styleable.ChangeLogListView_chglib_current_version_color,
                    colorCurrentVersion);

        } finally {
            a.recycle();
        }
    }

    /**
     * Init adapter
     */
    protected void initAdapter() {

        try {
            //Read and parse changelog.xml
            XmlParser parse;
            if (changeLogFileResourceUrl != null) {
                parse = new XmlParser(getContext(), changeLogFileResourceUrl);
            } else {
                parse = new XmlParser(getContext(), changeLogFileResourceId);
            }
            //ChangeLog chg=parse.readChangeLogFile();
            ChangeLog chg = new ChangeLog();

            //Create adapter and set custom attrs
            adapter = new ChangeLogAdapter(getContext(), chg.getRows());
            adapter.setRowLayoutId(rowLayoutId);
            adapter.setRowHeaderLayoutId(rowHeaderLayoutId);
            adapter.setCurrentVersionColor(colorCurrentVersion);

            //Parse in a separate Thread to avoid UI block with large files
            if (changeLogFileResourceUrl == null || Util.isConnected(getContext())) {
                new ParseAsyncTask(adapter, parse).execute();
            } else {
                Toast.makeText(getContext(), R.string.changelog_internal_error_internet_connection,
                        Toast.LENGTH_LONG).show();
            }

            setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, getResources().getString(R.string.changelog_internal_error_parsing), e);
        }

    }

    /**
     * Async Task to parse xml file in a separate thread
     */
    protected class ParseAsyncTask extends AsyncTask<Void, Void, ChangeLog> {

        private final ChangeLogAdapter adapter;
        private final XmlParser parse;

        public ParseAsyncTask(ChangeLogAdapter adapter, XmlParser parse) {
            this.adapter = adapter;
            this.parse = parse;
        }

        @Override
        protected ChangeLog doInBackground(Void params) {

            try {
                if (parse != null) {
                    return parse.readChangeLogFile();
                }
            } catch (Exception e) {
                Log.e(TAG, getResources().getString(R.string.changelog_internal_error_parsing), e);
            }
            return null;
        }

        protected void onPostExecute(ChangeLog chg) {

            //Notify data changed
            if (chg != null) {
                adapter.addAll(chg.getRows());
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onBackgroundError(Exception e) {
            Log.e(TAG, getResources().getString(R.string.changelog_internal_error_parsing), e);
        }
    }

    /**
     * Sets the list's adapter, enforces the use of only a ChangeLogAdapter
     */
    public void setAdapter(ChangeLogAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO
    }
}
