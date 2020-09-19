package com.example.emec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegisterActivity extends AppCompatActivity {
    private Button DriverLoginButton;
    private Button DriverRegisterButton;
    private TextView DriverRegisterLink;
    private TextView DriverStatus;
    private EditText EmailDriver;
    private EditText PasswordDriver;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference DriverDatabaseRef;
    private String onlineDriverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth  = FirebaseAuth.getInstance();

        DriverLoginButton = (Button) findViewById(R.id.driverLogin);
        DriverRegisterButton = (Button) findViewById(R.id.driverRegistration);
        DriverRegisterLink = (TextView) findViewById(R.id.DrRegLink);
        DriverStatus = (TextView) findViewById(R.id.driverStatus);
        EmailDriver = (EditText) findViewById(R.id.email_driver);
        PasswordDriver = (EditText) findViewById(R.id.password_driver);
        loadingBar = new ProgressDialog(this);

        DriverRegisterButton.setVisibility((View.INVISIBLE));
        DriverRegisterButton.setEnabled(false);

        DriverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverLoginButton.setVisibility(View.INVISIBLE);
                DriverRegisterLink.setVisibility(View.INVISIBLE);
                DriverStatus.setText("Register Mechanic");

                DriverRegisterButton.setVisibility(View.VISIBLE);
                DriverRegisterButton.setEnabled(true);

            }
        });

        DriverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                RegisterDriver(email, password);
            }
        });

        DriverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailDriver.getText().toString();
                String password = PasswordDriver.getText().toString();

                SignInDriver(email, password);
            }
        });
    }

    private void SignInDriver(String email, String password) {

        if(TextUtils.isEmpty(email)){
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write Email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write Password...", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Mechanic Login");
            loadingBar.setMessage("Please wait, while we are checking your credentials..");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriversMapsActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(DriverLoginRegisterActivity.this, "Mechanic Logged in Successfully..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else{
                                Toast.makeText(DriverLoginRegisterActivity.this, "Login Unsuccessful, Please try Again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }


    private void RegisterDriver(String email, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write Email...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(DriverLoginRegisterActivity.this, "Please write Password...", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Mechanic Registration");
            loadingBar.setMessage("Please wait, while we are register your data..");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                onlineDriverID = mAuth.getCurrentUser().getUid();
                                DriverDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Mechanic").child(onlineDriverID);

                                DriverDatabaseRef.setValue(true);

                                Intent driverIntent = new Intent(DriverLoginRegisterActivity.this, DriversMapsActivity.class);
                                startActivity(driverIntent);

                                Toast.makeText(DriverLoginRegisterActivity.this, "Mechanic Register Successfully..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                            else{
                                Toast.makeText(DriverLoginRegisterActivity.this, "Registration Unsuccessful, Please try Again..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}