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
		int NT=Input.nextInt();
		int NR=Input.nextInt();
		ArrayList<Integer> resources=new ArrayList<Integer>();
		ArrayList<Integer> occupied=new ArrayList<Integer>();
		HashMap<Integer,task> tMap=new HashMap<Integer,task>();
		
		for (int i=0;i<NR;i++) {
			resources.add(Input.nextInt());
			occupied.add(0);
		}
		for(int i=1;i<=NT;i++) {
			task t=new task(i);
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
		
		
		
		Input.close();
	}

}
