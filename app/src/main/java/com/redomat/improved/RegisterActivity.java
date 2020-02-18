package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.redomat.improved.databinding.ActivityRegisterBinding;
import com.redomat.improved.pojo.Account;

public class RegisterActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    //View Binding
    private ActivityRegisterBinding mBiding;
    //--------------------------------------

    //Initializing activity variables
    private TextInputLayout regInputName;
    private TextInputLayout regInputLastName;
    private TextInputLayout regInputEmail;
    private TextInputLayout regInputPassword;

    private Button regBtnRegister;
    //--------------------------------

    //Progress dialog
    ProgressDialog progressDialog;
    //-------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBiding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View registerView = mBiding.getRoot();
        setContentView(registerView);

        //Initializing firebase stuff
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        //Activity input fields
        regInputName = mBiding.regInputName;
        regInputLastName = mBiding.regInputLastName;
        regInputEmail = mBiding.regInputEmail;
        regInputPassword = mBiding.regInputPassword;

        //Buttons
        regBtnRegister = mBiding.regBtnRegister;

        //CODE

        regBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();

                if(validateInputs()){
                    Account account = new Account(String.valueOf(regInputName.getEditText().getText()), String.valueOf(regInputLastName.getEditText().getText()), String.valueOf(regInputEmail.getEditText().getText()), String.valueOf(regInputPassword.getEditText().getText()));

                    mAuth.createUserWithEmailAndPassword(account.getEmail(), account.getPassword()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, getString(R.string.regInputEmailVertificationSent), Toast.LENGTH_LONG).show();
                                            closeProgressDialog();
                                            finish();
                                        } else {
                                            Log.d("regVertificationError", task.getException().getMessage());
                                            closeProgressDialog();
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("regVertificationError", task.getException().getMessage());
                                closeProgressDialog();
                            }
                        }
                    });
                }
            }
        });
    }

    //Function to go back to Login Acitivity - Used in XML as a link
    public void openLoginActivity(View v){
        finish();
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

    //Check if the information for the registration is all good
    public boolean validateInputs(){
        //Check if name is entered
        if(String.valueOf(regInputName.getEditText().getText()).isEmpty()){
            regInputName.setError(getString(R.string.regInputErrorName));
        } else {
            regInputName.setError(null);
        }

        //Check if lastname is entered
        if(String.valueOf(regInputLastName.getEditText().getText()).isEmpty()){
            regInputLastName.setError(getString(R.string.regInputErrorLastName));
        } else {
            regInputLastName.setError(null);
        }

        //Check if email is entered and if it is entered correctly
        validateEmailNoReturn(String.valueOf(regInputEmail.getEditText().getText()).trim());

        //Check if password is entered and if it is longer than 5 characters
        validatePasswordNoReturn(regInputPassword.getEditText().getText().toString().trim());

        //If any of the following is not entered or if the information is invalid close progress dialog and do not continue
        if( String.valueOf(regInputName.getEditText().getText()).isEmpty() || String.valueOf(regInputLastName.getEditText().getText()).isEmpty() || validatePassword(String.valueOf(regInputPassword.getEditText().getText()).trim()) || validateEmail(String.valueOf(regInputEmail.getEditText().getText()).trim())){
            closeProgressDialog();
            return false;
        }

        return true;
    }

    //Check if email is entered and if so, check if it is entered correctly and return the output
    public boolean validateEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.isEmpty()) {
            return true;
        }else {
            if (!email.trim().matches(emailPattern)) {
                return true;
            } else {
                return false;
            }
        }
    }

    //Check if email is entered and and display error code if it's not, or if it's not entered correctly
    public void validateEmailNoReturn(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.isEmpty()) {
            regInputEmail.setError(getString(R.string.regInputErrorEmail));
        }else {
            if (!email.trim().matches(emailPattern)) {
                regInputEmail.setError(getString(R.string.regInputErrorEmailValid));
            } else {
                regInputEmail.setError(null);
            }
        }
    }

    //Check if password is entered and if so, check if it is entered correctly and return the output
    public void validatePasswordNoReturn(String password){
        if(password.isEmpty()){
            regInputPassword.setError(getString(R.string.regInputErrorPassword));
        } else {
            if(password.length() < 6){
                regInputPassword.setError(getString(R.string.regInputErrorPasswordLength));
            } else {
                regInputPassword.setError(null);
            }
        }
    }

    //Check if password is entered and and display error code if it's not, or if it's not entered correctly
    public boolean validatePassword(String password){
        if(password.isEmpty()){
            return true;
        } else {
            if(password.length() < 6){
                return true;
            } else {
                return false;
            }
        }
    }


}
