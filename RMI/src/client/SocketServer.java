package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import compute.ProcessIncomingRequest;
import compute.RegistrationInfo;

public class SocketServer implements Runnable {
	
	private static RegistrationInfo connection;
	private static boolean running;
	
	protected SocketServer(RegistrationInfo userconnect) {
		
		SocketServer.connection = userconnect;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
	        ServerSocket chatServer = new ServerSocket(SocketServer.connection.getPort());
	        running = true;
	        
	        while(running) {
	        	try {
	        		Socket clientSocket = chatServer.accept();
	        		Thread thread  = new Thread(new ProcessIncomingRequest(clientSocket));
	        		thread.start();
	        		
        			//The below block is to correct memory leak.
	        		if(ChatClient.GetChk() == true) {
	        			running = false;
	        			

	        			try {
	        			if (chatServer != null) {
	        				chatServer.close();
	        		}
	        			} catch (IOException ignored) {
	        				
	        			}
	        		}
	        		// The above block is to correct memory leak.  Causing issues.
	        	} 
	        	catch (IOException e) {
	            // TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
	    	}
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return;
	    }
	    
		}
		
	}
