package com.example.sslsocket;

import android.app.Activity;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import javax.net.ssl.SSLSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NormalUserActivity extends Activity{
	
	private Button userLogOutBtn;
	private Button userUpdateBtn;
	private SSLSocket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private MedRecord MR;
	private TextView textViewIDNum;
	private EditText editTextName;
	private EditText editTextAge;
	private EditText editTextSex;
	private EditText editTextMedAllergy;
	private EditText editTextSSN;
	private EditText editTextInsurance;
	public static final String REQUESTONEMEDRECORD = "Request_One_MedRecord";
	public static final String REQUESTUPDATE = "Request_Update";
	public static final String APPLOGOUT = "App_Log_Out";

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient);
		
		socket = MySSLSocket.Client_sslSocket;
		
		userLogOutBtn = (Button)findViewById(R.id.buttonUserLogOut);
		userLogOutBtn.setOnClickListener(userLogOutBtnOnClickListener);
		
		userUpdateBtn = (Button)findViewById(R.id.buttonUpdate);
		userUpdateBtn.setOnClickListener(userUpdateBtnOnClickListener);	
		
		textViewIDNum = (TextView) findViewById(R.id.textViewIDNum);
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextAge = (EditText) findViewById(R.id.editTextAge);
		editTextSex = (EditText) findViewById(R.id.editTextSex);
		editTextMedAllergy = (EditText) findViewById(R.id.editTextMedAllergy);
		editTextSSN = (EditText) findViewById(R.id.editTextSSN);
		editTextInsurance = (EditText) findViewById(R.id.editTextInsurance);
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Thread(new UserRequestMedRecordThread()).start();
		
		/*
		//sent request for the MedRecord
		if (socket != null) {
			getOut(socket, REQUESTONEMEDRECORD);
			
			//wait to get the MedRecordsPackage
			try {		
				ois = new ObjectInputStream(socket.getInputStream());
				MR = (MedRecord) ois.readObject();
				ois.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			if (MR != null) {				
				textViewIDNum.setText(String.valueOf(MR.getId()));
				editTextName.setText(MR.getName());
				editTextAge.setText(String.valueOf(MR.getAge()));
				editTextSex.setText(MR.getSex());
				editTextMedAllergy.setText(MR.getMedAllergy());
				editTextSSN.setText(String.valueOf(MR.getSsn()));
				editTextInsurance.setText(MR.getInsuranceCompany());
			}
			
			
		}	
		*/
			
	}
	
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if (msg == null) {
				Log.d("NormalUserActivity", "User received message but it is null");
				return;
			} 
			
			MedRecord mr = (MedRecord) msg.obj;
			
			if (mr != null) {				
				textViewIDNum.setText(String.valueOf(mr.getId()));
				editTextName.setText(mr.getName());
				editTextAge.setText(String.valueOf(mr.getAge()));
				editTextSex.setText(mr.getSex());
				editTextMedAllergy.setText(mr.getMedAllergy());
				editTextSSN.setText(String.valueOf(mr.getSsn()));
				editTextInsurance.setText(mr.getInsuranceCompany());
			}
			
		}
		
		
	};
	
	public void getOut(SSLSocket socket,String message){  
    	BufferedWriter bw = null;  
        try {      	
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));            
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
	
	
	
	
	private OnClickListener userLogOutBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {		
			Log.d("NormalUserActivity", "LogOutBtnOnClick");
			new Thread(new UserLogoutRequestThread()).start();
			finish();
			
			/*
			try {
				//Tell the server, the app is gonna log out
				getOut(socket, APPLOGOUT);
				Thread.sleep(200);
				
				if (socket != null) {
					socket.close();
				}
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				Log.e("NormalUserActivity", "socket close fails");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			
			
			
		}
		
	};
	
	private OnClickListener userUpdateBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {			
			Log.d("NormalUserActivity", "UpdateBtnOnClick");
			new Thread(new UserUpdateThread()).start();
			
			/*
			//sent request to update uer's info
			if (socket != null) {
				
				int id = Integer.parseInt(textViewIDNum.getText().toString().trim());
				String name = editTextName.getText().toString().trim();
				int age = Integer.parseInt(editTextAge.getText().toString().trim());
				String sex = editTextSex.getText().toString().trim();
				String medical_allergy = editTextMedAllergy.getText().toString().trim();
				int ssn = Integer.parseInt(editTextSSN.getText().toString().trim());
				String insurance_company = editTextInsurance.getText().toString().trim();
				String userid = MR.getUserid();	
				MedRecord mr = new MedRecord(id, name, age, sex, medical_allergy, ssn, insurance_company, userid);
				
				getOut(socket, REQUESTUPDATE);
						
				try {
					oos = new ObjectOutputStream(socket.getOutputStream());
					if (mr != null) {
						oos.writeObject(mr);
						oos.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			*/
		}		
	};


	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Thread(new UserLogoutRequestThread()).start();
		
		/*
		try {
			//Tell the server, the app is gonna log out
			getOut(socket, APPLOGOUT);
			Thread.sleep(200);
			
			if (socket != null) {
				socket.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (ois != null) {
				ois.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("NormalUserActivity", "socket close fails");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
					
	}
	
	class UserRequestMedRecordThread implements Runnable {

		@Override
		public void run() {
			//sent request for the MedRecord
			if (socket != null) {
				getOut(socket, REQUESTONEMEDRECORD);
				
				//wait to get the MedRecordsPackage
				try {		
					ois = new ObjectInputStream(socket.getInputStream());
					MR = (MedRecord) ois.readObject();
					handler.sendMessage(Message.obtain(handler, 0, MR));

					ois.close();				
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
						
			}			
		}
		
	}
	
	

	class UserUpdateThread implements Runnable {
		
		@Override
		public void run() {
			update();
		}
		
		public void update() {
			//sent request to update uer's info
			if (socket != null) {
				
				int id = Integer.parseInt(textViewIDNum.getText().toString().trim());
				String name = editTextName.getText().toString().trim();
				int age = Integer.parseInt(editTextAge.getText().toString().trim());
				String sex = editTextSex.getText().toString().trim();
				String medical_allergy = editTextMedAllergy.getText().toString().trim();
				int ssn = Integer.parseInt(editTextSSN.getText().toString().trim());
				String insurance_company = editTextInsurance.getText().toString().trim();
				String userid = MR.getUserid();	
				MedRecord mr = new MedRecord(id, name, age, sex, medical_allergy, ssn, insurance_company, userid);
				
				getOut(socket, REQUESTUPDATE);
						
				try {
					oos = new ObjectOutputStream(socket.getOutputStream());
					if (mr != null) {
						oos.writeObject(mr);
						oos.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
		
	}
	
	
	class UserLogoutRequestThread implements Runnable {

		@Override
		public void run() {
			try {
				//Tell the server, the app is gonna log out
				getOut(socket, APPLOGOUT);
				//Thread.sleep(200);
				
				if (socket != null) {
					socket.close();
				} 
				
				if (oos != null) {
					oos.close();
				}
				
				if (ois != null) {
					ois.close();
				}
				
			} catch (IOException e) {
				Log.e("AdminActivity", "socket close fails");
			} 		
		}
		
	}
	
	
}
