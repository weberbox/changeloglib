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
package it.gmariotti.changelog.demo;

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

import com.google.android.material.navigation.NavigationView;

import it.gmariotti.changelog.demo.fragment.BaseFragment;
import it.gmariotti.changelog.demo.fragment.CustomLayoutFragment;
import it.gmariotti.changelog.demo.fragment.CustomLayoutRowFragment;
import it.gmariotti.changelog.demo.fragment.CustomXmlFileFragment;
import it.gmariotti.changelog.demo.fragment.DialogMaterialFragment;
import it.gmariotti.changelog.demo.fragment.MaterialFragment;
import it.gmariotti.changelog.demo.fragment.StandardFragment;
import it.gmariotti.changelog.demo.fragment.WithoutBulletPointFragment;
import it.gmariotti.changelog.demo.utils.PrefUtils;


/**
 * Main Activity
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private BaseFragment mSelectedFragment;

    private static final String TAG = "MainActivity";
    private static final String BUNDLE_SELECTEDFRAGMENT = "BDL_SELFRG";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SELECTEDFRAGMENT, mSelectedFragment.getSelfNavDrawerItem());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_changelog_main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // enable ActionBar app icon to behave as action to toggle nav drawer
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (savedInstanceState != null) {
            int mSelectedFragmentIndex = savedInstanceState.getInt(BUNDLE_SELECTEDFRAGMENT);
            mSelectedFragment = selectFragment(mSelectedFragmentIndex);
        } else {
            mSelectedFragment = new StandardFragment();
        }
        if (mSelectedFragment != null)
            openFragment(mSelectedFragment);

    }


    /**
     * Resolves the fragment
     *
     * @param itemId itemId
     * @return return
     */
    private BaseFragment selectFragment(int itemId) {
        BaseFragment baseFragment;

        switch (itemId) {
            default:
            case R.id.nav_ex_standard:
                baseFragment = new StandardFragment();
                break;
            case R.id.nav_ex_material:
                baseFragment = new MaterialFragment();
                break;
            case R.id.nav_ex_without_bull:
                baseFragment = new WithoutBulletPointFragment();
                break;
            case R.id.nav_ex_custom_xml:
                baseFragment = new CustomXmlFileFragment();
                break;
            case R.id.nav_ex_custom_header:
                baseFragment = new CustomLayoutFragment();
                break;
            case R.id.nav_ex_custom_row:
                baseFragment = new CustomLayoutRowFragment();
                break;
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
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    selectNavigationItem(menuItem.getItemId());
                    mDrawerLayout.closeDrawers();
                    return true;
                });

        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.
        if (!PrefUtils.isWelcomeDone(this)) {
            // first run of the app starts with the nav drawer open
            PrefUtils.markWelcomeDone(this);
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Handles the navigation view item click
     *
     * @param itemId item id
     */
    private void selectNavigationItem(int itemId) {

        BaseFragment fg = null;
        switch (itemId) {
            case R.id.nav_ex_standard:
            case R.id.nav_ex_material:
            case R.id.nav_ex_custom_xml:
            case R.id.nav_ex_custom_header:
            case R.id.nav_ex_custom_row:
            case R.id.nav_ex_without_bull:
                fg = selectFragment(itemId);
                break;
            case R.id.nav_ex_dialo:
                openDialogFragment(new DialogMaterialFragment());
                break;
            case R.id.nav_other_info:
                Utils.showAbout(this);
                break;
            case R.id.nav_other_github:
                String url = "https://github.com/weberbox/changeloglib/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
        if (fg != null) {
            mSelectedFragment = fg;
            openFragment(fg);
        }
    }
}