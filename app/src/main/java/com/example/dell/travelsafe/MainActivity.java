package com.example.dell.travelsafe;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.travelsafe.Model.Users;
import com.example.dell.travelsafe.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton btnRegister;
    private TextView tvlogin;
    private EditText InputNumber,InputPassword;
    private Button LoginButton;
    private CheckBox chkBoxRemeberMe;
    private ProgressDialog loadingBar;

    private String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRegister=(ImageButton) findViewById(R.id.add_btn);
        tvlogin=(TextView)findViewById(R.id.tvLogin);

        InputNumber=(EditText)findViewById(R.id.etPhoneNumber);
        InputPassword=(EditText)findViewById(R.id.etPassword);
        LoginButton=(Button)findViewById(R.id.login_btn);
        chkBoxRemeberMe=(CheckBox)findViewById(R.id.remember_me_chkb);
        loadingBar=new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnRegister.setOnClickListener(this);

        Paper.init(this);

        checkRememberMe();
    }

    private void checkRememberMe() {
      String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
      String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey!=""&& UserPasswordKey!=""){
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                AllowAccesstoAccount(UserPhoneKey,UserPasswordKey);
                }
             }
            else {
            return;
             }
    }

    private void login() {

        String phone=InputNumber.getText().toString();
        String password=InputPassword.getText().toString();


        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"please provide your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please write a password",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credantials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            
            AllowAccesstoAccount(phone,password);
        }

    }

    private void AllowAccesstoAccount(final String phone, final String password) {

        if(chkBoxRemeberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference rootRef;
        rootRef=FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).child(phone).exists()){

                    Users userData=dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if(userData.getPhone().equals(phone)){
                        if(userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"Logged in successfully",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUser=userData;
                            startActivity(intent);

                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Incorrect Password",Toast.LENGTH_LONG).show();
                        }
                    }

                }
                else {
                    Toast.makeText(MainActivity.this,"Account with this " +phone+ "doesn't exist ",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this,"Please create a new account",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        if(v==btnRegister){
            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
            Pair[] pairs=new Pair[1];
            pairs[0]=new Pair<View, String>(tvlogin,"tvLogin");

            ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
            startActivity(intent,activityOptions.toBundle());
        }
    }
}

