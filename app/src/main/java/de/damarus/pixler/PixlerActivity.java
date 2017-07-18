package de.damarus.pixler;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clans.fab.FloatingActionButton;
import de.damarus.pixler.canvas.PixlerView;
import de.damarus.pixler.canvas.RetainedPixlerFragment;

public class PixlerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG_RETAINED_PIC = "RetainedPic";

    @BindView(R.id.main_canvas)
    PixlerView mainCanvas;

    @BindView(R.id.fab_undo)
    FloatingActionButton fabUndo;

    @BindView(R.id.fab_redo)
    FloatingActionButton fabRedo;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.layers_drawer)
    ListView layerDrawer;

    @BindView(R.id.pixlerPrimaryColor)
    FloatingActionButton fabPrimaryColor;

    @BindView(R.id.pixlerSecondaryColor)
    FloatingActionButton fabSecondaryColor;

    private RelativeLayout navHeader;
    private RetainedPixlerFragment retainedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixler);
        ButterKnife.bind(this);

        navHeader = ButterKnife.findById(navigationView.getHeaderView(0), R.id.nav_header);

        // Toolbar
        setSupportActionBar(toolbar);

        // Drawer
        ActionBarDoubleDrawerToggle drawerToggle = new ActionBarDoubleDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_close, R.string.navigation_drawer_open);

        drawer.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle.syncState();

        // FAB Menu
        fabUndo.setOnClickListener(v -> mainCanvas.undoAction());
        fabRedo.setOnClickListener(v -> mainCanvas.redoAction());

        // Color Pickers
        fabPrimaryColor.setOnClickListener(v -> {
            DialogFragment frag = ColorPickerDialog.newInstance(mainCanvas.getConfig().getColor(), c -> {
                updateColors(c, retainedState.getSecondaryColor());
            });

            frag.show(getSupportFragmentManager(), "color-primary");
        });

        fabSecondaryColor.setOnClickListener(v -> {
            // Swap primary and secondary color
            int primary = fabPrimaryColor.getColorNormal();
            int secondary = fabSecondaryColor.getColorNormal();

            updateColors(secondary, primary);
        });

        // Restore state
        FragmentManager fm = getSupportFragmentManager();
        retainedState = (RetainedPixlerFragment) fm.findFragmentByTag(TAG_RETAINED_PIC);

        if (retainedState == null) {
            retainedState = new RetainedPixlerFragment();
            fm.beginTransaction().add(retainedState, TAG_RETAINED_PIC).commit();

            retainedState.setRetainedState(mainCanvas.getConfig());
            updateColors(ContextCompat.getColor(this, R.color.pixlerPrimary),
                    ContextCompat.getColor(this, R.color.pixlerSecondary));
        } else {
            mainCanvas.setConfig(retainedState.getRetainedState());
            updateColors(mainCanvas.getConfig().getColor(), retainedState.getSecondaryColor());
        }
    }

    private void updateColors(int primary, int secondary) {
        fabPrimaryColor.setColorNormal(primary);
        fabPrimaryColor.setColorPressed(primary);

        fabSecondaryColor.setColorNormal(secondary);
        fabSecondaryColor.setColorPressed(secondary);

        mainCanvas.getConfig().setColor(primary);
        retainedState.setSecondaryColor(secondary);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
