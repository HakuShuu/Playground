import java.util.ArrayList;
import java.util.HashMap;

public class Banker {
	public static void run(ArrayList<Integer> resource, HashMap<Integer,task> tMap, boolean verbose) {
		int time=0;
		int terminatedT=0;
		int NT=tMap.size();
		int NR=resource.size()-1;	//for simplicity I make resource[] 1-based

		
		
		ArrayList<Integer> available=new ArrayList<Integer>();
		available.addAll(resource);
		ArrayList<task> blockedT=new ArrayList<task>();
		ArrayList<task> unblockCache=new ArrayList<task>();
		ArrayList<Integer> releaseQ=new  ArrayList<Integer>();
		releaseQ.add(0,-42);
		for(int i=1;i<=NR;i++) {
			releaseQ.add(i,0);
		}
		
		
		int[] feedback=new int[3];//carrier variable
		int rType=-1;
		int rUnit=-1;
		
		while(terminatedT!=NT ) {
			
			if(verbose) {
				System.out.println("Cycle: "+time+"-"+(time+1)+": ");
				System.out.println();
			}
			
			if(!blockedT.isEmpty()) {
				if(verbose) {System.out.println("\t Processing blocked tasks ");}
				for(task T: blockedT) {	
					T.blockedTime++;
					rType=T.actPointer.field2;
					rUnit=T.actPointer.field3;
					if(bankerGrant(rType,rUnit,T,tMap,available)) {	//can we unblock a task?
					
						T.assign(rType,rUnit);
						available.set(rType,available.get(rType)-rUnit);
						T.nextAct();
					
						T.unblockMe();
						unblockCache.add(T);
					
						if(verbose) {
							System.out.println("\t Task: "+T.index+"'s request has been granted: ("+rType+","+rUnit+") and has become unblocked");
						}
					}else {
						if(verbose) {
							System.out.println("\t Task: "+T.index+"'s request still cannot be granted: ("+rType+","+rUnit+")" );
						}
					}
				}
				blockedT.removeAll(unblockCache);
				unblockCache.clear();
			}
			
			if(verbose && (NT-terminatedT)!=blockedT.size()) {System.out.println("\t Processing unblocked tasks");}
			for(int i=1;i<=NT;i++) {
				task T=tMap.get(i);
				if(T.status==3) {	//let a task compute
					if(T.computing() &&T.computeCnt==1) {
						if(verbose) {System.out.println("\t Task: "+T.index+" finishes the last cycle of computation and terminates ");}
						T.terminate(time+1);
						terminatedT++;
						
					}else if(T.computeCnt>0){
						if(verbose) {System.out.println("\t Task: "+T.index+" is computing for "+T.computeCnt+" more cycles");}
					}
				}
				if(T.status==5|| T.status==4 ||T.status==2) {continue;}
				if(T.skip==true) {T.skip=false;continue;}//only the tasks that just became unblocked will be skipped once
				
				feedback=T.process();
				if(feedback[0]==0) {	//this signals a claim
					if(feedback[2]>resource.get(feedback[1])) {
						if(verbose) {
							System.out.println("\t Task: "+T.index+" 's claim: ("+feedback[1]+","+feedback[2]+") exceeds the amount of resource and is thus aborted");
						}
						releaseAll(T,releaseQ);
						T.abort();
						terminatedT++;
						continue;
					}
					T.updateClaim(feedback[1], feedback[2]);
					T.nextAct();
					if(verbose) {
						System.out.println("\t Task: "+T.index+" has initiated a claim: ("+feedback[1]+","+feedback[2]+") ");
					}
					
				}else if(feedback[0]==1) {	//this signals a request
					rType=feedback[1];
					rUnit=feedback[2];
					
					if((rUnit+T.possession.get(rType))>T.claim.get(rType)) {	//request exceeds claim, abort task
						if(verbose) {
							System.out.println("\t Task: "+T.index+" 's request: ("+rType+","+rUnit+") exceeds its claim and is thus aborted");
						}
						
						releaseAll(T,releaseQ);
						T.abort();
						terminatedT++;
						continue;
					}
					
					if(bankerGrant(rType,rUnit,T,tMap,available)) {
					
						T.assign(rType,rUnit);
						available.set(rType,available.get(rType)-rUnit);
						T.nextAct();
						if(verbose) {
							System.out.println("\t Task: "+T.index+"'s request has been granted: ("+rType+","+rUnit+") ");
						}
						
					}else {
						blockedT.add(T);
						T.blockMe();
						
						if(verbose) {
							System.out.println("\t Task: "+T.index+"'s request: ("+rType+","+rUnit+") cannot be granted and is blocked.");
						}
					}
				}else if(feedback[0]==2 || feedback[0]==24) {	//this signals a release, or a release+terminate
					rType=feedback[1];
					rUnit=feedback[2];
				
					pushReleaseQ(rType,rUnit,releaseQ);
					T.release(rType, rUnit);
					T.nextAct();
					
					
						if(feedback[0]==24) {
							if(verbose) {System.out.println("\t Task: "+T.index+" has released: ("+rType+","+rUnit+") and terminates");}
							T.terminate(time+1);
							terminatedT++;
						}else {
							if(verbose) {System.out.println("\t Task: "+T.index+" has released: ("+rType+","+rUnit+") ");}
						}
					
					
				
				}else if(feedback[0]==3 || feedback[0]==34) {	//this signals a compute
					
					
					if(feedback[0]==34) {
						if(verbose) {System.out.println("\t Task: "+T.index+" starts computing for "+feedback[1]+" cycles and will terminate once the computation is complete");}
						T.countDown();
						T.computeFor(feedback[1]);
						if(feedback[1]==1) {
							T.terminate(time+1);
							terminatedT++;
						}
					}else {
						if(verbose) {System.out.println("\t Task: "+T.index+" starts computing for "+feedback[1]+" cycles");}
						T.computeFor(feedback[1]);
					}
					
				}
				
			}
			
			
			int returned=0;				//returning all released resources at the end of a cycle 
			for(int i=1;i<=NR;i++) {
				returned=available.get(i)+releaseQ.get(i);
				available.set(i, returned);
			}
			
			clearArray(releaseQ);
			
			time++;
			if(verbose) {System.out.println();}
		}
		
		System.out.println("All tasks have terminated under the banker: ");
		double d=0;
		int f1=0;
		int f2=0;
		for(int i=1;i<=NT;i++) {
			task T=tMap.get(i);
			if(T.status==5) {
				System.out.println("\t Task"+T.index+": aborted");
				
			}else {
				d=(double)T.blockedTime/(T.NOA+T.blockedTime);
				f1+=T.runningT;
				f2+=T.blockedTime;
				System.out.println("\t Task"+T.index+": "+(T.runningT)+" "+T.blockedTime+" "+ 100*d+"%");
			}
			T.reset();
		}
		System.out.println("\t Total: "+f1+" "+f2+" "+ 100*(double)f2/(double)f1+"%");
	}
	

public static void releaseAll(task T, ArrayList<Integer> q) {
	int NR=q.size()-1;
	for(int type=1;type<=NR;type++) {
		pushReleaseQ(type,T.possession.get(type),q);
	}
	T.clearArray(T.possession);
}
public static void pushReleaseQ(int t, int u, ArrayList<Integer> q) {	//release resource only at the end of a cycle
	q.set(t, q.get(t)+u);
}
public static void clearArray(ArrayList<Integer> a) {
	for(int i=0;i<a.size();i++) {
		a.set(i,0);
	}
	a.set(0, -42);
}

public static boolean bankerGrant(int type, int unit,task T, HashMap<Integer,task> tMap, ArrayList<Integer> available ) {	

	if(unit>available.get(type)) {return false;}
	ArrayList<Integer> A=new ArrayList<Integer>();
	A.addAll(available);
	A.set(type, A.get(type)-unit);
	
	T.assign(type, unit);
	
	
	ArrayList<task> candidates=new ArrayList<task>();
	
	for(int i=1;i<=tMap.size();i++) {
		task t=tMap.get(i);
		if(t.status==1 || t.status==3 || (t.index==T.index)) {	//dump all unblocked tasks and this task into the stewing pot
			candidates.add(t);
		}
	}
	
	boolean result=recurCheck(candidates,A);
	T.release(type, unit);
	return result;
}
public static boolean recurCheck(ArrayList<task> C,ArrayList<Integer> A) {	//recursive subroutine to verify safety

	if(C.size()==0) {	//when there is no task, of course the system is safe
		return true;
	}

	boolean hit=false;

	task finisher;
	int NR= A.size()-1;
	int maxAmount=0;
	boolean temp=true;
	
	for(int i=0;i<C.size();i++) {
	
		finisher=C.get(i);
		temp=true;
		
		for(int t=1;t<=NR;t++) {
			maxAmount=finisher.claim.get(t)-finisher.possession.get(t);
			if(maxAmount>A.get(t)) {
				temp=false;				//if a task has unsatisfiable request, it can't be the first to terminate, go to the next
				break;
			}
		}
		
		if(temp) {
			hit=true;
			for(int t=1;t<=NR;t++) {
				A.set(t, A.get(t)+finisher.possession.get(t));
			}
			C.remove(finisher);	//if a task can be guaranteed to finish, let it release and finish so we can check the rest
			break;
		}
	}
	
	if(hit) {
		return(recurCheck(C,A));
	}else{
		return false;
	}
}
}



