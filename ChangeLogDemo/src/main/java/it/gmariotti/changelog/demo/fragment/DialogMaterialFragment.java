/*
 *  ******************************************************************************
 *     Copyright (c) 2013 Gabriele Mariotti.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *    *****************************************************************************
 */
package it.gmariotti.changelog.demo.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;
import it.gmariotti.changelog.demo.R;

/**
 * Example with Dialog
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class DialogMaterialFragment extends DialogFragment {

    public DialogMaterialFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogRecyclerView chgList = (ChangeLogRecyclerView) layoutInflater.inflate(
                R.layout.demo_changelog_fragment_dialogmaterial, null);

        return new AlertDialog.Builder(requireActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.demo_changelog_title_standarddialog)
                .setView(chgList)
                .setPositiveButton(R.string.about_ok,
                        (dialog, whichButton) -> dialog.dismiss()
                )
                .create();

    }

}
