package com.example.xsmil.grouper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import models.projectGroup;
import models.user;

public class CreateGroupActivity extends AppCompatActivity {

    DatabaseReference db;
    DatabaseReference projectGroupRef;
    DatabaseReference userRef;
    FirebaseAuth firebaseAuth;

    Button back;
    Button create;

    private String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        db = FirebaseDatabase.getInstance().getReference();
        projectGroupRef = db.child("projectGroups");
        firebaseAuth = FirebaseAuth.getInstance();
        userRef = db.child("users");

        back = (Button) findViewById(R.id.btn_back);
        create = (Button) findViewById(R.id.btn_create);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain();
            }
        });

        // Pressing create creates new project group and saves information to database
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewProjGroup();;

                // Tried to start implementing functionality for updating group details
                /*if (TextUtils.isEmpty(groupID)) {
                    createNewProjGroup();
                }
                else {
                    updateProjGroup();
                }*/
            }
        });

//        toggleButton();

        // Internal outputs to check that information is being read from the database correctly
        projectGroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                projectGroup newProjGroup = dataSnapshot.getValue(projectGroup.class);
                String groupID = dataSnapshot.getKey();
                System.out.println("Group ID: " + newProjGroup.groupID);
                System.out.println("Course:" + newProjGroup.course);
                System.out.println("Team Name: " + newProjGroup.teamName);
                System.out.println("Project Title: " + newProjGroup.projectTitle);
                System.out.println("Project Deadline: " + newProjGroup.projectDeadline);
//                System.out.println("Deadline to form group: " + newProjGroup.formGroupDeadline);
                System.out.println("Description: " + newProjGroup.descript);
                System.out.println("Members: " + newProjGroup.members);
                System.out.println("Max Capacity: " + newProjGroup.maxCapacity);
                System.out.println("Admin: " + newProjGroup.admin);
                Log.d("Added","new project group with groupID: " + groupID);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d("Changed", "project group with groupID: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Removed","project group with groupID: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d("Moved","project group with groupID: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Cancel:",databaseError.toString());
            }
        });

    }

    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // change the button text between save and update
 /*   private void toggleButton() {
        save = (Button) findViewById(R.id.btn_save);

        if (TextUtils.isEmpty(groupID)) {
            save.setText(getResources().getString(R.string.save));
        }
        else {
            save.setText(getResources().getString(R.string.update));
        }
    }*/

    public void createNewProjGroup() {
        // Create new Project Group at /projectGroups with random key generation
        final String groupID = projectGroupRef.push().getKey();

        final EditText projectNameInput = (EditText) findViewById(R.id.proj_name);
        final EditText courseInput = (EditText) findViewById(R.id.etCourse);
        final EditText teamNameInput = (EditText) findViewById(R.id.etTeamName);
        final EditText projectDeadlineInput = (EditText) findViewById(R.id.proj_deadline_input);
//        final EditText groupFormDeadlineInput = (EditText) findViewById(R.id.form_group_deadline);
        final EditText descriptionInput = (EditText) findViewById(R.id.etDescription);
        final EditText maxCapacityInput = (EditText) findViewById(R.id.max_capacity_input);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.createProjectGroup);

        alertDialogBuilder
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // does nothing
                    }
                })
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // get user input and set it to result
                        String projectName = projectNameInput.getText().toString();
                        String course = courseInput.getText().toString();
                        String teamName = teamNameInput.getText().toString();
                        String projectDeadlineString = projectDeadlineInput.getText().toString();
