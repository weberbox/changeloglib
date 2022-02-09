# ChangeLog Library

ChangeLog Library provides an easy way to display a change log in your Android app.

![Screen](/ChangeLogDemo/images/screen2.png)

---
## Examples

* **Sample** application: The demo is a showcase of the functionality of the library.

* Browse the [source code of the sample application](/ChangeLogDemo) for a complete example of use.

**If you would like, you can support my work, donating through the demo app.**


For more examples and screenshots you can read this [document:](/ChangeLogDemo/README.md)


## Feature

ChangeLog Library provides a custom `RecyclerView` to display a change log through a xml file.

* you can use it in Activities, Fragments, Dialogs
* it works with a RecyclerView or a ListView
* it supports html text markup as bold and italics
* you can customize layout and behaviour
* it supports multi language
* it supports API 7+

## Doc

See the customisation [page:](/doc/CUSTOMIZATION.md) for more information.

---
## Quick Start

ChangeLog Library is pushed to Maven Central as a AAR, so you just need to add the following dependency to your `build.gradle`.

``` groovy
dependencies {
    implementation 'com.github.weberbox.changeloglib:x.x.x'
}
```

[To build the library and demo locally you can see this page for more info](/doc/BUILD.md).


## ChangeLog

* [Changelog:](CHANGELOG.md) A complete changelog

## Usage

Implementing this library in your own apps is pretty simple.<br/>
First, add in your layout the `ChangeLogRecyclerView ` that displays your changelog.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<com.weberbox.changelibs.library.view.ChangeLogRecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center" />
```

Then, you need a XML file with change log in `res/raw` folder.
It automatically searches for [`res/raw/changelog.xml`](/ChangeLogLibrary/src/main/res/raw/changelog.xml) but you can customize filename.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<changelog bulletedList="false">

    <changelogversion versionName="1.0" changeDate="Aug 26,2013" currentVersion="true">
            <changelogtext>Initial release</changelogtext>
    </changelogversion>

    <changelogversion versionName="0.9" changeDate="Aug 11,2013">
        <changelogtext logType="new">[b]New![/b] Add new attrs to customize header and row layout</changelogtext>
        <changelogtext logType="fix">Fixed log while parsing</changelogtext>
        <changelogtext logType="new">Add support for html markup</changelogtext>
        <changelogtext logType="note">Add bullet point in</changelogtext>
        <changelogtext logType="imp">Support for customized xml filename</changelogtext>
    </changelogversion>

</changelog>

```

Last, if you would like a multi language changelog, you just have to put the translated files `changelog.xml` in the appropriate folders `res/raw-xx/`.


Credits
-------

Original Author: Gabriele Mariotti (gabri.mariotti@gmail.com)

License
-------

    Copyright 2013-2015 Gabriele Mariotti
	Copyright 2021 James Weber

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
