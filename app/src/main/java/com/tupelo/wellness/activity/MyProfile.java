package com.tupelo.wellness.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.R;
import com.tupelo.wellness.database.AssetsDbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.TakeImageClass;
import com.tupelo.wellness.parcer.Jsonparser;
import com.tupelo.wellness.view.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyProfile extends Fragment implements View.OnClickListener {

    boolean editTransactionGoingOn = false;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String sessid, uid;
    String userAvatarURL;
    String prefsbirthDate;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private ProgressDialog pDialog;
    private double weightvalue = 10, heightvalue = 10;
    private String TAG = "MyProfile";
    private EditText et_fname, et_lname;
    TextView et_userName, editPhotoTV;
    EditText tv_birthdate, tv_height, tv_weight, auto_country, auto_city, auto_zip;
    ImageView iv_profile_img;

    private TakeImageClass takeImageClass;
    ArrayAdapter<String> countryAdapter;
    Button btn_edit;
    private RadioGroup radio_group;
    TextInputLayout til_city, til_zip;
    private RadioButton rb_gender, rb_male;
    private String heightWhichIHaveToSaveToServer = "", weightWhichIHaveToSaveToServer = "";
    private String heightWhichIhaveToSaveToPrefs = "", weightWhichIHaveToSaveToPrefs = "";
    private double heightUser = 0.00;
    DatePickerDialog datePickerDialog;
    String[] country, city;
    AssetsDbAdapter assetsDbAdapter;
    ArrayAdapter<String> cityAdapter;
    GetterSetter getterSetter;
    Cursor cursor;
    Bitmap bitmap;
    boolean booCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.my_profile, container, false);
        try {
            assetsDbAdapter = new AssetsDbAdapter(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initViews(v);
        return v;


    }

    public void setHeight(String height) {
        tv_height.setText(height);
        this.heightWhichIhaveToSaveToPrefs = height;
        this.heightWhichIHaveToSaveToServer = height;
        Log.e("height", this.heightWhichIHaveToSaveToServer);
    }

    public void setHeight(double feet, double inch) {
        tv_height.setText((int) feet + " feet" + " " + (int) inch + " inch");
        heightWhichIhaveToSaveToPrefs = (int) feet + " feet" + " " + (int) inch + " inch";
        heightWhichIHaveToSaveToServer = Helper.getCMFromFootsAndInch(feet,inch);
                  Log.e("height", this.heightWhichIHaveToSaveToServer);
    }
    //weight will store in kg inside db and we will use it in kg throughout the app.

    public void setWeight(String weight) {
        tv_weight.setText(weight);
        this.weightWhichIHaveToSaveToPrefs = weight;
        this.weightWhichIHaveToSaveToServer = weight;
        Log.e("weight", this.weightWhichIHaveToSaveToServer);
    }

    public void setWeight(double weight, String unit) {
        tv_weight.setText(weight + " " + unit);
        this.weightWhichIHaveToSaveToPrefs = weight + " " + unit;
        this.weightWhichIHaveToSaveToServer = Math.round((weight / 2.20462) *10.0) / 10.0 + " kgs";
        Log.e("weight", this.weightWhichIHaveToSaveToServer);
    }


    private void edit(View v) {


        if (editTransactionGoingOn == false) {
            Button b = (Button) v;
            editTransactionGoingOn = true;
            setEditableAll(editTransactionGoingOn, b);

        } else {

            if (validation()) {
                if (CheckConnection.isConnection(getActivity())) {

                    String fname = et_fname.getText().toString().trim();
                    String lname = et_lname.getText().toString().trim();
                    //String height = tv_height.getText().toString().trim();
                    //String weight = tv_weight.getText().toString().trim();
                    String dob = tv_birthdate.getText().toString().trim();
                    rb_gender = (RadioButton) radio_group.findViewById(radio_group.getCheckedRadioButtonId());
                    String gender = rb_gender.getText().toString().trim();
                    if (gender.equalsIgnoreCase("male"))
                        gender = "0";
                    else
                        gender = "1";
                    String city = auto_city.getText().toString().trim();
                    String country = auto_country.getText().toString().trim();

                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) iv_profile_img.getDrawable());
                    if (bitmapDrawable != null)
                        bitmap = bitmapDrawable.getBitmap();
                    // new myProfileAsync(getActivity(), bitmap).execute(sessid, uid, city, fname, lname, country, dob, gender, weight, height);
                    saveImageAndUploadData(bitmap, sessid, uid, city, fname, lname, country, dob, gender, weightWhichIHaveToSaveToServer, heightWhichIHaveToSaveToServer, auto_zip.getText().toString());
                    //  sessionIdCheck(fname, lname, username, email, password, height, weight, dob, gender, city, country, WalkingPrefrence.getInstance().corpId);

                } else
                    new Helper().showNoInternetToast(getActivity());
            }
        }
    }

    private void saveImageAndUploadData(final Bitmap bitmap, final String... eclipseParams) {

        if (bitmap != null) {

            //   String resimage = new NetworkCall().saveProfileImage(bitmap, eclipseParams[1], eclipseParams[0]);

            pDialog.show();
            StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "Response is " + response);
                            String fid, path;
                            Jsonparser jsonparser;
                            Log.e("My profile Image", response);
                            jsonparser = new Jsonparser(response);
                            fid = jsonparser.getProfileImagefid()[0];
                            path = jsonparser.getProfileImagefid()[1];
                            Log.e("fid", fid + " " + path);

                            uploadRemainingDataWithFid(path, fid, eclipseParams);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ERROR", "error 3=> " + error.getMessage());
                            pDialog.hide();
                            Toast.makeText(getActivity(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                        }
                    }
            ) {
                // this is the relevant method
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    Helper helper = new Helper();
                    String method = "file.save", jsonString = "";
                    JSONObject param = new JSONObject();

                    try {
                        String bitString = helper.bitmaptoString(bitmap);
                        String time_stamp = helper.getUnixTimestamp();

                        String sessid = eclipseParams[0];
                        String uid = eclipseParams[1];
                        param.put("file", bitString);
                        param.put("filename", "myPic" + uid + ".png");
                        param.put("filepath", "sites/default/files/myPic" + uid + ".png");
                        param.put("uid", uid);
                        param.put("timestamp", time_stamp);
                        jsonString = param.toString();

                        params = new Helper().addRequiredParams(params, method);
                        params.put("method", method);
                        params.put("file", jsonString);
                        params.put("sessid", sessid);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }


                    return params;


                }
            };
            // Adding request to request queue

            AppController.getInstance().addToRequestQueue(postRequest);

        } else
            uploadRemainingDataWithFid("", "", eclipseParams);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        pDialog.dismiss();
    }


    private void uploadRemainingDataWithFid(final String path, final String fid, final String... eclipseParams) {

        if (!pDialog.isShowing())
            pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Log.e(TAG, "Response is " + response);

                        String result = "";
                        if (!response.equals("Connection Timeout")) {
                            Jsonparser jsonparser = new Jsonparser(response);

                            String message = jsonparser.parseErrorMsg();

                            result = message;
                        } else
                            result = "-3";

                        if (result.equals("1")) {
                            setEditableAll(false, btn_edit);


                            Toast.makeText(getActivity(), "Details Edited Sucessfully", Toast.LENGTH_SHORT).show();


                            //now change my current prefrence to what is edited

                            String userCity = eclipseParams[2];
                            String userFname = eclipseParams[3];
                            String userLname = eclipseParams[4];
                            String userCountry = eclipseParams[5];
                            String userbirthDate = eclipseParams[6];
                            String userGender = eclipseParams[7];

                            Log.e(TAG, "Details are edited sucessfully Now Gender is " + userGender);
                            String userWeight = eclipseParams[8];
                            String userHeight = eclipseParams[9];

                            String zipCode = eclipseParams[10];
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("MyProfile", Context.MODE_PRIVATE).edit();

                            Picasso.with(getActivity()).invalidate(userAvatarURL);
                            ((TabActivity) getActivity()).changeNavDrawerImage(path);
                            editor.putString("userAvatarURL", path);
                            editor.putString("userFname", userFname);
                            editor.putString("userLname", userLname);
                            editor.putString("userCity", userCity);
                            editor.putString("userWeight", weightWhichIHaveToSaveToPrefs);
                            editor.putString("userHeight", heightWhichIhaveToSaveToPrefs);
                            editor.putString("userGender", userGender);
                            editor.putString("userCountry", userCountry);
                            editor.putString("userbirthDate", userbirthDate);
                            editor.putString("zipCode", zipCode);
                            editor.commit();


                        } else if (result.equals("-3")) {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.universal_err), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Unable to Edit Details", Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error 3=> " + error.getMessage());
                        pDialog.hide();
                        Toast.makeText(getActivity(), R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                String sessid = eclipseParams[0];
                String uid = eclipseParams[1];
                String userCity = eclipseParams[2];
                String userFname = eclipseParams[3];
                String userLname = eclipseParams[4];
                String userCountry = eclipseParams[5];
                String userbirthDate = eclipseParams[6];
                String userGender = eclipseParams[7];
                String userWeight = eclipseParams[8];
                String userHeight = eclipseParams[9];
                String zipcode = eclipseParams[10];
                String method = "userV2.saveV2";

                String enteredHeightSiUnit =  heightWhichIhaveToSaveToPrefs.contains("feet")?"ft":"cms";
                String enteredWeightSiUnit = weightWhichIHaveToSaveToPrefs.contains("lbs")?"lbs":"kgs";

                params = new Helper().addRequiredParams(params, method);

                params.put("method", method);
                params.put("sessid", sessid);
                params.put("uid", uid);
                params.put("city", userCity);
                params.put("fname", userFname);
                params.put("lname", userLname);
                params.put("country", userCountry);
                params.put("imageId", fid);
                params.put("birthDate", userbirthDate);
                params.put("gender", userGender);
                params.put("height", heightWhichIHaveToSaveToServer);
                params.put("heightPref",enteredHeightSiUnit);
                params.put("weight", weightWhichIHaveToSaveToServer);
                params.put("weightPref",enteredWeightSiUnit);
                params.put("zip", zipcode);


                Log.e(TAG, params.toString());
                return params;


            }
        };
        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(postRequest);


    }


    private void setEditableAll(boolean value, Button b) {

        if (b != null) {
            if (value)
                b.setText("Save");
            else
                b.setText("Edit");

            editTransactionGoingOn = value;
        }


        int visibility = value ? View.VISIBLE : View.GONE;
        editPhotoTV.setVisibility(visibility);

        if (value) {
            et_fname.requestFocus();
            et_fname.setSelection(et_fname.getText().toString().length());
        }
        editEditText(et_fname, value);
        editEditText(et_lname, value);
        editEditText(tv_weight, value);
        editEditText(tv_birthdate, value);
        editEditText(auto_city, value);
        editEditText(auto_country, value);
        editEditText(tv_height, value);
        radio_group.getChildAt(0).setEnabled(value);
        radio_group.getChildAt(1).setEnabled(value);
        iv_profile_img.setClickable(value);


    }

    private void editEditText(EditText editText, Boolean value) {
        if (editText != null) {
            editText.setEnabled(value);
        }
    }

    private boolean validation() {


        final String fname = et_fname.getText().toString().trim();
        final String lname = et_lname.getText().toString().trim();
        final String height = tv_height.getText().toString().trim();
        final String weight = tv_weight.getText().toString().trim();
        final String dob = tv_birthdate.getText().toString().trim();
        final String city = auto_city.getText().toString().trim();
        final String country = auto_country.getText().toString().trim();
        final String zipCode = auto_zip.getText().toString().trim();
        rb_gender = (RadioButton) radio_group.findViewById(radio_group.getCheckedRadioButtonId());
        String gender = rb_gender.getText().toString().trim();
        if (gender.equalsIgnoreCase("male"))
            gender = "0";
        else
            gender = "1";
        if (fname.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.fname_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (lname.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.lname_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (height.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.empty_height_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (weight.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.empty_weight_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (dob.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.empty_dob_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (radio_group.getCheckedRadioButtonId() == 0) {
            Toast.makeText(getActivity(), getString(R.string.select_gender_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (country.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.empty_country_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (city.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.empty_city_txt), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (country.equalsIgnoreCase("United States")) {
            if (auto_zip.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.empty_zip_txt), Toast.LENGTH_SHORT).show();
                return false;
            }
            else  if (auto_zip.getText().toString().length() !=5 ) {
                Toast.makeText(getActivity(), getString(R.string.invalid_zip), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (iv_profile_img.getDrawable() == null) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setTitle("No Profile Picture added");
            alertDialogBuilder.setMessage("No profile picture added. Do you want to proceed");

            // set dialog message
            final String finalGender = gender;
            alertDialogBuilder.setCancelable(false).setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (CheckConnection.isConnection(getActivity())) {


                        //call ionic method

                        //String title_other = et_title_other.getText().toString();
                        bitmap = ((BitmapDrawable) iv_profile_img.getDrawable()).getBitmap();
                        saveImageAndUploadData(bitmap, sessid, uid, city, fname, lname, country, dob, finalGender, weight, height, auto_zip.getText().toString());

                    }
                }
            })
                    .setNegativeButton("ADD NOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            verifyStoragePermissions();
                            takeImageClass.getImagePickerDialog(getActivity(), getString(R.string.app_name), getString(R.string.choose_image));

                        }
                    });

        }


        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {


        Dialog dialog = null;
        if (Build.VERSION.SDK_INT >= 11)
            dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
        else
            dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.height_weight_dialog);
        dialog.closeOptionsMenu();


        switch (v.getId()) {
            case R.id.edit:
                edit(v);
                break;
            case R.id.tv_birthdate:
                datePickerDialog.show();
                break;
            case R.id.tv_height:

                showDialogForHeight(dialog);
                dialog.show();
                break;
            case R.id.tv_weight:

                showDialogForWeight(dialog);
                dialog.show();
                break;

            case R.id.iv_profile:
                editPhoto();
                break;
            case R.id.editPhotoTV:
                editPhoto();
                break;
        }
    }

    private void editPhoto() {

        if (editTransactionGoingOn) {
            verifyStoragePermissions();
            takeImageClass.getImagePickerDialog(getActivity(), getString(R.string.app_name), getString(R.string.choose_image));
        }
    }


    private void showDialogForHeight(final Dialog dialog) {

        Drawable myDrawable = getResources().getDrawable(R.mipmap.radio_btn_active);
        myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);


        TextView headerText = (TextView) dialog.findViewById(R.id.headerText);
        headerText.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        headerText.setTextColor(Color.WHITE);
        headerText.setText(R.string.height);


        String textOfHeight = tv_height.getText().toString();
        Log.e(TAG, "text of height is " + textOfHeight);

        int defaultValueFornm1 = 30;
        int defaultValueFornm2 = 0;
        String text = "";
        String[] heightValues;
        if (textOfHeight.contains("cms")) {

            defaultValueFornm1 = 170;
            text = textOfHeight.replace("cms", "");
            conversionUnitLength = 0;

            try {
                heightValues = text.split("\\.");
                Log.e(TAG, heightValues.toString());
                defaultValueFornm1 = Integer.parseInt(heightValues[0].trim());
                defaultValueFornm2 = Integer.parseInt(heightValues[1].trim());
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (textOfHeight.contains("feet")) {

            defaultValueFornm1 = 5;
            text = textOfHeight.replace("inch", "");

            conversionUnitLength = 1;

            try {

                heightValues = text.split("feet");
                defaultValueFornm1 = Integer.parseInt(heightValues[0].trim());
                defaultValueFornm2 = Integer.parseInt(heightValues[1].trim());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        final NumberPicker nm1 = (NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);

        nm1.setMaxValue(275);
        nm1.setMinValue(30);


        final NumberPicker nm2 = (NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);
        if(textOfHeight.contains("feet"))
        {
            nm2.setMinValue(0);
            nm2.setMaxValue(11);

            nm1.setMinValue(0);
            nm1.setMaxValue(9);


        }
        else
        {


            nm1.setMinValue(10);
            nm1.setMaxValue(275);

            nm2.setMinValue(0);
            nm2.setMaxValue(9);


        }

        nm1.setValue(defaultValueFornm1);
        nm2.setValue(defaultValueFornm2);

        RadioButton cmUnitBtn = ((RadioButton) dialog.findViewById(R.id.cmUnit));
        RadioButton incheUnitBtn = ((RadioButton) dialog.findViewById(R.id.incheUnit));
        if (conversionUnitLength == 0)
            cmUnitBtn.setChecked(true);
        else
            incheUnitBtn.setChecked(true);

        CompoundButton.OnCheckedChangeListener cmUnitBtnListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;
                conversionUnitLength = 0;
                double value = nm1.getValue() + ((double) nm2.getValue()) / 12;
                nm1.setMinValue(10);
                nm1.setMaxValue(275);
                nm2.setMinValue(0);
                nm2.setMaxValue(9);
                nm2.setValue(0);
                heightvalue = value;
                value = (value / 0.0328084);
                nm1.setValue((int) value);
                nm2.setValue((int) Math.round((value - (int) value) * 10));
            }
        };
        CompoundButton.OnCheckedChangeListener inchUnitBtnListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;
                conversionUnitLength = 1;
                double value = nm1.getValue() + ((double) nm2.getValue()) / 10;
                nm1.setMinValue(0);
                nm1.setMaxValue(9);
                nm2.setMinValue(0);
                nm2.setMaxValue(11);
                heightvalue = value;
                value = value * 0.0328084;
                nm1.setValue((int) value);
                nm2.setValue((int) Math.round((value - (int) value) * 12));
            }
        };

        cmUnitBtn.setOnCheckedChangeListener(cmUnitBtnListener);
        incheUnitBtn.setOnCheckedChangeListener(inchUnitBtnListener);
        if (heightvalue > 0) {
            /*nm1.setCurrent((int)heightvalue);
            nm2.setCurrent((int)((heightvalue - (int)heightvalue) * 10));*/
        }
        TextView b = ((TextView) dialog.findViewById(R.id.submitBtn));
        b = AppTheme.changeBackgroundColor(b);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((RadioButton) dialog.findViewById(R.id.cmUnit)).isChecked()) {
                    heightvalue = nm1.getValue() + 0.1 * nm2.getValue();
                    //((SignUpActivity)mActivity).setHeight(heightvalue,mActivity.getString(R.string.cm));
                    setHeight(heightvalue + " cms");
                }
                //saveMetric(dialog,nm1.getCurrent1() + 0.1 * nm2.getCurrent1(),forDate);
                else {
                    heightvalue = nm1.getValue() + 0.1 * nm2.getValue();
                    int feet = nm1.getValue();
                    int inch = nm2.getValue();
                    setHeight(feet, inch);
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/0.393701,forDate);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.weightUnits).setVisibility(View.GONE);
    }

    private int conversionUnitWeight, conversionUnitLength;


    private void showDialogForWeight(final Dialog dialog) {

        Drawable myDrawable = getResources().getDrawable(R.mipmap.radio_btn_active);
        myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);


        TextView header = (TextView) dialog.findViewById(R.id.headerText);
        header.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        header.setTextColor(Color.WHITE);
        header.setText(R.string.weight);


        String textOfWieght = tv_weight.getText().toString();
        Log.e(TAG, "text of weight is " + textOfWieght);

        int defaultValueFornm1 = 64;
        int defaultValueFornm2 = 0;
        String text = "";
        String[] weightValues;
        if (textOfWieght.contains("lbs")) {
            defaultValueFornm1 = 142;
            text = textOfWieght.replace("lbs", "");
            conversionUnitWeight = 1;
        } else if (textOfWieght.contains("kgs")) {

            defaultValueFornm1 = 64;
            text = textOfWieght.replace("kgs", "");
            conversionUnitWeight = 0;
        }
        try {
            weightValues = text.split("\\.");
            Log.e(TAG, weightValues.toString());
            defaultValueFornm1 = Integer.parseInt(weightValues[0].trim());
            defaultValueFornm2 = Integer.parseInt(weightValues[1].trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final NumberPicker nm1 = (NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);
        nm1.setMinValue(30);
        nm1.setMaxValue(635);
        nm1.setValue(defaultValueFornm1);


        final NumberPicker nm2 = (NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);
        nm2.setMinValue(0);
        nm2.setMaxValue(9);
        nm2.setValue(defaultValueFornm2);


        final RadioButton kgUnitBtn = ((RadioButton) dialog.findViewById(R.id.kgUnit));
        final RadioButton poundsUnitBtn = ((RadioButton) dialog.findViewById(R.id.lbsUnit));


        if (conversionUnitWeight == 0)
            kgUnitBtn.setChecked(true);
        else
            poundsUnitBtn.setChecked(true);

        CompoundButton.OnCheckedChangeListener kgUnitBtnListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;
                conversionUnitWeight = 0;
                double value = nm1.getValue() + ((double) nm2.getValue()) / 10;
                nm1.setMinValue(30);
                nm1.setMaxValue(635);

                nm2.setMinValue(0);
                nm2.setMaxValue(9);

                weightvalue = value;
                value = (value / 2.20462);
                nm1.setValue((int) value);
                nm2.setValue((int) Math.round((value - (int) value) * 10));
            }
        };


        CompoundButton.OnCheckedChangeListener lbsUnitBtnListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;
                conversionUnitWeight = 1;
                double value = nm1.getValue() + ((double) nm2.getValue()) / 10;
                nm1.setMinValue(66);
                nm1.setMaxValue(1400);

                nm2.setMinValue(0);
                nm2.setMaxValue(9);

                weightvalue = value;
                double currentvalueInPounds = value * 2.20462;
                nm1.setValue((int) currentvalueInPounds);
                nm2.setValue((int) Math.round((currentvalueInPounds - (int) currentvalueInPounds) * 10));
            }
        };


        kgUnitBtn.setOnCheckedChangeListener(kgUnitBtnListener);
        poundsUnitBtn.setOnCheckedChangeListener(lbsUnitBtnListener);

        TextView b = ((TextView) dialog.findViewById(R.id.submitBtn));
        b = AppTheme.changeBackgroundColor(b);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((RadioButton) dialog.findViewById(R.id.kgUnit)).isChecked()) {

                    weightvalue = nm1.getValue() + 0.1 * nm2.getValue();
                    setWeight(weightvalue + " kgs");
                }
                //saveMetric(dialog,nm1.getCurrent1() + 0.1 * nm2.getCurrent1(),forDate);
                else if (((RadioButton) dialog.findViewById(R.id.lbsUnit)).isChecked()) {

                    weightvalue = nm1.getValue() + 0.1 * nm2.getValue();
                    //double  weightInkg=(nm1.getCurrent1() + 0.1 * nm2.getCurrent1()/2.20462);
                    setWeight(weightvalue, getActivity().getString(R.string.lbs));
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/2.20462,forDate);

                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.lengthUnits).setVisibility(View.GONE);

    }


    public boolean verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    private void initViews(View v) {


        Drawable myDrawable = getResources().getDrawable(R.mipmap.radio_btn_active);
        myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);


        iv_profile_img = (CircularImageView) v.findViewById(R.id.iv_profile);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyProfile", Context.MODE_PRIVATE);

        String userName = prefs.getString("userName", "");
        String prefsFname = prefs.getString("userFname", "");
        String prefsLname = prefs.getString("userLname", "");
        String prefsCountry = prefs.getString("userCountry", "");
        String prefsCity = prefs.getString("userCity", "");
        prefsbirthDate = prefs.getString("userbirthDate", "");
        String prefsWeight = prefs.getString("userWeight", "");
        String prefsHeight = prefs.getString("userHeight", "");

        heightWhichIhaveToSaveToPrefs = prefsHeight;
        weightWhichIHaveToSaveToPrefs = prefsWeight;

        heightWhichIHaveToSaveToServer = prefsHeight.contains("feet") ? getHeightIncms(prefsHeight) : prefsHeight;
        weightWhichIHaveToSaveToServer = prefsWeight.contains("lbs") ? getWeightInKg(prefsWeight) : prefsWeight;

        String log = "heightWhichIhaveToSaveToPrefs" + heightWhichIhaveToSaveToPrefs
                + " weightWhichIHaveToSaveToPrefs " + weightWhichIHaveToSaveToPrefs
                + " heightWhichIHaveToSaveToServer" + heightWhichIHaveToSaveToServer
                + " weightWhichIHaveToSaveToServer " + weightWhichIHaveToSaveToServer;


        Log.e(TAG, log);
        String prefsGender = prefs.getString("userGender", "0");
        userAvatarURL = prefs.getString("userAvatarURL", "");
        String zipCode = prefs.getString("zipCode", "");


        Log.e(TAG, "username is " + userName + " weight is " + prefsWeight + " height is " + prefsHeight);


        Log.e(TAG, "initilizing views the gender is  " + prefsGender);


        Log.e(TAG, "user avatar url is   " + userAvatarURL);

        sessid = new Helper().getSessionId(getActivity());
        uid = new Helper().getUserId(getActivity());

        if (userAvatarURL != "")
            Picasso.with(getActivity()).load(userAvatarURL.replaceAll(" ", "%20")).placeholder(Helper.GetPlaceHolder(prefs)).error(Helper.GetPlaceHolder(prefs)).into(iv_profile_img);
        else
            Picasso.with(getActivity()).load(Helper.GetPlaceHolder(prefs)).placeholder(Helper.GetPlaceHolder(prefs)).error(Helper.GetPlaceHolder(prefs)).into(iv_profile_img);


        takeImageClass = new TakeImageClass(getActivity());

        iv_profile_img.setOnClickListener(this);


        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        btn_edit = (Button) v.findViewById(R.id.edit);
        btn_edit.setOnClickListener(this);
        btn_edit = AppTheme.changeBackgroundColor(btn_edit);


        editPhotoTV = (TextView) v.findViewById(R.id.editPhotoTV);
        et_userName = (TextView) v.findViewById(R.id.userName);

        et_userName.setText(userName);
        editPhotoTV.setTextColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        editPhotoTV.setOnClickListener(this);
        et_fname = (EditText) v.findViewById(R.id.et_fname);
        et_fname.setText(prefsFname);
        til_city = (TextInputLayout) v.findViewById(R.id.til_city);
        til_zip = (TextInputLayout) v.findViewById(R.id.til_zip);
        et_lname = (EditText) v.findViewById(R.id.et_lname);
        et_lname.setText(prefsLname);
        tv_height = (EditText) v.findViewById(R.id.tv_height);

        auto_zip = (EditText) v.findViewById(R.id.auto_zip);
        auto_zip.setText(zipCode);
        if (!prefsHeight.contains("cms") && !prefsHeight.contains("feet"))
            tv_height.setText(prefsHeight + " cms");
        else
            tv_height.setText(prefsHeight);
        tv_height.setOnClickListener(this);
        tv_weight = (EditText) v.findViewById(R.id.tv_weight);
        if (!prefsWeight.contains("kgs") && !prefsWeight.contains("lbs"))
            tv_weight.setText(prefsWeight + " kgs");
        else
            tv_weight.setText(prefsWeight);
        tv_weight.setOnClickListener(this);
        tv_birthdate = (EditText) v.findViewById(R.id.tv_birthdate);


        tv_birthdate.setText(prefsbirthDate);
        tv_birthdate.setOnClickListener(this);
        radio_group = (RadioGroup) v.findViewById(R.id.radio_group);
        if (!prefsGender.isEmpty()) {

            if (prefsGender.equalsIgnoreCase("male") || prefsGender.equalsIgnoreCase("0")) {
                Log.e(TAG, "i am inside pref gender checking male true");
                prefsGender = "0";
            } else {

                Log.e(TAG, "i am inside pref gender checking female true");
                prefsGender = "1";
            }

            RadioButton rb = (RadioButton) radio_group.getChildAt(Integer.parseInt(prefsGender));
            Log.e(TAG, "rb radio button text is " + rb.getText());
            rb.setChecked(true);
        }
        rb_male = (RadioButton) v.findViewById(R.id.rb_male);

        booCity = true;
        auto_city = (EditText) v.findViewById(R.id.auto_city);
        auto_country = (EditText) v.findViewById(R.id.auto_country);
        auto_city.setText(prefsCity);

        auto_country.setText(prefsCountry);

        if (prefsCountry.equalsIgnoreCase("United States"))
            til_zip.setVisibility(View.VISIBLE);
        else
            til_city.setVisibility(View.VISIBLE);

        auto_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View country) {

                auto_city.setText("");
                setCountry();

                final Dialog dlg = new Dialog(getActivity());
                LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = li.inflate(R.layout.countrycitylistview, null, false);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(v);

                EditText inputSearch = (EditText) v.findViewById(R.id.inputSearch);

                inputSearch.setHint("Search Country");

                final ListView countryList = (ListView) v.findViewById(R.id.countrycitylist);

                countryList.setAdapter(countryAdapter);
                dlg.show();

                inputSearch.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        // When user changed the Text
                        countryAdapter.getFilter().filter(cs);
                        countryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                    }
                });


                countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dlg.dismiss();
                        String selectedFromList = (countryList.getItemAtPosition(position).toString());
                        Log.e(TAG, "selected country is " + selectedFromList);
                        auto_country.setText(selectedFromList);

                        if (!selectedFromList.equalsIgnoreCase("United States")) {
                            //
                            til_city.setVisibility(View.VISIBLE);
                            til_zip.setVisibility(View.GONE);
                            auto_city.setText("");
                        } else {
                            til_city.setVisibility(View.GONE);
                            til_zip.setVisibility(View.VISIBLE);
                            auto_city.setText("city");
                        }

                    }
                });


            }
        });

        auto_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View country) {


                if (auto_country.length() > 0 && !auto_country.getText().toString().equalsIgnoreCase("United States")) {
                    setCity(auto_country.getText().toString());

                    //now in register screen, set city is called inside the country list touch listener,
                    //this cannot be done here because here, user can go directly to city, as country is already filled
                    // there city/zip is hidden untill right country is selected.
                    //and if user goes direcylt to cities in that case if set city is not called then city array would point to null
                    //if we initilize the city array it will give emply listview, which should not be the case.

                    cityAdapter = new ArrayAdapter<String>(getActivity(), R.layout.auto_complete, R.id.auto_text, city);

                    final Dialog dlg = new Dialog(getActivity());
                    LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = li.inflate(R.layout.countrycitylistview, null, false);
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlg.setContentView(v);

                    EditText inputSearch = (EditText) v.findViewById(R.id.inputSearch);

                    inputSearch.setHint("Search City");

                    final ListView city = (ListView) v.findViewById(R.id.countrycitylist);

                    city.setAdapter(cityAdapter);
                    dlg.show();

                    inputSearch.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                            // When user changed the Text
                            cityAdapter.getFilter().filter(cs);
                            cityAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            // TODO Auto-generated method stub
                        }
                    });
                    city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            dlg.dismiss();
                            String selectedFromList = (city.getItemAtPosition(position).toString());
                            Log.e(TAG, "selected country is " + selectedFromList);
                            auto_city.setText(selectedFromList);

                        }
                    });


                }

            }
        });






       /* auto_country.setThreshold(1);//will start working from first character
        auto_country.setAdapter(countryAdapter);
        auto_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {


                EditText EditText = (EditText) view.v.findViewById(R.id.auto_text);
                String country = EditText.getText().toString();
                if (country.equalsIgnoreCase("united states")) {
                    booCity = false;
                    auto_city.setVisibility(View.GONE);
                } else {
                    booCity = true;
                    auto_city.setVisibility(View.VISIBLE);
                    auto_city.setText("");
                    setCity();
                }

            }
        });*/


        showDatePicker();


        setEditableAll(editTransactionGoingOn, btn_edit);
        //colored white again, because in My device screen we are using the same icon.
        //so if user comes here from my device screen we will see the changed icon in My device
    }

    private String getWeightInKg(String prefsWeight) {

        Log.e(TAG, prefsWeight);

        double weight = 66;
        try {
            weight = Double.parseDouble(prefsWeight.replace("lbs", "").trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Math.round((weight / 2.20462) * 10.0) / 10.0 +  " kgs";
    }

    private String getHeightIncms(String prefsHeight) {

        Log.e(TAG, prefsHeight);

        prefsHeight = prefsHeight.replace("inch", "");

        int nm1 = 170, nm2 = 0;

        try {

            String heightValues[] = prefsHeight.split("feet");
            nm1 = Integer.parseInt(heightValues[0].trim());
            nm2 = Integer.parseInt(heightValues[1].trim());

        } catch (Exception e) {
            e.printStackTrace();
        }


        return Helper.getCMFromFootsAndInch(nm1,nm2);
    }

    public void setCountry() {
        cursor = assetsDbAdapter.fetchQuery("country_list");
        country = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            country[i] = cursor.getString(0);
            Log.e(TAG, "country is " + country[i]);
            cursor.moveToNext();
        }

        Arrays.sort(country);
        countryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.auto_complete, R.id.auto_text, country);

    }

    public void setCity(String selectedCountry) {
        cursor = assetsDbAdapter.fetchQuery("city_list", new String[]{selectedCountry});
        //IDK why this casting is happening
        city = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++) {
            city[i] = cursor.getString(1);
            Log.e(TAG, "city is " + city[i]);
            cursor.moveToNext();
        }

        Arrays.sort(city);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "1 here on activity result, the result code is " + resultCode + " the get activity result ok is  " + getActivity().RESULT_OK);

        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "here on activity result, the result code is " + resultCode + " the get activity result ok is  " + getActivity().RESULT_OK);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case TakeImageClass.REQUEST_CODE_TAKE_PICTURE:
                    takeImageClass.onActivityResult(requestCode, resultCode, data);
                    break;
                case TakeImageClass.REQUEST_CODE_GALLERY:
                    takeImageClass.onActivityResult(requestCode, resultCode, data);
                    break;
                case TakeImageClass.REQUEST_CODE_CROP_IMAGE:
                    takeImageClass.onActivityResult(requestCode, resultCode, data);
                    if (TakeImageClass.sImagePath != null) {
                        bitmap = BitmapFactory.decodeFile(TakeImageClass.sImagePath);
                        if (bitmap != null) {
                            iv_profile_img.setImageBitmap(bitmap);
                        }
                    }
            }
        }

    }

    public void showDatePicker() {


        Log.e(TAG,"date value is " + prefsbirthDate);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month =  calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Log.e(TAG,"the initilizing datre is " + year + " "+ month + " " +day);

        try{


            //this formatter is used when the user is logged in from signup screen
            //as this is the format with which we are getting value from the server
            SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
            Date date = format.parse(prefsbirthDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DATE);
            year = cal.get(Calendar.YEAR);

        }
        catch (Exception e)
        {
            e.printStackTrace();

            try{

                //this is the formatter when user have arrived here from sign up
                //as this format is used during sign up

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = format.parse(prefsbirthDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DATE);
                year = cal.get(Calendar.YEAR);

            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }

        }


        Log.e(TAG,"the initilizing datre is " + year + " "+ month + " " +day);

        final SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        datePickerDialog = new DatePickerDialog(getActivity(), R.style.myTheme, new DatePickerDialog.OnDateSetListener() {



            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tv_birthdate.setText(simpleDateFormat.format(newDate.getTime()));
            }

        }, year,month, day);

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }


}
