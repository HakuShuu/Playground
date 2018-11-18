import java.util.ArrayList;

public class task {
	int index;
	activity actHeader=new activity();
	int NOA=-1;//number of activity, assuming terminate doesn't count
	
	activity actPointer;	//I store the activities as a linked list
	ArrayList<Integer> claim=new ArrayList<Integer>();
	ArrayList<Integer> possession=new ArrayList<Integer>();
	int status=0;	//0 for unstarted, 1 for running,2 for blocked, 3 for computing, 4 for finished, 5 for aborted
	int computeCnt=0;
	int blockedTime=0;
	int runningT=0;
	boolean skip=false;//I made this bit to prevent the task being unblocked and processed during the same cycle
	boolean countDown=false;
	

	
public task(int i, int NR) {
	index=i;	
	actHeader.next=actHeader;
	actPointer=actHeader;
	status=0;
	
	claim.add(0,-42);
	possession.add(0,-42);
	for(int t=1;t<=NR;t++) {
		claim.add(t,0);
		possession.add(t,0);
	}
}

public void addAct(activity a) {
	actPointer.next=a;
	actPointer=a;
	NOA++;
}
public int[] process() {
	if(status==0) {status=1;}
	
	int[] feedback=new int[3];
	if(status==2) {	//if the task is being blocked
		feedback[0]=-2;
		feedback[1]=-1;
		feedback[2]=-1;
		return feedback;
	}
	if(status==3) {	//if the task is computing
		feedback[0]=-3;
		feedback[1]=-1;
		feedback[2]=-1;
		return feedback;
	}
	if(status==4) {	//if the task has finished
		return null;
	}
	
	activity thisAct=actPointer;
	
	feedback[0]=thisAct.category;
	feedback[1]=thisAct.field2;
	feedback[2]=thisAct.field3;
	
	if(thisAct.category==3) {feedback[2]=-1;}
	
	if(thisAct.next.category==4) {
		feedback[0]=feedback[0]*10+4;
	}
	
	return feedback;

}
public void printMe() {	//for debugging only
	System.out.println("Task index: "+index+" NOA: "+NOA);
	activity ptr=actHeader.next;
	while(ptr!=null) {
		System.out.println("\t category: "+ptr.category+" f1: "+ptr.field1+" f2:"+ptr.field2+" f3:"+ptr.field3);
		ptr=ptr.next;
	}
}
public void updateClaim(int t, int u) {
	claim.add(t, u);
}
public void clearArray(ArrayList<Integer> AL) {
	int N=AL.size()-1;
	for(int i=1;i<=N;i++) {
		AL.add(i, 0);
	}
}
public void assign(int t, int u) {
	int current=possession.get(t);
	possession.set(t, current+u);
}
public void release(int t, int u) {
	int current=possession.get(t);
	possession.set(t, current-u);
	
}
public void blockMe() {
	status=2;
}
public void unblockMe() {
	status=1;
	skip=true;
}
public void computeFor(int t) {
	computeCnt=t;
	status=3;
}
public void countDown() {	//if the activity follows a "compute+terminate" pattern, call this function
	countDown=true;
}
public boolean computing() {	//the returned boolean notifies the manager whether this task is about to terminate
	computeCnt--;

	if(computeCnt==0) {
		this.wakeUp();
	}
	return countDown;
}
public void wakeUp() {
	status=1;
	this.nextAct();
}
public void terminate(int t) {
	status=4;
	runningT=t;
}
public void abort() {
	status=5;
}
public void reset() {	//clean up after each run
	status=0;
	computeCnt=0;
	blockedTime=0;
	runningT=0;
	skip=false;
	countDown=false;
	actPointer=actHeader.next;
	this.clearArray(possession);
	this.clearArray(claim);
}
public void nextAct() {	//only the manager can decide whether the task keeps on processing its activities
	actPointer=actPointer.next;
}
}
