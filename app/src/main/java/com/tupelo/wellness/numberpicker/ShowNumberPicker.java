package com.tupelo.wellness.numberpicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.tupelo.wellness.R;
import com.tupelo.wellness.activity.SignUpActivity;
import com.tupelo.wellness.helper.AppTheme;


public class ShowNumberPicker {
    private double weightvalue = 10, heightvalue = 10;
    private Activity mActivity;
    private String TAG = ShowNumberPicker.class.getSimpleName();
    String textOfHeight = "";
    String textOfWieght = "";
    public ShowNumberPicker(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @SuppressLint("InlinedApi")
    public void showCustomDialog(int value)//value=0 for height and value=1 for weight
    {
        Dialog dialog = null;
        if (Build.VERSION.SDK_INT >= 11)
            dialog = new Dialog(mActivity, android.R.style.Theme_Holo_Light_Dialog);
        else
            dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.height_weight_dialog);
        dialog.closeOptionsMenu();
        if (value == 0) {
            showDialogForHeight(dialog);
        } else if (value == 1) {
            showDialogForWeight(dialog);
        }
        dialog.show();
    }

    private void showDialogForHeight(final Dialog dialog) {

        Drawable myDrawable = mActivity.getResources().getDrawable(R.mipmap.radio_btn_active);
        myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);


        TextView headerText = (TextView) dialog.findViewById(R.id.headerText);
        headerText.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        headerText.setTextColor(Color.WHITE);
        headerText.setText(R.string.height);



        Log.e(TAG, "text of height is " + textOfHeight);

        int defaultValueFornm1 = 30;
        int defaultValueFornm2 = 0;
        String text = "";
        String[] heightValues;
       if (textOfHeight.contains("feet")) {

            defaultValueFornm1 = 0;
            text = textOfHeight.replace("inch", "");

            conversionUnitLength = 1;

            try {

                heightValues = text.split("feet");
                defaultValueFornm1 = Integer.parseInt(heightValues[0].trim());
                defaultValueFornm2 = Integer.parseInt(heightValues[1].trim());

            } catch (Exception e) {
                e.printStackTrace();
            }

            conversionUnitLength = 0;
        }
        else {
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


        final android.widget.NumberPicker nm1 = (android.widget.NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);
        nm1.setMaxValue(275);
        nm1.setMinValue(30);
        nm1.setValue(defaultValueFornm1);

        final android.widget.NumberPicker nm2 = (android.widget.NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);
        nm2.setMinValue(0);
        nm2.setMaxValue(9);
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
                    ((SignUpActivity)mActivity).setHeight(heightvalue + " cms");
                    textOfHeight = heightvalue + " cms";
                }
                //saveMetric(dialog,nm1.getCurrent1() + 0.1 * nm2.getCurrent1(),forDate);
                else {
                    heightvalue = nm1.getValue() + 0.1 * nm2.getValue();
                    double heightInCm = (nm1.getValue() + 0.1 * nm2.getValue()) / 0.0328084;
                    int feet = nm1.getValue();
                    int inch = nm2.getValue();
                    ((SignUpActivity)mActivity).setHeight(feet, inch);
                    textOfHeight = (int) feet + " feet" + " " + (int) inch + " inch";
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/0.393701,forDate);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.weightUnits).setVisibility(View.GONE);
    }

    private int conversionUnitWeight, conversionUnitLength;


    private void showDialogForWeight(final Dialog dialog) {

        Drawable myDrawable = mActivity.getResources().getDrawable(R.mipmap.radio_btn_active);
        myDrawable.setColorFilter(Color.parseColor(AppTheme.getInstance().colorPrimary), PorterDuff.Mode.SRC_ATOP);


        TextView header = (TextView) dialog.findViewById(R.id.headerText);
        header.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        header.setTextColor(Color.WHITE);
        header.setText(R.string.weight);


        Log.e(TAG, "text of weight is " + textOfWieght);

        int defaultValueFornm1 = 30;
        int defaultValueFornm2 = 0;
        String text = "";
        String[] weightValues;
        if (textOfWieght.contains("lbs")) {
            defaultValueFornm1 = 66;
            text = textOfWieght.replace("lbs", "");
            conversionUnitWeight = 1;
        } else  {

            defaultValueFornm1 = 30;
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

        final android.widget.NumberPicker nm1 = (android.widget.NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);

        nm1.setMinValue(30);
        nm1.setMaxValue(635);
        nm1.setValue(defaultValueFornm1);


        final android.widget.NumberPicker nm2 = (android.widget.NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);

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
                    ((SignUpActivity)mActivity).setWeight(weightvalue + " kgs");

                    textOfWieght = weightvalue + " kgs";
                }
                //saveMetric(dialog,nm1.getCurrent1() + 0.1 * nm2.getCurrent1(),forDate);
                else if (((RadioButton) dialog.findViewById(R.id.lbsUnit)).isChecked()) {

                    weightvalue = nm1.getValue() + 0.1 * nm2.getValue();
                    //double  weightInkg=(nm1.getCurrent1() + 0.1 * nm2.getCurrent1()/2.20462);
                    ((SignUpActivity)mActivity).setWeight(weightvalue, mActivity.getString(R.string.lbs));
                    textOfWieght = weightvalue + " " + mActivity.getString(R.string.lbs);
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/2.20462,forDate);

                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.lengthUnits).setVisibility(View.GONE);

    }



