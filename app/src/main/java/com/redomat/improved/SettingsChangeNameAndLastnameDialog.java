package com.redomat.improved;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redomat.improved.databinding.DialogSettingsChangeNameAndLastnameBinding;

public class SettingsChangeNameAndLastnameDialog extends DialogFragment {

    //View binding
    private DialogSettingsChangeNameAndLastnameBinding diagChngNaLBinding;

    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference currentUser = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());
    //---------------------------------------------------------------------------------------------------------------

    //Initialize dialog variables

    //TextInputLayout
    private TextInputEditText stgChngNameLastnameInputName;
    private TextInputEditText stgChngNameLastnameInputLastname;

    //Buttons
    private Button stgChngNameLastnameBtnConfirm;

    //-----------------------------------------


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        diagChngNaLBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_settings_change_name_and_lastname, null, false);
        builder.setView(diagChngNaLBinding.getRoot());

        //TextInputLayout
        stgChngNameLastnameInputName = diagChngNaLBinding.settingsDialogChangeNameLastnameInputNameValue;
        stgChngNameLastnameInputLastname = diagChngNaLBinding.settingsDialogChangeNameLastnameInputLastnameValue;

        //Buttons
        stgChngNameLastnameBtnConfirm = diagChngNaLBinding.settingsDialogChangeNameLastnameBtnConfrim;

        builder.setTitle(getString(R.string.settingsChngNameAndLastnameDialogTitle))
               .setNegativeButton(getString(R.string.settingsChngNameAndLastnameCancel), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //Do nothing
                   }
               });



        return builder.create();
    }


    private void listenerForUserNameAndLastname(){
        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stgChngNameLastnameInputName.setText(String.valueOf(dataSnapshot.child("firstName").getValue()));
                stgChngNameLastnameInputLastname.setText(String.valueOf(dataSnapshot.child("lastName").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
