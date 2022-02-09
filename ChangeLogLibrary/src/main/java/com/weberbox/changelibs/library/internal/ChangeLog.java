/*
 Copyright (c) 2013 Gabriele Mariotti.
 Copyright (c) 2021 James Weber.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.weberbox.changelibs.library.internal;

import androidx.annotation.NonNull;

import java.util.LinkedList;

/**
 * ChangeLog model
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
@SuppressWarnings("unused")
public class ChangeLog {

    /**
     * All changelog rows
     */
    private LinkedList<ChangeLogRow> rows;

    /**
     * Use a bulleted List
     */
    private boolean bulletedList;

    //-----------------------------------------------------------------------

    public ChangeLog() {
        rows = new LinkedList<>();
    }

    /**
     * Add new {@link ChangeLogRow} to rows
     */
    public void addRow(ChangeLogRow row) {
        if (row != null) {
            if (rows == null) rows = new LinkedList<>();
            rows.add(row);
        }
    }

    /**
     * Clear all rows
     */
    public void clearAllRows() {
        rows = new LinkedList<>();
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("bulletedList=").append(bulletedList);
        sb.append("\n");
        if (rows != null) {
            for (ChangeLogRow row : rows) {
                sb.append("row=[");
                sb.append(row.toString());
                sb.append("]\n");
            }
        } else {
            sb.append("rows:none");
        }
        return sb.toString();
    }

    //-----------------------------------------------------------------------

    public boolean isBulletedList() {
        return bulletedList;
    }

    public void setBulletedList(boolean bulletedList) {
        this.bulletedList = bulletedList;
    }

    public LinkedList<ChangeLogRow> getRows() {
        return rows;
    }

    public void setRows(LinkedList<ChangeLogRow> rows) {
        this.rows = rows;
    }


}
