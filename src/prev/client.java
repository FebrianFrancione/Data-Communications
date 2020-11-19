package prev;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.*;


public class client{

    private static final int DEFAULT_PORT = 8080;
    public static void main(String[]args){
        Options command_option = getParserOptions();
        System.out.println(command_option);
        DefaultParser command_parser = new DefaultParser();
        System.out.println("COMMAND PARSER: " + command_parser);
        CommandLine parsedOptions = null;

        try{
            System.out.println("1");
            parsedOptions = command_parser.parse(command_option,args);
            System.out.println("parsedoptions: " + parsedOptions);
        }
        catch (ParseException e){
            System.out.println("Invalid options parsed exception");
            System.out.println(guide);
            return;
        }
        //post validation
        int port = DEFAULT_PORT;
//checking for option v
        if(parsedOptions.hasOption('v')){
            System.out.println("Port number: " + port);
        }
//checking for option p
        if(parsedOptions.hasOption('p')){
            try{
                port = Integer.parseInt(parsedOptions.getOptionValue('p'));
            }catch(NumberFormatException e){
                System.out.println("Invalid port!: " + port);
                System.out.println(guide);
            }
        }


        if(port > 65535 || port < 0){
            System.out.println("Port out of range. Please select a port in range [0, 65535]");
            return;
        }
        else if(port != 80 && port < 1024){
            System.out.println("Port error. You amy not select this port as it belongs to the well-know ports list");
        }

        //creating new path validation
        String root = System.getProperty("user.dir");
        File directory = new File(root);

        if(parsedOptions.hasOption('v')){
            System.out.println("Root dir: " + root);
        }

        if (parsedOptions.hasOption('d')){
            root =parsedOptions.getOptionValue('d');
            directory= new File(root);
            if(!directory.isDirectory()){
                System.out.println("Sorry this path is not a directory.");
                return;
            }
        }

        if(!directory.canWrite()){
            System.out.println("Directory cannot be written to: " + root);
            return;
        }else if(!directory.canRead()){
            System.out.println("Directory cannot be read: " + root);
            return;
        }



        boolean verbose = parsedOptions.hasOption('v');
        server fileServer = new server(port, new httpfs(directory.getPath()), verbose);
        try{
            fileServer.run_server();
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
