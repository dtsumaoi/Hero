package com.example.hero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import ernestoyaquello.com.verticalstepperform.Step;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class SignupActivity extends AppCompatActivity implements StepperFormListener {
    private String TAG = "SIGNUP";

    private NameStep firstName;
    private NameStep middleName;
    private NameStep lastName;
    private DateStep birthDay;
    private PasswordStep password;
    private CountryCodeStep countryCode;
    private MobileNumberStep mobileNumber;
    private EmailStep email;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @BindView(R.id.stepper_form)
    VerticalStepperFormView verticalStepperFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppActionBar);
        setTitle("Sign Up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        firstName = new NameStep("First Name");
        middleName = new NameStep("Middle Name");
        lastName = new NameStep("Last Name");
        birthDay = new DateStep("Birthday");
        password = new PasswordStep("Password");
        countryCode = new CountryCodeStep("Country Code");
        mobileNumber = new MobileNumberStep("Mobile Number");
        email = new EmailStep("E-mail Address");

        // Find the form view, set it up and initialize it.
        verticalStepperFormView = findViewById(R.id.stepper_form);
        verticalStepperFormView
                .setup(this, lastName, firstName, middleName, birthDay, countryCode, mobileNumber, email, password)
                .displayStepButtons(true)
                .displayBottomNavigation(true)
                .displayStepDataInSubtitleOfClosedSteps(true)
                .displayCancelButtonInLastStep(true)
                .init();
    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.

        mAuth.createUserWithEmailAndPassword(email.getStepDataAsHumanReadableString(), password.getStepData())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String uid = user.getUid();
                            String fn = firstName.getStepDataAsHumanReadableString();
                            String mn = middleName.getStepDataAsHumanReadableString();
                            String ln = lastName.getStepDataAsHumanReadableString();
                            String bd = birthDay.getStepDataAsHumanReadableString();
                            String ccode = countryCode.getStepDataAsHumanReadableString();
                            String mnum = mobileNumber.getStepDataAsHumanReadableString();
                            String em = email.getStepDataAsHumanReadableString();

                            storeDB(uid, ln, fn, mn, bd, ccode, mnum, em);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void storeDB(String uid, String lastName, String firstName, String middleName, String birthDay, String ccode, String mobileNumber,  String email) {

        User user = new User(lastName, firstName, middleName, birthDay, ccode + mobileNumber, email);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child("Profile").child(uid).setValue(user);

        updateUI();
    }

    private void updateUI() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCancelledForm() {
        // This method will be called when the user clicks on the cancel button of the form.
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("last_name", lastName.getStepData());
        savedInstanceState.putString("first_name", firstName.getStepData());
        savedInstanceState.putInt("year", birthDay.getStepData().year);
        savedInstanceState.putInt("month", birthDay.getStepData().month);
        savedInstanceState.putInt("date", birthDay.getStepData().date);
        savedInstanceState.putString("password", password.getStepData());
        savedInstanceState.putString("country_code", countryCode.getStepData());
        savedInstanceState.putString("mobile_number", mobileNumber.getStepData());
        savedInstanceState.putString("email", email.getStepData());

        // IMPORTANT: The call to the super method must be here at the end.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("last_name")) {
            String lastNameHolder = savedInstanceState.getString("last_name");
            lastName.restoreStepData(lastNameHolder);
        }

        if (savedInstanceState.containsKey("first_name")) {
            String firstNameHolder = savedInstanceState.getString("first_name");
            firstName.restoreStepData(firstNameHolder);
        }

        if (savedInstanceState.containsKey("middle_name")) {
            String middleNameHolder = savedInstanceState.getString("middle_name");
            middleName.restoreStepData(middleNameHolder);
        }

        if (savedInstanceState.containsKey("year") && savedInstanceState.containsKey("month") && savedInstanceState.containsKey("date")) {
            int year = savedInstanceState.getInt("year");
            int month = savedInstanceState.getInt("month");
            int date = savedInstanceState.getInt("date");

            DateStep.DateHolder time = new DateStep.DateHolder(year, month, date);
            birthDay.restoreStepData(time);
        }

        if (savedInstanceState.containsKey("password")) {
            String passwordHolder = savedInstanceState.getString("password");
            password.restoreStepData(passwordHolder);
        }

        if (savedInstanceState.containsKey("country_code")) {
            String countryCodeHolder = savedInstanceState.getString("country_code");
            countryCode.restoreStepData(countryCodeHolder);
        }

        if (savedInstanceState.containsKey("mobile_number")) {
            String mobileNumberHolder = savedInstanceState.getString("mobile_number");
            mobileNumber.restoreStepData(mobileNumberHolder);
        }

        if (savedInstanceState.containsKey("email")) {
            String emailHolder = savedInstanceState.getString("email");
            email.restoreStepData(emailHolder);
        }

        // IMPORTANT: The call to the super method must be here at the end.
        super.onRestoreInstanceState(savedInstanceState);
    }
}


