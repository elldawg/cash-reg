package org.eclipse.che.examples;

import java.io.Console;
import java.util.EnumSet;
import java.util.regex.Pattern;

public class CashReg {
    
    private enum Commands {
        RESTOCK(
            "R|r \t\t\t- Restocks the cash machine to the original pre-stock levels",
            "^R$"),
        WITHDRAW(
            "W|w <dollar amount> \t- Withdraws that amount from the cash machine (e.g. \"W $145\") ",
            "^W$"),
        CHECK_DENOMINATIONS( 
            "I|i <denominations> \t- Displays the number of bills in that " + 
            "denomination present in the cash machine (e.g. I $20 $10 $1) ",
            "^I$"),
        QUIT("Q|q \t\t\t- Quits the application",
            "^Q$");
        
        public final String description;
        public final String patternString;
        private Commands(String description, String patternString){
            this.description = description;
            this.patternString = patternString;
        }
        
        public boolean matches(String matchString){
            
            return Pattern.matches(this.patternString, matchString);
        }

    }
    
    private static final EnumSet<Commands> cmdSet = EnumSet.allOf(Commands.class);
    private static final Console con = System.console();
    
    public static void main(String[] argvs) {
        System.out.println("Welcome to Command-Line Cash Machine");
        listCommands();
        
        do{
            println("Please enter a command: ");
        } while (true == handleCommand());
    }
    
    private static boolean handleCommand(){
        
        String cmdStr = con.readLine().trim().toUpperCase();
        boolean keepRunning = true;
        boolean matched = false;
        
        for(Commands cmd: cmdSet){
            if(cmd.matches(cmdStr)){
                matched = true;
                println(cmd.description);
                
                if(cmd.equals(Commands.QUIT)){
                    keepRunning = false;
                }
                
                break;
            }
        }
        
        if(!matched){
            println(String.format("Unrecognized command: %1$s", cmdStr));
        }
        
        return keepRunning;
    }
    
    public static void listCommands(){
        
        for(Commands cmd : cmdSet){
            println(cmd.description);
        }
    }
    
    private static void println(String line){
        con.printf("%1$s\n", line);
    }
}
