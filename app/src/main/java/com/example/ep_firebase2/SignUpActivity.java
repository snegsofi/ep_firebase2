package com.example.ep_firebase2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SignUpActivity extends AppCompatActivity {
    Button signUpBtn;
    Button cancelBtn;

    EditText firstnameEditText;
    EditText lastnameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText phoneEditText;
    EditText addressEditText;

    MaterialButtonToggleGroup toggleGroup;

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseFirestore db;
    private String userType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        firstnameEditText = findViewById(R.id.name_EditText);
        lastnameEditText=findViewById(R.id.lastname_EditText);
        emailEditText = findViewById(R.id.email_EditText);
        passwordEditText = findViewById(R.id.password_EditText);
        phoneEditText=findViewById(R.id.phone_EditText);
        addressEditText=findViewById(R.id.address_EditText);
        toggleGroup=findViewById(R.id.toggleButton);

        signUpBtn = findViewById(R.id.signUp2_button);
        cancelBtn = findViewById(R.id.cancel_button);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        db = FirebaseFirestore.getInstance();

        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isChecked){
                    if(checkedId==R.id.button1){
                        userType="UsersClient";
                        Log.d(TAG, userType);
                    }
                    if(checkedId==R.id.button2){
                        userType="UsersCourier";
                        Log.d(TAG, userType);
                    }

                }
            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailEditText.getText().toString(),passwordEditText.getText().toString());

                addUser(userType,
                        firstnameEditText.getText().toString(),
                        lastnameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        addressEditText.getText().toString());
                if(!fail) {
                    if(userType.equals("UsersCourier")){
                        Intent intent=new Intent(SignUpActivity.this,ViewingOrderActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(SignUpActivity.this,OrderingFoodActivity.class);
                        intent.putExtra("email", emailEditText.getText().toString());
                        startActivity(intent);
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fail=false;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            isFail();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private boolean fail;
    private void isFail(){
        fail=true;
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }

    private void addUser(String userType,String name,String surname, String email,String phone, String address){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        user.put("Surname", surname);
        user.put("Email", email);
        user.put("Phone", phone);
        user.put("Id", UUID.randomUUID().toString());
        if(userType.equals("UsersClient")){
            user.put("Adress", address);
        }

        // Add a new document with a generated ID
        db.collection(userType)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private static AtomicLong idCounter = new AtomicLong();

    public static String createID()
    {
        return String.valueOf(idCounter.getAndIncrement());
    }

}
