package com.example.hero;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import ernestoyaquello.com.verticalstepperform.Step;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class SignupActivity extends AppCompatActivity implements StepperFormListener{

    private NameStep firstName;
    private NameStep middleName;
    private NameStep lastName;

    @BindView(R.id.stepper_form)
    VerticalStepperFormView verticalStepperFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppActionBar);
        setTitle("Sign Up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        firstName = new NameStep("Juan");
        middleName = new NameStep("Santos");
        lastName = new NameStep("Dela Cruz");

        // Find the form view, set it up and initialize it.
        verticalStepperFormView = findViewById(R.id.stepper_form);
        verticalStepperFormView
                .setup(this, lastName, firstName, middleName)
                .displayStepButtons(true)
                .displayBottomNavigation(true)
                .displayCancelButtonInLastStep(true)
                .init();
    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.
        super.onBackPressed();
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
        savedInstanceState.putString("middle_name", middleName.getStepData());

        // IMPORTANT: The call to the super method must be here at the end.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("last_name")) {
            String lastNameHolder = savedInstanceState.getString("last_name");
            lastName.restoreStepData(lastNameHolder);
        }

        if(savedInstanceState.containsKey("first_name")) {
            String firstNameHolder = savedInstanceState.getString("first_name");
            firstName.restoreStepData(firstNameHolder);
        }

        if(savedInstanceState.containsKey("middle_name")) {
            String middleNameHolder = savedInstanceState.getString("middle_name");
            middleName.restoreStepData(middleNameHolder);
        }

        // IMPORTANT: The call to the super method must be here at the end.
        super.onRestoreInstanceState(savedInstanceState);
    }
}


class NameStep extends Step<String> {

    private EditText nameView;

    public NameStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        nameView = new EditText(getContext());
        nameView.setSingleLine(true);
        nameView.setHint("First Name");

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
        String errorMessage = !isNameValid ? "Invalid Name" : "";

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

