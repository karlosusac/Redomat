package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redomat.improved.pojo.Account;

public class LoginActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //Initializing activity variables

    //--------------------------------


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in, if so just transfer him to MainMenuActivity
        //currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText logInputEmail = findViewById(R.id.loginInputEmail);
        EditText logInputPass = findViewById(R.id.loginInputPassword);

        Button logBtnLogin = findViewById(R.id.loginBtnLogin);
        Button logBtnEnterALine = findViewById(R.id.loginBtnEnterALine);

    }


    //Functions
    public void  updateUI(FirebaseUser user){
        if(user != null){
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        }
    }
}
