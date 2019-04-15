package com.isa_t.proyectofinalmasterd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BaseActivity {

    public static MainActivity activity;
    public static SqlLiteHelper sqliteHelper;
    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorToolbar));
        activity = this;
        Utils.requestPermissions(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onPermissionsGranted () {

        sqliteHelper = new SqlLiteHelper(this, "ejericioFinalNotas.db", null, SqlLiteHelper.CURRENT_VERSION);

        loadFragment(new FragmentPrincipal());
    }

    //lo llamare para pasar de un fragment a otro
    public final void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        for(int index = 0; index < fragmentManager.getBackStackEntryCount(); index++)
            fragmentManager.popBackStackImmediate(); //dejo 1 activo
        FragmentTransaction transaction = fragmentManager.beginTransaction().replace(R.id.fragment, fragment);
        transaction.addToBackStack("Fragment Actual"); //dejo activo solo mi fragmento actual
        transaction.commit();
        currentFragment = fragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == null || currentFragment instanceof FragmentPrincipal ) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return;
        }
        loadFragment(new FragmentPrincipal());
    }
}
