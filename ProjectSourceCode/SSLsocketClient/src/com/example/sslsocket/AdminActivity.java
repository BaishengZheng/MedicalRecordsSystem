package com.example.sslsocket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.net.ssl.SSLSocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AdminActivity extends Activity{
	
	private ArrayList<String> arrayListForListView;
	private ArrayList<MedRecord> arrayListMedRecords;
	private ListView lv;
	private Button adminLogOutBtn;
	private SSLSocket socket;
	public static final String REQUESTMEDRECORDSPACKAGE = "Request_MedRecordsPackage";
	public static final String APPLOGOUT = "App_Log_Out";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin);
			
		socket = MySSLSocket.Client_sslSocket;
		
		adminLogOutBtn = (Button)findViewById(R.id.buttonAdminLogOut);
		
		lv = (ListView) findViewById(R.id.listView1);
			
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if (msg == null) {
				Log.d("AdminActivity", "Admin received message but it is null");
				return;
			} 
			
			MedRecordsPackage MRP = (MedRecordsPackage) msg.obj;
			arrayListMedRecords = MRP.getAllMedRecords();
			
			//construct the arrayListForListView
			for (int i=0; i<arrayListMedRecords.size(); i++) {
				arrayListForListView.add("ID: " + arrayListMedRecords.get(i).getId() + "           " + arrayListMedRecords.get(i).getName() + "           " + "age: " + arrayListMedRecords.get(i).getAge());
			}
			
			//Set up the list view
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminActivity.this, android.R.layout.simple_list_item_1, arrayListForListView);
			lv.setAdapter(adapter);
			
			//Set up the list view item on click listener
			lv.setOnItemClickListener(myListViewOnItemClickListener);
			
		}
		
	};
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		socket = MySSLSocket.Client_sslSocket;
		arrayListForListView = new ArrayList<String>();
		arrayListMedRecords = new ArrayList<MedRecord>();
		
		new Thread(new AdminRequestMedRecordsPackageThread()).start();					
	}


	
	private OnItemClickListener myListViewOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView temp = (TextView) arg1;
			//Toast.makeText(getApplicationContext(), temp.getText() + ", the " + arg2 + " item is clicked", Toast.LENGTH_SHORT).show();
			
			Intent AdminToNormalUserIntent = new Intent(getApplicationContext(), NormalUserFromAdminActivity.class);
			Bundle myBundle = new Bundle();
			myBundle.putInt(NormalUserFromAdminActivity.LOOKUP_ID, arrayListMedRecords.get(arg2).getId());
			myBundle.putString(NormalUserFromAdminActivity.LOOKUP_NAME, arrayListMedRecords.get(arg2).getName());
			myBundle.putInt(NormalUserFromAdminActivity.LOOKUP_AGE, arrayListMedRecords.get(arg2).getAge());
			myBundle.putString(NormalUserFromAdminActivity.LOOKUP_SEX, arrayListMedRecords.get(arg2).getSex());
			myBundle.putString(NormalUserFromAdminActivity.LOOKUP_MEDICAL_ALLERGY, arrayListMedRecords.get(arg2).getMedAllergy());
			myBundle.putInt(NormalUserFromAdminActivity.LOOKUP_SSN, arrayListMedRecords.get(arg2).getSsn());
			myBundle.putString(NormalUserFromAdminActivity.LOOKUP_INSURANCE_COMPANY, arrayListMedRecords.get(arg2).getInsuranceCompany());
			myBundle.putString(NormalUserFromAdminActivity.LOOKUP_USERID, arrayListMedRecords.get(arg2).getUserid());
			
			AdminToNormalUserIntent.putExtras(myBundle);
			startActivity(AdminToNormalUserIntent);
		}
		
	};
	
	
	private OnClickListener adminLogOutBtnOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d("AdminActivity", "LogOutBtnOnClick");		
			
			new Thread(new AdminLogoutRequestThread()).start();
			finish();
		}
	};
	
	public void getOut(SSLSocket socket,String message){  
    	BufferedWriter bw = null;  
        try {      	
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                  
            Log.d("AdminActivity: ", "get the bufffered writer successfully");
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
		new Thread(new AdminLogoutRequestThread()).start();
	}
	
	
	
	class AdminRequestMedRecordsPackageThread implements Runnable {

		protected ObjectInputStream ois;

		@Override
		public void run() {
			//sent request for the MedRecordsPackage
			if (socket != null) {
				//Log.e("AdminActivity", "socket is not null");
				
				adminLogOutBtn.setOnClickListener(adminLogOutBtnOnClickListener);
				
				getOut(socket, REQUESTMEDRECORDSPACKAGE);
				
				//wait to get the MedRecordsPackage	
				try {		
					ois = new ObjectInputStream(socket.getInputStream());
					MedRecordsPackage MRP = (MedRecordsPackage) ois.readObject();
					
					//construct the arrayListForListView
					for (int i=0; i<arrayListMedRecords.size(); i++) {
						arrayListForListView.add("ID: " + arrayListMedRecords.get(i).getId() + "           " + arrayListMedRecords.get(i).getName() + "           " + "age: " + arrayListMedRecords.get(i).getAge());
					}
					handler.sendMessage(Message.obtain(handler, 0, MRP));
					
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			} else {
				Log.e("AdminActivity", "socket is null");
			}			
		}		
	}
	
	
	class AdminLogoutRequestThread implements Runnable {

		@Override
		public void run() {
			try {
				//Tell the server, the app is gonna log out
				getOut(socket, APPLOGOUT);
				//Thread.sleep(200);
				
				if (socket != null) {
					socket.close();
				}
				
			} catch (IOException e) {
				Log.e("AdminActivity", "socket close fails");
			} 			
		}
		
	}
	
	
	
	
	
}
