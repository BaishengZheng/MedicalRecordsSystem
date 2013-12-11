package com.example.sslsocket;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
 
public class SSLServer {  
  
    private static final int SERVER_PORT = 50031;  
    private static final String SERVER_KEY_PASSWORD = "123456";
    private static final String SERVER_TRUST_PASSWORD = "123456";  
    private static final String SERVER_AGREEMENT = "TLS";//use TLS protocol  
    private static final String SERVER_KEY_MANAGER = "SunX509";
    private static final String SERVER_TRUST_MANAGER = "SunX509";
    private static final String SERVER_KEY_KEYSTORE = "JKS"; 
    private static final String SERVER_TRUST_KEYSTORE = "JKS";
    private static final String SERVER_KEYSTORE_PATH = "E:/java/eclipse/SSLsocketServer/lib/kserver";
    private static final String SERVER_TRUSTSTORE_PATH = "E:/java/eclipse/SSLsocketServer/lib/tserver";
    private SSLServerSocket serverSocket;
    public static String userName="root";
    public static String password;
  
    public static void main(String[] args) throws Exception {  
        SSLServer server = new SSLServer();  
        (new Thread(new DataBaseThread())).start();
        server.init();  
        server.start();     
    }  
  
    //initialize the server
    public void start() {  
        if (serverSocket == null) {  
            System.out.println("ERROR");  
            return;  
        }  
        SSLSocket s = null;
          
        try {  
        	while (true) {
        		System.out.println("Server Side......");  

                s = (SSLSocket)serverSocket.accept(); 
                
                //Put the obtained socket to a new thread
                Client c = new Client(s);
                new Thread(c).start();
                
                System.out.println("a client connected!");
        	}            
        } catch (Exception e) {  
            System.out.println(e);  
        } finally {
        	if (s != null) {
        		try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        } 
    }  
      
      
    public void init() {  
        try {  
            //get SSLContext  
            SSLContext ctx = SSLContext.getInstance(SERVER_AGREEMENT);  
            //get SunX509
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(SERVER_KEY_MANAGER);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(SERVER_TRUST_MANAGER);
            //get JKS  
            KeyStore ks = KeyStore.getInstance(SERVER_KEY_KEYSTORE);  
            KeyStore tks = KeyStore.getInstance(SERVER_TRUST_KEYSTORE);
            
            ks.load(new FileInputStream(SERVER_KEYSTORE_PATH), SERVER_KEY_PASSWORD.toCharArray());  
            tks.load(new FileInputStream(SERVER_TRUSTSTORE_PATH), SERVER_TRUST_PASSWORD.toCharArray());
            //initialization
            kmf.init(ks, SERVER_KEY_PASSWORD.toCharArray()); 
            tmf.init(tks);
            //initialize SSLContext  
            ctx.init(kmf.getKeyManagers(),tmf.getTrustManagers(), null);  
            //get ServerSocketFactory through SSLContext to create ServerSocket  
            serverSocket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(SERVER_PORT);
            serverSocket.setNeedClientAuth(true); 
        } catch (Exception e) {  
            System.out.println(e);  
        }  
    }  
    
    
    
}  

class DataBaseThread implements Runnable {

	public static MySQLAccess DBAccess;

	@Override
	public void run() {
		DBAccess = new MySQLAccess();
		try {
			DBAccess.DBInitialization();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
