package engine;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.util.Vector;

import compute.*;

public class ChatServer extends UnicastRemoteObject implements PresenceService {
	
	/**
	 * This class handles communication with the Presence Service.  All clients use this
	 * class in order to reference the stored Vector and makes changes/references to their/other clients'
	 * registration info.
	 */
	private static final long serialVersionUID = -5493056279692685386L;
	private Vector<RegistrationInfo> chatClients = new Vector<RegistrationInfo>();
	private static Thread t;
	private static ZMQBroadSocket broadcaster = new ZMQBroadSocket();
	
	protected ChatServer() throws RemoteException {
        super();
	}
	
	//Handles registration of Client by inputting passed Client registration info into the
	// list of registered users stored server vector.
	
	public synchronized boolean register(RegistrationInfo reg) throws RemoteException {
		boolean y = true;
		if (chatClients != null && chatClients.size() > 0) {
		for(RegistrationInfo e : chatClients) {
			System.out.println(e.getUserName() + "server list");
			System.out.println(reg.getUserName() + "client name");
			if(e.getUserName().equals(reg.getUserName())) {
				System.out.println("flag set to false");
				y = false;

			}
			
		}
		}
		if(y) {
			assert this.chatClients != null;
			System.out.println(this.chatClients.size());
			this.chatClients.add(reg);		
		}
		
		return y;
	}

	@Override
	
	//Below allows clients to change their availability from available to busy and vise versa.
	
	public synchronized boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException {
		for(RegistrationInfo e : chatClients) {
			if(e.getUserName().equals(reg.getUserName())) {
				if(e.getStatus() != reg.getStatus()) {
					e.setStatus(reg.getStatus());
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}
	
	//Removes client from list of registerd users stored vector upon client termination.

	@Override
	public synchronized void unregister(String userName) throws RemoteException {
		for(int i = 0; i < chatClients.size(); i ++) {
			if(chatClients.get(i).getUserName().equals(userName)) {
				chatClients.remove(chatClients.get(i));
			}
		}
		
	}
	
	//Allows clients to search for registration info of other registered users in the system.

	@Override
	public synchronized RegistrationInfo lookup(String name) throws RemoteException {
		for(RegistrationInfo i : this.chatClients) {
			if(name.contains(i.getUserName())) {
				return i;
			}
		}	
		return null;
	}
	
	//Below is the vector storing the registration info of users locally on the server.

	@Override
	public synchronized Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException {
		return this.chatClients;
	}

	@Override
	public synchronized void broadcast(String msg) throws RemoteException {
	    broadcaster.SetMessage(msg);
	}


	public static void main(String[] args) {
		
		//Below creates the server as a bound RMI object that interacts with the shared PresenceService
		//interface.  
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        String name = "//localhost/ChatServer";
        try {
            PresenceService serve = new ChatServer();
            
        // Below creates an accessible registry to bind the remote objects to.  MANDATORY to work correctly.
            
            LocateRegistry.createRegistry(9800);
            Naming.rebind(name, serve);
            
       // Below pulls IP information to help with troubleshooting as part of the binding process.
            
            InetAddress ip = InetAddress.getByName("localhost");
            System.out.println("ChatServer bound:\nport: " + "9800" + "\nIP: " + ip);
            broadcaster.initialize();
            Object m = new Object();
			synchronized (m) {
				m.wait();
			}
        } catch (Exception e) {
            System.err.println("ChatServer exception: " + 
			       e.getMessage());
            e.printStackTrace();
        }

	}

}
