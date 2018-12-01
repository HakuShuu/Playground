import java.util.Scanner;

public class process {
	int index;
	int ref;
	
	int curW;
	int curP;
	int	remRef;	//remaining references
	boolean finished;
	
	int numEviction;
	int totalResidency;
	int numFault;
	
	double A;
	double B;
	double C;
	
	
	public process(int i,double a, double b, double c) {
		index=i;
		ref=Driver.N;
		remRef=ref;
		finished=false;
		curW=(111*index)%Driver.S;
		curP=curW/Driver.P;
		numEviction=0;
		numFault=0;
		totalResidency=0;
		A=a;
		B=b;
		C=c;
	}
	
	public void nextRef(Scanner s) {
		int option=Driver.ranABCD(s, A, B, C);
		if(option==1) {
			curW=(curW+1)%Driver.S;
		}else if(option==2) {
			curW=(curW-5+Driver.S)%Driver.S;
		}else if(option==3) {
			curW=(curW+4)%Driver.S;
		}else {
			curW=s.nextInt()%Driver.S;
		}
		
		curP=curW/Driver.P;
	}
	public void updateFault() {
		numFault++;
	}
}
