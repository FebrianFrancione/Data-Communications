import java.util.Scanner;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Httpc {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
			RequestLibrary lib = new RequestLibrary();
			Pattern pattern;
		    Matcher matcher;
			String entry = " ";
			String cmd;
			int stop = 0;
			boolean match;
			
			Scanner scan = new Scanner(System.in);
			System.out.println("What command do you want to use: ");
			entry = scan.nextLine();
			while(!entry.contains("Close")) {
				//lib.get("get http://httpbin.org/get?course=networking&assignment=1");
				if(entry.contains("get ")) {
					pattern = Pattern.compile(".*-v.*");
					matcher = pattern.matcher(entry);
					if(matcher.find() == true) {
						lib.getWVerbose(entry);
					}
					else{
						lib.get(entry);
					}
				}
				//for post
				else {
					
				}
				System.out.println("Connection Stopped");
				entry = "Close";
			}
	}
}
