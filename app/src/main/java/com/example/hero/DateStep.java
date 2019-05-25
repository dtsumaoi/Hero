package com.example.hero;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import ernestoyaquello.com.verticalstepperform.Step;

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
