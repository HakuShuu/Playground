import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class FCFS {
	
	public static void run(ArrayList<process> bucket, boolean verbose,File rn) throws FileNotFoundException {
		Scanner rng=new Scanner(rn);
		
		ArrayList<process> unstartedList=new ArrayList<process>();
		ArrayList<process> waitingList=new ArrayList<process>();
		ArrayList<process> ioList=new ArrayList<process>();
		ArrayList<process> temp=new ArrayList<process>();
		
		for(process p:bucket) {
			unstartedList.add(p);
		}
		
		int time=0;
		int finished=0;
		double cpuCounter=0;
		double ioCounter=0;
		process runningP=null;
		
		while(finished<bucket.size()) {
			
			if(verbose) {
				System.out.print("Before cycle: "+time);
				if(time<10) {System.out.print("\t");}
				for(process p:bucket) {
					//System.out.print(" Process "+p.index+": ");
					if(p.status==0) {System.out.print("\t unstarted: 0");}
					if(p.status==1) {System.out.print("\t ready: 0");}
					if(p.status==2) {System.out.print("\t running: "+p.untilBurst);}
					if(p.status==3) {System.out.print("\t blocked: "+p.ioFor);}
					if(p.status==4) {System.out.print("\t terminated: 0");}
					
				}
				System.out.println();
			}

		
			if(!ioList.isEmpty()){		//do IO before cpu since reversing the order allows a process to do 2 things in 1 cycle
				ioCounter++;
				for(process p:ioList) {
					p.ioFor--;
					p.ioT++;
					if(p.ioFor==0) {
						p.status=1;
						p.arrivalT=time;
						temp.add(p);
						
					}
				}
			}
			
			if(runningP!=null) {		//run
				cpuCounter++;
	
				runningP.untilBurst--;
				runningP.remC--;
				if(runningP.untilBurst==0) {	//if a process shouldn't be running anymore
					
					if(runningP.remC==0) {		//if the remaining cpu time hits 0, it terminates
						runningP.status=4;
						finished++;
					}else {						//if the remaining cpu time is still not 0 yet, do IO
						runningP.status=3;
						ioList.add(runningP);
					}
					
					runningP=null;				
				}
			}
			
			if(!unstartedList.isEmpty()) {		//check every unstarted processes every time
				for(process p:unstartedList) {
					
					if(p.A==time) {
						temp.add(p);
						p.status=1;
						
					}
				}
			}
			
			stdSortProcess(temp);		//temp is our carrier variable here
			waitingList.addAll(temp);
			ioList.removeAll(temp);
			unstartedList.removeAll(temp);
			temp.clear();				//clean up cuz' more is coming
			
			if(runningP==null) {				//choose a process to run from the ready list
				if(!waitingList.isEmpty()) {
					runningP=waitingList.get(0);
					runningP.status=2;
					int b=randomOS(runningP.B,rng);
					if(b>runningP.remC){
						runningP.giveBurst(runningP.remC);
					}else {
						runningP.giveBurst(b);
					}
					waitingList.remove(runningP);
				}
			}
			
			for(process p: waitingList) {		//updating waiting time
				p.waitingT++;
			}
			
			
			
			time++;
			
		}
		System.out.println();
		System.out.println("The scheduler's algorithm: First Come First Serve");
		time--;
		double taCounter=0;
		double	wtCounter=0;
		for(process p:bucket) {
			p.updateAndPrint();
			taCounter+=p.taT;
			wtCounter+=p.waitingT;
			p.initialize();
		}
		double tpt=(double)(100*finished)/time;
		taCounter=taCounter/finished;
		wtCounter=wtCounter/finished;
		ioCounter=ioCounter/time;
		cpuCounter=cpuCounter/time;
		System.out.println();
		System.out.println("Summary Data: ");
		System.out.println("\t Finishing time: "+time);
		System.out.println("\t CPU Util.: "+cpuCounter);
		System.out.println("\t I/O Util.: "+ioCounter);
		System.out.println("\t Throughput: "+tpt+" per hundred cycles");
		System.out.println("\t Avg. turnaround time: "+taCounter);
		System.out.println("\t Avg. waiting time: "+wtCounter);
		
		rng.close();
	}
	

	private static int randomOS(int b,Scanner rng) {
		return(1+(rng.nextInt()%b));
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