//                        String groupFormDeadlineString = groupFormDeadlineInput.getText().toString();
                        String description = descriptionInput.getText().toString();
                        String maxCapacity = maxCapacityInput.getText().toString();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//                        LocalDate groupFormDeadline = LocalDate.parse(groupFormDeadlineString, formatter);

                        // checks if project name or course are empty/null, then sets a default one
                        if (TextUtils.isEmpty(projectName)) {
                            projectName = "project name";
                        }

                        if (TextUtils.isEmpty(course)) {
                            course = "course";
                        }

                        // checks to make sure date is in the correct format
                        try {
                            LocalDate projectDeadline = LocalDate.parse(projectDeadlineString, formatter);
                            Log.i("",projectDeadlineString+" is in valid date format");
                        } catch (DateTimeParseException e) {
                            Log.i("",projectDeadlineString+" is not in valid date format");
                            Toast.makeText(getApplicationContext(), "Please enter the date with the format mm/dd/yyyy.", Toast.LENGTH_SHORT).show();
                        }

                        // checks to make sure maximum capacity is a number entered.
                        try {
                            int num = Integer.parseInt(maxCapacity);
                            Log.i("",num+" is a number");
                        } catch (NumberFormatException e) {
                            Log.i("",maxCapacity+" is not a number");
                            Toast.makeText(getApplicationContext(), "Please enter a number for max capacity", Toast.LENGTH_SHORT).show();
                        }

                        String currentUid = firebaseAuth.getCurrentUser().getUid();

                        // creates new project group with user inputs, adds the user to the group, and adds the group to the database at /projectGroups
                        projectGroup projGroup = new projectGroup(course, teamName, projectName, LocalDate.parse(projectDeadlineString, formatter), description, Integer.parseInt(maxCapacity),currentUid);
                        projGroup.setGroupID(groupID);
                        projGroup.addMember(currentUid);
                        Map<String, Object> projectGroupValues = projGroup.toMap();

                        projectGroupRef.child(groupID).setValue(projectGroupValues);

                        addGroupToUserInDB(groupID);

                        confirmationAlert();
                        goToMain();
                    }
                }).create()
                .show();


        /*Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/projectGroups/" + groupID, projectGroupValues);
        db.updateChildren(childUpdates);*/
    }

    public void confirmationAlert() {
        // Dialog pops up to allow user to confirm group has been successfully created
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.confirmationAlert);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        // does nothing
                    }
                }).create()
                .show();
    }

    // when a group is created, the user who created the group needs to have his/her node updated in the database
    // similarly, we update the projGroupMembers node in the database (this node isn't currently being utilized,
    // but was implemented as part of the data structure to allow for quicker querying/enhance scalability)
    public void addGroupToUserInDB(String groupID) {

        final String currentUid = firebaseAuth.getCurrentUser().getUid();
        final String groupIDFinal = groupID;

        userRef.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // reads each user object at each userID node under /users
                user currentUser = dataSnapshot.getValue(user.class);
                currentUser.addGroup(groupIDFinal);

                Map<String, Object> updatedUserValues = currentUser.toMap();
                // inserts the updated user object into the current userID node under /users in the database
                userRef.child(currentUid).setValue(updatedUserValues);

                DatabaseReference projGroupMembersRef = db.child("projGroupMembers");
                projGroupMembersRef.child(groupIDFinal).child(currentUid).setValue(updatedUserValues);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Cancel: ", "onCancelled", databaseError.toException());
            }
        });
    }

/*    public void updateProjGroup() {
        // Updates Project Group at groupID

        final EditText projectNameInput = (EditText) findViewById(R.id.proj_name);
        final EditText courseInput = (EditText) findViewById(R.id.course);
        final EditText teamNameInput = (EditText) findViewById(R.id.team_name);
        final EditText projectDeadlineInput = (EditText) findViewById(R.id.proj_deadline);
        final EditText groupFormDeadlineInput = (EditText) findViewById(R.id.form_group_deadline);
        final EditText descriptionInput = (EditText) findViewById(R.id.description);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String projectName = projectNameInput.getText().toString();
                String course = courseInput.getText().toString();
                String teamName = teamNameInput.getText().toString();
                String projectDeadlineString = projectDeadlineInput.getText().toString();
                String groupFormDeadlineString = groupFormDeadlineInput.getText().toString();
                String description = descriptionInput.getText().toString();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // this needs to match the pattern of how it's entered in the string...may need to update depending on front-end
                LocalDate projectDeadline = LocalDate.parse(projectDeadlineString, formatter);
                LocalDate groupFormDeadline = LocalDate.parse(groupFormDeadlineString, formatter);

                if (!TextUtils.isEmpty(projectName))
                    projectGroupRef.child(groupID).child("projectTitle").setValue(projectName);

                if (!TextUtils.isEmpty(course))
                    projectGroupRef.child(groupID).child("course").setValue(course);

                if (!TextUtils.isEmpty(teamName))
                    projectGroupRef.child(groupID).child("teamName").setValue(teamName);

                if (!TextUtils.isEmpty(projectDeadlineString))
                    projectGroupRef.child(groupID).child("projectDeadline").setValue(projectDeadline);

                if (!TextUtils.isEmpty(groupFormDeadlineString))
                    projectGroupRef.child(groupID).child("formGroupDeadline").setValue(groupFormDeadline);

                if (!TextUtils.isEmpty(description))
                    projectGroupRef.child(groupID).child("description").setValue(description);

            }
        });
    }*/
}