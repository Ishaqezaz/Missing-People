package com.example.missingpeople3;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegistration extends AppCompatActivity implements View.OnClickListener {


    private TextView title, registerUser;
    private EditText editTextNamn, editTextAlder, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mAuth = FirebaseAuth.getInstance();     //initializering
        title = (TextView) findViewById(R.id.title);
        title.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextNamn = (EditText) findViewById(R.id.namn);
        editTextAlder = (EditText) findViewById(R.id.alder);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        //sparar indatat i en string

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String namn = editTextNamn.getText().toString().trim();
        String alder = editTextAlder.getText().toString().trim();

        //kontroll för inmattning

        if (namn.isEmpty()) {
            editTextNamn.setError("Namn är obligatoriskt!");
            editTextNamn.requestFocus();
            return;
        }

        if (alder.isEmpty()) {
            editTextAlder.setError("Ålder är obligatoriskt!");
            editTextAlder.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email är obligatoriskt!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Var vänlig och ange en korrekt email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Lösenord är obligatoriskt!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Lösenordet ska vara minst 6 tecken!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility((View.VISIBLE));
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task1) {

                        if(task1.isSuccessful()) {

                            UserInsert user = new UserInsert(namn, alder, email);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReferenceFromUrl("https://missingpeople3-92f63-default-rtdb.firebaseio.com/");
                            myRef.child("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if (task2.isSuccessful()) {
                                        Toast.makeText(UserRegistration.this, "Registreringen lyckades!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(UserRegistration.this, userLocation.class));
                                    } else {
                                        Toast.makeText(UserRegistration.this, "Registreringen misslyckades! Försök igen!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                             Toast.makeText(UserRegistration.this, "Registreringen misslyckades! Försök igen!", Toast.LENGTH_LONG).show();
                             progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        }
}