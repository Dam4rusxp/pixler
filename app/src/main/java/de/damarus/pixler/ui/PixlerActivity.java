package de.damarus.pixler.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.damarus.pixler.PixlerManager;
import de.damarus.pixler.R;
import de.damarus.pixler.layers.LayerAdapter;

import java.io.FileNotFoundException;

public class PixlerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG_RETAINED_PIC = "RetainedPic";

    public static final int PICK_IMAGE = 0;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.layers_recycler)
    RecyclerView layerDrawer;

    @BindView(R.id.btn_add_layer)
    ImageButton btnAddLayer;

    @BindView(R.id.btn_remove_layer)
    ImageButton btnRmLayer;

    private RelativeLayout navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixler);

        ButterKnife.bind(this);
        navHeader = ButterKnife.findById(navigationView.getHeaderView(0), R.id.nav_header);

        // Toolbar + Drawer
        setSupportActionBar(toolbar);
        ActionBarDoubleDrawerToggle drawerToggle = new ActionBarDoubleDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_close, R.string.navigation_drawer_open);
        drawer.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle.syncState();

        // Initialize canvas fragment
        PixlerFragment frag = (PixlerFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragment);
        if (frag == null) {
            frag = PixlerFragment.createInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFragment, frag).commit();
        }

        // Initialize layers drawer
        layerDrawer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        layerDrawer.setAdapter(new LayerAdapter(this));

        btnAddLayer.setOnClickListener(view -> PixlerManager.getInstance().createNewLayer());
        btnRmLayer.setOnClickListener(view -> PixlerManager.getInstance().removeLayer());
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_new:
                // Start activity for creating a new image
                break;

            case R.id.nav_open:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();

                    try {
                        PixlerManager.getInstance().openFromUri(this, selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Aborted", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
