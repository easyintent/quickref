package io.github.easyintent.quickref;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import io.github.easyintent.quickref.fragment.MessageDialogFragment;
import io.github.easyintent.quickref.fragment.ReferenceListFragment;

@EActivity
public class QuickRefActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            MessageDialogFragment.Listener {

    @Extra
    protected String category;

    @ViewById
    protected Toolbar toolbar;

    /** Create new reference list intent.
     *
     * @param context
     * @param category
     *      The category category, if null it is root category.
     * @return
     */
    @NonNull
    public static Intent newIntent(Context context, @Nullable String category) {
        Intent intent = new Intent();
        intent.putExtra("category", category);
        intent.setClass(context, QuickRefActivityEx.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_ref);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initFragment() {
        FragmentManager manager = getSupportFragmentManager();
        ReferenceListFragment fragment = (ReferenceListFragment) manager.findFragmentByTag("reference_list");
        if (fragment != null) {
            return;
        }

        fragment = ReferenceListFragment.newInstance(category);
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "reference_list")
                .commit();
    }

    @Override
    public void onOkClicked(MessageDialogFragment dialogFragment) {
        finish();
    }


}
