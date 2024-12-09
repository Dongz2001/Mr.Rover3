package com.example.mrrover;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    HistoryFragment historyFragment;
    MessageFragment messageFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        homeFragment = new HomeFragment();
        historyFragment = new HistoryFragment();
        messageFragment = new MessageFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_home){

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,homeFragment).commit();
                }
                if(item.getItemId()==R.id.menu_history){

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,historyFragment).commit();
                }
                if(item.getItemId()==R.id.menu_message){

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,messageFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }

                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

    }
}