import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class FCFS {
	
	public static void run(ArrayList<process> bucket, boolean verbose) throws FileNotFoundException {
		File rn=new File("/home/haku/Desktop/random-numbers");
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
				for(process p:bucket) {
					System.out.print(" Process "+p.index+": ");
					if(p.status==0) {System.out.print("unstarted");}
					if(p.status==1) {System.out.print("ready");}
					if(p.status==2) {System.out.print("running");}
					if(p.status==3) {System.out.print("blocked");}
					if(p.status==4) {System.out.print("terminated");}
					
				}
				System.out.println();
			}

		
			if(!ioList.isEmpty()){
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
			
			if(runningP!=null) {
				cpuCounter++;
	
				runningP.untilBurst--;
				runningP.remC--;
				if(runningP.untilBurst==0) {
					
					if(runningP.remC==0) {
						runningP.status=4;
						finished++;
					}else {
						runningP.status=3;
						ioList.add(runningP);
					}
					
					runningP=null;
				}
			}
			
			if(!unstartedList.isEmpty()) {
				for(process p:unstartedList) {
					
					if(p.A==time) {
						temp.add(p);
						p.status=1;
						
					}
				}
			}
			
			stdSortProcess(temp);
			waitingList.addAll(temp);
			ioList.removeAll(temp);
			unstartedList.removeAll(temp);
			temp.clear();
			
			if(runningP==null) {
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
			
			for(process p: waitingList) {
				p.waitingT++;
			}
			
			
			
			time++;
			
		}
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
