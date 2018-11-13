
public class activity {
	int category;	//0 for initiate, 1 for request, 2 for release, 3 for compute, and 4 for terminate
	int field1;
	int field2;
	int field3;
	activity next;
	
public activity(int a, int b, int c, int d) {
	category=a;
	field1=b;
	field2=c;
	field3=d;
}
public activity() {} //this default constructor is made specifically for actHeader

}
