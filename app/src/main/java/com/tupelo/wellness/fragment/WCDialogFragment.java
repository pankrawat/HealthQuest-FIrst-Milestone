package com.tupelo.wellness.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tupelo.wellness.AppController;
import com.tupelo.wellness.R;
import com.tupelo.wellness.activity.SignInActivity;
import com.tupelo.wellness.helper.CheckConnection;
import com.tupelo.wellness.helper.Constants;
import com.tupelo.wellness.helper.Helper;
import com.tupelo.wellness.parcer.Jsonparser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


@SuppressLint("ValidFragment")
public class WCDialogFragment extends android.support.v4.app.DialogFragment {
	private Activity activity;
	private long serialNo;
	private String TAG = WCDialogFragment.class.getName();
	private ProgressDialog pDialog;
	public WCDialogFragment()
	{
		
	}
	public WCDialogFragment(Activity activity)
	{
		this.activity=activity;
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		if(android.os.Build.VERSION.SDK_INT<=android.os.Build.VERSION_CODES.KITKAT){
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
		}


		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);

		return null;	
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Bundle bundle=getArguments();
		int dialogNo=bundle.getInt("dialogNo");
		serialNo=bundle.getLong("serialNo");
		
		if(dialogNo==1){
			return createForgetPasswordDialog();
		}
		return null;
	}


	private Dialog createForgetPasswordDialog(){
		AlertDialog.Builder builder=null;
		if(Build.VERSION.SDK_INT> Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			builder=new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
		else
			builder=new AlertDialog.Builder(getActivity(),android.R.style.Theme_Dialog);
		builder.setTitle(getString(R.string.pleaseenter));
		View view = getActivity().getLayoutInflater().inflate(R.layout.forget_password,null);
		final EditText editText=(EditText)view.findViewById(R.id.etUsername);

		//new Helper().showKeyBoard(getActivity());
		builder.setView(view);

		builder.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {

				//	new Helper().hidekeyboard(getActivity(),editText);
				if(CheckConnection.isConnection(getActivity())){
					String username=editText.getText().toString().trim();
					if(!username.equals("")){
						//new ForgetPasswordAsync(getActivity()).execute(username)
						sessionIdCheck(username);
					}
					else
						Toast.makeText(getActivity(), getString(R.string.plzprovideusernameid), Toast.LENGTH_SHORT).show();}
				else
					new Helper().showNoInternetToast(getActivity());
			}
		});
		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//new Helper().hidekeyboard(getActivity(),editText);
				dismiss();
			}
		});
		return builder.create();
	}

	private void sessionIdCheck(final String... params)
	{
		pDialog.show();
		StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							JSONObject jsonObject = json.getJSONObject("#data");
							Log.e(TAG,"session id is " + jsonObject.getString("sessid"));
							forgotPasswordVolley(jsonObject.getString("sessid"),params[0]);


						} catch (JSONException j) {
							j.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("ERROR", "error => " + error.getMessage());
						pDialog.hide();
						Toast.makeText(getActivity(), R.string.network_not_connected , Toast.LENGTH_SHORT).show();

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


	public void forgotPasswordVolley(final String... eclipseParams )
	{

		StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
							  pDialog.hide();
							     Log.e(TAG,"Response is " + response);

                                 int index = response.indexOf("{");
						            Log.e(TAG,"index is " + index);
						                  String pesponse = response.substring(index);

					             Log.e(TAG,"pesponse is " + pesponse);
                              String result;

							if (!response.equals("Connection Timeout")) {
								Jsonparser jsonparser = new Jsonparser(response);
								if (jsonparser.forgetPassword())
									  result = "0";
								else
									result  =  "1";
							}
							else
								result = "-3";


						if (result.equals("-3")) {
							Toast.makeText(getActivity(), getActivity().getString(R.string.universal_err), Toast.LENGTH_SHORT).show();
						} else {
							((SignInActivity) getActivity()).afterResetPass(result);
						}

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("ERROR", "error => " + error.getMessage());
						pDialog.hide();
						Toast.makeText(getActivity(), R.string.network_not_connected , Toast.LENGTH_SHORT).show();

					}
				}
		) {
			// this is the relevant method
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				String method = "userV2.resetpwd", mSessId = "";
				params.clear();
				mSessId = eclipseParams[0];

				params = new Helper().addRequiredParams(params,method);
				params.put("method", method);
				params.put("usernameid", eclipseParams[1]);
				params.put("sessid", mSessId);
				return params;
			}
		};


		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(postRequest);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		pDialog.dismiss();
	}
}
