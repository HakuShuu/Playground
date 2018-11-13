import java.util.ArrayList;
import java.util.HashMap;

public class Optimist {
	public static void run(ArrayList<Integer> resource, ArrayList<Integer> occupied, HashMap<Integer,task> tMap, boolean verbose) {
		int time=0;
		int terminatedT=0;
		int NT=tMap.size();
		int[] feedback=new int[3];
		ArrayList<task> blockedT=new ArrayList<task>();
		while(terminatedT!=NT) {

			for(int i=1;i<=NT;i++) {
				task T=tMap.get(i);
				
				feedback=T.proceed();
			}
			
			if(verbose) {
				
			}
			time++;
		}
	}
}
