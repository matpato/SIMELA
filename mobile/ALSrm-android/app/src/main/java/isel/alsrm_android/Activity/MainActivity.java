package isel.alsrm_android.Activity;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import isel.alsrm_android.Bluetooth.ManageConnectedSocket;
import isel.alsrm_android.Database.AlsrmSchema;
import isel.alsrm_android.Fragment.AboutFragment;
import isel.alsrm_android.Fragment.BodyPartExamFragment;
import isel.alsrm_android.Fragment.EMG_Fragment;
import isel.alsrm_android.Fragment.ListExamsFragment;
import isel.alsrm_android.Fragment.MainFragment;
import isel.alsrm_android.Fragment.SPO2_Fragment;
import isel.alsrm_android.Fragment.SettingsFragment;
import isel.alsrm_android.PopUpDialogFragment.StopDialogFragment;
import isel.alsrm_android.R;
import isel.alsrm_android.Service.UpdateService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String USER_ID = "USER_ID";
    public static final String PASSWORD = "PASSWORD";
    public static final String MAC = "MAC";

    private static final String TAG = "MainFragment";
    NavigationView navigationView = null;
    Toolbar toolbar = null;

    public static final String MUSCLE = "MUSCLE";
    public static final String EXAM = "EXAM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set the fragment initially
        MainFragment fragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, TAG);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }else {
                super.onBackPressed();
            }
        }
    }

    public void onClick_button_ECG(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);

        if(run){
            FragmentManager fm = this.getFragmentManager();
            DialogFragment dialog = new StopDialogFragment();
            dialog.show(fm, "dialog");
        }else {
            Intent i = new Intent(getBaseContext(), ECG_Activity.class);
            startActivity(i);
        }
    }

    public void onClick_button_EMG(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);

       if(run){
            FragmentManager fm = this.getFragmentManager();
            DialogFragment dialog = new StopDialogFragment();
            dialog.show(fm, "dialog");
        }
        else {

            getIntent().putExtra(EXAM, EMG_Fragment.EMG);
            BodyPartExamFragment fragment = new BodyPartExamFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void onClick_button_SPO2(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);

        if(run){
            FragmentManager fm = this.getFragmentManager();
            DialogFragment dialog = new StopDialogFragment();
            dialog.show(fm, "dialog");
        }else {

            getIntent().putExtra(EXAM, SPO2_Fragment.SPO2);
            BodyPartExamFragment fragment = new BodyPartExamFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void onClick_button_Head(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);

        if(run){
            FragmentManager fm = this.getFragmentManager();
            DialogFragment dialog = new StopDialogFragment();
            dialog.show(fm, "dialog");
        }
        else {
            Bundle bundle = getIntent().getExtras();
            String exam = bundle.getString(EXAM, null);

            if (exam.equals(EMG_Fragment.EMG)) {
                Intent i = new Intent(getBaseContext(), EMG_Activity.class);
                i.putExtra(MUSCLE, AlsrmSchema.SternocleidoMastoideus);
                startActivity(i);
            }

            if (exam.equals(SPO2_Fragment.SPO2)) {
                Intent i = new Intent(getBaseContext(), SPO2_Activity.class);
                i.putExtra(MUSCLE, AlsrmSchema.SternocleidoMastoideus);
                startActivity(i);
            }
        }
    }

    public void onClick_button_Leg(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);

        if(run){
            FragmentManager fm = this.getFragmentManager();
            DialogFragment dialog = new StopDialogFragment();
            dialog.show(fm, "dialog");
        }
        else {
            Bundle bundle = getIntent().getExtras();
            String exam = bundle.getString(EXAM, null);

            if (exam.equals(EMG_Fragment.EMG)) {
                Intent i = new Intent(getBaseContext(), EMG_Activity.class);
                i.putExtra(MUSCLE, AlsrmSchema.AnteriorTibialis);
                startActivity(i);
            }

            if (exam.equals(SPO2_Fragment.SPO2)) {
                Intent i = new Intent(getBaseContext(), SPO2_Activity.class);
                i.putExtra(MUSCLE, AlsrmSchema.AnteriorTibialis);
                startActivity(i);
            }
        }
    }

    public void onClick_button_Arm(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean run = sharedPreferences.getBoolean(ManageConnectedSocket.IS_INTENT_SERVICE_RUNNING, false);

        if(run){
            FragmentManager fm = this.getFragmentManager();
            DialogFragment dialog = new StopDialogFragment();
            dialog.show(fm, "dialog");
        }
        else {
            Bundle bundle = getIntent().getExtras();
            String exam = bundle.getString(EXAM, null);

            if (exam.equals(EMG_Fragment.EMG)) {
                Intent i = new Intent(getBaseContext(), EMG_Activity.class);
                i.putExtra(MUSCLE, AlsrmSchema.FlexorCarpiRadialis);
                startActivity(i);
            }

            if (exam.equals(SPO2_Fragment.SPO2)) {
                Intent i = new Intent(getBaseContext(), SPO2_Activity.class);
                i.putExtra(MUSCLE, AlsrmSchema.FlexorCarpiRadialis);
                startActivity(i);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_homepage) {

            MainFragment mainFragment = (MainFragment)getFragmentManager().findFragmentByTag(TAG);
            if (!mainFragment.isVisible()) {

                for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                    getFragmentManager().popBackStack();
                }

                MainFragment fragment = new MainFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment, TAG);
                fragmentTransaction.commit();
            }

        } else if (id == R.id.nav_exams) {
            ListExamsFragment fragment = new ListExamsFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_search) {
            if (MainFragment.bluetoothAdapt != null)
                MainFragment.bluetoothAdapt.startDiscovery();

        }else if (id == R.id.nav_update) {
            Intent service = new Intent(this, UpdateService.class);
            this.startService(service);

        } else if (id == R.id.nav_info) {
            AboutFragment fragment = new AboutFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_settings) {
            SettingsFragment fragment = new SettingsFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

