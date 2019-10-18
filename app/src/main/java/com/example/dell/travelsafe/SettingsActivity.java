package com.example.dell.travelsafe;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.usage.StorageStats;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.travelsafe.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn;
    private EditText fullNameEditText, userPhoneEditText, emergencyPhoneEditText, bloodGroupEditText, vehicleNumberEditText, allergiesEditText, surgeriesEditText;
    private Button addContactBtn;

    private Uri imageUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker="";

    public  static final int RequestPermissionCode  = 1 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef=FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView=(CircleImageView)findViewById(R.id.settings_profile_image);
        profileChangeTextBtn=(TextView)findViewById(R.id.profile_image_change_btn);
        closeTextBtn=(TextView)findViewById(R.id.close_settings_btn);
        saveTextBtn=(TextView)findViewById(R.id.update_account_settings_btn);
        fullNameEditText=(EditText)findViewById(R.id.settings_full_name);
        userPhoneEditText=(EditText)findViewById(R.id.settings_phone_number);
        emergencyPhoneEditText=(EditText)findViewById(R.id.settings_emergency_phone_number);
        bloodGroupEditText=(EditText)findViewById(R.id.settings_blood_group);
        vehicleNumberEditText=(EditText)findViewById(R.id.settings_vehicle_number);
        allergiesEditText=(EditText)findViewById(R.id.settings_allergies);
        surgeriesEditText=(EditText)findViewById(R.id.settings_surgeries);
        addContactBtn=(Button)findViewById(R.id.settings_emergency_phone_number_btn);


        EnableRuntimePermission();
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 7);

            }
        });


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }
        });

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, emergencyPhoneEditText, bloodGroupEditText, vehicleNumberEditText, allergiesEditText, surgeriesEditText);
    }

    //TODO: Emergency Number Picker
    private void EnableRuntimePermission() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                android.Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(SettingsActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(SettingsActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }

    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(SettingsActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(SettingsActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


    private void updateOnlyUserInfo() {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("phoneOwner",userPhoneEditText.getText().toString());
        userMap.put("emergencyno",emergencyPhoneEditText.getText().toString());
        userMap.put("vehicleNo",vehicleNumberEditText.getText().toString());
        userMap.put("bloodgrp",bloodGroupEditText.getText().toString());
        userMap.put("allergies",allergiesEditText.getText().toString());
        userMap.put("surgeries",surgeriesEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this,"Profile updated Successfully",Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            profileImageView.setImageURI(imageUri);
        }


        //Code for Emergency contact btn


        else if(requestCode==7){
            if (resultCode == Activity.RESULT_OK) {

                Uri uri;
                Cursor cursor1, cursor2;
                String TempNumberHolder, TempContactID, IDresult = "" ;
                int IDresultHolder ;

                uri = data.getData();

                cursor1 = getContentResolver().query(uri, null, null, null, null);

                if (cursor1.moveToFirst()) {

                    TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                    IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    IDresultHolder = Integer.valueOf(IDresult) ;

                    if (IDresultHolder == 1) {

                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                        while (cursor2.moveToNext()) {

                            TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            emergencyPhoneEditText.setText(TempNumberHolder);

                        }
                    }

                }
            }
        }

        else {
            Toast.makeText(SettingsActivity.this,"Erroe, Try Again",Toast.LENGTH_LONG).show();
            startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
            finish();

        }

    }



    private void userInfoSaved() {

        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Name is Mandatory",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Phone is Mandatory",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(emergencyPhoneEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Emergency Phone is Mandatory",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(bloodGroupEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Blood Group is Mandatory for improved services",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(allergiesEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Allergies are Mandatory for improved services",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(surgeriesEditText.getText().toString())){
            Toast.makeText(SettingsActivity.this,"Surgerical Info is Mandatory for improved services",Toast.LENGTH_LONG).show();
        }
        else if(checker.equals("clicked")){
            uploadImage();
        }


    }

    private void uploadImage() {

        final ProgressDialog loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Update Profile");
        loadingBar.setMessage("Please Wait, While updating account");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if(imageUri !=null){
            final StorageReference fileRef=storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask =fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        myUrl=downloadUri.toString();


                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",fullNameEditText.getText().toString());
                        userMap.put("phoneOwner",userPhoneEditText.getText().toString());
                        userMap.put("emergencyno",emergencyPhoneEditText.getText().toString());
                        userMap.put("vehicleNo",vehicleNumberEditText.getText().toString());
                        userMap.put("bloodgrp",bloodGroupEditText.getText().toString());
                        userMap.put("allergies",allergiesEditText.getText().toString());
                        userMap.put("surgeries",surgeriesEditText.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        loadingBar.dismiss();
                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this,"Profile updated Successfully",Toast.LENGTH_LONG).show();
                        finish();

                    }

                    else {
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

        else {
            Toast.makeText(SettingsActivity.this,"Image Not selected",Toast.LENGTH_LONG).show();

        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText emergencyPhoneEditText, final EditText bloodGroupEditText, final EditText vehicleNumberEditText, final EditText allergiesEditText, final EditText surgeriesEditText) {

        DatabaseReference UserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phoneOwner").getValue().toString();
                        String emergencyNumber=dataSnapshot.child("emergencyno").getValue().toString();
                        String vehicleNumber=dataSnapshot.child("vehicleNo").getValue().toString();
                        String bloodgrp=dataSnapshot.child("bloodgrp").getValue().toString();
                        String allergies=dataSnapshot.child("allergies").getValue().toString();
                        String surgeries=dataSnapshot.child("surgeries").getValue().toString();


                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        emergencyPhoneEditText.setText(emergencyNumber);
                        vehicleNumberEditText.setText(vehicleNumber);
                        bloodGroupEditText.setText(bloodgrp);
                        allergiesEditText.setText(allergies);
                        surgeriesEditText.setText(surgeries);

                    }
                    else if(dataSnapshot.child("phone").exists()){
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();

                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
