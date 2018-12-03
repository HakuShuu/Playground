import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
	static int M;
	static int P;
	static int S;
	static int J;
	static int N;
	static String R;
	
	public static void main(String[] args) throws FileNotFoundException {
		M=Integer.parseInt(args[0]);
		P=Integer.parseInt(args[1]);
		S=Integer.parseInt(args[2]);
		J=Integer.parseInt(args[3]);
		N=Integer.parseInt(args[4]);
		R=args[5];
		boolean verbose;
		if(args.length==7) {
			verbose= Integer.parseInt(args[6])==1 ? true:false;	//verbose output trigger
		}else {
			verbose=false;
		}
		
		System.out.println("The machine size is: "+M);
		System.out.println("The page size is: "+P);
		System.out.println("The process size is: "+S);
		System.out.println("The job mix number is: "+J);
		System.out.println("The number of references per process is: "+N);
		System.out.println("The replacement algorithim is: "+R);
		System.out.println("Verbose output: "+verbose);
		System.out.println();
		
		int repAlgo=-1;
		if(R.equalsIgnoreCase("lifo")) {
			repAlgo=1;
		}else if(R.equalsIgnoreCase("random")) {
			repAlgo=2;
		}else if(R.equalsIgnoreCase("lru")) {
			repAlgo=3;
		}
		
		String pathFinder=System.getProperty("user.dir");
		pathFinder=pathFinder+"/random-numbers";
		FileReader fr=new FileReader(pathFinder);
		BufferedReader br=new BufferedReader(fr);
		Scanner RNG=new Scanner(br);
		
		frameTable fTable=new frameTable(M/P);	
		
		ArrayList<process> pList=new ArrayList<process>();	//granted this implementation of processes seems dumb and cumbersome
		process p1;
		process p2;
		process p3;
		process p4;
		if(J==1) {
			p1=new process(1,1,0,0);
			pList.add(p1);
		}else if(J==2) {
			p1=new process(1,1,0,0);
			p2=new process(2,1,0,0);
			p3=new process(3,1,0,0);
			p4=new process(4,1,0,0);
			pList.add(p1);
			pList.add(p2);
			pList.add(p3);
			pList.add(p4);
		}else if(J==3) {
			p1=new process(1,0,0,0);
			p2=new process(2,0,0,0);
			p3=new process(3,0,0,0);
			p4=new process(4,0,0,0);
			pList.add(p1);
			pList.add(p2);
			pList.add(p3);
			pList.add(p4);
		}else if(J==4) {
			p1=new process(1,0.75,0.25,0);
			p2=new process(2,0.75,0,0.25);
			p3=new process(3,0.75,0.125,0.125);
			p4=new process(4,0.5,0.125,0.125);
			pList.add(p1);
			pList.add(p2);
			pList.add(p3);
			pList.add(p4);
		}
		
		int q=3;
		int finishedP=0;	//finished process counter
		int time=1;			//odd enough that time is 1-based in Gottlieb's sample output
		process runningP=pList.get(0);
		int intPointer;
		frame fPointer;		//these two are just carrier variables 
		
		while(finishedP<pList.size() ) {
			if(verbose) {System.out.print("At Time "+time+", process "+runningP.index+" references word "+ runningP.curW+" (page "+runningP.curP+")");}
			
			intPointer=fTable.findHit(runningP,time);	//is this a hit?
			
			if(intPointer!=-1) {		//found a hit
				if(verbose) {System.out.print(" and found a hit at frame "+ intPointer+" holding its page "+runningP.curP);}
			}else {						//found a fault
				if(verbose) {System.out.print(" and spawned a fault, ");}
				
				runningP.updateFault();
				
				fPointer=fTable.findFree();	//is there a free frame?
				
				if(fPointer!=null) {		//there is one!
					if(verbose) {System.out.print("but a free frame "+fPointer.index +" has been found and used.");}
					
					fPointer.inhabit(runningP, time);
				}else {						//there is no free frame, have to evict one
					fPointer=fTable.findVictim(repAlgo, RNG);
					
					if(verbose) {System.out.print("the manager evicts frame "+fPointer.index +" holding page "+fPointer.pageOfTenant+" of process "+fPointer.tenant.index);}
					
					fPointer.evict(time);	//RIP
					fPointer.inhabit(runningP, time);	//and welcome the new dweller
				}
			}
			
			runningP.remRef--;			//one less reference to make
			runningP.nextRef(RNG);
			
			q--;
			if(runningP.remRef==0) {
				runningP.finished=true;
				finishedP++;
				q=0;
			}
			
			if(q==0 && finishedP<pList.size()) {	//if the scheduler should move onto the next process
				runningP=pList.get(runningP.index%pList.size());
				while(runningP.finished) {
					runningP=pList.get(runningP.index%pList.size());
				}	
				q=3;
			}
			
			time++;
			if(verbose) {System.out.println();}
		}
		
	
		System.out.println();
		
		double R;
		int sum1=0;
		double sum2=0;
		double sum3=0;
		String handler;
		for(process p: pList) {
			if(p.numEviction==0) {
				R=0;
			}else {
				R=(double) p.totalResidency;
			}
			
			sum1+=p.numFault;
			sum2+=R;
			sum3+=p.numEviction;
			
			handler= R==0 ? "undefined": Double.toString(R/ (double) p.numEviction);
			System.out.println("Process "+p.index+" had "+p.numFault+" fault(s) and "+handler+" average residency.");
		}
		handler= sum2==0 ? "undefined" : Double.toString(sum2/sum3);
		System.out.println();
		System.out.println("The total number of fault(s) is "+sum1+" and the overall avg. residency is "+handler);
		RNG.close();
	}
	
	static int ranABCD(Scanner s, double a, double b, double c) {
		double i=Double.parseDouble(s.next());
		i=i/(double)(Integer.MAX_VALUE+1d);
		if(i<a) {
			return 1;
		}else if(i<a+b) {
			return 2;
		}else if(i<a+b+c) {
			return 3;
		}else {
			return 4;
		}
		
	}
}
