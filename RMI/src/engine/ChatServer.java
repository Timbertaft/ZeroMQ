package engine;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.util.Vector;

import compute.*;

public class ChatServer extends UnicastRemoteObject implements PresenceService, Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5493056279692685386L;
	private Vector<RegistrationInfo> chatClients = new Vector<RegistrationInfo>();
	
	protected ChatServer() throws RemoteException {
	}
	
	@Override
	public void run() {
		
	}
	
	@Override
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
		if(y == true) {
			System.out.println(this.chatClients.size());
			this.chatClients.add(reg);		
		}
		
		return y;
	}

	@Override
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

	@Override
	public synchronized void unregister(String userName) throws RemoteException {
		for(int i = 0; i < chatClients.size(); i ++) {
			if(chatClients.get(i).getUserName().equals(userName)) {
				chatClients.remove(chatClients.get(i));
			}
		}
		
	}

	@Override
	public synchronized RegistrationInfo lookup(String name) throws RemoteException {
		for(RegistrationInfo i : this.chatClients) {
			if(name.contains(i.getUserName())) {
				return i;
			}
		}	
		return null;
	}

	@Override
	public synchronized Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException {
		return this.chatClients;
	}


	public static void main(String[] args) {
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        
        try {
            PresenceService serve = new ChatServer();
        // Below creates an accessible registry to bind the remote objects to.  MANDATORY to work correctly.
            LocateRegistry.createRegistry(9800);
            String name = "//localhost/ChatServer";
            Naming.rebind(name, serve);
       // Below pulls IP information to help with troubleshooting as part of the binding process.
            InetAddress ip = InetAddress.getByName("localhost");
            System.out.println("ChatServer bound:\nport: " + "9800" + "\nIP: " + ip);
            
        } catch (Exception e) {
            System.err.println("ChatServer exception: " + 
			       e.getMessage());
            e.printStackTrace();
        }

	}

}
