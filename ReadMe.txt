***********************
Read me
***********************

Steps to run this application:

1. Construct the database and tables we need:
1.1 In our project, we use MySQL server, and create an account named admin, and set its password to "admin", and also create a database named medication.
1.2 Run the InitializeDatabase.java in the ConstructDB folder. This java file is used to initialize the encrypted data within the database that are used to test.

2. Set up the SSL socket configuarations:
2.1 Set up SERVER_KEYSTORE_PATH in the SSLServer.java of the SSLsocketServer project to be the path where the file "kserver" is. The "kserver" has the server public and private key.
2.2 Set up SERVER_TRUSTSTORE_PATH in the SSLServer.java of the SSLsocketServer project to be the path where the file "tserver" is. The "tserver" has the client's certificate.

3. Set up the Application's IP destination:
3.1 Set up SERVER_IP in the MySSLSocket.java of the SSLsocketClient project to the IP destination. In our project, we use our computer as a server, so just set up that with our computer's IP address.

Note: When we run this application, we should start the MySQL server first, then run the SSLServer.java in SSLsocketServer project to start our server. And then we can run our application. And please make sure your android device has accessed the Internet.


**************************************
Some users' name and password are:(You can also find those in the InitializeDatabase.java file)

user id         password
admin             admin
susan            Hoover
anna             Williams
bill             Sunday