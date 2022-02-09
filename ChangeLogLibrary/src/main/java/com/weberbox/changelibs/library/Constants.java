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
package com.weberbox.changelibs.library;


import com.weberbox.changelibs.R;

/**
 * Constants used by library
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 * @author James Weber
 */
public class Constants {

    /**
     * Resource id for changelog.xml file.
     * <p>
     * You shouldn't modify this value.
     * You can use chglib_changelog_resource attribute in ChangeLogListView
     **/
    public static final int logFileResourceId = R.raw.changelog;

    /**
     * Layout resource id for changelog item rows.
     * <p>
     * You shouldn't modify this value.
     * You can use chglib_row_layout attribute in ChangeLogListView
     **/
    public static final int rowLayoutId = R.layout.changelog_row_layout;

    /**
     * Layout resource id for changelog header rows.
     * <p>
     * You shouldn't modify this value.
     * You can use chglib_row_header_layout attribute in ChangeLogListView
     **/
    public static final int rowHeaderLayoutId = R.layout.changelog_header_layout;

    /**
     * String resource id for text Version in header row.
     * <p>
     * You shouldn't modify this value.
     * You can use chglib_changelog_header_version in ChangeLogListView
     */
    public static final int stringVersionHeader = R.string.changelog_header_version;

    /**
     * Resource id for current version color
     * <p>
     * You shouldn't modify this value.
     * You can use chglib_current_version_color attribute in ChangeLogListView
     */
    public static final int currentVersionColor = R.color.chglib_current_version_color;

    /**
     * Resource id for material text color one.
     * <p>
     * You shouldn't modify this value.
     * You can use chglib_material_color_text_one attribute in ChangeLogListView
     **/
    public static final int previousVersionColor = R.color.chglib_version_text_color;
}
