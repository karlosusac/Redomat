package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redomat.improved.databinding.ActivityLoginBinding;
import com.redomat.improved.pojo.Account;

public class LoginActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //View Binding
    private ActivityLoginBinding mBiding;
    //---------------------------------
    

    //Initializing activity variables
    private TextInputLayout logInputEmail;
    private TextInputLayout logInputPass;

    private Button logBtnLogin;
    private Button logBtnEnterALine;
    //--------------------------------

    //Progress dialog
    ProgressDialog progressDialog;
    //-------------


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
        mBiding = ActivityLoginBinding.inflate(getLayoutInflater());
        View loginView = mBiding.getRoot();
        setContentView(loginView);

        //mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //Layout elements
        //EditText
        logInputEmail = mBiding.loginInputEmail;
        logInputPass = mBiding.loginInputPassword;

        //Buttons
        logBtnLogin = mBiding.loginBtnLogin;
        logBtnEnterALine = mBiding.loginBtnEnterALine;

        /*
        final TextInputLayout logInputEmail = mBinding.loginInputEmail;

        final Button logBtnLogin = mBinding.loginBtnLogin;

        logBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, logInputEmail.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

         */
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        closeProgressDialog();

    }

    //Functions
    public void  updateUI(FirebaseUser user){
        if(user != null){
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        }
    }

    //Function to go to Register Acitivity - Used in XML as a link
    public void openRegisterActivity(View v){
        showProgressDialog();

        Intent newIntent = new Intent(this, RegisterActivity.class);
        startActivity(newIntent);
    }

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
