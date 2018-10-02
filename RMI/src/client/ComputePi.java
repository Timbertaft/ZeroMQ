package client;

import java.rmi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.math.*;
import compute.*;

public class ComputePi {
    public static void main(String args[]) {
    	
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        int x = 0;
        while(x != 4) {
        //Start of Primary Try-Catch Block for Pi function.
        try {
        	Scanner scanner = new Scanner(System.in);
        	boolean validOption = false;
        	
        	
        	x = MainMenu(validOption, x, scanner);
        	
        	if(x == 1) {
        	x = DecimalPick(validOption, x, scanner);
      	
        	args[1] = Integer.toString(x);
            String name = "//" + args[0] + "/Compute";
            Compute comp = (Compute) Naming.lookup(name);
            Pi task = new Pi(Integer.parseInt(args[1]));
            BigDecimal pi = (BigDecimal) (comp.executeTask(task));
            System.out.println("\nPi calculated to " + args[1] + " decimals.\n" + pi + "\n");
            x = 0;
        	}
        	
        	if(x == 2) {
        		ArrayList<Integer> y = PrimePick(validOption, x, scanner);
        		String name = "//" + args[0] + "/Compute";
        		Compute comp = (Compute) Naming.lookup(name);
        		Primes task = new Primes(y);
				Object[] primeresult = (Object[]) (comp.executeTask(task));
				System.out.println("All prime numbers between " + y.toArray()[1] + " and " + y.toArray()[0] + 
						":\n" + Arrays.toString(primeresult) + "\n");
        		x = 0;
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
    
    
    public static int MainMenu(boolean a, int i, Scanner s) {
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
    
    public static int DecimalPick(boolean a, int i, Scanner s) {
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
    
    public static ArrayList<Integer> PrimePick(boolean a, int i, Scanner s) {
    	a = false;
    	s.nextLine();
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	ArrayList<Integer> minmax = new ArrayList<Integer>();
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
        		//TODO: Horribly broken.  Need to fix so it returns an array of integers.  Should contain two values.
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
}