
public class process {
	int A;
	int B;
	int C;
	int M;
	
	int index;
	
	int lastBurst;
	int untilBurst;
	int arrivalT;
	int remC;
	int ioFor;
	
	int status;
	int waitingT;
	int ioT;
	int finishT;
	int taT;
	
	double r;
	
	public process(int a, int b, int c, int d, int e) {
		this.initialize();
		A=a;
		B=b;
		C=c;
		M=d;
		index=e;
		arrivalT=A;
		remC=C;
		
	
	}
	
	public void initialize() {
		lastBurst=0;
		untilBurst=0;
		arrivalT=A;
		waitingT=0;
		ioT=0;
		status=0;
		ioFor=0;
		finishT=0;
		taT=0;
		remC=C;
		r=0;
	}
	
	public void giveBurst(int a) {
		lastBurst=a;
		untilBurst=a;
		ioFor=lastBurst*M;
	}
	
	public void updateAndPrint() {
		
		taT=waitingT+ioT+C;
		finishT=taT+A;
		System.out.println("Process: "+index);
		System.out.println("\t (A B C M) ("+A+" "+B+" "+C+" "+M+")");
		System.out.println("\t Finish time: "+finishT);
		System.out.println("\t Turnaround time: "+taT);
		System.out.println("\t I/O time: "+ioT);
		System.out.println("\t Waiting time: "+waitingT);
		
	}
}
