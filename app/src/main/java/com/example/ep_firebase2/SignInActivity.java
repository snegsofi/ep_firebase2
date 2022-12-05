package com.example.ep_firebase2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    Button signInBtn;
    Button signUpBtn;
    EditText passwordTxt;
    EditText emailTxt;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseFirestore db;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_screen);

        signInBtn=findViewById(R.id.signIn_Button);
        signUpBtn=findViewById(R.id.signUp_button);
        emailLayout=findViewById(R.id.emailContainer);
        emailTxt=findViewById(R.id.emailEditText);
        passwordTxt=findViewById(R.id.passwordEditText);
        passwordLayout=findViewById(R.id.passwordContainer);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        db = FirebaseFirestore.getInstance();


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate(emailTxt.getText().toString())){
                    showError(emailLayout,"Invalid mail");
                }
                else if(passwordTxt.getText().toString().isEmpty()){
                    showError(passwordLayout,"Field empty");
                }
                else{
                    signIn(emailTxt.getText().toString(),passwordTxt.getText().toString());
                    if(!fail) {
                        getID("UsersCourier",emailTxt.getText().toString(),ViewingOrderActivity.class);
                        getID("UsersClient",emailTxt.getText().toString(),OrderingFoodActivity.class);
                    }
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!emailTxt.getText().toString().isEmpty() && validate(emailTxt.getText().toString())){
                    hideError(emailLayout);
                }
            }
        });

        passwordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!passwordTxt.getText().toString().isEmpty()){
                    hideError(passwordLayout);
                }
            }
        });
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-z0-9._]+@[a-z0-9]+\\.[a-z]{1,3}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private void showError(TextInputLayout textInputLayout, String string) {
        textInputLayout.setError(string);
    }

    private void hideError(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
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

    private void signIn(String email, String password) {

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fail=false;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(SignInActivity.this, "Вход выполнен.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            isFail();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    //Toast.LENGTH_SHORT).show();

                            Toast.makeText(SignInActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]

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

    private void checkID(String id){
        db.collection("UsersCourier")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("Id").toString().equals(id)){
                                    Intent intent=new Intent(SignInActivity.this,ViewingOrderActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent=new Intent(SignInActivity.this,OrderingFoodActivity.class);
                                    startActivity(intent);
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

    }

    private void getID(String collectionPath, String email, Class layout){
        final String[] id = {""};
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "email" + " => " + document.get("Email").toString() + " " +email);
                                if(document.get("Email").toString().equals(email)){
                                    id[0]=document.get("Id").toString();
                                    Log.d(TAG, "hi "+id[0]);
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            if(!id[0].isEmpty()){
                                Intent intent=new Intent(SignInActivity.this,layout);
                                intent.putExtra("email",email);
                                startActivity(intent);
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
