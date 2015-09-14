 package com.zmy.zahomy_app.src;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.zmy.zahomy_app.R;
import com.zmy.zahomy_app.util.DatabaseHandler;
import com.zmy.zahomy_app.util.ServerInteractions;
		 
		public class LoginActivity extends ActionBarActivity {
		    Button btnLogin;
		    EditText inputEmail;
		    EditText inputPassword;
		    TextView loginErrorMsg;
		    LoginTask loginTask;
		    ServerInteractions userFunction;
		    DatabaseHandler db;
		    JSONObject json_user;
		    JSONObject json;
		    String errorMsg;
		    String res; 
		    ProgressBar pbpp;
		    // String email, password;
		    public static String email,password;
		    // JSON Response node names
		    private static String KEY_SUCCESS = "success";
		    private static String KEY_ERROR = "error";
		    private static String KEY_ERROR_MSG = "error_msg";
		    private static String KEY_UID = "uid";
		    private static String KEY_FNAME = "fname";
		    private static String KEY_LNAME = "lname";
		    private static String KEY_EMAIL = "email";
		    private static String KEY_INST_ID = "inst_id";
		    
		    private ProgressDialog mProgress;
		 
		    @SuppressLint("NewApi")
			@Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.log_in);
		        
		       /* mProgress = new ProgressDialog(this, R.style.CustomDialogTheme);
		        //mProgress.setTitle("Loading...");
		        mProgress.setMessage("Please wait...");
		        mProgress.setCancelable(false);
		        mProgress.setIndeterminate(true);*/
		        
		        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		        
		        // Importing all assets like buttons, text fields
		        inputEmail = (EditText) findViewById(R.id.loginEmail);
		        inputPassword = (EditText) findViewById(R.id.loginPassword);
		        btnLogin = (Button) findViewById(R.id.btnLogin);
		        loginErrorMsg = (TextView) findViewById(R.id.login_error);
		        TextView txtT = (TextView) findViewById(R.id.txtTitle);
                Typeface font_b = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");
		        Typeface font_d = Typeface.createFromAsset(this.getAssets(), "fonts/Comfortaa-Bold.ttf");
		        txtT.setTypeface(font_d);
		     
		  
		        // Login button Click Event
		        btnLogin.setOnClickListener(new View.OnClickListener() {
		        	
		            public void onClick(View view) {
		            	
		            String email = inputEmail.getText().toString();
		            String password = inputPassword.getText().toString();
		                
		                if((email.isEmpty())&&(password.isEmpty())){
		                	loginErrorMsg.setText("Enter Email & Password");
		                }else{
		                	
		                mProgress.show();
		                db = new DatabaseHandler(getApplicationContext());
		                //start task
		                MyLoginParams params = new MyLoginParams(email, password);
		                loginTask = new LoginTask();
		                loginTask.execute(params);
		               
		                }
		                
		            }
		        });
		    }
		    
		    private class LoginTask extends AsyncTask<MyLoginParams, Void, JSONObject> {
		        @Override
		        protected JSONObject doInBackground(MyLoginParams... params) {
		        	userFunction = new ServerInteractions();
		        	//userFunction2 = new UserFunctions();
		
		        	email = params[0].email;
		        	password = params[0].password;
		        	
		        	json = userFunction.loginUser(email, password);
		            try {
		                if (json.getString(KEY_SUCCESS) != null) {
		                	errorMsg = "";
		                    res = json.getString(KEY_SUCCESS);
		                    if(Integer.parseInt(res) == 1){
		                        json_user = json.getJSONObject("user");
		                        //jsonInstSettings = json.getJSONObject("settings");
		                    }else{
		                    	 mProgress.dismiss();
		                    	 loginErrorMsg.setText("Incorrect username/password");
		                 }
	                    }
		            }catch (JSONException e) {
		                e.printStackTrace();
		                mProgress.dismiss();
		                loginErrorMsg.setText("Incorrect username/password");
		            }
					return json_user; 
		        }
		        
		        @Override
		        protected void onPostExecute(JSONObject json_user) {        	
		        	try {
		        		loginErrorMsg.setText(errorMsg);
		        		if(isCancelled())        	
						return;
		        		
		        		// Clear all previous data in database
		                userFunction.logoutUser(getApplicationContext());
		                if(Integer.parseInt(res) == 1){
			                // user successfully logged in
			                // Store user details in SQLite Database
			                db.addUser(
			                		json_user.getString(KEY_FNAME), 
			                		json_user.getString(KEY_LNAME), 
			                		json_user.getString(KEY_EMAIL),
			                		password,
			                		json.getString(KEY_UID)
			                		);
			                
				        	// Launch Dashboard Screen
				            Intent dashboard = new Intent(getApplicationContext(), MainZahomyActivity.class);
				            // Close all views before launching Dashboard
				            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				            startActivity(dashboard);
				            // Close Login Screen
				            //mProgress.dismiss();
				            finish();
		                }
		        	} catch(Exception e){
						Log.e(this.getClass().getSimpleName(), "Error Logging in", e);
						showErrorAlert();
					}
		        }
		        private void showErrorAlert() {
					
					try {
						Builder lBuilder = new AlertDialog.Builder(LoginActivity.this);
						lBuilder.setTitle("Login Error");
						lBuilder.setCancelable(false);
						lBuilder.setMessage("Sorry, there was a problem logging you in");
			
						lBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			
							@Override
							public void onClick(DialogInterface pDialog, int pWhich) {
								LoginActivity.this.finish();
							}
							
						});
			
						AlertDialog lDialog = lBuilder.create();
						lDialog.show();
					} catch(Exception e){
						Log.e(this.getClass().getSimpleName(), "Problem showing error dialog.", e);
					}
				}
		    }
		    private static class MyLoginParams {
		        String email, password;        
		
		        MyLoginParams(String email, String password) {
		            this.email = email;
		            this.password = password;
		            
		        }
		    }
		    
}