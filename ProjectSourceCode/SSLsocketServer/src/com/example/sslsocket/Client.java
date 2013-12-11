package com.example.sslsocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import javax.net.ssl.SSLSocket;


class Client implements Runnable {
	private SSLSocket socket;
	private BufferedReader br = null;
	private BufferedWriter bw = null;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public MySQLAccess DBAccess;
	private boolean bConnected = false;
	private AESEncryptDecrypt aes;
	private String userIdStr;
	private String passwordStr;
	private String passwordFromDB;
	private String priorityFromDB;
	public static final String LOGINRESPONSELABEL = "Login_Response";
	public static final String LOGINREQUESTLABEL = "Login_Request";
	public static final String USERLOGINRESULT = "successful_user";
	public static final String ADMINLOGINRESULT = "successful_admin";
	public static final String LOGINFAIL = "fail";
	public static final String REQUESTMEDRECORDSPACKAGE = "Request_MedRecordsPackage";
	public static final String REQUESTONEMEDRECORD = "Request_One_MedRecord";
	public static final String REQUESTUPDATE = "Request_Update";
	public static final String APPLOGOUT = "App_Log_Out";


	
	
	public Client(SSLSocket s) {
		this.socket = s;
		try {			
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			bConnected = true;
			DBAccess = DataBaseThread.DBAccess;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		try {
			aes = new AESEncryptDecrypt();
			while (bConnected) {	
				String MsgFromClient = br.readLine();
				
				if (MsgFromClient != null) {
					//Request for log in
System.out.println(MsgFromClient);
					if (MsgFromClient.equals(LOGINREQUESTLABEL)) {
						userIdStr = br.readLine();
						passwordStr = br.readLine();
						
						passwordFromDB = DBAccess.getPasswordFromDB(userIdStr);
						priorityFromDB = DBAccess.getPriorityFromDB(userIdStr);
System.out.println("password from db: " + passwordFromDB);
						
						//sent the LOGINRESPONSELABLE to client
						bw.write(LOGINRESPONSELABEL);
						bw.newLine();
						bw.flush();
System.out.println(userIdStr);
System.out.println(passwordStr);
//System.out.println(passwordFromDB);
						
						//sent the log in result to client, fail or successful
						if (passwordFromDB != null && passwordStr.equals(passwordFromDB)) {
							if (priorityFromDB.equals("guest")) {	
								bw.write(USERLOGINRESULT);
								bw.newLine();
								bw.flush();
							} else if (priorityFromDB.equals("admin")) {
								bw.write(ADMINLOGINRESULT);
								bw.newLine();
								bw.flush();
							}		
						} else {
							bw.write(LOGINFAIL);
							bw.newLine();
							bw.flush();
						}
					} 
					
					//Request for the MedRecordsPackage
					else if (MsgFromClient.equals(REQUESTMEDRECORDSPACKAGE)) {
						MedRecordsPackage MRP = DBAccess.getAllMedRecords();
						oos = new ObjectOutputStream(socket.getOutputStream());
						
						if (MRP != null) {
							oos.writeObject(MRP);
							oos.flush();
						}
						

					} 
					
					//Request for one MedRecord
					else if (MsgFromClient.equals(REQUESTONEMEDRECORD)) {
						MedRecord MR = DBAccess.getOneMedRecord(userIdStr);
						oos = new ObjectOutputStream(socket.getOutputStream());
						
						if (MR != null) {
							oos.writeObject(MR);
							oos.flush();
						}
						

					}
					
					//Request for update user's info
					else if (MsgFromClient.equals(REQUESTUPDATE)) {
						MedRecord mr = null;
						//wait to get the MedRecordsPackage
						try {		
							ois = new ObjectInputStream(socket.getInputStream());
							mr = (MedRecord) ois.readObject();
								
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						
						if (mr != null) {				
							DBAccess.updateMedRecord(mr);
						}
					}
					
					//App log out request
					else if (MsgFromClient.equals(APPLOGOUT)) {
						bConnected = false;
					}
					
					
				}
				
				
			}
		} catch (EOFException e) {
			e.printStackTrace();
			System.out.println("Client disconnect!");
		} catch (IOException e) {			
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) socket.close();
				if (br != null) br.close();
				if (bw != null) bw.close();
				if (ois != null) ois.close();
				if (oos != null) oos.close();
				//if (DBAccess != null) DBAccess.DBclose();
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}			
	}
}

