/*
 * ******************************************************************************
 *   Copyright (c) 2013-2015 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */
package com.weberbox.changelibs.library.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.weberbox.changelibs.R;
import com.weberbox.changelibs.library.internal.ChangeLogRecyclerViewAdapter;
import com.weberbox.changelibs.library.parser.XmlParser;

import com.weberbox.changelibs.library.Constants;
import com.weberbox.changelibs.library.Util;
import com.weberbox.changelibs.library.internal.ChangeLog;

/**
 * RecyclerView for ChangeLog
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class ChangeLogRecyclerView extends RecyclerView {

    //--------------------------------------------------------------------------
    // Custom Attrs
    //--------------------------------------------------------------------------
    protected int rowLayoutId = Constants.rowLayoutId;
    protected int rowHeaderLayoutId = Constants.rowHeaderLayoutId;
    protected int changeLogFileResourceId = Constants.changeLogFileResourceId;
    protected String changeLogFileResourceUrl = null;

    //--------------------------------------------------------------------------
    protected static String TAG = "ChangeLogRecyclerView";

    // Adapter
    protected ChangeLogRecyclerViewAdapter adapter;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public ChangeLogRecyclerView(Context context) {
        this(context, null);
    }

    public ChangeLogRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeLogRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void init(AttributeSet attrs, int defStyle) {

        //Init attrs
        initAttrs(attrs, defStyle);

        //Init adapter
        initAdapter();

        //Init the LayoutManager
        initLayoutManager();

    }

    /**
     * Init the Layout Manager
     */
    protected void initLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.setLayoutManager(layoutManager);
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
            rowLayoutId = a.getResourceId(R.styleable.ChangeLogListView_rowLayoutId, rowLayoutId);
            rowHeaderLayoutId = a.getResourceId(R.styleable.ChangeLogListView_rowHeaderLayoutId,
                    rowHeaderLayoutId);

            //Changelog.xml file
            changeLogFileResourceId = a.getResourceId(
                    R.styleable.ChangeLogListView_changeLogFileResourceId, changeLogFileResourceId);

            changeLogFileResourceUrl = a.getString(
                    R.styleable.ChangeLogListView_changeLogFileResourceUrl);
            //String which is used in header row for Version
            //mStringVersionHeader= a.getResourceId(
            // R.styleable.ChangeLogListView_StringVersionHeader,mStringVersionHeader);

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
            if (changeLogFileResourceUrl != null)
                parse = new XmlParser(getContext(), changeLogFileResourceUrl);
            else
                parse = new XmlParser(getContext(), changeLogFileResourceId);
            //ChangeLog chg=parse.readChangeLogFile();
            ChangeLog chg = new ChangeLog();

            //Create adapter and set custom attrs
            adapter = new ChangeLogRecyclerViewAdapter(getContext(), chg.getRows());
            adapter.setRowLayoutId(rowLayoutId);
            adapter.setRowHeaderLayoutId(rowHeaderLayoutId);

            //Parse in a separate Thread to avoid UI block with large files
            if (changeLogFileResourceUrl == null || Util.isConnected(getContext()))
                new ParseAsyncTask(adapter, parse).execute();
            else
                Toast.makeText(getContext(), R.string.changelog_internal_error_internet_connection,
                        Toast.LENGTH_LONG).show();
            setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, getResources().getString(R.string.changelog_internal_error_parsing), e);
        }

    }


    /**
     * Async Task to parse xml file in a separate thread
     */
    @SuppressLint("StaticFieldLeak")
    protected class ParseAsyncTask extends AsyncTask<Void, Void, ChangeLog> {

        private final ChangeLogRecyclerViewAdapter adapter;
        private final XmlParser parse;

        @SuppressWarnings("deprecation")
        public ParseAsyncTask(ChangeLogRecyclerViewAdapter adapter, XmlParser parse) {
            this.adapter = adapter;
            this.parse = parse;
        }

        @Override
        protected ChangeLog doInBackground(Void... params) {

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
                adapter.add(chg.getRows());
            }
        }
    }


}
