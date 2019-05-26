package com.example.hero;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.rilixtech.CountryCodePicker;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.Step;

class JobStep extends Step<String> {

    private static final String DEFAULT_NUMBER = "00-000-000-000";

    private EditText numberView;
    private String hint;

    private NiceSpinner niceSpinner;


    public JobStep(String title) {
        this(title, "");
    }

    public JobStep(String title, String subtitle) {
        super(title, subtitle);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {
        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View numberStepContent = inflater.inflate(R.layout.layout_spinner, null, false);
        niceSpinner = numberStepContent.findViewById(R.id.nice_spinner);

        List<String> dataset = new LinkedList<>(Arrays.asList("construction worker", "engineer", "seaman", "medical worder", "it professional", "domestic helper", "factory worker", "entertainer"));
        niceSpinner.attachDataSource(dataset);

        return numberStepContent;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        return niceSpinner.getSelectedItem() == null ? "(Empty)" : niceSpinner.getSelectedItem().toString();
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
