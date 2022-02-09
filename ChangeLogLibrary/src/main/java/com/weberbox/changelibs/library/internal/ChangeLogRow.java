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

import androidx.annotation.NonNull;

/**
 * ChangeLogRow model
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 * @author James Weber
 */
@SuppressWarnings("unused")
public class ChangeLogRow {

    /**
     * Default type
     */
    public static final int DEFAULT = 0;

    /**
     * Improvement type
     */
    public static final int IMPROVEMENT = 1;

    /**
     * Fix type
     */
    public static final int FIX = 2;

    /**
     * Note type
     */
    public static final int NOTE = 3;

    /**
     * New type
     */
    public static final int NEW = 4;


    //-------------------------------------------------------------------------------------------------------------------

    /**
     * Flag to indicate a header row
     */
    protected boolean header;

    /**
     * This corresponds to the android:versionName attribute in your manifest file. It is required
     */
    protected String versionName;

    /**
     * This corresponds to the android:versionCode attribute in your manifest file. It is optional
     */
    protected int versionCode;

    /**
     * Change data. It is optional
     */
    protected String changeDate;

    //-------------------------------------------------------------------------------------------------------------------

    /**
     * Use a bulleted list. It overrides general flag. It is optional
     */
    private boolean bulletedList;

    /**
     * If this is the current version. Removes top padding for first list item
     */
    private boolean currentVersion;

    /**
     * Contains the actual text that will be displayed in your change log. It is required
     */
    private String changeText;

    /**
     * The type of change: bug, improvement, default.
     */
    private int type;

    //-------------------------------------------------------------------------------------------------------------------

    /**
     * Replace special tags [b] [i]
     *
     * @param changeLogText text
     */
    public void parseChangeText(String changeLogText) {
        if (changeLogText != null) {
            changeLogText = changeLogText.replaceAll("\\[", "<").replaceAll("\\]", ">");
        }
        setChangeText(changeLogText);
    }

    @NonNull
    @Override
    public String toString() {
        return "header=" +
                header +
                "," +
                "versionName=" +
                versionName +
                "," +
                "versionCode=" +
                versionCode +
                "," +
                "bulletedList=" +
                bulletedList +
                "," +
                "changeText=" +
                changeText;
    }

    //-------------------------------------------------------------------------------------------------------------------


    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isBulletedList() {
        return bulletedList;
    }

    public void setBulletedList(boolean bulletedList) {
        this.bulletedList = bulletedList;
    }

    public boolean isCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(boolean currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getChangeText() {
        return changeText;
    }

    public void setChangeText(String changeText) {
        this.changeText = changeText;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public int getLogType() {
        return type;
    }

    public void setLogType(int type) {
        this.type = type;
    }

}
