import java.util.ArrayList;
import java.util.Scanner;

public class frameTable {
	int size;
	ArrayList<frame> fList;
	frame victim;
	boolean full;		//this filed exists only for the sake of detecting free frames
	
	public frameTable(int n) {
		fList=new ArrayList<frame>();
		size=n;
		for(int i=0;i<n;i++) {
			frame f=new frame(i);
			fList.add(f);
		}
		victim=null;
		full=false;
	}
	public int findHit(process p,int t) {
		for(frame f: fList) {
			if(f.tenant==null) {continue;}
			else if(f.tenant.index==p.index && f.pageOfTenant==p.curP) {	//checks process and its current page
				f.recentUse=t;
				return f.index;			//return the index upon hit, -1 upon miss
				}
		}	
		return -1;
	}
	public frame findFree() {
		if(full) {return null;}
		int id=-1;
		frame target=null;
		for(frame f:fList) {
			if(f.index>id && f.free) {
				id=f.index;
				target=f;
			}
		}
		if (target==null) {full=true;}	//might as well mark it full since it can never go back to a non-full state 
		return target;
	}
	
	public frame findVictim(int mode,Scanner s) {
		if(mode==1) {
			lifoFindVictim();
		}else if(mode==2) {
			randomFindVictim(s);
		}else if(mode==3) {
			lruFindVictim();
		}
		
		return victim;
	}
	
	
	private void lifoFindVictim() {
		int lt=-1;
		for(frame f: fList) {
			if(f.loadTime>lt) {
				lt=f.loadTime;
				victim=f;
			}
		}
	}
	private void randomFindVictim(Scanner s) {
		int id=(s.nextInt()+size)%size;
		victim=fList.get(id);
	}
	private void lruFindVictim() {
		int lru=Integer.MAX_VALUE;
		for(frame f: fList) {
			if (f.recentUse<lru) {
				lru=f.recentUse;
				victim=f;
			}
		}
	}
}
