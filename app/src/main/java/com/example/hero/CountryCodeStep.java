package com.example.hero;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.rilixtech.CountryCodePicker;

import ernestoyaquello.com.verticalstepperform.Step;

class CountryCodeStep extends Step<String> {

    private static final String DEFAULT_NUMBER = "00-000-000-000";

    private EditText numberView;
    private String hint;

    private CountryCodePicker ccp;

    public CountryCodeStep(String title) {
        this(title, "");
    }

    public CountryCodeStep(String title, String subtitle) {
        super(title, subtitle);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {
        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View numberStepContent = inflater.inflate(R.layout.step_number_layout, null, false);
        ccp = numberStepContent.findViewById(R.id.ccp);

        return numberStepContent;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        return ccp.getFullNumberWithPlus() == null ? "(Empty)" : ccp.getFullNumberWithPlus();
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
        numberView.setText(stepData);
    }
}
