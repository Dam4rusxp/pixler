package de.damarus.pixler.ui;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * A version of {@link ActionBarDrawerToggle} that properly supports a drawer on each side of the screen
 */
class ActionBarDoubleDrawerToggle extends ActionBarDrawerToggle {
    private boolean forceClosing = false;
    private int closingViewGravity = Integer.MIN_VALUE;

    private DrawerLayout drawer;
    private int mainDrawerGravity = GravityCompat.START;

    public ActionBarDoubleDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        drawer = drawerLayout;
    }

    public ActionBarDoubleDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        drawer = drawerLayout;

        // The original listener from ActionBarDrawerToggle doesn't support two drawers properly
        toolbar.setNavigationOnClickListener(v -> {
            if (isDrawerIndicatorEnabled()) {
                doubleToggle();
            } else if (getToolbarNavigationClickListener() != null) {
                getToolbarNavigationClickListener().onClick(v);
            }
        });
    }

    private void doubleToggle() {
        int startLockMode = drawer.getDrawerLockMode(GravityCompat.START);
        int endLockMode = drawer.getDrawerLockMode(GravityCompat.END);

        if (drawer.isDrawerVisible(GravityCompat.START) && (startLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerVisible(GravityCompat.END) && (endLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            drawer.closeDrawer(GravityCompat.END);

        } else if (startLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            drawer.openDrawer(mainDrawerGravity);
        }
    }


    @Override
    public void onDrawerClosed(View drawerView) {
        if (forceClosing) {
            forceClosing = false;
            closingViewGravity = Integer.MIN_VALUE;
        } else {
            super.onDrawerClosed(drawerView);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        DrawerLayout.LayoutParams drawnViewParams = (DrawerLayout.LayoutParams) drawerView.getLayoutParams();

        if (!forceClosing) {
            if (drawnViewParams.gravity == GravityCompat.START && drawer.isDrawerVisible(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
                closingViewGravity = GravityCompat.END;
                forceClosing = true;
            } else if (drawnViewParams.gravity == GravityCompat.END && drawer.isDrawerVisible(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                closingViewGravity = GravityCompat.START;
                forceClosing = true;
            }
        }

        if (!forceClosing || closingViewGravity != drawnViewParams.gravity) {
            super.onDrawerSlide(drawerView, slideOffset);
        }
    }

    public void setMainDrawerGravity(int gravity) {
        this.mainDrawerGravity = gravity;
    }

    public int getMainDrawerGravity() {
        return mainDrawerGravity;
    }
}
