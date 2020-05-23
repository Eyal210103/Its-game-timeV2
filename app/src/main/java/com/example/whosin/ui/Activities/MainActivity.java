package com.example.whosin.ui.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Listeners.DataLoadListener;
import com.example.whosin.model.Listeners.UserMeetingsLoadListener;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.example.whosin.model.Singleton.Fragments;
import com.example.whosin.model.ViewModels.UserSharedViewModel;
import com.example.whosin.ui.Adapters.ViewPagerAdapter;
import com.example.whosin.ui.ProfileInfoFragment;
import com.example.whosin.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener , DataLoadListener, UserMeetingsLoadListener {


    private static final String TAG = "Main Activity";

    UserSharedViewModel userSharedViewModel;

    private Dialog share;
    public  ViewPager viewPager;
    public  ViewPagerAdapter adapter;
    MenuItem prevMenuItem;
    Dialog load;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ChipNavigationBar bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setItemSelected(R.id.nav_home ,true);
        bottomNavigationView.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getHomeFragment(), "home").commit();
                        break;

                    case R.id.nav_find:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getFindGroupFragment(), "find").commit();
                        break;

                    case R.id.nav_myGroups:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getMyGroupsFragment(), "my groups").commit();
                        break;

                    case R.id.nav_Profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getSettingFragment(), "profile").commit();
                        break;
                }
            }
        });

//========================================================================================================
        User thisUser = CurrentUser.getInstance();

        CircleImageView circleImageView = findViewById(R.id.toolbar_imageView);
        Glide.with(this).load(thisUser.getImageUri()).into(circleImageView);

        load = new Dialog(this);
        load.setContentView(R.layout.waiting_layout);
        load.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        load.setCancelable(false);
        load.show();

        this.share = new Dialog(this);
        this.share.setContentView(R.layout.shere_methods_dialog);
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        homeFragment.setArguments(bundle);
        Log.d("", "onCreate: ++++++++++" + thisUser.toString());
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments,Fragments.getHomeFragment()).commit();
        //viewPager = (ViewPager) findViewById(R.id.container_fragments);
//        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        setupViewPager(viewPager);

        userSharedViewModel = ViewModelProviders.of(this).get(UserSharedViewModel.class);
        userSharedViewModel.init(this);
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (prevMenuItem != null)
//                    prevMenuItem.setChecked(false);
//                else
//                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
//                if (position < 4) {
//                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                    prevMenuItem = bottomNavigationView.getMenu().getItem(position);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, new ProfileInfoFragment()).commit();
                return true;

            case R.id.menu_logout:
                logout();
                return true;

            case R.id.share:
                share.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(Fragments.getHomeFragment());
        adapter.addFragment(Fragments.getMyGroupsFragment());
        adapter.addFragment(Fragments.getFindGroupFragment());
        adapter.addFragment(Fragments.getSettingFragment());
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getHomeFragment(), "home").commit();
                break;

            case R.id.nav_find:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getFindGroupFragment(), "find").commit();
                break;

            case R.id.nav_myGroups:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getMyGroupsFragment(), "my groups").commit();
                break;

            case R.id.nav_Profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_fragments, Fragments.getSettingFragment(), "profile").commit();
                break;
        }
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//                viewPager.setCurrentItem(0);
//                break;
//            case R.id.nav_myGroups:
//                viewPager.setCurrentItem(1);
//
//                break;
//            case R.id.nav_find:
//                viewPager.setCurrentItem(2);
//
//                break;
//            case R.id.nav_Profile:
//                viewPager.setCurrentItem(3);
//                break;
//        }
        return true;
    }

    //Logout______________________________________________________________________
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        CurrentUser.logout();
        Intent logScreen = new Intent(this, LoginActivity.class);
        startActivity(logScreen);
        finish();
    }

    public void onClickWhatsapp(View view) {
        try {
            Intent sharing = new Intent(Intent.ACTION_SEND);
            sharing.setType("text/plain");
            sharing.putExtra(Intent.EXTRA_TEXT, "Try My App" + "\n" + "https://drive.google.com/file/d/1WcOxyQLvJZQBYJ18qivyrcwHFcgoINN_/view?usp=sharing");
            sharing.setPackage("com.whatsapp");
            startActivity(sharing);
        } catch (Exception e) {
            Toast.makeText(this, "Install Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickInstagram(View view) {
        try {
            Intent sharing = new Intent(Intent.ACTION_SEND);
            sharing.setType("text/plain");
            sharing.putExtra(Intent.EXTRA_TEXT, "Try My App" + "\n" + "https://drive.google.com/file/d/1WcOxyQLvJZQBYJ18qivyrcwHFcgoINN_/view?usp=sharing");
            sharing.setPackage("com.instagram.android");
            startActivity(sharing);
        } catch (Exception e) {
            Toast.makeText(this, "Install Instagram", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickFacebook(View view) {
        try {
            Intent sharing = new Intent(Intent.ACTION_SEND);
            sharing.setType("text/plain");
            sharing.putExtra(Intent.EXTRA_TEXT, "https://drive.google.com/file/d/1WcOxyQLvJZQBYJ18qivyrcwHFcgoINN_/view?usp=sharing");
            sharing.setPackage("com.facebook.katana");
            startActivity(sharing);
        } catch (Exception e) {
            Toast.makeText(this, "Install Facebook", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickTwitter(View view) {
        try {
            Intent sharing = new Intent(Intent.ACTION_SEND);
            sharing.setType("text/plain");
            sharing.putExtra(Intent.EXTRA_TEXT, "Try My App" + "\n" + "https://drive.google.com/file/d/1WcOxyQLvJZQBYJ18qivyrcwHFcgoINN_/view?usp=sharing");
            sharing.setPackage("com.twitter.android");
            startActivity(sharing);
        } catch (Exception e) {
            Toast.makeText(this, "Install Twitter", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGroupsLoaded() {
        if (load.isShowing()){
            load.dismiss();
        }
        DataLoadListener home = Fragments.getHomeFragment();
        DataLoadListener myGroups = Fragments.getMyGroupsFragment();
        home.onGroupsLoaded();
        myGroups.onGroupsLoaded();
    }

    @Override
    public void onUserMeetingsLoaded() {
        if (load.isShowing()){
            load.dismiss();
        }
        UserMeetingsLoadListener home = Fragments.getHomeFragment();
        home.onUserMeetingsLoaded();
    }

}




