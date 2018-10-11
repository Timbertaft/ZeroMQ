package client;

import java.rmi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import java.math.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

import compute.*;

public class ComputePi implements Runnable {
	
	private static Thread start = new Thread(new ComputePi());
	static int x = 0;
    static int iterator = 0;
    final static Scanner scanner = new Scanner(System.in);
    static boolean validOption = false;
    static Random rand = new Random();
	public static int port = rand.nextInt(9999) + 1000;
	
	public static void main(String args[]) {
		start.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
    
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        Loop();
    }
	
	public static void Loop() {
    	port = rand.nextInt(9999) + 1000;
		while(x != 4) {
	        //Start of Primary Try-Catch Block for Pi function.
	        try {
	        	if (x == 0) {
	        	
	        	x = MainMenu(validOption, x, scanner);
	        	}
	        	
	        	if(x == 1) {
	        	x = DecimalPick(validOption, x, scanner);
	      	
	        	String y = Integer.toString(x);
	            String name = "//" + "localhost" + "/Compute";
	            Compute comp = (Compute) Naming.lookup(name);
	            Pi task = new Pi(Integer.parseInt(y));
	            BigDecimal pi = (BigDecimal) (comp.executeTask(task));
	            System.out.println("\nPi calculated to " + y + " decimals.\n" + pi + "\n");
	            x = 0;
	        	}
	        	
	        	if(x == 2) {
	        		iterator += 1;
	        		ArrayList<Integer> y = PrimePick(validOption, x, scanner, iterator);
	        		String name = "//" + "localhost" + "/Compute";
	        		Compute comp = (Compute) Naming.lookup(name);
	        		Primes task = new Primes(y);
					Object[] primeresult = (Object[]) (comp.executeTask(task));
					if(Arrays.toString(primeresult).contains("Invalid entries.  Please try again.")) {
						System.out.println(Arrays.toString(primeresult));
						x = 2;
					}
					else {
					System.out.println("All prime numbers between " + y.toArray()[1] + " and " + y.toArray()[0] + 
							":\n" + Arrays.toString(primeresult) + "\n");
	        		x = 0;
	        		iterator = 0;
	        		
					}
	        	}
	        	
	        	if (x == 3) {
	        		String y = Username(validOption, x, scanner);
	        		String name = "//" + "localhost" + "/ChatServer";
	        		boolean loginstatus = false;
	        		while(!available(port)) {
	        			port += 1;
	        		}
	        		PresenceService serve = (PresenceService) Naming.lookup(name);
	        		String hostname = InetAddress.getLocalHost().getHostName();
	        		RegistrationInfo registered = new RegistrationInfo(y, hostname, port, loginstatus);
	        		ChatClient client = new ChatClient(serve, registered);
	        		new Thread(client).start();
	        		x = 0;
	        		return;

	        	}
	        	
	        	if (x == 4) {
	        		scanner.close();
	        		System.out.println("Terminating Application...\nPlease do not panic...\nApplication Successfully Terminated!\n");
	        	}
	        } catch (Exception e) {
	            System.err.println("ComputePi exception: " + 
	                               e.getMessage());
	            e.printStackTrace();
	        }
	    }
	}
    
    
    private static int MainMenu(boolean a, int i, Scanner s) {
   	 //Beginning of user prompt responses to menu of questions.
      	while(!a) {
      	System.out.println("Which task would you like performed?"
      			+ "\nPlease enter a corresponding number:"
      			+ "\n1: Compute Pi"
      			+ "\n2: Compute Primes"
      			+ "\n3: Chat Client"
      			+ "\n4: Exit");
      	// Try-Catch Block for catching invalid user entries for main menu.
      	try {
      	int selection = s.nextInt();
      	i = selection;
      	if(i <= 4) {
      		a = true;
      	}
      	// Above returns true if range of values are acceptable.  Terminates loop. 
      	//Below catches error if value above option 3.
      	else {
      		System.out.println("Invalid option.  Please try again.\n");
      		i = 0;
      		s.nextLine();
      	}
      	// Below catches error if value is not an integer.
      	}
      	catch(java.util.InputMismatchException e) {
      		System.out.println("Invalid option.  Please try again.\n");
      		a = false;
      		i = 0;
      		s.nextLine();
      	}
      	}
		return i;
      }
    
    private static int DecimalPick(boolean a, int i, Scanner s) {
    	a = false;
    	s.nextLine();
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	while(!a) {    		
        	System.out.println("COMPUTE PI:\n To how many decimal places do you want Pi computed?");
        	try {
            	int selection = s.nextInt();
            	i = selection;
            	a = true;
            	// Below catches error if value is not an integer.
            	}
            	catch(java.util.InputMismatchException e) {
            		System.out.println("Invalid option.  Please try again.\n");
            		a = false;
            		i = 0;
            		s.nextLine();
            	}
    	}
    	return i;
    }
    
    private static ArrayList<Integer> PrimePick(boolean a, int i, Scanner s, int rep) {
    	a = false;
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	ArrayList<Integer> minmax = new ArrayList<Integer>();
    	//iterator value "rep" used to assess if previous entry was invalid.  Prevents hanging from lack of user input.
    	if(rep <= 1) {
    	s.nextLine();
    	}
    	while(!a) {    		
    		if(minmax.isEmpty()) {
        	System.out.println("COMPUTE PRIMES:\n What are the lower and upper bounds within "
        			+ "which you would like to search for prime numbers?");
        	System.out.println("\nChoose the upper bound.");
    		}
    		else {
    			System.out.println("\nChoose the lower bound.");
    		}
        	try {
            	int selection = s.nextInt();
            	i = selection;
            	minmax.add(i);
            	if(minmax.size() >= 2) {
            		a = true;
            		s.nextLine();
            	}
            	// Below catches error if value is not an integer.
            	}
            	catch(java.util.InputMismatchException e) {
            		System.out.println("Invalid option.  Please try again.\n");
            		a = false;
            		i = 0;
            		s.nextLine();
            	}
    	}
    	return minmax;
    }
    
    private static String Username(boolean a, int i, Scanner s) {
    	a = false;
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	s.nextLine();
    	String username = null;
    	while(!a) {
    		System.out.println("CHAT CLIENT:\n Welcome to RMI CHAT!"
    				+ "  Please enter your preferred username.");
    		try {
    			username = s.nextLine();
    			a = true;
    		}
    		catch (java.util.InputMismatchException e) {
    			System.out.println("Something was wrong with this Username choice.  Please try again.\n");
    			a = false;
    			i = 0;
    			s.nextLine();
    		}
    	}
    	return username;
    	
    }
    
    private static boolean available(int port) {
        System.out.println("--------------Testing port " + port);
        Socket s = null;
        try {
            s = new Socket(InetAddress.getLocalHost().getHostName(), port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            System.out.println("--------------Port " + port + " is not available");
            return false;
        } catch (IOException e) {
            System.out.println("--------------Port " + port + " is available");
            return true;
        } finally {
            if( s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unexpected socket error." , e);
                }
            }
        }
    }
}