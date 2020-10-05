import java.util.Scanner;

public class Httpc {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	
			//just testing for now
			RequestLibrary lib = new RequestLibrary();
			lib.post("httpc post -h Content-Type:application/json -d '{\"Assignment\": 1}' http://httpbin.org/post");
			String entry = " ";
			Scanner scan = new Scanner(System.in);
			//RequestLibrary lib = new RequestLibrary();
			System.out.println("What command do you want to use: ");
			entry = scan.nextLine();
			while(!entry.contains("Close")) {
				if(entry.contains("get")) {
					lib.get(entry);
					if(entry.contains("-v")) {
						lib.getWVerbose(entry);
					}
				}
				else {
					lib.post(entry);
				}
			}
	}
}
