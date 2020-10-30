import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.*;


public class client{

    private static final int DEFAULT_PORT = 80;
    private static final int MAX_PORT = 65535;
    private static final String PORT_ERROR = "Port out of range. Please select a port in range [0, 65535]";
    private static final String DIR_ERROR = "The path does not correspond to a directory.";


    public static void main(String[]args){
        Options options = getParserOptions();
        System.out.println(options);
        DefaultParser cmdParser = new DefaultParser();
        System.out.println("cmdpar: " + cmdParser);
        CommandLine parsedOptions = null;

        try{
            System.out.println("1");
            parsedOptions = cmdParser.parse(options,args);
            System.out.println("parsedoptions: " + parsedOptions);
            //checking for irregularoptions
            var leftover = parsedOptions.getArgList();
            System.out.println("parsed options: " + leftover);
            if(!leftover.isEmpty()){
                System.out.println("\nInvalid options: leftover:  " + leftover);
                System.out.println(guide);
                return;
            }
        }
        catch (ParseException e){
            System.out.println("\nInvalid options parsed exception");
            System.out.println(guide);
            return;
        }

//validating port
        int port = DEFAULT_PORT;
        if(parsedOptions.hasOption('p')){
            try{
                port = Integer.parseInt(parsedOptions.getOptionValue('p'));
            }catch(NumberFormatException e){
                System.out.println("Invalid port!");

                System.out.println(guide);
            }
        }

        if(parsedOptions.hasOption('v')){
            System.out.println("Port: " + port);
        }

        if(port > 65535 || port < 0){
            System.out.println("Port out of range. Please select a port in range [0, 65535]");
            return;
        }
        else if(port != 80 && port < 1024){
            System.out.println("Port error. port is a well-known port");
        }

        //validate path
        String rootDir =System.getProperty("user.dir");
        File dir =new File(rootDir);
        if (parsedOptions.hasOption('d')){
            rootDir =parsedOptions.getOptionValue('d');
            dir= new File(rootDir);
            if(!dir.isDirectory()){
                System.out.println("The path does not correspond to a directory.");
                return;
            }
        }

        if(!dir.canWrite()){
            System.out.println("Cannot write to dir: " + rootDir);
            return;
        }else if(!dir.canRead()){
            System.out.println("Cannot read dir: " + rootDir);
            return;
        }

        if(parsedOptions.hasOption('v')){
            System.out.println("Root dir: " + rootDir);
        }

        boolean verbose = parsedOptions.hasOption('v');
        server fileServer = new server(port, new httpfs(dir.getPath()), verbose);
        try{
            fileServer.run();
        }catch(IOException e){
            System.out.println("Issue creating server socket" + e.getMessage());
        }
    }

    static private Options getParserOptions(){

        Option debug = Option.builder("v").hasArg(false).required(false).build();

        Option directory = Option.builder("d").hasArg().required(false).build();

        Option path= Option.builder("p").hasArg().required(false).build();

        return new Options().addOption(debug).addOption(directory).addOption(path);
    }


    static final String guide = "usage: httpfs [-v] [-p Port] [-d PATH-TO-DIR]\\n\" +\n" +
            "                    \"\\n\" +\n" +
            "                    \"-v   Prints debugging messages\\n\" +\n" +
            "                    \"-p   Specifies the port number that the server will listen and serve at.\\n\" +\n" +
            "                    \"     Default is 8080.\\n\" +\n" +
            "                    \"-d   Specifies the directory that the server will use to read/write requested files.\\n\" +\n" +
            "                    \"     Default is the current directory when launching the application.\\n";
}

//import java.util.Scanner;
//
//public class Httpc {
//    public static void main(String[] args) throws Exception {
//        // TODO Auto-generated method stub
//        RequestLibrary lib = new RequestLibrary();
//        String entry = " ";
//        String cmd;
//        int stop = 0;
//        Scanner scan = new Scanner(System.in);
//        System.out.println("What command do you want to use: ");
//        entry = scan.nextLine();
//        while(!entry.contains("Close")) {
//            if(entry.contains("get -v")) {
//                cmd = entry.substring(6);
//                lib.getWVerbose(cmd);
//                entry = scan.nextLine();
//            }
//            else if(entry.contains("get")) {
//                cmd = entry.substring(3);
//                lib.get(cmd);
//                entry = scan.nextLine();
//            }
//            else if(entry.contains("post")){
//                lib.post(entry);
//                entry = scan.nextLine();
//            }
//            System.out.println("Connection Stopped");
//            entry = "Close";
//        }
//    }
//}
