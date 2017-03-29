package com.ghosttech.kptrafficapp.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ghosttech.kptrafficapp.utilities.Configuration;
import com.ghosttech.kptrafficapp.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegistrationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ProgressDialog dialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static RequestQueue mRequestQueue;
    View view;
    TextView tvSkip;
    Button btnSubmit;
    Fragment fragment;
    String strPassword, strCNIC, strPhoneNumber, strName, strConfirmPassword;
    EditText etName, etPhoneNumber, etCNIC, etPassword, etConfirmPassword;
    private OnFragmentInteractionListener mListener;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
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
        view = inflater.inflate(R.layout.fragment_registration, container, false);
        tvSkip = (TextView) view.findViewById(R.id.tvSkip);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        SpannableString content = new SpannableString("Login");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        tvSkip.setText(content);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new LoginFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });
        onSubmitButton();
        customActionBar();
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onSubmitButton() {
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // dialog.show();
                formValidation();
                /*fragment = new LoginFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit();*/
            }
        });
    }
    public void formValidation() {
        etName = (EditText) view.findViewById(R.id.et_name);
        etCNIC = (EditText) view.findViewById(R.id.et_cnic);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        etConfirmPassword = (EditText) view.findViewById(R.id.et_confirm_password);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);

        strPassword = etPassword.getText().toString();
        strCNIC = etCNIC.getText().toString();
        strName = etName.getText().toString();
        strConfirmPassword = etConfirmPassword.getText().toString();
        strPhoneNumber = etPhoneNumber.getText().toString();
        if (strName.equals("") || strName.length() < 3) {
            etName.setError("Please enter a valid name");
        } else if (strPhoneNumber.equals("") || strPhoneNumber.length() < 5) {
            etPhoneNumber.setError("Please enter a valid number");
        } else if (strCNIC.equals("") || strCNIC.length() < 13) {
            etCNIC.setError("Please insert your Complete CNIC");
        } else if (strPassword.equals("") || strCNIC.length() < 6) {
            etPassword.setError("Password should be six characters long");
        } else if (!strConfirmPassword.equals(strPassword)) {
            etConfirmPassword.setError("Password doesn't match");
        }
        String url = Configuration.END_POINT_LIVE + "/kp-traffic-police/signup/?name=" + strName + "&cnic=" + strCNIC + "&password=" + strPassword
                + "&phone_no=" + strPhoneNumber;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("status")) {
                        Log.d("zma status registration", String.valueOf(response.getBoolean("status")));
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("zma error registration",String.valueOf(error));

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }
    public void customActionBar() {
        android.support.v7.app.ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        ImageView mBackArrow = (ImageView) mCustomView.findViewById(R.id.iv_back_arrow);
        mBackArrow.setImageResource(R.mipmap.ic_launcher);
        mTitleTextView.setText("Sign Up");
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

}