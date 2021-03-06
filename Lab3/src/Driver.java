import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException {
		String FileName=args[0];
		FileReader f=new FileReader(FileName);
		BufferedReader bf=new BufferedReader(f);
		Scanner Input=new Scanner(bf);
		int NT=Input.nextInt();//number of tasks
		int NR=Input.nextInt();//number of resources
		ArrayList<Integer> resources=new ArrayList<Integer>();
		HashMap<Integer,task> tMap=new HashMap<Integer,task>();
		
		resources.add(-42);//adjust my arrayList so everything is 1-based, which aligns well with the input
		for (int i=0;i<NR;i++) {
			resources.add(Input.nextInt());
		}

		for(int i=1;i<=NT;i++) {
			task t=new task(i,NR);
			tMap.put(i, t);
		}
		
		
		String operation=null;
		int i1,i2,i3;
		while(Input.hasNext()) {
			operation=Input.next();
			i1=Input.nextInt();
			i2=Input.nextInt();
			i3=Input.nextInt();
			task thisT=tMap.get(i1);
			
			if(operation.equals("initiate")) {
				activity nAct=new activity(0,i1,i2,i3);
				thisT.addAct(nAct);
			}else if(operation.equals("request")){
				activity nAct=new activity(1,i1,i2,i3);
				thisT.addAct(nAct);
			}else if(operation.equals("release")) {
				activity nAct=new activity(2,i1,i2,i3);
				thisT.addAct(nAct);
			}else if(operation.equals("compute")) {
				activity nAct=new activity(3,i1,i2,i3);
				thisT.addAct(nAct);
			}else if(operation.equals("terminate")) {
				activity nAct=new activity(4,i1,i2,i3);
				thisT.addAct(nAct);
			}
			
			
		}
		for(int i=1;i<=NT;i++) {
			task T=tMap.get(i);
			T.reset();
		}
		Optimistic.run(resources, tMap, false);
		System.out.println();
		Banker.run(resources, tMap, false);
		
		Input.close();
	}

}