/*
    private void showDialogForHeight(final Dialog dialog) {
        dialog.setTitle(R.string.height);
        final NumberPicker nm1 = (NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);
        nm1.setRange(10, 275);
        nm1.setCurrent(170);
        final NumberPicker nm2 = (NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);
        nm2.setRange(0, 9);
        nm2.setCurrent(0);
        RadioButton cmUnitBtn = ((RadioButton) dialog.findViewById(R.id.cmUnit));
        RadioButton incheUnitBtn = ((RadioButton) dialog.findViewById(R.id.incheUnit));
        if (conversionUnitLength == 0)
            cmUnitBtn.setChecked(true);
        else
            incheUnitBtn.setChecked(true);

        OnCheckedChangeListener cmUnitBtnListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;

                double value = nm1.getCurrent1() + ((double) nm2.getCurrent1()) / 12;
                nm1.setRange(10, 275);
                nm2.setRange(0, 9);
                heightvalue = value;
                value = (value / 0.0328084);
                nm1.setCurrent((int) value);
                nm2.setCurrent((int) Math.round((value - (int) value) * 10));
            }
        };
        OnCheckedChangeListener inchUnitBtnListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;

                double value = nm1.getCurrent1() + ((double) nm2.getCurrent1()) / 10;
                nm1.setRange(0, 9);
                nm2.setRange(0, 11);
                heightvalue = value;
                value = value * 0.0328084;
                nm1.setCurrent((int) value);
                nm2.setCurrent((int) Math.round((value - (int) value) * 12));
            }
        };

        cmUnitBtn.setOnCheckedChangeListener(cmUnitBtnListener);
        incheUnitBtn.setOnCheckedChangeListener(inchUnitBtnListener);
        if (heightvalue > 0) {
            *//*nm1.setCurrent((int)heightvalue);
			nm2.setCurrent((int)((heightvalue - (int)heightvalue) * 10));*//*
        }
        ((Button) dialog.findViewById(R.id.submitBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((RadioButton) dialog.findViewById(R.id.cmUnit)).isChecked()) {
                    heightvalue = nm1.getCurrent1() + 0.1 * nm2.getCurrent1();
                    //((SignUpActivity)mActivity).setHeight(heightvalue,mActivity.getString(R.string.cm));
                    ((SignUpActivity) mActivity).setHeight(heightvalue + " cms");
                }
                //saveMetric(dialog,nm1.getCurrent1() + 0.1 * nm2.getCurrent1(),forDate);
                else {
                    heightvalue = nm1.getCurrent1() + 0.1 * nm2.getCurrent1();
                    double heightInCm = (nm1.getCurrent1() + 0.1 * nm2.getCurrent1()) / 0.0328084;
                    int feet = nm1.getCurrent1();
                    int inch = nm2.getCurrent1();
                    ((SignUpActivity) mActivity).setHeight(feet, inch);
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/0.393701,forDate);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.weightUnits).setVisibility(View.GONE);
    }

    private int conversionUnitWeight*//*value 0 for KG and 1 for lbs*//*, conversionUnitLength*//*value 0 for CM and 1 for inch*//*;

    private void showDialogForWeight(final Dialog dialog) {

        dialog.setTitle(R.string.weight);
        final NumberPicker nm1 = (NumberPicker) dialog.findViewById(R.id.metricValueBeforeDecimalPicker);
        nm1.setRange(10, 560);
        nm1.setCurrent(64);
        final NumberPicker nm2 = (NumberPicker) dialog.findViewById(R.id.metricValueAfterDecimalPicker);
        nm2.setRange(0, 9);
        nm2.setCurrent(0);
        final RadioButton kgUnitBtn = ((RadioButton) dialog.findViewById(R.id.kgUnit));
        final RadioButton poundsUnitBtn = ((RadioButton) dialog.findViewById(R.id.lbsUnit));
        if (conversionUnitWeight == 0)
            kgUnitBtn.setChecked(true);
        else
            poundsUnitBtn.setChecked(true);

        OnCheckedChangeListener kgUnitBtnListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;
                double value = nm1.getCurrent1() + ((double) nm2.getCurrent1()) / 10;
                nm1.setRange(10, 560);
                weightvalue = value;
                value = (value / 2.20462);
                nm1.setCurrent((int) value);
                nm2.setCurrent((int) Math.round((value - (int) value) * 10));
            }
        };
        OnCheckedChangeListener lbsUnitBtnListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked)
                    return;
                double value = nm1.getCurrent1() + ((double) nm2.getCurrent1()) / 10;
                nm1.setRange(22, 1235);
                weightvalue = value;
                double currentvalueInPounds = value * 2.20462;
                nm1.setCurrent((int) currentvalueInPounds);
                nm2.setCurrent((int) Math.round((currentvalueInPounds - (int) currentvalueInPounds) * 10));
            }
        };


        kgUnitBtn.setOnCheckedChangeListener(kgUnitBtnListener);
        poundsUnitBtn.setOnCheckedChangeListener(lbsUnitBtnListener);
		*//*if(weightvalue > 0)
		{
			nm1.setCurrent((int)weightvalue);
			nm2.setCurrent((int)((weightvalue - (int)weightvalue) * 10));
		}*//*
        ((Button) dialog.findViewById(R.id.submitBtn)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((RadioButton) dialog.findViewById(R.id.kgUnit)).isChecked()) {
                    weightvalue = nm1.getCurrent1() + 0.1 * nm2.getCurrent1();
                    ((SignUpActivity) mActivity).setWeight(weightvalue + " kgs");
                }
                //saveMetric(dialog,nm1.getCurrent1() + 0.1 * nm2.getCurrent1(),forDate);
                else if (((RadioButton) dialog.findViewById(R.id.lbsUnit)).isChecked()) {
                    weightvalue = nm1.getCurrent1() + 0.1 * nm2.getCurrent1();
                    //double  weightInkg=(nm1.getCurrent1() + 0.1 * nm2.getCurrent1()/2.20462);
                    ((SignUpActivity) mActivity).setWeight(weightvalue, mActivity.getString(R.string.lbs));
                }
                //saveMetric(dialog,(nm1.getCurrent1() + 0.1 * nm2.getCurrent1())/2.20462,forDate);

                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.lengthUnits).setVisibility(View.GONE);

    }*/
}
