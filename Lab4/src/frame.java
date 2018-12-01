
public class frame {
	int index;
	
	boolean free;
	int recentUse;
	int loadTime;	//the last time when this frame is loaded
	process tenant;
	int pageOfTenant;
	
	public frame(int i) {
		index=i;
		free=true;
		recentUse=-1;
		loadTime=-1;
		tenant=null;
		pageOfTenant=-1;
	}
	
	public void inhabit(process p,int t) {
		free=false;
		tenant=p;
		pageOfTenant=p.curP;
		recentUse=t;
		if(loadTime==-1) {
			loadTime=t;
		}
	}
	public void evict(int t) {
		tenant.numEviction++;
		tenant.totalResidency+=t-loadTime;
		
		tenant=null;
		pageOfTenant=-1;
		loadTime=-1;
		free=true;
	}
}
