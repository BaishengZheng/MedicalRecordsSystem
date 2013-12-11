package com.example.sslsocket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import android.app.Activity;
import javax.net.ssl.SSLSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NormalUserFromAdminActivity extends Activity{
	
	private Button adminUpdateBtn;
	private Button adminReturnBtn;
	private SSLSocket socket;
	private TextView textViewIDNumAdmin;
	private EditText editTextNameAdmin;
	private EditText editTextAgeAdmin;
	private EditText editTextSexAdmin;
	private EditText editTextMedAllergyAdmin;
	private EditText editTextSSNAdmin;
	private EditText editTextInsuranceAdmin;
	private String userid;
	public static final String LOOKUP_ID = "id";
	public static final String LOOKUP_NAME = "name";
	public static final String LOOKUP_AGE = "age";
	public static final String LOOKUP_SEX = "sex";
	public static final String LOOKUP_MEDICAL_ALLERGY = "medical_allergy";
	public static final String LOOKUP_SSN = "ssn";
	public static final String LOOKUP_INSURANCE_COMPANY = "insurance_company";
	public static final String LOOKUP_USERID = "userid";
	public static final String REQUESTUPDATE = "Request_Update";
	public static final String APPLOGOUT = "App_Log_Out";


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_from_admin);
		
		socket = MySSLSocket.Client_sslSocket;
		
		adminReturnBtn = (Button)findViewById(R.id.buttonReturnAdmin);
		adminReturnBtn.setOnClickListener(adminReturnBtnOnClickListener);
		
		adminUpdateBtn = (Button)findViewById(R.id.buttonUpdateAdmin);
		adminUpdateBtn.setOnClickListener(adminUpdateBtnOnClickListener);
		
		
		Bundle myBundle = getIntent().getExtras();
		
		int id = myBundle.getInt(NormalUserFromAdminActivity.LOOKUP_ID);
		String name = myBundle.getString(NormalUserFromAdminActivity.LOOKUP_NAME);
		int age = myBundle.getInt(NormalUserFromAdminActivity.LOOKUP_AGE);
		String sex = myBundle.getString(NormalUserFromAdminActivity.LOOKUP_SEX);
		String medical_allergy = myBundle.getString(NormalUserFromAdminActivity.LOOKUP_MEDICAL_ALLERGY);
		int ssn = myBundle.getInt(NormalUserFromAdminActivity.LOOKUP_SSN);
		String insurance_company = myBundle.getString(NormalUserFromAdminActivity.LOOKUP_INSURANCE_COMPANY);
		userid = myBundle.getString(NormalUserFromAdminActivity.LOOKUP_USERID);
		
		
		textViewIDNumAdmin = (TextView) findViewById(R.id.textViewIDNumAdmin);
		editTextNameAdmin = (EditText) findViewById(R.id.editTextNameAdmin);
		editTextAgeAdmin = (EditText) findViewById(R.id.editTextAgeAdmin);
		editTextSexAdmin = (EditText) findViewById(R.id.editTextSexAdmin);
		editTextMedAllergyAdmin = (EditText) findViewById(R.id.editTextMedAllergyAdmin);
		editTextSSNAdmin = (EditText) findViewById(R.id.editTextSSNAdmin);
		editTextInsuranceAdmin = (EditText) findViewById(R.id.editTextInsuranceAdmin);
		
		textViewIDNumAdmin.setText(String.valueOf(id));
		editTextNameAdmin.setText(name);
		editTextAgeAdmin.setText(String.valueOf(age));
		editTextSexAdmin.setText(sex);
		editTextMedAllergyAdmin.setText(medical_allergy);
		editTextSSNAdmin.setText(String.valueOf(ssn));
		editTextInsuranceAdmin.setText(insurance_company);
			
	}
	
	private OnClickListener adminReturnBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {		
			Log.d("NormalUserFromAdminActivity", "adminReturnBtnOnClick");	
			finish();
		}
		
	};
	
	private OnClickListener adminUpdateBtnOnClickListener = new OnClickListener() {
		private ObjectOutputStream oos;

		@Override
		public void onClick(View v) {			
			Log.d("NormalUserFromAdminActivity", "adminUpdateBtnOnClick");
			new Thread(new AdminUpdateThread()).start();			
		}		
	};
	
	public void getOut(SSLSocket socket,String message){  
    	BufferedWriter bw = null;  
        try {      	
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                  
            Log.d("NormalUserFromAdminActivity: ", "get the bufffered writer successfully");
            Log.d("Sent message: ", message);
            
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
        	if (bw != null) {
        		try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    }  


	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("NormalUserFromAdminActivity", "onDestroy");
	}
	
	
	class AdminUpdateThread implements Runnable {
		private ObjectOutputStream oos;
		
		@Override
		public void run() {
			update();
		}
		
		public void update() {
			//sent request to update uer's info
			if (socket != null) {
				
				int id = Integer.parseInt(textViewIDNumAdmin.getText().toString().trim());
				String name = editTextNameAdmin.getText().toString().trim();
				int age = Integer.parseInt(editTextAgeAdmin.getText().toString().trim());
				String sex = editTextSexAdmin.getText().toString().trim();
				String medical_allergy = editTextMedAllergyAdmin.getText().toString().trim();
				int ssn = Integer.parseInt(editTextSSNAdmin.getText().toString().trim());
				String insurance_company = editTextInsuranceAdmin.getText().toString().trim();
				MedRecord mr = new MedRecord(id, name, age, sex, medical_allergy, ssn, insurance_company, userid);
				
				//sent request
				getOut(socket, REQUESTUPDATE);
				
				//send the updated MedRecord
				try {
					oos = new ObjectOutputStream(socket.getOutputStream());
					if (mr != null) {
						oos.writeObject(mr);
						oos.flush();
						//oos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}
		
	}


}
