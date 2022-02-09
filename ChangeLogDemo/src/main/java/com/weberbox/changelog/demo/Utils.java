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
package com.weberbox.changelog.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by gabriele on 28/08/13.
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class Utils {

    public static void showAbout(AppCompatActivity activity) {

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new AboutDialog().show(ft,"dialog_about");
    }

    /**
     * About Dialog
     */
    public static class AboutDialog extends DialogFragment {

        private static final String VERSION_UNAVAILABLE = "N/A";

        public AboutDialog() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get app version
            PackageManager pm = requireActivity().getPackageManager();
            String packageName = requireActivity().getPackageName();
            String versionName;
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                versionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = VERSION_UNAVAILABLE;
            }

            LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View rootView = layoutInflater.inflate(R.layout.demo_dialog_about, null);
            TextView nameAndVersionView = rootView.findViewById(
                    R.id.app_name_and_version);
            nameAndVersionView.setText(Html.fromHtml(
                    getString(R.string.title_about, versionName)));

            TextView aboutBodyView = rootView.findViewById(R.id.about_body);
            aboutBodyView.setText(Html.fromHtml(getString(R.string.about_body)));
            aboutBodyView.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(requireActivity(),R.style.AppCompatAlertDialogStyle)
                    .setView(rootView)
                    .setPositiveButton(R.string.about_ok,
                            (dialog, whichButton) -> dialog.dismiss()
                    )
                    .create();
        }
    }
}
