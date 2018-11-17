import java.util.ArrayList;
import java.util.HashMap;


public class Optimistic {
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
					if(optGrant(rType,rUnit,available)) {
					
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
							System.out.println("\t Task: "+T.index+"'s request still cannot be granted");
						}
					}
				}
				blockedT.removeAll(unblockCache);
				unblockCache.clear();
			}
			
			if(verbose && (NT-terminatedT)!=blockedT.size()) {System.out.println("\t Processing unblocked tasks");}
			for(int i=1;i<=NT;i++) {
				task T=tMap.get(i);
				if(T.status==3) {
					if(T.computing() &&T.computeCnt==1) {
						if(verbose) {System.out.println("\t Task: "+T.index+" finishes the last cycle of computation and terminates ");}
						T.terminate(time+1);
						terminatedT++;
						
					}else if(T.computeCnt>0){
						if(verbose) {System.out.println("\t Task: "+T.index+" is computing for "+T.computeCnt+" more cycles");}
					}
				}
				if(T.status==5|| T.status==4 ||T.status==2) {continue;}
				if(T.skip==true) {T.skip=false;continue;}
				
				feedback=T.process();
				if(feedback[0]==0) {
					T.nextAct();
					if(verbose) {
						System.out.println("\t Task: "+T.index+" has been initiated ");
					}
					
				}else if(feedback[0]==1) {	//this signals a request
					rType=feedback[1];
					rUnit=feedback[2];
					
					if(optGrant(rType,rUnit,available)) {
					
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
							System.out.println("\t Task: "+T.index+"'s request cannot be granted and is blocked.");
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
			
			
			if(blockedT.size()==(NT-terminatedT) && checkLock(blockedT,available,releaseQ)) { //this represents a deadlock
				if(verbose) {System.out.println("\t Deadlock detected!");}
				boolean flow=false;
				task victim;
				while(!flow) {	
					
					for(int k=1;k<=NT;k++) {
						victim=tMap.get(k);
						if(!blockedT.contains(victim)) {continue;}
						rType=victim.actPointer.field2;
						rUnit=victim.actPointer.field3;

						if(!stillLocked(rType,rUnit,releaseQ,available)) {					
							
							flow=true;
							
							if(verbose) {
								System.out.println("\t Post abortion, task "+victim.index+"'s request can be granted: ("+rType+","+rUnit+"), deadlock resolved");
							}
							
						}else {
							for(int i=1;i<=NR;i++) {
								if(victim.possession.containsKey(i)) {
									pushReleaseQ(i,victim.possession.get(i),releaseQ);
									victim.release(i, victim.possession.get(i));
								}
							}
						
							victim.abort();
							terminatedT++;
							unblockCache.add(victim);
					
							if(verbose) {
								System.out.println("\t Task "+victim.index+" has released its resources and is aborted");
							}
						}
					}
						
				
					
				}
			}
			blockedT.removeAll(unblockCache);
			unblockCache.clear();
			
			int returned=0;
			for(int i=1;i<=NR;i++) {
				returned=available.get(i)+releaseQ.get(i);
				available.set(i, returned);
			}
			
			clearArray(releaseQ);
			
			time++;
			if(verbose) {System.out.println();}
		}
		
		System.out.println("All tasks have terminated under the optimistic manager: ");
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
	
public static boolean optGrant(int t, int u,ArrayList<Integer> available) {
	if(available.get(t)>=u) {
		return true;
	}else {
		return false;
	}
}

public static void pushReleaseQ(int t, int u, ArrayList<Integer> q) {
	q.set(t, q.get(t)+u);
}
public static void clearArray(ArrayList<Integer> a) {
	for(int i=0;i<a.size();i++) {
		a.set(i,0);
	}
	a.set(0, -42);
}
public static boolean stillLocked(int type, int requested, ArrayList<Integer> release, ArrayList<Integer> available) {
	int a=available.get(type)+release.get(type);
	return (a<requested);
}

public static boolean checkLock(ArrayList<task> BT, ArrayList<Integer> AV,ArrayList<Integer> RQ) {
	if(BT.size()==0) {return false;}
	for(task t: BT) {
		int type=t.actPointer.field2;
		int unit=t.actPointer.field3;
		if(AV.get(type)+RQ.get(type)>=unit) {
			return false;
		}
	}
	
	return true;
}

}

