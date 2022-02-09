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
package com.weberbox.changelibs.library.parser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.weberbox.changelibs.library.Constants;
import com.weberbox.changelibs.library.Util;
import com.weberbox.changelibs.library.internal.ChangeLog;
import com.weberbox.changelibs.library.internal.ChangeLogAdapter;
import com.weberbox.changelibs.library.internal.ChangeLogException;
import com.weberbox.changelibs.library.internal.ChangeLogRow;
import com.weberbox.changelibs.library.internal.ChangeLogRowHeader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@SuppressWarnings("unused")
public class XmlParser extends BaseParser {

    private static final String TAG = "XmlParser";

    private int changeLogFileResourceId = Constants.logFileResourceId;
    private String changeLogFileResourceUrl = null;

    protected ChangeLogAdapter changeLogAdapter;

    private static final String TAG_CHANGELOG = "changelog";
    private static final String TAG_CHANGELOG_VERSION = "changelogversion";
    private static final String TAG_CHANGELOG_TEXT = "changelogtext";
    private static final String TAG_CHANGELOG_NOTE = "note";
    private static final String TAG_CHANGELOG_FIX = "fix";
    private static final String TAG_CHANGELOG_NEW = "new";
    private static final String TAG_CHANGELOG_IMP = "imp";

    private static final String ATTRIBUTE_BULLETED_LIST = "bulletedList";
    private static final String ATTRIBUTE_CURRENT_VERSION = "currentVersion";
    private static final String ATTRIBUTE_VERSION_NAME = "versionName";
    private static final String ATTRIBUTE_VERSION_CODE = "versionCode";
    private static final String ATTRIBUTE_LOG_TYPE = "logType";
    private static final String ATTRIBUTE_CHANGE_DATE = "changeDate";

    //--------------------------------------------------------------------------------
    //Constructors
    //--------------------------------------------------------------------------------

    /**
     * Create a new instance for a context.
     *
     * @param context current Context
     */
    public XmlParser(Context context) {
        super(context);
    }

    /**
     * Create a new instance for a context and for a custom changelogfile.
     * <p>
     * You have to use file in res/raw folder.
     *
     * @param context                 current Context
     * @param changeLogFileResourceId reference for a custom xml file
     */
    public XmlParser(Context context, int changeLogFileResourceId) {
        super(context);
        this.changeLogFileResourceId = changeLogFileResourceId;
    }

    /**
     * Create a new instance for a context and with a custom url .
     *
     * @param context                  current Context
     * @param changeLogFileResourceUrl url with xml files
     */
    public XmlParser(Context context, String changeLogFileResourceUrl) {
        super(context);
        this.changeLogFileResourceUrl = changeLogFileResourceUrl;
    }

    //--------------------------------------------------------------------------------

    /**
     * Read and parse res/raw/changelog.xml or custom file
     *
     * @return {@link ChangeLog} obj with all data
     * @throws Exception if changelog.xml or custom file is not found or if there are errors on parsing
     */
    @Override
    public ChangeLog readChangeLogFile() throws Exception {

        ChangeLog chg;

        try {
            InputStream is = null;

            if (changeLogFileResourceUrl != null) {
                if (Util.isConnected(super.context)) {
                    URL url = new URL(changeLogFileResourceUrl);
                    is = url.openStream();
                }
            } else {
                is = context.getResources().openRawResource(changeLogFileResourceId);
            }
            if (is != null) {

                // Create a new XML Pull Parser.
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);
                parser.nextTag();

                // Create changelog obj that will contain all data
                chg = new ChangeLog();
                // Parse file
                readChangeLogNode(parser, chg);

                // Close inputstream
                is.close();
            } else {
                Log.d(TAG, "Changelog.xml not found");
                throw new ChangeLogException("Changelog.xml not found");
            }
        } catch (XmlPullParserException xpe) {
            Log.d(TAG, "XmlPullParseException while parsing changelog file", xpe);
            throw xpe;
        } catch (IOException ioe) {
            Log.d(TAG, "Error i/o with changelog.xml", ioe);
            throw ioe;
        }

