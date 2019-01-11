package io.github.easyintent.quickref;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.androidannotations.annotations.EActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.easyintent.quickref.fragment.AboutFragment;
import io.github.easyintent.quickref.fragment.ClosableFragment;
import io.github.easyintent.quickref.fragment.FavoriteListFragment;
import io.github.easyintent.quickref.fragment.MessageDialogFragment;
import io.github.easyintent.quickref.fragment.ReferenceListFragment;

@EActivity
public class MainActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            MessageDialogFragment.Listener {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    private Toolbar toolbar;
    private NavigationView navigationView;

    // closable fragment on top
    private ClosableFragment closableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.lbl_nav_open, R.string.lbl_nav_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        final MenuItem item = menu.findItem(R.id.search_ref);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.lbl_search_reference));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                item.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mayPopFragment();
        }
    }

    private void mayPopFragment() {
        FragmentManager manager = getSupportFragmentManager();

        if (!closableFragment.allowBack()) {
            return;
        }

        if (manager.getBackStackEntryCount() > 1) {
            showMainFragment();
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        showMainFragment();

        switch (item.getItemId()) {
            case R.id.nav_all:
                break;
            case R.id.nav_favorite:
                showFavorites();
                break;
            case R.id.nav_about:
                showAbout();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void search(String query) {
        startActivity(QuickRefActivity.newSearchIntent(this, query));
    }

    private void showFavorites() {
        FragmentManager manager = getSupportFragmentManager();
        FavoriteListFragment fragment = FavoriteListFragment.newInstance();
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "favorite_list")
                .addToBackStack("favorite")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        closableFragment = fragment;
    }

    private void showAbout() {
        FragmentManager manager = getSupportFragmentManager();
        AboutFragment fragment = AboutFragment.newInstance();
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment, "about_fragment")
                .addToBackStack("about")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        closableFragment = fragment;

    }

    private void initFragment() {
        FragmentManager manager = getSupportFragmentManager();
        ReferenceListFragment fragment = (ReferenceListFragment) manager.findFragmentByTag("reference_list");

        if (fragment == null) {
            // show main reference list
            fragment = ReferenceListFragment.newListChildrenInstance(null);
            manager.beginTransaction()
                    .replace(R.id.content_frame, fragment, "reference_list")
                    .addToBackStack("main")
                    .commit();
        }

        navigationView.setCheckedItem(R.id.nav_all);
        closableFragment = fragment;
    }

    @Override
    public void onOkClicked(MessageDialogFragment dialogFragment) {
        // nothing to do
    }

    private void showMainFragment() {

        setTitle(getString(R.string.app_name));
        getSupportFragmentManager()
                .popBackStack("main", 0);

        navigationView.setCheckedItem(R.id.nav_all);
    }
}
