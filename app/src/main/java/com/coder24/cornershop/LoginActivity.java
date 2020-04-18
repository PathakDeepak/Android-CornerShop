package com.coder24.cornershop;
/* *
   Coding cafe lecture-15
   */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.coder24.cornershop.model.Users;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coder24.cornershop.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhoneNumber, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private String parentDBName = "Users";
    private CheckBox chkBoxRememberMe;
    private TextView admin, notAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_btn);
        inputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        inputPassword = (EditText) findViewById(R.id.login_password_input);
        admin = (TextView) findViewById(R.id.admin_panel_link);
        notAdmin = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        //Checkbox handle for Remember Me Button user Paper plugin,
        // so whenever app starts if remember me is checked previously then user doesn't need to login again
        // App will start from HomeActivity rather than MainActivity. "PLEASE CHECK MAINACTIVITY"
        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);//to initialize paper
        //now goto allowAccessToAccount function
        
        
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
            }
        });
        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText(" Login");
                admin.setVisibility(View.VISIBLE);
                notAdmin.setVisibility(View.INVISIBLE);
                parentDBName = "Users";
            }
        });
    }

    private void loginUser() {
        String phoneNumber = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please Enter Your Phone Number!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password!", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phoneNumber, password);
        }
    }

    private void allowAccessToAccount(final String phoneNumber, final String password) {

        if(chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey,phoneNumber);
            Paper.book().write(Prevalent.userPasswordKey,password);
        }
        final DatabaseReference rootReference;
        rootReference = FirebaseDatabase.getInstance().getReference();

        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child(parentDBName).child(phoneNumber).exists())){
                    Users users = dataSnapshot.child(parentDBName).child(phoneNumber).getValue(Users.class);
                    if(users.getPhoneNumber().equals(phoneNumber)){
                        if(users.getPassword().equals(password)){
                            if(parentDBName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are logged in Successfully!....", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDBName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Logged in Successfully!....", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                //to get user data for HomeActivity
                                Prevalent.currentOnlineUsers = users;
                                startActivity(intent);
                            }
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phoneNumber + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
