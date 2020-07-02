package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import com.redomat.improved.pojo.AccountLine;
import com.redomat.improved.pojo.BroadcastReciever;
import com.redomat.improved.pojo.ProgressBar;

import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class LoginActivity extends AppCompatActivity implements EnterNewRedomatDialog.EnterAnNewRedomatListener {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference enterRedomatRef;

    //View Binding
    private ActivityLoginBinding mBiding;
    //---------------------------------

    //Broadcast reciever
    BroadcastReceiver broadcastReceiver = new BroadcastReciever();
    

    //Initializing activity variables
    private TextInputLayout logInputEmail;
    private TextInputLayout logInputPass;

    private Button logBtnLogin;
    private Button logBtnEnterALine;
    //--------------------------------

    //CODE
    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);

        // Check if user is already signed in, if so just transfer him to MainMenuActivity
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            openMainMenu(currentUser);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBiding = ActivityLoginBinding.inflate(getLayoutInflater());
        View loginView = mBiding.getRoot();
        setContentView(loginView);

        //Layout elements
        //EditText
        logInputEmail = mBiding.loginInputEmail;
        logInputPass = mBiding.loginInputPassword;

        //Buttons
        logBtnLogin = mBiding.loginBtnLogin;
        logBtnEnterALine = mBiding.loginBtnEnterALine;

        logBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logInputEmail.getEditText().getText().toString().isEmpty() && !logInputPass.getEditText().getText().toString().isEmpty()){
                    ProgressBar.showProgressDialog(LoginActivity.this);

                    mAuth.signInWithEmailAndPassword(logInputEmail.getEditText().getText().toString(), logInputPass.getEditText().getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        if(mAuth.getCurrentUser().isEmailVerified()){
                                            openMainMenu(mAuth.getCurrentUser());
                                        } else {
                                            ProgressBar.closeProgressDialog();
                                            Toast.makeText(LoginActivity.this, getString(R.string.loginVertifyEmail), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        ProgressBar.closeProgressDialog();
                                        logInputEmail.setError(" ");
                                        logInputPass.setError(getString(R.string.loginWrongEmailAddress));
                                    }
                                }
                            });
                } else {
                    logInputEmail.setError(" ");
                    logInputPass.setError(getString(R.string.loginErrorEmptyInputs));
                }
            }
        });

        logBtnEnterALine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterNewRedomatDialog enterNewRedomatDialog = new EnterNewRedomatDialog();
                enterNewRedomatDialog.show(getSupportFragmentManager(), "enter_redomat");
            }
        });
    }

    //CUSTOM METHODS
    //If requestCode is from RegisterActivity and it says ok close ProgressDialog
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If RegisterActivity has finished, close it and
        if(requestCode == 0 && resultCode == RESULT_OK){
            ProgressBar.closeProgressDialog();
        }
    }

    //If user exists, open Main Menu with that user
    public void  openMainMenu(FirebaseUser user){
        if(user != null){
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        }
    }

    //Method to go to the Register Acitivity - Used in XML as a link
    public void openRegisterActivity(View v){
        ProgressBar.showProgressDialog(LoginActivity.this);

        Intent newIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(newIntent, 0);
        //startActivity(newIntent);
    }

    //Method to go to the ForgotPassword Dialog - Used in XML as a link
    public void openForgotPasswordDialog(View view){
        ForgotPasswordDialog frgtPassDialog = new ForgotPasswordDialog();
        frgtPassDialog.show(getSupportFragmentManager(), "forgot_password_dialog");
    }

    @Override
    public void enterAnNewRedomat(long myRedomatPosition, String pin) {
        showProgressDialog(LoginActivity.this);
        enterRedomatRef = db.getReference("Redomats").child(pin);
        AccountLine newUser = new AccountLine(enterRedomatRef.getKey());

        Intent i = new Intent(this, RedomatUserUnregisteredActivity.class);

        i.putExtra("enteredUserPosition", String.valueOf(myRedomatPosition));
        i.putExtra("pin", pin);

        startActivity(i);
    }

    @Override
    public void onLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped();

        unregisterReceiver(broadcastReceiver);
    }
}
