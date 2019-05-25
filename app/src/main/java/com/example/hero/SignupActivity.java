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
        email = new EmailStep("E-mail Address");

        // Find the form view, set it up and initialize it.
        verticalStepperFormView = findViewById(R.id.stepper_form);
        verticalStepperFormView
                .setup(this, lastName, firstName, middleName, birthDay, email, password)
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

        mAuth.createUserWithEmailAndPassword(email.getStepDataAsHumanReadableString(), password.getStepDataAsHumanReadableString())
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
                            String em = email.getStepDataAsHumanReadableString();

                            System.out.println("TANGINAMO LO ");

                            storeDB(uid, ln, fn, mn, bd, em);
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

    public void storeDB(String uid, String lastName, String firstName, String middleName, String birthDay, String email) {

        User user = new User(lastName, firstName, middleName, birthDay, email);

        System.out.println("TANGINAMO LO " + user);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(uid).setValue(user);

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

        if (savedInstanceState.containsKey("email")) {
            String emailHolder = savedInstanceState.getString("email");
            email.restoreStepData(emailHolder);
        }

        // IMPORTANT: The call to the super method must be here at the end.
        super.onRestoreInstanceState(savedInstanceState);
    }
}

class NameStep extends Step<String> {

    private EditText nameView;
    private String hint;

    public NameStep(String stepTitle) {
        super(stepTitle);
        this.hint = stepTitle;
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        nameView = new EditText(getContext());
        nameView.setSingleLine(true);
        nameView.setHint(hint);

        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return nameView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isNameValid = stepData.matches("^[\\p{L}\\s'.-]+$");
        String errorMessage = !isNameValid ? "Please indicate a valid name" : "";

        return new IsDataValid(isNameValid, errorMessage);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable name = nameView.getText();
        return name != null ? name.toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String name = getStepData();
        return !name.isEmpty() ? name : "(Empty)";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        nameView.setText(stepData);
    }
}

class PasswordStep extends Step<String> {

    private EditText passwordView;
    private String hint;

    public PasswordStep(String stepTitle) {
        super(stepTitle);
        this.hint = stepTitle;
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        passwordView = new EditText(getContext());
        passwordView.setSingleLine(true);
        passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return passwordView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isPasswordValid = stepData.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$");
        String errorMessage = !isPasswordValid ? "- At least Min Characters 8 and Maximum Characters 15\n" +
                "- At least One Number and 1 special characters from (! @#$%^&*-=+?.);\n" +
                "- At least One lower case letter\n" +
                "- Password shouldn't be sub strings of username and email (min length 3 and max length 15).\n" +
                "- Password should be case sensitive." : "";

        return new IsDataValid(isPasswordValid, errorMessage);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable name = passwordView.getText();
        return name != null ? name.toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String password = getStepData();
        // return !password.isEmpty() ? password : "(Empty)";
        return "(Secured)";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        passwordView.setText(stepData);
    }
}

class EmailStep extends Step<String> {

    private EditText emailView;
    private String hint;

    public EmailStep(String stepTitle) {
        super(stepTitle);
        this.hint = stepTitle;
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        emailView = new EditText(getContext());
        emailView.setSingleLine(true);
        emailView.setHint(hint);

        emailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return emailView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        boolean isNameValid = stepData.matches("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");
        String errorMessage = !isNameValid ? "Please indicate a valid e-mail address" : "";

        return new IsDataValid(isNameValid, errorMessage);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable email = emailView.getText();
        return email != null ? email.toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String email = getStepData();
        return !email.isEmpty() ? email : "(Empty)";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        emailView.setText(stepData);
    }
}

class DateStep extends Step<DateStep.DateHolder> {

    private TextView dateTextView;
    private DatePickerDialog datePicker;

    private static final int DEFAULT_YEAR = 2000;
    private static final int DEFAULT_MONTH = 1;
    private static final int DEFAULT_DATE = 1;

    private int year;
    private int month;
    private int date;

    public DateStep(String stepTitle) {
        this(stepTitle, "");
    }

    public DateStep(String stepTitle, String subTitle) {
        super(stepTitle, subTitle);

        year = DEFAULT_YEAR;
        date = DEFAULT_DATE;
        month = DEFAULT_MONTH;
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        dateTextView = new TextView(getContext());
        dateTextView.setText(getStepDataAsHumanReadableString());
        setupDate();

        return dateTextView;
    }

    public void setupDate() {
        if (datePicker == null) {
            datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int _year, int _month, int _date) {
                    year = _year;
                    month = _month;
                    date = _date;

                    updatedDate();
                }
            }, year, month, date);
        } else {
            datePicker.updateDate(year, month, date);
        }

        if (dateTextView != null) {
            dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePicker.show();
                }
            });
        }
    }


    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public DateHolder getStepData() {
        return new DateHolder(year, month, date);
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return month + "/" + date + "/" + year;
    }

    @Override
    public void restoreStepData(DateHolder data) {
        year = data.year;
        month = data.month;
        date = data.date;

        datePicker.updateDate(year, month, date);
        updatedDate();
    }

    @Override
    protected IsDataValid isStepDataValid(DateHolder stepData) {
        return new IsDataValid(true);
    }

    private void updatedDate() {
        dateTextView.setText(getStepDataAsHumanReadableString());
    }

    public static class DateHolder {

        public int year;
        public int month;
        public int date;


        public DateHolder(int year, int month, int date) {
            this.year = year;
            this.month = month;
            this.date = date;
        }
    }
}

