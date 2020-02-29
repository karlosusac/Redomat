package com.redomat.improved;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends PreferenceFragment {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    //---------------------------------------------------------------------------------------------------------------

    //ChangeNameAndLastname
        //Firebase stuff
        private DatabaseReference currentUser = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());

        //Initialize dialog variables

        //TextInputLayout
        private TextInputLayout stgChngNameLastnameInputName;
        private TextInputLayout stgChngNameLastnameInputLastname;

        //TextInputEditText
        private TextInputEditText stgChngNameLastnameInputNameValue;
        private TextInputEditText stgChngNameLastnameInputLastnameValue;

        //Buttons
        private Button stgChngNameLastnameBtnConfirm;
        //-----------------------------------------

    //ChangePassword
        //Firebase stuff
        private FirebaseUser setChngPassCurrentUser = mAuth.getCurrentUser();

        //TextInputLayout
        private TextInputLayout settingsDialogChangePassNewPass;
        private TextInputLayout settingsDialogChangePassOldPass;
        private TextInputLayout settingsDialogChangePassConNewPass;


        //TextInputEditText
        private TextInputEditText stgChngPassOldPass;
        private TextInputEditText stgChngPassNewPass;
        private TextInputEditText stgChngPassConNewPass;

        private Button getStgChngNameLastnameBtnConfirm;
        //------------------------------

        //ProgressBar
        private ProgressBar settingsDialogChangePassProgressBar;
        //-----------------------------------------------------------

    //-----------------------------------------

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if(key.equals("settingsActivityPrefNameLastName")){
            Log.d("Ovo", "Zašto uđe");
            makeSettingsChangeNameAndLastnameDialog();

            return true;
        }

        if(key.equals("settingsActivityPrefChngPass")){
            makeSettingsChangePasswordDialog();

            return true;
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.activity_settings);


    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    //ChangeNameAndLastnameDialog

        private void listenerForUserNameAndLastname(){
            currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    stgChngNameLastnameInputNameValue.setText(String.valueOf(dataSnapshot.child("firstName").getValue()));
                    stgChngNameLastnameInputLastnameValue.setText(String.valueOf(dataSnapshot.child("lastName").getValue()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void makeSettingsChangeNameAndLastnameDialog(){
            final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();

            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog_settings_change_name_and_lastname, null);

            builder.setView(view);


            //TextInputEditText
            stgChngNameLastnameInputNameValue = view.findViewById(R.id.settingsDialogChangeNameLastnameInputNameValue);
            stgChngNameLastnameInputLastnameValue = view.findViewById(R.id.settingsDialogChangeNameLastnameInputLastnameValue);

            //Buttons
            stgChngNameLastnameBtnConfirm = view.findViewById(R.id.settingsDialogChangeNameLastnameBtnConfrim);

            //TextInputLayout
            stgChngNameLastnameInputName = view.findViewById(R.id.settingsDialogChangeNameLastnameInputName);
            stgChngNameLastnameInputLastname = view.findViewById(R.id.settingsDialogChangeNameLastnameInputLastname);
            //-------------------------------------------

            listenerForUserNameAndLastname();

            builder.setTitle(getString(R.string.settingsChngNameAndLastnameTitle));
            builder.setButton(builder.BUTTON_NEGATIVE, getString(R.string.settingsChngNameAndLastnameCancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    builder.dismiss();
                }
            });

            stgChngNameLastnameBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validateNameAndLastname()){
                        currentUser.child("firstName").setValue(stgChngNameLastnameInputNameValue.getText().toString());
                        currentUser.child("lastName").setValue(stgChngNameLastnameInputLastnameValue.getText().toString());

                        builder.dismiss();
                    }
                }
            });

            builder.show();
        }

        private boolean validateNameAndLastname(){
            boolean firstName = true;
            boolean lastName = true;

            if(stgChngNameLastnameInputNameValue.getText().toString().isEmpty()){
                stgChngNameLastnameInputName.setError(getString(R.string.settingsChngNameAndLastnameInputErrorEmpty));
                firstName = false;
            } else {
                stgChngNameLastnameInputName.setError(null);
            }

            if(stgChngNameLastnameInputLastnameValue.getText().toString().isEmpty()){
                stgChngNameLastnameInputLastname.setError(getString(R.string.settingsChngNameAndLastnameInputErrorEmpty));
                lastName = false;
            } else {
                stgChngNameLastnameInputLastname.setError(null);
            }

            if(firstName == false || lastName == false){
                return false;
            }

            return true;
        }
    //-------------------------------------------------------------------------------------------------------------------



    //ChangePassword
    private void makeSettingsChangePasswordDialog(){
        final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_settings_change_password, null);

        builder.setView(view);

        //TextInputEditText
        stgChngPassOldPass = view.findViewById(R.id.settingsDialogChangePassOldPassValue);
        stgChngPassNewPass = view.findViewById(R.id.settingsDialogChangePassNewPassValue);
        stgChngPassConNewPass = view.findViewById(R.id.settingsDialogChangePassConNewPassValue);

        //TextInputLayout
        settingsDialogChangePassNewPass = view.findViewById(R.id.settingsDialogChangePassNewPass);
        settingsDialogChangePassOldPass = view.findViewById(R.id.settingsDialogChangePassOldPass);
        settingsDialogChangePassConNewPass = view.findViewById(R.id.settingsDialogChangePassConNewPass);
        //----------------------------------------------------

        //Button
        getStgChngNameLastnameBtnConfirm = view.findViewById(R.id.settingsDialogChangePassBtnConfrim);
        //-------------------------------------------

        //ProgressBar
        settingsDialogChangePassProgressBar = view.findViewById(R.id.settingsDialogChangePassProgressBar);
        //---------------------------------------

        builder.setTitle("Promjenite lozinku");
        builder.setMessage("Polje za unos lozike ne smije biti prazno, te lozinka mora imati minimalno 6 znakova.");
        builder.setButton(builder.BUTTON_NEGATIVE, getString(R.string.settingsChngNameAndLastnameCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });

        //Set progressBar to be invisible
        settingsDialogChangePassProgressBar.setVisibility(View.GONE);

        getStgChngNameLastnameBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialogChangePassProgressBar.setVisibility(View.VISIBLE);

                if(validatePasswords()){
                    if(String.valueOf(stgChngPassNewPass.getText()).trim().equals(String.valueOf(stgChngPassConNewPass.getText()).trim())){
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(setChngPassCurrentUser.getEmail(), String.valueOf(stgChngPassOldPass.getText()).trim());

                        // Prompt the user to re-provide their sign-in credentials
                        setChngPassCurrentUser.reauthenticate(credential)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        setChngPassCurrentUser.updatePassword(String.valueOf(stgChngPassNewPass.getText()).trim())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("Ovo", String.valueOf(stgChngPassNewPass.getText()).trim());
                                                            builder.dismiss();
                                                            settingsDialogChangePassProgressBar.setVisibility(View.GONE);
                                                            Toast.makeText(getActivity(), "Lozinka uspiješno promjenuta", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })

                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        settingsDialogChangePassProgressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        settingsDialogChangePassOldPass.setError("Neispravna lozinka");
                                        settingsDialogChangePassProgressBar.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        settingsDialogChangePassConNewPass.setError("Nove lozinke se ne podudaraju");
                        settingsDialogChangePassProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });


        builder.show();
    }
    //------------------------------

    private boolean validatePasswords(){
        boolean oldPassword = false;
        boolean newPassword = false;
        boolean conNewPassword = false;

        if(validatePassword(String.valueOf(stgChngPassOldPass.getText()).trim(), settingsDialogChangePassOldPass)){
            oldPassword = true;
        }

        if(validatePassword(String.valueOf(stgChngPassNewPass.getText()).trim(), settingsDialogChangePassNewPass)){
            newPassword = true;
        }

        if(validatePassword(String.valueOf(stgChngPassConNewPass.getText()).trim(), settingsDialogChangePassConNewPass)){
            conNewPassword = true;
        }



        if(oldPassword && newPassword && conNewPassword){
            return true;
        } else {
            return false;
        }
    }

    private boolean validatePassword(String password, TextInputLayout errorField){
        if(!password.isEmpty()){
            if(password.length() >= 6){
                errorField.setError(null);
                return true;
            } else {
                errorField.setError("Prekratka lozinka");
            }
        } else {
            errorField.setError("Polje za unos lozinke je prazno");
        }

        return false;
    }
}
