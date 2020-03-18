package com.redomat.improved;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redomat.improved.databinding.DialogStatisticsBinding;

public class StatisticsDialog extends DialogFragment {

    //View binding
    private DialogStatisticsBinding diagStatBinding;

    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference currentUserRef = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());

    //TextView
    private TextView statDialogNumOfMadeRedomatsValue;
    private TextView statDialogNumOfParticipatedRedomatsValue;
    private TextView statDialogNumOfLeftRedomatsValue;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        diagStatBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_statistics, null, false);
        builder.setView(diagStatBinding.getRoot());

        //Initialize TextView elements
        statDialogNumOfMadeRedomatsValue = diagStatBinding.statDialogNumOfMadeRedomatsValue;

        statDialogNumOfParticipatedRedomatsValue = diagStatBinding.statDialogNumOfParticipatedRedomatsValue;

        statDialogNumOfLeftRedomatsValue = diagStatBinding.statDialogNumOfLeftRedomatsValue;


        //Apply loading message
        statDialogNumOfMadeRedomatsValue.setText(getString(R.string.statDialogLoading));

        statDialogNumOfParticipatedRedomatsValue.setText(getString(R.string.statDialogLoading));

        statDialogNumOfLeftRedomatsValue.setText(getString(R.string.statDialogLoading));


        builder.setTitle(getString(R.string.statDialogTitle))
                .setNegativeButton(getString(R.string.statDialogCloseDialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });

                currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        statDialogNumOfMadeRedomatsValue.setText(dataSnapshot.child("numOfMadeRedomats").getValue().toString());
                        statDialogNumOfParticipatedRedomatsValue.setText(dataSnapshot.child("numOfParticipatedRedomats").getValue().toString());
                        statDialogNumOfLeftRedomatsValue.setText(dataSnapshot.child("numOfLeftRedomats").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return builder.create();
    }
}
