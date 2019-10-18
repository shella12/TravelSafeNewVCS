package com.example.dell.travelsafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private RelativeLayout rLayout;
    private Animation animation;

    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=(Toolbar)findViewById(R.id.bgHeader);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rLayout=(RelativeLayout)findViewById(R.id.rlayout);

        animation=AnimationUtils.loadAnimation(this,R.anim.uptodowndigonal);
        rLayout.setAnimation(animation);


        CreateAccountButton=(Button)findViewById(R.id.register_btn);
        InputName=(EditText)findViewById(R.id.register_username_input);
        InputPhoneNumber=(EditText)findViewById(R.id.register_phone_number_input);
        InputPassword=(EditText)findViewById(R.id.register_password_input);
        loadingBar=new ProgressDialog(this);
        CreateAccountButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });



    }

    private void CreateAccount() {
        String name=InputName.getText().toString();
        String phone_number=InputPhoneNumber.getText().toString();
        String password=InputPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"please provide your name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone_number)){
            Toast.makeText(this,"please provide your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please write a password",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait while we are checking the credantials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validateData(name,phone_number,password);

        }

    }

    private void validateData(final String name, final String phone_number, final String password) {
        final DatabaseReference rootRef;
        rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone_number).exists())){
                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("phone",phone_number);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                    rootRef.child("Users").child(phone_number).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Congradulations your account has been created",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this,"Check your internet connection",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

                else{
                    Toast.makeText(RegisterActivity.this,"This " + phone_number + " already exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Try again with some other number",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