        return chg;
    }

    /**
     * Parse changelog node
     *
     * @param parser    parser
     * @param changeLog changeLog
     */
    protected void readChangeLogNode(XmlPullParser parser, ChangeLog changeLog) throws Exception {

        if (parser == null || changeLog == null) return;

        // Parse changelog node
        parser.require(XmlPullParser.START_TAG, null, TAG_CHANGELOG);

        // Read attributes
        String bulletedList = parser.getAttributeValue(null, ATTRIBUTE_BULLETED_LIST);
        if (bulletedList == null || bulletedList.equals("true")) {
            changeLog.setBulletedList(true);
            super.bulletedList = true;
        } else {
            changeLog.setBulletedList(false);
            super.bulletedList = false;
        }

        // Parse nested nodes
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tag = parser.getName();

            if (tag.equals(TAG_CHANGELOG_VERSION)) {
                readChangeLogVersionNode(parser, changeLog);
            }
        }
    }

    /**
     * Parse changeLogVersion node
     *
     * @param parser    parser
     * @param changeLog changeLog
     * @throws Exception exception
     */
    protected void readChangeLogVersionNode(XmlPullParser parser, ChangeLog changeLog)
            throws Exception {

        if (parser == null) return;

        parser.require(XmlPullParser.START_TAG, null, TAG_CHANGELOG_VERSION);

        // Read attributes
        String versionName = parser.getAttributeValue(null, ATTRIBUTE_VERSION_NAME);
        String versionCodeStr = parser.getAttributeValue(null, ATTRIBUTE_VERSION_CODE);
        int versionCode = 0;
        if (versionCodeStr != null) {
            try {
                versionCode = Integer.parseInt(versionCodeStr);
            } catch (NumberFormatException ne) {
                Log.w(TAG, "Error while parsing versionCode.It must be a numeric value. " +
                        "Check your file.");
            }
        }

        String changeDate = parser.getAttributeValue(null, ATTRIBUTE_CHANGE_DATE);
        if (versionName == null)
            throw new ChangeLogException("VersionName required in changeLogVersion node");

        ChangeLogRowHeader row = new ChangeLogRowHeader();
        row.setVersionName(versionName);
        //row.setVersionCode(versionCode);
        row.setChangeDate(changeDate);
        changeLog.addRow(row);

        String currentVersion = parser.getAttributeValue(null, ATTRIBUTE_CURRENT_VERSION);
        if (currentVersion != null) {
            row.setCurrentVersion(currentVersion.equals("true"));
        } else {
            row.setCurrentVersion(false);
        }

        // Parse nested nodes
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Log.d(TAG,"Processing tag="+tag);

            readChangeLogRowNode(parser, changeLog, versionName, versionCode);
        }
    }

    /**
     * Parse changeLogText node
     *
     * @param parser    parser
     * @param changeLog changeLog
     * @throws Exception exception
     */
    private void readChangeLogRowNode(XmlPullParser parser, ChangeLog changeLog, String versionName,
                                      int versionCode) throws Exception {
        if (parser == null) return;

        String tag = parser.getName();

        ChangeLogRow row = new ChangeLogRow();
        row.setVersionName(versionName);
        row.setVersionCode(versionCode);

        parser.require(XmlPullParser.START_TAG, null, TAG_CHANGELOG_TEXT);

        // It is possible to force bulleted List
        String bulletedList = parser.getAttributeValue(null, ATTRIBUTE_BULLETED_LIST);
        if (bulletedList != null) {
            row.setBulletedList(bulletedList.equals("true"));
        } else {
            row.setBulletedList(super.bulletedList);
        }

        String logType = parser.getAttributeValue(null, ATTRIBUTE_LOG_TYPE);
        if (logType != null) {
            int type;
            switch (logType) {
                case TAG_CHANGELOG_FIX:
                    type = ChangeLogRow.FIX;
                    break;
                case TAG_CHANGELOG_IMP:
                    type = ChangeLogRow.IMPROVEMENT;
                    break;
                case TAG_CHANGELOG_NEW:
                    type = ChangeLogRow.NEW;
                    break;
                case TAG_CHANGELOG_NOTE:
                    type = ChangeLogRow.NOTE;
                    break;
                default:
                    type = ChangeLogRow.DEFAULT;
            }
            row.setLogType(type);
        } else {
            row.setLogType(ChangeLogRow.DEFAULT);
        }

        // Read text
        if (parser.next() == XmlPullParser.TEXT) {
            String changeLogText = parser.getText();
            if (changeLogText == null)
                throw new ChangeLogException("ChangeLogText required in changeLogText node");
            row.parseChangeText(changeLogText);
            parser.nextTag();
        }

        changeLog.addRow(row);
    }

    public void setChangeLogAdapter(ChangeLogAdapter changeLogAdapter) {
        this.changeLogAdapter = changeLogAdapter;
    }
}
