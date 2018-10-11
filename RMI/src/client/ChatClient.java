package client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import compute.*;

public class ChatClient extends UnicastRemoteObject implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7925812905228927260L;
	 private static PresenceService server;
	 private static RegistrationInfo ClientName;
	 static boolean chkExit = true;
	 boolean chkLog = true;
	 static boolean chkExit2 = true;
	 //private static Thread thread = new Thread();

	 

	 protected ChatClient(PresenceService chatinterface, RegistrationInfo clientinfo) throws RemoteException
	 {
	  ChatClient.server = chatinterface;
	  ChatClient.ClientName = clientinfo;
	  chkLog = server.register(clientinfo);
	  System.out.println(chkLog);
	 }

	 
	 protected ChatClient() throws RemoteException {
		 
	 }
	 
	 public static boolean GetChk() {
		return chkExit; 
	 }
	 
	@Override
	public void run() {
			  if(chkLog)
			  {
				  
				  Scanner scanner = new Scanner(System.in);
				  String message;
			   System.out.println("Successfully Connected to the Chat Server!");
			   try {
				   //PresenceService client = new ChatClient(ChatClient.server, ChatClient.ClientName);
				   //LocateRegistry.createRegistry(registernumber);
		            //Naming.rebind(name, client);
		            InetAddress ip = InetAddress.getLocalHost();
		            System.out.println("ChatClient bound:\nport: " + ClientName.getPort() + "\nIP: " + ip);
		            ClientName.setStatus(true);
		            server.updateRegistrationInfo(ClientName);
		            SocketServer socket = new SocketServer(ChatClient.ClientName);
		            chkExit = true;
		            new Thread(socket).start();
		            
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
			    message = scanner.nextLine();
			    if(message.contains("EXIT") || chkExit2 == false)
			    {
			     chkExit = false;
			     ClientName.setStatus(false);
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
			  else if(!chkLog){
				  System.out.println("Sorry, this username is already taken.");
				  ComputePi.Loop();
			  }
	}
		// TODO Auto-generated method stub
	
	
	public static void EchoClient(RegistrationInfo client, RegistrationInfo clienttarget, String m) {
		 try {
	            String line;
	            BufferedReader is, server;
	            PrintStream os;

	            Socket clientSocket = new Socket("localhost", clienttarget.getPort());
	            m = client.getUserName() + " on " + "[" + client.getHost() + ":"  + client.getPort() + "]" + " says: " + m;
	            InputStream messagestream = new ByteArrayInputStream(m.getBytes());
	            is = new BufferedReader(new InputStreamReader(messagestream) );
	            os = new PrintStream(clientSocket.getOutputStream());
	            server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	            while(true) {
	                line = is.readLine();
	                if(line == null) {
	                	//clientSocket.close();
	                    break;
	                } else if(line.equals("EXIT")) {
	                    clientSocket.close();
	                    break;
	                }
	                os.println(line);
	                line = server.readLine();
	            }

	        } catch (UnknownHostException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
}

	public static void ChatMenu(Scanner s) {
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
				
			}
		}
		if (message.contains("TALK")) {
			message = message.replace("TALK" + " ", "");
			try {
				RegistrationInfo e = server.lookup(message);
				
					if(e == null) {
						System.out.println("Specified user does not presently exist in the system.");
					}		
					else if(e != null && e.getStatus() && !(ClientName.getUserName()).equals(e.getUserName())) {
						message = message.replace(e.getUserName() + " ", "");
						EchoClient(ClientName, e, message);
						}
					else if(e != null && !e.getStatus()) {
						System.out.println("User " + e.getUserName() + " is currently unavailable.");
					}
					else if(ClientName.getUserName().equals(e.getUserName()) && e != null) {
						System.out.println("Why would you want to talk to yourself?");
					}
					
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		if (message.contains("BROADCAST")) {
			message = message.replace("BROADCAST" + " ", "");
			try {
				for(RegistrationInfo e: server.listRegisteredUsers()) {
					if(e.getStatus() && !(e.getUserName().equals(ClientName.getUserName()))) {
					message.replaceFirst(e.getUserName() + " ", "");
					String broad = "@everyone: ";
					message = broad + message;
					EchoClient(ClientName, e, message);
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
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
		
		if (message.contains("EXIT")) {
			try {
				server.unregister(ClientName.getUserName());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chkExit2 = false;
		}
			
			}
}
