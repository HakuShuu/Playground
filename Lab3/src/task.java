import java.util.HashMap;

public class task {
	int number;
	activity actPointer;	//I store the activities in a linked list
	activity actHeader=new activity();
	HashMap<Integer,Integer> claim=new HashMap<Integer,Integer>();	//the first field stores the type of resource, the second field stores number
	int status=0;	//0 for unstarted, 1 for running,2 for blocked, 3 for computing, 4 for finished
	int computeCnt=0;
	
public task(int i) {
	number=i;	
	actHeader.next=actHeader;
	actPointer=actHeader;
	status=0;
	
}
public void addAct(activity a) {
	actPointer.next=a;
	a=actPointer;
}
public int[] proceed() {
	if(status==0) {status=1;}
	
	int[] feedback=new int[3];
	if(status==2) {
		feedback[0]=-2;
		feedback[1]=-2;
		feedback[2]=-2;
		return feedback;
	}
	if(status==3) {
		computeCnt--;
		if(computeCnt==0) {
			this.wakeUp();
		}
		feedback[0]=-3;
		feedback[1]=-3;
		feedback[2]=-3;
		return feedback;
	}
	if(status==4) {
		return null;
	}
	
	activity thisAct=actPointer;
	if(thisAct.category==0) {
		this.updateClaim(thisAct.field2, thisAct.field3);
		feedback[0]=0;
		feedback[1]=0;
		feedback[2]=0;
	}else if(thisAct.category==1) {
		feedback[0]=1;
		feedback[1]=thisAct.field2;
		feedback[2]=thisAct.field3;
	}else if(thisAct.category==2) {
		feedback[0]=2;
		feedback[1]=thisAct.field2;
		feedback[2]=thisAct.field3;
	}else if(thisAct.category==3) {
		feedback[0]=3;
		feedback[1]=thisAct.field2;
		feedback[2]=-1;
	}
	
	if(thisAct.next.category==4) {
		feedback[0]=feedback[0]*10+4;
		this.terminate();
	}
	
	actPointer=actPointer.next;
	return feedback;

}
public void updateClaim(int a1, int a2) {
	claim.put(a1, a2);
}
public void blockMe() {
	status=2;
}
public void computeFor(int t) {
	computeCnt=t;
	status=3;
}
public void wakeUp() {
	status=1;
}
public void terminate() {
	status=4;
}
public void reset() {
	status=0;
	actPointer=actHeader;
}
}
