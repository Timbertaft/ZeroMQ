package client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import org.zeromq.ZMQ;

import compute.*;

public class ChatClient extends UnicastRemoteObject implements Runnable{
	
	/**
	 * The below class handles all client side interaction once within the Chat Client of ComputePi.
	 */
	private static final long serialVersionUID = 7925812905228927260L;
	 private static PresenceService server;
	 private static RegistrationInfo ClientName;
	 static boolean chkExit = true;
	 boolean chkLog = true;
	 static boolean chkExit2 = true;

	 

	 ChatClient(PresenceService chatinterface, RegistrationInfo clientinfo) throws RemoteException
	 {
	  ChatClient.server = chatinterface;
	  ChatClient.ClientName = clientinfo;
	  chkLog = server.register(clientinfo);
	 }

	 
	 static boolean GetChk() {
		return chkExit; 
	 }
	 
	@Override
	public void run() {
			  if(chkLog)
				  //Starts by validating that the user is successfully registered with the Presence Service.
			  {
				  
				  Scanner scanner = new Scanner(System.in);
				  String message;
			   System.out.println("Successfully Connected to the Chat Server!");
			   try {
		            InetAddress ip = InetAddress.getLocalHost();
		            System.out.println("ChatClient bound:\nport: " + ClientName.getPort() + "\nIP: " + ip);
		            
		            // Below sets default initial state of user to Active to be able to accept chat messages.
		           
		            ClientName.setStatus(true);
		            server.updateRegistrationInfo(ClientName);
		            
		            // Below instantiates a Server Socket on the client on a different thread to listen for
		            // client socket requests. (receive messages)
		            
		            //SocketServer socket = new SocketServer(ChatClient.ClientName);
		            SocketServerZMQ MQsocket = new SocketServerZMQ(ChatClient.ClientName);
                   ZMQPubSocket PubSocket = new ZMQPubSocket(ChatClient.ClientName);

		            
		            // Below insures proper reset of ChkExit value for re-entry into the ChatMenu.
		            chkExit = true;
		            //new Thread(socket).start();
		            new Thread(MQsocket).start();
                    new Thread(PubSocket).start();
		            
			   }
			   catch(Exception e) {
			            System.err.println("ChatServer exception: " + 
						       e.getMessage());
			            e.printStackTrace();
			   }
			   
			   
			   
			   while(chkExit)
			   {
		        ChatMenu(scanner);
		        System.out.println("Hit Enter to return to Menu or type EXIT to leave the Chat Server.");
		        
		        //Below listens to the scanner to receive user entry and processes based on 
		        //chosen menu option.
		        
			    message = scanner.nextLine();

			    // Below processes exit reqeuest.  Unregisters client and sets booleans to exit chat client section
			    // of ComputePi.  Re-enters the ComputePi original menu upon completion.
			 
			    if(message.contains("EXIT") || !chkExit2)
			    {
			     chkExit = false;
			     chkExit2 = true;
			     try {
					server.unregister(ChatClient.ClientName.getUserName());
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     System.out.println("\nSuccessfully Logged Out From The Chat Server!\n");
			     ComputePi.Loop();
			    }
			   }  
			  }
			  
			  // Below is the response if registration fails.  Will always be triggered
			  // by finding duplicate names registered.
			  
			  else if(!chkLog){
				  System.out.println("Sorry, this username is already taken.");
				  ComputePi.Loop();
			  }
	}
	
	// Below enters EchoClient method.  Creates a new socket using client's information to send
	// scanned message from client to serversocket thread on destination client system.
	
	private static void EchoClient(RegistrationInfo client, RegistrationInfo clienttarget, String m) {
        String line;
        BufferedReader is, server;
        PrintStream os;
        ZMQ.Context context = ZMQ.context(1);
        System.out.println("Connecting to ZMQServer...");

        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:" + clienttarget.getPort());
        //Socket clientSocket = new Socket("localhost", clienttarget.getPort());
        m = client.getUserName() + " on " + "[" + client.getHost() + ":"  + client.getPort() + "]" + " says: " + m;

       // for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
            String request = m;
            System.out.println("Sending message...");
            requester.send(request.getBytes(), 0);

            //byte[] reply = requester.recv(0);
            //System.out.println("Received " + new String(reply) + " " + requestNbr);
       // }
        requester.close();
        context.term();

        //InputStream messagestream = new ByteArrayInputStream(m.getBytes());
        //is = new BufferedReader(new InputStreamReader(messagestream) );
        //os = new PrintStream(clientSocket.getOutputStream());
        //server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //while(true) {
        //line = is.readLine();

        //Below checks if input has been completely processed or if inputted scanner value was
        // EXIT.  This is included in order to safeguard proper socket termination.

        // if(line == null) {
        //clientSocket.close();
        //    break;
        // } else if(line.equals("EXIT")) {
        //clientSocket.close();
        //   break;
        // }
        //  os.println(line);
        //   line = server.readLine();
        //  }

    }

	//Below is the main Chat dialogue tree.  Provides instructions to the user on what each command does,
	// An invalid command will prompt if user wants to exit.  All other commands are handled within.
	
	private static void ChatMenu(Scanner s) {
		System.out.println("Awaiting input.\n Please enter one of the following commands:\n FRIENDS -"
				+ " shows a list of all registered users.\n TALK <username> <message> - "
				+ " Starts a pirvate message conversation with inputted username if they are available.\n"
				+ " BROADCAST <message> - " + " sends a message to every available user.\n BUSY -"
				+ " Sets users current status to show as unavailable.\n AVAILABLE -"
				+ " Sets users current status to show as available.\n EXIT -"
				+ " Unregisters username and terminates chat application.");

        String message = s.nextLine();
		if (message.contains("FRIENDS")) {
			try {
				for(RegistrationInfo e: server.listRegisteredUsers()) {
					if(!e.getUserName().equals(ClientName.getUserName()))
					System.out.println(e.getUserName());
				}
			}
			catch(RemoteException e) {
				e.printStackTrace();
			}
		}
		
		//Below triggers handling of message submission attempt using TALK command. If message and user
		// are valid, begins creation of ClietnSocket to communicate to peer's socket server.
		
		if (message.contains("TALK")) {
			message = message.replace("TALK" + " ", "");
			try {
				RegistrationInfo e = server.lookup(message);
				
					if(e == null) {
						System.out.println("Specified user does not presently exist in the system.");
					}		
					else if(e.getStatus() && !(ClientName.getUserName()).equals(e.getUserName())) {
						message = message.replace(e.getUserName() + " ", "");
						EchoClient(ClientName, e, message);
						}
					else if(!e.getStatus()) {
						System.out.println("User " + e.getUserName() + " is currently unavailable.");
					}
					else if(ClientName.getUserName().equals(e.getUserName())) {
						System.out.println("Why would you want to talk to yourself?");
					}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		//Below is similar to TALK but submits the message with the exclusion of the current user
		// by looping through the Vector of registered users on the PresenceServer.
		
		if (message.contains("BROADCAST")) {
			message = message.replace("BROADCAST" + " ", "");
			String broad = "@everyone: ";
			message = broad + message;
            message = ClientName.getUserName() + " on " + "[" + ClientName.getHost() + ":"  + ClientName.getPort() +
                    "]" + " says: " + message;

			try {
				//for(RegistrationInfo e: server.listRegisteredUsers()) {
					//if(e.getStatus() && !(e.getUserName().equals(ClientName.getUserName()))) {
					//EchoClient(ClientName, e, message);
					//}
                server.broadcast(message);
                //System.out.println("Message Sent");
				//}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		//Changes users status to Available or changes nothing if user is already available.
		
		if (message.contains("AVAILABLE")) {
			try {
				if(!ClientName.getStatus()) {
					ClientName.setStatus(true);
					server.updateRegistrationInfo(ClientName);
					System.out.println("Successfully set self to available!");
				}
				else {
					System.out.println("You are already in available status!");
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Changes user's status to Busy preventing chat requests or does nothing if user is already available.
		
		if (message.contains("BUSY")) {
			try {
				if(ClientName.getStatus()) {
					ClientName.setStatus(false);
					server.updateRegistrationInfo(ClientName);
					System.out.println("Successfully set self to unavailable.");
				}
				else {
					System.out.println("You are already in unavailable status!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//Unregisters the client and flags boolean for final exit call back to Computepi.
		
		if (message.contains("EXIT")) {
			try {
                ClientName.setStatus(false);
                server.updateRegistrationInfo(ClientName);
				server.unregister(ClientName.getUserName());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chkExit2 = false;
		}
			
			}
}
