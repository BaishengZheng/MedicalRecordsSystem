package com.example.sslsocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MySSLSocket extends Activity {
	private static final int SERVER_PORT = 50031;//port number
    private static final String SERVER_IP = "192.168.2.4";//IP destination  
    private static final String CLIENT_KET_PASSWORD = "123456"; 
    private static final String CLIENT_TRUST_PASSWORD = "123456";
    private static final String CLIENT_AGREEMENT = "TLS";//use TLS protocol
    private static final String CLIENT_KEY_MANAGER = "X509";
    private static final String CLIENT_TRUST_MANAGER = "X509";
    private static final String CLIENT_KEY_KEYSTORE = "BKS";
    private static final String CLIENT_TRUST_KEYSTORE = "BKS"; 
    private static final String ENCONDING = "utf-8";
    public static SSLSocket Client_sslSocket;  
	private EditText userId;
	private EditText passwordInput;
	private Button btnLogin;  
	private String userIdStr;
	private String passwordStr;
	public static final String LOGINRESPONSELABEL = "Login_Response";
	public static final String LOGINREQUESTLABEL = "Login_Request";
	public static final String USERLOGINRESULT = "successful_user";
	public static final String ADMINLOGINRESULT = "successful_admin";
	public static final String LOGINFAIL = "fail";
	@Override
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main); 	       
        
        userId = (EditText) findViewById(R.id.userId);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        btnLogin = (Button) findViewById(R.id.btnLogin);
             
        btnLogin.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				userIdStr = userId.getText().toString();
				userId.setText("");
				passwordStr = passwordInput.getText().toString();
				passwordInput.setText("");
				
				//connect to the server in other thread
				new Thread(new ConnectionLoginRequestThread()).start();
			}
        	
        });
        
     
    } 
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if (msg == null) {
				Log.d("MainActivity", "Client received message but it is null");
				return;
			} 
			
			String message = (String)msg.obj;
			
			if (message.equals(LOGINFAIL)) {
				showLoginFailDialog();
			}
			
			if (message.equals(ADMINLOGINRESULT)) {
				Intent adminIntent = new Intent(getApplicationContext(), AdminActivity.class);
				startActivity(adminIntent);
			}
			
			if (message.equals(USERLOGINRESULT)) {
				Intent userIntent = new Intent(getApplicationContext(), NormalUserActivity.class);
				startActivity(userIntent);
			}
		}
		
	};
     
     
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Show the priority choices with a dialog for the user
	private void showLoginFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("The user ID doesn't match the password, please input again.");
        builder.setNeutralButton("OK", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
	}
	
	
	
	class ConnectionLoginRequestThread implements Runnable {
		 
		@Override
		public void run() {
			init();
			if (Client_sslSocket != null) {
				Log.e("client ssl socket", "not null");
				getOut(Client_sslSocket, LOGINREQUESTLABEL);
				getOut(Client_sslSocket, userIdStr);
				getOut(Client_sslSocket, passwordStr);
				getLoginResponse(Client_sslSocket);
			}		
		}
		
		
		public void init() {  
	        try {  
	            //SSL SSLContext initialization 
	            SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);  
	            //KeyManagerFactory and TrustManagerFactory's X509
	            KeyManagerFactory keyManager = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);  
	            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);  
	            //BKS  
	            KeyStore kks= KeyStore.getInstance(CLIENT_KEY_KEYSTORE);  
	            KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);  

	            kks.load(getBaseContext()  
	                    .getResources()  
	                    .openRawResource(R.drawable.kclient),CLIENT_KET_PASSWORD.toCharArray());  
	            tks.load(getBaseContext()  
	                    .getResources()  
	                    .openRawResource(R.drawable.lt_client),CLIENT_TRUST_PASSWORD.toCharArray()); 
	            //initialize keyManager  
	            keyManager.init(kks,CLIENT_KET_PASSWORD.toCharArray());  
	            trustManager.init(tks);  
	            //initialize SSLContext  
	            sslContext.init(keyManager.getKeyManagers(),trustManager.getTrustManagers(),null);  
	            //get SSLSocket  
	            Client_sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(SERVER_IP,SERVER_PORT);  
	        } catch (Exception e) {  
	        	e.printStackTrace();
	            //tag.e("MySSLSocket",e.getMessage());  
	        }  
	    }
		
		
		public void getOut(SSLSocket socket,String message){  
	    	BufferedWriter bw = null;  
	        try {  
	        	
	            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	                  
	            Log.d("MySSLSocket: ", "get the bufffered writer successfully");
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
		
		
		public void getLoginResponse(SSLSocket socket) {
	    	if (socket == null) {
	    		Log.e("client ssl sock", "is null");
	    	}
	    	BufferedReader br = null;	
	    	String str1 = null;
	    	String str2 = null;
	    	try {  
	            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            str1 = br.readLine();
Log.d("str1: ", str1);

	            str2 = br.readLine();
Log.d("str2: ", str2);

	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } 
	    	
	    	if (str1.equals(LOGINRESPONSELABEL)) {
	    		if (str2.equals(LOGINFAIL)) {
	    			//showLoginFailDialog();
	    			handler.sendMessage(Message.obtain(handler, 0, LOGINFAIL));	    			
	    		} else if (str2.equals(USERLOGINRESULT)) {
	    			handler.sendMessage(Message.obtain(handler, 0, USERLOGINRESULT));				
	        	} else if (str2.equals(ADMINLOGINRESULT)) {
	    			handler.sendMessage(Message.obtain(handler, 0, ADMINLOGINRESULT));					
	        	}   		
	    	} else {
	    		Log.d("MySSLSocket", "Fail to get the log in response");
	    	}
	    	
	    	if (br != null) {
	    		try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}   	
	    }
	}
	
}
