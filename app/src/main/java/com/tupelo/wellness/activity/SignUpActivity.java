package com.tupelo.wellness.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.GetterSetter;
import com.tupelo.wellness.R;
import com.tupelo.wellness.database.AssetsDbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.helper.SharedPreference;
import com.tupelo.wellness.helper.WalkingPrefrence;
import com.tupelo.wellness.parcer.Jsonparser;

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
import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private double weightvalue = 10, heightvalue = 10;
    private ProgressDialog pDialog;
    private Context context;
    private String TAG = "SignUpActivity";
    private EditText et_fname, et_lname, et_username, et_email, et_password, et_con_password;
    EditText tv_birthdate, tv_height, tv_weight, auto_zip;
    EditText auto_city;
    TextView auto_country;
    ArrayAdapter<String> countryAdapter;
    private RadioGroup radio_group;
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
    boolean booCity;
    public ImageView btn_register;
    SharedPreference spMain;
    int isEmailFilter, isUserName;
    String emaildomainFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        context = SignUpActivity.this;
        spMain = spMain.getInstance(context);

        try {
            assetsDbAdapter = new AssetsDbAdapter(SignUpActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initViews();

        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }
        LinearLayout backgroundColor = (LinearLayout) this.findViewById(R.id.login_background);
        backgroundColor.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
    }

    @Override
    public void onBackPressed() {
        // new Helper().setActivityBackAnimation(SignInActivity.this);
        goToLoginOrRegister(new View(SignUpActivity.this));
    }

    private void initViews() {


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        SharedPreferences prefs = getSharedPreferences("MyCorpProfile", MODE_PRIVATE);
        try {
            isEmailFilter = Integer.parseInt(prefs.getString("isEmailFilter", ""));
            isUserName = Integer.parseInt(prefs.getString("isUsername", ""));
        }catch (Exception e){
            e.printStackTrace();
        }



        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);

        et_username = (EditText) findViewById(R.id.et_username);

        if (isUserName == 0) {
            et_username.setVisibility(View.GONE);
        } else {
            et_username.setVisibility(View.VISIBLE);
        }

        et_username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                if (arg0.toString().contains(" ")) {
                    String result = arg0.toString().replaceAll(" ", "");
                    et_username.setText(result);
                    et_username.setSelection(result.length());
                }
            }
        });


        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_con_password = (EditText) findViewById(R.id.et_cnfrm_password);
        tv_height = (EditText) findViewById(R.id.tv_height);
        tv_height.setOnClickListener(this);
        tv_weight = (EditText) findViewById(R.id.tv_weight);
        tv_weight.setOnClickListener(this);
        tv_birthdate = (EditText) findViewById(R.id.tv_birthdate);
        tv_birthdate.setOnClickListener(this);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        rb_male.setChecked(true);
        booCity = true;
        auto_city = (EditText) findViewById(R.id.auto_city);

        auto_zip = (EditText) findViewById(R.id.auto_zip);
        auto_country = (TextView) findViewById(R.id.auto_country);


        et_lname.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE) {
                            // the user is done typing.
                            countryWork();

                            //                       et_lname.setCursorVisible(false);
                            Log.e(TAG, "Done Typing");
                            return true; // consume.
                        }
                        return false; // pass on to other listeners.
                    }
                });


        et_fname.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            // the user is done typing.
                            et_lname.setCursorVisible(true);

                            et_lname.requestFocus();
                            //                       et_lname.setCursorVisible(false);
                            Log.e(TAG, "Done Typing");
                            return true; // consume.
                        }
                        return false; // pass on to other listeners.
                    }
                });



    /*    auto_zip.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE) {
                            // the user is done typing.

                            Dialog dialog = null;
                            if (Build.VERSION.SDK_INT >= 11)
                                dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog);
                            else
                                dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.height_weight_dialog);
                            dialog.closeOptionsMenu();
                            showDialogForHeight(dialog);

                            Log.e(TAG, "Done Typing");
                            return true; // consume.
                        }
                        return false; // pass on to other listeners.
                    }
                });*/


        auto_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View country) {


                countryWork();

            }
        });


        auto_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View country) {


                if (auto_country.length() > 0) {

                    cityAdapter = new ArrayAdapter<String>(SignUpActivity.this, R.layout.auto_complete, R.id.auto_text, city);

                    final Dialog dlg = new Dialog(SignUpActivity.this);
                    LayoutInflater li = (LayoutInflater) SignUpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    city.setOnItemClickListener(new OnItemClickListener() {
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


                TextView textView = (TextView) view.findViewById(R.id.auto_text);
                String country = textView.getText().toString();
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


        btn_register = (ImageView) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        Drawable myDrawable = btn_register.getDrawable();
        myDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        btn_register.setImageDrawable(myDrawable);
        showDatePicker();


        et_lname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_lname.setCursorVisible(true);
            }
        });


        //colored white again, because in My device screen we are using the same icon.
        //so if user comes here from my device screen we will see the changed icon in My device
    }


    private void countryWork() {

        auto_country.setCursorVisible(true);

        et_lname.setCursorVisible(false);


        setCountry();

        final Dialog dlg = new Dialog(SignUpActivity.this);
        LayoutInflater li = (LayoutInflater) SignUpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


        countryList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dlg.dismiss();
                String selectedFromList = (countryList.getItemAtPosition(position).toString());
                Log.e(TAG, "selected country is " + selectedFromList);
                auto_country.setText(selectedFromList);

                TextInputLayout til_city = (TextInputLayout) SignUpActivity.this.findViewById(R.id.til_city);
                TextInputLayout til_zip = (TextInputLayout) SignUpActivity.this.findViewById(R.id.til_zip);

                if (!selectedFromList.equalsIgnoreCase("United States")) {

                    til_city.setVisibility(View.VISIBLE);
                    til_zip.setVisibility(View.GONE);
                    auto_city.setText("");
                    setCity(selectedFromList);
                } else {
                    til_city.setVisibility(View.GONE);
                    til_zip.setVisibility(View.VISIBLE);
                    auto_city.setText("city");
                }

            }
        });

    }

    @Override
    public void onClick(View v) {

        Dialog dialog = null;
        if (Build.VERSION.SDK_INT >= 11)
            dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog);
        else
            dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.height_weight_dialog);
        dialog.closeOptionsMenu();

        switch (v.getId()) {
            case R.id.btn_register:
                signup();
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
        }
    }

    public void setHeight(String height) {
        tv_height.setText(height);
        this.heightWhichIhaveToSaveToPrefs = height;
        this.heightWhichIHaveToSaveToServer = height;
        Log.e("height", this.heightWhichIHaveToSaveToServer);
    }

    public void setHeight(double feet, double inch) {
        tv_height.setText((int) feet + " feet" + " " + (int) inch + " inch");
        this.heightWhichIhaveToSaveToPrefs = (int) feet + " feet" + " " + (int) inch + " inch";
        //  this.heightWhichIHaveToSaveToServer = Math.round((feet + (inch / 12)) / 0.0328084) + " cms";
        this.heightWhichIHaveToSaveToServer = Helper.getCMFromFootsAndInch(feet,inch);
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
        this.weightWhichIHaveToSaveToServer = Math.round((weight / 2.20462) * 10.0) / 10.0 + " kgs";
        Log.e("weight", this.weightWhichIHaveToSaveToServer);
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


        if (textOfHeight.contains("feet")) {

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

        } else {
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


        }

        final NumberPicker nm1 = (NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);

        nm1.setMaxValue(275);
        nm1.setMinValue(30);


        final NumberPicker nm2 = (NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);
        nm2.setMinValue(0);
        nm2.setMaxValue(9);


        if (textOfHeight.contains("feet")) {
            nm2.setMinValue(0);
            nm2.setMaxValue(11);

            nm1.setMinValue(0);
            nm1.setMaxValue(9);


        } else {
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
                    double heightInCm = (nm1.getValue() + 0.1 * nm2.getValue()) / 0.0328084;
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
            defaultValueFornm1 = 66;
            text = textOfWieght.replace("lbs", "");
            conversionUnitWeight = 1;
        } else {

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


        if (textOfWieght.contains("lbs")) {

            nm1.setMinValue(66);
            nm1.setMaxValue(1400);

            nm2.setMinValue(0);
            nm2.setMaxValue(9);

        } else {

            nm1.setMinValue(30);
            nm1.setMaxValue(635);

            nm2.setMinValue(0);
            nm2.setMaxValue(9);

        }


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
                    setWeight(weightvalue, context.getString(R.string.lbs));
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/2.20462,forDate);

                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.lengthUnits).setVisibility(View.GONE);

    }


    private void signup() {
        new Helper().hidekeyboard(this, et_password);
        if (validation()) {
            if (CheckConnection.isConnection(SignUpActivity.this)) {
                String username = "";
                String fname = et_fname.getText().toString().trim();
                String lname = et_lname.getText().toString().trim();
                if (isUserName == 1) {
                    username = et_username.getText().toString().trim();
                } else {
                    username = et_email.getText().toString().trim();
                }
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                //String height = tv_height.getText().toString().trim();
                //String weight = tv_weight.getText().toString().trim();
                String dob = tv_birthdate.getText().toString().trim();
                rb_gender = (RadioButton) findViewById(radio_group.getCheckedRadioButtonId());
                String gender = rb_gender.getText().toString().trim();
                String city = auto_city.getText().toString().trim();
                String country = auto_country.getText().toString().trim();
                // new SignUpAsync(SignUpActivity.this).execute(fname, lname, username, email, password, height, weight, dob, gender, city, country, "33");
                sessionIdCheck(fname, lname, username, email, password, heightWhichIHaveToSaveToServer, weightWhichIHaveToSaveToServer, dob, gender, city, country, WalkingPrefrence.getInstance().roleId);

            } else
                new Helper().showNoInternetToast(SignUpActivity.this);
        }
    }


    private void sessionIdCheck(final String fname, final String lname, final String username, final String email, final String password, final String height, final String weight, final String dob, final String gender, final String city, final String country, final String roleId) {
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject jsonObject = json.getJSONObject("#data");
                            Log.e(TAG, "session id is " + jsonObject.getString("sessid"));
                            signUpVolley(jsonObject.getString("sessid"), fname, lname, username, email, password, height, weight, dob, gender, city, country, roleId);


                        } catch (JSONException j) {
                            j.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(SignUpActivity.this, R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                        Log.e("ERROR", "error => " + error.getMessage());
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                String method = "system.connect";
                params.put("method", method);

                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);


    }

    private void signUpVolley(final String mSessId, final String fname, final String lname, final String username, final String email, final String password, final String height, final String weight, final String dob, final String gender, final String city, final String country, final String roleid) {

        String url = Constants.BASE_URL;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result;
                        pDialog.hide();

                        if (!response.equals("Connection Timeout")) {
                            Jsonparser jsonparser = new Jsonparser(response);
                            Log.e(TAG, " signupasyncresponse here " + response);
                            Log.e("signnnnnnn here",response.toString());


                            try {
                                JSONObject user = null;
                                user = new JSONObject(response).getJSONObject("#data").getJSONObject("user");

                                SharedPreferences pref1 = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor1 = pref1.edit();
                                editor1.putString("userId", user.getString("userId")); // Storing string
                                editor1.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }





                            result = jsonparser.getError();

                            Log.e(TAG, result);
//            Log.e("message", message);
                            if (result.equals("0")) {

                                Log.e(TAG, result);

                                jsonparser.setTupeloSignIn(SignUpActivity.this, "signup");
                            }
                            getterSetter = jsonparser.getSignUpInfo(SignUpActivity.this);

                        } else
                            result = "-3";

                        Log.e(TAG, result);

                        btn_register.setEnabled(true);
                        if (result.equals("0")) {

                            String finalGender;
                            if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("0"))
                                finalGender = "0";
                            else
                                finalGender = "1";


                            SharedPreferences.Editor editor = getSharedPreferences("MyProfile", MODE_PRIVATE).edit();
                            editor.putString("userName", username);
                            editor.putString("userFname", fname);
                            editor.putString("userLname", lname);
                            editor.putString("userCity", city);
                            editor.putString("userWeight", weightWhichIHaveToSaveToPrefs);
                            editor.putString("userHeight", heightWhichIhaveToSaveToPrefs);
                            editor.putString("userGender", finalGender);
                            editor.putString("userCountry", country);
                            editor.putString("userbirthDate", dob);
                            editor.putString("zipCode", auto_zip.getText().toString());
                            editor.putString("heightPref",heightWhichIhaveToSaveToPrefs.contains("feet") ? "ft" : "cms");
                            editor.commit();


                            ((SignUpActivity) SignUpActivity.this).afterSignUpSuccessFully(getterSetter);
                        } else if (result.equals("1")) {
                            Toast.makeText(SignUpActivity.this, SignUpActivity.this.getString(R.string.signup_server_err), Toast.LENGTH_SHORT).show();
                        } else if (result.equals("-3")) {
                            Toast.makeText(SignUpActivity.this, SignUpActivity.this.getString(R.string.universal_err), Toast.LENGTH_SHORT).show();
                        } else if (result.equals("4") || result.equals("6")) {
                            Toast.makeText(SignUpActivity.this, SignUpActivity.this.getString(R.string.username_exists_err), Toast.LENGTH_SHORT).show();
                        } else if (result.equals("5") || result.equals("7")) {
                            Toast.makeText(SignUpActivity.this, SignUpActivity.this.getString(R.string.email_exists_err), Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(SignUpActivity.this, R.string.network_not_connected, Toast.LENGTH_SHORT).show();

                        Log.e("ERROR", "error => " + error.getMessage());
                    }
                }
        ) {
            // this is the relevant method
            @Override
            protected Map<String, String> getParams() {
                Log.e(TAG, "user name is " + username);
                String method = "userV2.register_challengeV2";

                String enteredHeightSiUnit = heightWhichIhaveToSaveToPrefs.contains("feet") ? "ft" : "cms";
                String enteredWeightSiUnit = weightWhichIHaveToSaveToPrefs.contains("lbs") ? "lbs" : "kgs";


                Map<String, String> params = new HashMap<String, String>();
                params = new Helper().addRequiredParams(params, method);
                params.put("method", method);
                params.put("sessid", mSessId);
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("uname", username);
                params.put("email", email);
                params.put("password", password);
                params.put("height", height);
                params.put("heightPref", enteredHeightSiUnit);
                params.put("weight", weight);
                params.put("weightPref", enteredWeightSiUnit);
                params.put("birthdate", dob);
                params.put("gender", gender);
                params.put("city", city);
                params.put("zip", auto_zip.getText().toString());
                params.put("country", country);
                params.put("roleid", roleid);
                Log.e(TAG, params.toString());
                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest);


    }

    private boolean validation() {
        String fname = et_fname.getText().toString().trim();
        String lname = et_lname.getText().toString().trim();
        String username = et_username.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String con_pass = et_con_password.getText().toString().trim();
        String height = tv_height.getText().toString().trim();
        String weight = tv_weight.getText().toString().trim();
        String dob = tv_birthdate.getText().toString().trim();
        String city = auto_city.getText().toString().trim();
        String country = auto_country.getText().toString().trim();
        if (username.isEmpty() && isUserName == 1) {
            Toast.makeText(this, getString(R.string.empty_user_name_err_txt), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_email_name_err_txt), Toast.LENGTH_SHORT).show();
            return false;

        }

        String[] emailarray = email.split("@");
        if ((isEmailFilter == 1) && !emailarray[1].equals(emaildomainFilter)) {
            Toast.makeText(this, "This email is not allowed to register.\n Use @" + emaildomainFilter, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!new Helper().validEmail(email)) {
            Toast.makeText(this, getString(R.string.email_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_pass_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(this, getString(R.string.pass_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (con_pass.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_pass_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!con_pass.equals(password)) {
            Toast.makeText(this, getString(R.string.pass_mismatch_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (fname.length() == 0) {
            Toast.makeText(this, getString(R.string.fname_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (lname.length() == 0) {
            Toast.makeText(this, getString(R.string.lname_err_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (height.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_height_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (weight.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_weight_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (dob.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_dob_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (radio_group.getCheckedRadioButtonId() == 0) {
            Toast.makeText(this, getString(R.string.select_gender_txt), Toast.LENGTH_SHORT).show();
            return false;
        } else if (country.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_country_txt), Toast.LENGTH_SHORT).show();
            return false;

        } else if (city.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_city_txt), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (country.equalsIgnoreCase("United States")) {
            if (auto_zip.getText().toString().isEmpty()) {
                Toast.makeText(context, getString(R.string.empty_zip_txt), Toast.LENGTH_SHORT).show();
                return false;
            } else if (auto_zip.getText().toString().length() != 5) {
                Toast.makeText(context, getString(R.string.invalid_zip), Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        return true;
    }


    public void afterSignUpSuccessFully(GetterSetter getterSetter) {
        Toast.makeText(this, getString(R.string.signup_success_msg), Toast.LENGTH_SHORT).show();
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        //final Intent intent = new Intent(this, SaveCouncilActivity.class);

        //new code for dropdown
       /* CompanyGroupingsAsync asyncTask = (CompanyGroupingsAsync) new CompanyGroupingsAsync(new CompanyGroupingsAsync.AsyncResponse(){

            @Override
            public void processFinish(ArrayList<ClusterBean> output) {
                intent.putExtra("list", output);
            }

        }).execute();*/


        SharedPreference sharedPref = SharedPreference.getInstance(SignUpActivity.this);
        sharedPref.putBoolean("booCity", booCity);
        sharedPref.putString("username", username);
        sharedPref.putString("password", password);

        showThanksPopup();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }

    public void showDatePicker() {
        final SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, R.style.myTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tv_birthdate.setText(simpleDateFormat.format(newDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
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
        countryAdapter = new ArrayAdapter<String>(this, R.layout.auto_complete, R.id.auto_text, country);

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
//
//    public void setHeight(Double height, String unit) {
//
//        long hei = Math.round(height);
//        this.height = hei + "";
//        this.heightUser = height;
//        tv_height.setText(hei + " " + unit);
//    }

//    public void setHeight(Double heightinInch, Double heighInCm, String unit) {
//        long heinch = Math.round(heightinInch);
//        long hecm = Math.round(heighInCm);
//        this.height = hecm + "";
//        this.heightUser = heighInCm;
//        tv_height.setText(heinch + " " + unit);
//    }

    //weight will store in kg inside db and we will use it in kg throughout the app.


    public void goToLoginOrRegister(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginOrRegister.class);
        startActivity(intent);
        finish();
    }

//    public void setWeight(Double weight, String unit) {
//        int w = weight.intValue();
//        this.weight = w + "";
//        tv_weight.setText(w + " " + unit);
//    }
//
//    public void setWeight(Double weightInlbs, Double weightInkg, String unit) {
//        long welbs = Math.round(weightInlbs);
//        long wekg = Math.round(weightInkg);
//        this.weight = wekg + "";
//        tv_weight.setText(welbs + " " + unit);
//    }
//
    public void showThanksPopup() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.thank_you_popup);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        TextView ok = (TextView) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}