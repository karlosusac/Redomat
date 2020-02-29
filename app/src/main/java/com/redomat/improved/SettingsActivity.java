package com.redomat.improved;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.os.Bundle;
import android.view.View;

import com.redomat.improved.databinding.ActivityLoginBinding;
import com.redomat.improved.databinding.ActivityRedomatAdminBinding;
import com.redomat.improved.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity{

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
}
