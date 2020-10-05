import java.util.Scanner;

public class Httpc {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
			RequestLibrary lib = new RequestLibrary();
			String entry = " ";
			String cmd;
			int stop = 0;
			Scanner scan = new Scanner(System.in);
			System.out.println("What command do you want to use: ");
			entry = scan.nextLine();
			while(!entry.contains("Close")) {
				if(entry.contains("get -v")) {
					cmd = entry.substring(6);
					lib.getWVerbose(cmd);
					entry = scan.nextLine();
				}
				else if(entry.contains("get")) {
					cmd = entry.substring(3);
					lib.get(cmd);
					entry = scan.nextLine();
				}
				else if(entry.contains("post")){
					lib.post(entry);
					entry = scan.nextLine();
				}
				System.out.println("Connection Stopped");
				entry = "Close";
			}
	}
}
