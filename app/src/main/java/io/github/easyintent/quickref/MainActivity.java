package io.github.easyintent.quickref;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.github.easyintent.quickref.databinding.ActivityMainBinding;
import io.github.easyintent.quickref.view.AboutFragment;
import io.github.easyintent.quickref.view.ClosableFragment;
import io.github.easyintent.quickref.view.FavoriteListFragment;
import io.github.easyintent.quickref.view.MessageDialogFragment;
import io.github.easyintent.quickref.view.ReferenceListFragment;

public class MainActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            MessageDialogFragment.Listener {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);


    // closable fragment on top
    private ClosableFragment closableFragment;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBar.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout,
                binding.appBar.toolbar, R.string.lbl_nav_open, R.string.lbl_nav_close);

        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);

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

        int itemId = item.getItemId();
        if (itemId == R.id.nav_favorite) {
            showFavorites();
        } else if (itemId == R.id.nav_about) {
            showAbout();
        } // else if (itemId == R.id.nav_all) {}

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

        binding.navigationView.setCheckedItem(R.id.nav_all);
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

        binding.navigationView.setCheckedItem(R.id.nav_all);
    }
}
