package com.example.mugambiapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mugambiapplication.Model.Users;
import com.example.mugambiapplication.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button Loginbtn , Registerbtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Loginbtn = (Button) findViewById(R.id.main_login_btn);
        Registerbtn = (Button) findViewById(R.id.join_now_btn);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        Registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPasswordKey !="" && UserPhoneKey !="" )
        {
            if ( !TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey , UserPasswordKey);

                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please wait,while we are confirm your credentials");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users" ).child(phone).exists()){

                    Users usersData = dataSnapshot.child("Users" ).child(phone).getValue(Users.class);
                    //retrieve data using getter and setter

                    if (usersData.getPhone().equals(phone)) {
                        //if phone number is correct check the password
                        if (usersData.getPassword().equals(password)) {
                            //allow user access to his account

                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            //redirect user to home activity after login
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);

                        }
                    }

                }
                else{
                    Toast.makeText(MainActivity.this,"Account with"+ phone + "number do not exist.",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    Toast.makeText(MainActivity.this,"You need to create a new account",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }
}
