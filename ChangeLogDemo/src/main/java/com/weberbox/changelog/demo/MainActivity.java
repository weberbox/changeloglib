/*
 * Copyright (c) 2013 Gabriele Mariotti.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package com.weberbox.changelog.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.weberbox.changelog.demo.fragment.BaseFragment;
import com.weberbox.changelog.demo.fragment.CustomLayoutFragment;
import com.weberbox.changelog.demo.fragment.CustomLayoutRowFragment;
import com.weberbox.changelog.demo.fragment.CustomXmlFileFragment;
import com.weberbox.changelog.demo.fragment.DialogMaterialFragment;
import com.weberbox.changelog.demo.fragment.MaterialFragment;
import com.weberbox.changelog.demo.fragment.StandardFragment;
import com.weberbox.changelog.demo.fragment.WithoutBulletPointFragment;
import com.weberbox.changelog.demo.utils.PrefUtils;
import com.google.android.material.navigation.NavigationView;

/**
 * Main Activity
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 * @author James Weber
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BaseFragment selectedFragment;

    private static final String BUNDLE_SELECTEDFRAGMENT = "BDL_SELFRG";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SELECTEDFRAGMENT, selectedFragment.getSelfNavDrawerItem());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // enable ActionBar app icon to behave as action to toggle nav drawer
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (savedInstanceState != null) {
            int mSelectedFragmentIndex = savedInstanceState.getInt(BUNDLE_SELECTEDFRAGMENT);
            selectedFragment = selectFragment(mSelectedFragmentIndex);
        } else {
            selectedFragment = new StandardFragment();
        }

        openFragment(selectedFragment);

    }


    /**
     * Resolves the fragment
     *
     * @param itemId itemId
     * @return return
     */
    private BaseFragment selectFragment(int itemId) {
        BaseFragment baseFragment;

        if (itemId == R.id.nav_ex_standard) {
            baseFragment = new StandardFragment();
        } else if (itemId == R.id.nav_ex_material) {
            baseFragment = new MaterialFragment();
        } else if (itemId == R.id.nav_ex_without_bull) {
            baseFragment = new WithoutBulletPointFragment();
        } else if (itemId == R.id.nav_ex_custom_xml) {
            baseFragment = new CustomXmlFileFragment();
        } else if (itemId == R.id.nav_ex_custom_header) {
            baseFragment = new CustomLayoutFragment();
        } else if (itemId == R.id.nav_ex_custom_row) {
            baseFragment = new CustomLayoutRowFragment();
        } else {
            baseFragment = new StandardFragment();
        }

        return baseFragment;
    }


    /**
     * Opens the dialog
     *
     * @param dialogStandardFragment dialogStandardFragment
     */
    private void openDialogFragment(DialogFragment dialogStandardFragment) {
        if (dialogStandardFragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("changelogdemo_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            dialogStandardFragment.show(ft, "changelogdemo_dialog");
        }
    }

    /**
     * Adds the fragment to MainContent
     *
     * @param baseFragment baseFragment
     */
    private void openFragment(BaseFragment baseFragment) {
        if (baseFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_main, baseFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    selectNavigationItem(menuItem.getItemId());
                    drawerLayout.closeDrawers();
                    return true;
                });

        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.
        if (!PrefUtils.isWelcomeDone(this)) {
            // first run of the app starts with the nav drawer open
            PrefUtils.markWelcomeDone(this);
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Handles the navigation view item click
     *
     * @param itemId item id
     */
    private void selectNavigationItem(int itemId) {
        BaseFragment fg = null;

        if (itemId == R.id.nav_ex_dialog) {
            openDialogFragment(new DialogMaterialFragment());
        } else if (itemId == R.id.nav_other_info) {
            Utils.showAbout(this);
        } else if (itemId == R.id.nav_other_github) {
            String url = "https://github.com/weberbox/changeloglib/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            fg = selectFragment(itemId);
        }

        if (fg != null) {
            selectedFragment = fg;
            openFragment(fg);
        }
    }
}