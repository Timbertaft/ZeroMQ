package client;

import compute.*;
import java.util.ArrayList;

public class Primes implements Task {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4787698337153180499L;
	private ArrayList<Integer> range;
	
	public Primes(ArrayList<Integer> range) {
        this.range = range;
    }
	@Override
	public Object execute() {
		return PrimeCalculate(range);
	}
	
	public static Object[] PrimeCalculate(ArrayList<Integer> prime) {
		boolean flag = false;
		boolean loop = false; 
		
		//"loop" value used to assess whether or not loop was entered.  
		//Prevents entering bounds in wrong order.
		
		int j = 0;
		ArrayList<Integer> primelist = new ArrayList<Integer>();
		Object[] primearray = prime.toArray();
		
		if((int) primearray[0] >= 2) {
			primelist.add(2);
		}
		
			for(int i = (int) primearray[1]; i <= (int) primearray[0]; i++) {
				loop = true;
				
			//entered the loop
				
				for(j = 2; j < i; j++) {
					
					//Entered internal loop
					
					if (i % j == 0) {
						
						//entered breakpoint
						
						flag = false;
						break;
					}
					else {
						
						//entered flagging state
						
						flag = true;
					}
				}
				if(flag == true) {
					
					//added prime
					
					primelist.add(i);
				}
			}
			if(loop == true) {
			primearray = primelist.toArray();
			return primearray;
		} else {
			Object[] errorarray = new Object[1];
			errorarray[0] = "Invalid entries.  Please try again.";
			return errorarray;
		}
	}
	
}
