package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.redomat.improved.databinding.ActivityLoginBinding;
import com.redomat.improved.databinding.ActivityMainMenuBinding;

public class MainMenuActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //View Binding
    private ActivityMainMenuBinding mBiding;
    //---------------------------------


    //Progress dialog
    ProgressDialog progressDialog;
    //-------------

    //CODE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBiding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        View mainMenuView = mBiding.getRoot();
        setContentView(mainMenuView);
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
                showProgressDialog();

                mAuth = FirebaseAuth.getInstance();
                mAuth.getInstance().signOut();

                Intent i = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // '\'Options menu -----------------------------------------------------------------------------


    //CUSTOM METHODS

    //Make new Progress dialog for loading screen
    public void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    //Close progress dialog
    public void closeProgressDialog(){
        progressDialog.dismiss();
    }
}
