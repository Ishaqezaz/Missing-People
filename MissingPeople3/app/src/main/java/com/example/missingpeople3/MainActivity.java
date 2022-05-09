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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button logIn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        logIn = (Button) findViewById(R.id.login);
        logIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                startActivity(new Intent(this, UserRegistration.class));
                break;

            case R.id.login:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("E-postadress krävs!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Ange en giltig e-postadress.");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Lösenord krävs");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Lösenordet är mindre än 6 tecken");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Inloggningen lyckades.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(MainActivity.this, userLocation.class));

                } else {
                    Toast.makeText(MainActivity.this, "Inloggningen misslyckades, kontrollera dina uppgifter.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    /*private String getJson(String filename) {

        String json = null;
        try {
            InputStream inputStream = getAssets().open("filename");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private ArrayList generateHeatMapData() {

        ArrayList data = new ArrayList<WeightedLatLng>();

        try {
            JSONObject jsonObject = new JSONObject(getJson("cords"));
            JSONArray jsonArray = jsonObject.getJSONArray("");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Double lat = object.getDouble("lat");
                Double lon = object.getDouble("lon");
                Double density = object.getDouble("density");

                if (density != 0.0) {
                    Object weightedLatLng = new WeightedLatLng(new LatLng(lat, lon), density);
                    data.add(weightedLatLng);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }*/
}

