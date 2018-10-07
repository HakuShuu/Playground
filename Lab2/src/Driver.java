import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException {
		String fileName=args[0];
		FileReader f=new FileReader(fileName);
		BufferedReader buffer=new BufferedReader(f);
		Scanner input=new Scanner(buffer);
		
		int num=input.nextInt(); //num= number of processes
		ArrayList<process> bucket=new ArrayList<process>();
		
		String ptKiller;//A temporary variable to kill the parenthesis in the input
		int tempA;
		int tempB;
		int tempC;
		int tempM;
		
		
		for(int i=0;i<num;i++) {
			ptKiller=input.next();
			ptKiller=ptKiller.substring(1);
			tempA=Integer.parseInt(ptKiller);
			tempB=input.nextInt();
			tempC=input.nextInt();
			ptKiller=input.next();
			ptKiller=ptKiller.substring(0,1);
			tempM=Integer.parseInt(ptKiller);
			
			process p=new process(tempA,tempB,tempC,tempM,i);
			bucket.add(p);
		}

		System.out.print("The original input was: "+num);
		for(process p: bucket) {
			System.out.print(" ("+p.A+" "+p.B+" "+p.C+" "+p.M+") ");
		}
		System.out.println();
		
		stdSortProcess(bucket);
		for(int i=0;i<bucket.size();i++) {
			bucket.get(i).index=i;
		}
		
		System.out.print("The sorted input was: "+num);
		for(process p: bucket) {
			System.out.print(" ("+p.A+" "+p.B+" "+p.C+" "+p.M+") ");
		}
		System.out.println();
		
	//	new FCFS();
	//	FCFS.run(bucket, true);
		
		
		
		new LCFS();
		LCFS.run(bucket, true);
		
		
		
		
		input.close();
	}

	private static void stdSortProcess(ArrayList<process> bucket) {
		class myComparator implements Comparator<process>{

			public int compare(process p1, process p2) {
				if(p1.arrivalT!=p2.arrivalT) {
					return p1.arrivalT-p2.arrivalT;
				}else {
					return p1.index-p2.index;
				}
				
			};
		
		}
		
	
		Collections.sort(bucket, new myComparator());
		
	
	}
}
