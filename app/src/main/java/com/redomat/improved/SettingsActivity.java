package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.redomat.improved.databinding.ActivityLoginBinding;
import com.redomat.improved.databinding.ActivityRedomatAdminBinding;
import com.redomat.improved.databinding.ActivitySettingsBinding;

import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class SettingsActivity extends AppCompatActivity{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ActivitySettingsBinding mBinding;

    //Preferences
    Preference settingsChangeNameAndLastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View loginView = mBinding.getRoot();
        setContentView(loginView);
        setTitle("Postavke");

        if(findViewById(R.id.fragmentContainer) != null){
            if(savedInstanceState != null){
                return;
            }

            getFragmentManager().beginTransaction().add(R.id.fragmentContainer, new SettingsFragment()).commit();
        }
    }

    //Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_dialog, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.optMenuLogout:
                showProgressDialog(SettingsActivity.this);

                mAuth = FirebaseAuth.getInstance();
                mAuth.getInstance().signOut();

                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

                return true;

            case R.id.optMenuStatistics:
                StatisticsDialog statDialog = new StatisticsDialog();
                statDialog.show(getSupportFragmentManager(), "stat_dialog");
                return true;

            case R.id.optMenuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // '\'Options menu -----------------------------------------------------------------------------
}
