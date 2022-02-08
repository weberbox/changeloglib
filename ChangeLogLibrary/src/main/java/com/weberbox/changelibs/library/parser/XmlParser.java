/*
 * Copyright (c) 2013 Gabriele Mariotti.
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

import com.weberbox.changelibs.library.internal.ChangeLogAdapter;
import com.weberbox.changelibs.library.internal.ChangeLogException;
import com.weberbox.changelibs.library.internal.ChangeLogRow;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.weberbox.changelibs.library.Constants;
import com.weberbox.changelibs.library.Util;
import com.weberbox.changelibs.library.internal.ChangeLog;
import com.weberbox.changelibs.library.internal.ChangeLogRowHeader;

/**
 * Read and parse res/raw/changelog.xml.
 * Example:
 *
 * <pre>
 *    XmlParser parse = new XmlParser(this);
 *    ChangeLog log=parse.readChangeLogFile();
 * </pre>
 * <p>
 * If you want to use a custom xml file, you can use:
 * <pre>
 *    XmlParser parse = new XmlParser(this,R.raw.mycustomfile);
 *    ChangeLog log=parse.readChangeLogFile();
 * </pre>
 * <p>
 * It is a example for changelog.xml
 * <pre>
 *  <?xml version="1.0" encoding="utf-8"?>
 *       <changelog bulletedList=false>
 *            <changelogversion versionName="1.2" changeDate="20/01/2013">
 *                 <changelogtext>new feature to share data</changelogtext>
 *                 <changelogtext>performance improvement</changelogtext>
 *            </changelogversion>
 *            <changelogversion versionName="1.1" changeDate="13/01/2013">
 *                 <changelogtext>issue on wifi connection</changelogtext>*
 *            </changelogversion>*
 *       </changelog>
 * </pre>
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
@SuppressWarnings("unused")
public class XmlParser extends BaseParser {

    /**
     * TAG for logging
     **/
    private static final String TAG = "XmlParser";
    private int changeLogFileResourceId = Constants.changeLogFileResourceId;
    private String changeLogFileResourceUrl = null;

    protected ChangeLogAdapter changeLogAdapter;

    //--------------------------------------------------------------------------------
    //TAGs and ATTRIBUTEs in xml file
    //--------------------------------------------------------------------------------

    private static final String TAG_CHANGELOG = "changelog";
    private static final String TAG_CHANGELOGVERSION = "changelogversion";
    private static final String TAG_CHANGELOGTEXT = "changelogtext";
    private static final String TAG_CHANGELOGBUG = "changelogbug";
    private static final String TAG_CHANGELOGIMPROVEMENT = "changelogimprovement";

    private static final String ATTRIBUTE_BULLETEDLIST = "bulletedList";
    private static final String ATTRIBUTE_VERSIONNAME = "versionName";
    private static final String ATTRIBUTE_VERSIONCODE = "versionCode";
    private static final String ATTRIBUTE_CHANGEDATE = "changeDate";
    //private static final String ATTRIBUTE_CHANGETEXT="changeText";
    private static final String ATTRIBUTE_CHANGETEXTTITLE = "changeTextTitle";

    private static final List<String> changeLogTags = new ArrayList<String>() {{
        add(TAG_CHANGELOGBUG);
        add(TAG_CHANGELOGIMPROVEMENT);
        add(TAG_CHANGELOGTEXT);
    }};

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
        //Log.d(TAG,"Processing main tag=");

        // Read attributes
        String bulletedList = parser.getAttributeValue(null, ATTRIBUTE_BULLETEDLIST);
        if (bulletedList == null || bulletedList.equals("true")) {
            changeLog.setBulletedList(true);
            super.bulletedList = true;
        } else {
            changeLog.setBulletedList(false);
            super.bulletedList = false;
        }

        //Parse nested nodes
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tag = parser.getName();
            //Log.d(TAG,"Processing tag="+tag);

            if (tag.equals(TAG_CHANGELOGVERSION)) {
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
    protected void readChangeLogVersionNode(XmlPullParser parser, ChangeLog changeLog) throws Exception {

        if (parser == null) return;

        parser.require(XmlPullParser.START_TAG, null, TAG_CHANGELOGVERSION);

        // Read attributes
        String versionName = parser.getAttributeValue(null, ATTRIBUTE_VERSIONNAME);
        String versionCodeStr = parser.getAttributeValue(null, ATTRIBUTE_VERSIONCODE);
        int versionCode = 0;
        if (versionCodeStr != null) {
            try {
                versionCode = Integer.parseInt(versionCodeStr);
            } catch (NumberFormatException ne) {
                Log.w(TAG, "Error while parsing versionCode.It must be a numeric value. Check you file.");
            }
        }
        String changeDate = parser.getAttributeValue(null, ATTRIBUTE_CHANGEDATE);
        if (versionName == null)
            throw new ChangeLogException("VersionName required in changeLogVersion node");

        ChangeLogRowHeader row = new ChangeLogRowHeader();
        row.setVersionName(versionName);
        //row.setVersionCode(versionCode);
        row.setChangeDate(changeDate);
        changeLog.addRow(row);

        //Log.d(TAG,"Added rowHeader:"+row.toString());

        // Parse nested nodes
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            //Log.d(TAG,"Processing tag="+tag);

            if (changeLogTags.contains(tag)) {
                readChangeLogRowNode(parser, changeLog, versionName, versionCode);
            }
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

        // Read attributes
        String changeLogTextTitle = parser.getAttributeValue(null,
                ATTRIBUTE_CHANGETEXTTITLE);
        if (changeLogTextTitle != null)
            //noinspection deprecation
            row.setChangeTextTitle(changeLogTextTitle);

        // It is possible to force bulleted List
        String bulletedList = parser.getAttributeValue(null, ATTRIBUTE_BULLETEDLIST);
        if (bulletedList != null) {
            row.setBulletedList(bulletedList.equals("true"));
        } else {
            row.setBulletedList(super.bulletedList);
        }

        // Read text
        if (parser.next() == XmlPullParser.TEXT) {
            String changeLogText = parser.getText();
            if (changeLogText == null)
                throw new ChangeLogException("ChangeLogText required in changeLogText node");
            row.parseChangeText(changeLogText);
            row.setType(tag.equalsIgnoreCase(TAG_CHANGELOGBUG) ? ChangeLogRow.BUGFIX :
                    tag.equalsIgnoreCase(TAG_CHANGELOGIMPROVEMENT) ? ChangeLogRow.IMPROVEMENT :
                            ChangeLogRow.DEFAULT);
            parser.nextTag();
        }
        changeLog.addRow(row);

        //Log.d(TAG, "Added row:" + row.toString());

    }


    public void setChangeLogAdapter(ChangeLogAdapter changeLogAdapter) {
        this.changeLogAdapter = changeLogAdapter;
    }
}
