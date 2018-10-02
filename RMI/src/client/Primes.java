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
		int flag = 0;
		int j = 0;
		ArrayList<Integer> primelist = new ArrayList<Integer>();
		Object[] primearray = prime.toArray();
		System.out.println("Primes between " + primearray[1] + " and " + primearray[0]);
		if((int) primearray[0] >= 2) {
			primelist.add(2);
		}
		for(int i = (int) primearray[1]; i <= (int) primearray[0]; i++) {
			System.out.println("entered the loop");
			for(j = 2; j < i; j++) {
				System.out.println("Entered internal loop");
				if (i % j == 0) {
					System.out.println("entered breakpoint.");
					flag = 0;
					break;
				}
				else {
					System.out.println("entered flagging state");
					flag = 1;
				}
			}
			if(flag == 1) {
				System.out.println("added prime.");
				primelist.add(i);
			}
		}
		primearray = primelist.toArray();
		System.out.println(primearray[0] + " " + primearray[1] + " " + primearray[2] + "from inside Primes method.");
		return primearray;
	}
}
