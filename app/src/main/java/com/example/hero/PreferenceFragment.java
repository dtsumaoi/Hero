package com.example.hero;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ernestoyaquello.com.verticalstepperform.Step;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreferenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferenceFragment extends Fragment implements StepperFormListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PREFERENCE";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Preference preference;

    @BindView(R.id.preference_form)
    VerticalStepperFormView preferenceFormView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private JobStep job;
    private CountryStep country;
    private SalaryStep salary;
    private View view;

    public PreferenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreferenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreferenceFragment newInstance(String param1, String param2) {
        PreferenceFragment fragment = new PreferenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_preference, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();

        job = new JobStep("Job");
        country = new CountryStep("Country");
        salary = new SalaryStep("Salary");


        // Find the form view, set it up and initialize it.
        preferenceFormView = view.findViewById(R.id.preference_form);
        preferenceFormView
                .setup(this, job, country, salary)
                .displayStepButtons(true)
                .displayBottomNavigation(true)
                .displayStepDataInSubtitleOfClosedSteps(true)
                .displayCancelButtonInLastStep(true)
                .init();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCompletedForm() {
        storeDB(new Preference(job.getStepDataAsHumanReadableString(), country.getStepDataAsHumanReadableString(), salary.getStepDataAsHumanReadableString()));
    }

    @Override
    public void onCancelledForm() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void storeDB(Preference preference) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(mAuth.getUid()).child("Profile/Preference/").setValue(preference);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }


    static class SalaryStep extends Step<String> {

        private EditText salaryView;

        public SalaryStep(String stepTitle) {
            super(stepTitle);
        }

        @Override
        protected View createStepContentLayout() {
            // Here we generate the view that will be used by the library as the content of the step.
            // In this case we do it programmatically, but we could also do it by inflating an XML layout.
            salaryView = new EditText(getContext());
            salaryView.setSingleLine(true);
            salaryView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);

            salaryView.addTextChangedListener(new TextWatcher() {
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

            return salaryView;
        }

        @Override
        protected IsDataValid isStepDataValid(String stepData) {
            // The step's data (i.e., the user name) will be considered valid only if it is longer than
            // three characters. In case it is not, we will display an error message for feedback.
            // In an optional step, you should implement this method to always return a valid value.
            boolean isNumberValid = stepData.length() >= 5 && stepData.length() <= 8;
            String errorMessage = !isNumberValid ? "Provide a salary" +
                    " greater than 10000 and less than 99999999" : "";

            return new IsDataValid(isNumberValid, errorMessage);
        }

        @Override
        public String getStepData() {
            // We get the step's data from the value that the user has typed in the EditText view.
            Editable number = salaryView.getText();
            return number != null ? number.toString() : "";
        }

        @Override
        public String getStepDataAsHumanReadableString() {
            // Because the step's data is already a human-readable string, we don't need to convert it.
            // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
            // This string will be displayed in the subtitle of the step whenever the step gets closed.
            String number = getStepData();
            return !number.isEmpty() ? number : "(Empty)";
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
            salaryView.setText(stepData);
        }
    }

    static class JobStep extends Step<String> {

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

            List<String> dataset = new LinkedList<>(Arrays.asList("Construction Worker", "Engineer", "Seaman", "Medical Worker", "IT Professional", "Domestic Helper", "Factory Worker", "Entertainer"));
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
            niceSpinner.getSelectedItem();
        }
    }

    static class CountryStep extends Step<String> {

        private NiceSpinner niceSpinner;


        public CountryStep(String title) {
            this(title, "");
        }

        public CountryStep(String title, String subtitle) {
            super(title, subtitle);
        }

        @NonNull
        @Override
        protected View createStepContentLayout() {
            // We create this step view by inflating an XML layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View numberStepContent = inflater.inflate(R.layout.layout_spinner, null, false);
            niceSpinner = numberStepContent.findViewById(R.id.nice_spinner);

            List<String> dataset = new LinkedList<>(Arrays.asList("Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"));
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
            niceSpinner.getSelectedItem();
        }
    }
}
