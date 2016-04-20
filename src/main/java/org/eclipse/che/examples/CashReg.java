package org.eclipse.che.examples;

import java.io.Console;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.Map;

public class CashReg {
    
    private enum Commands {
        RESTOCK(
            "R|r \t\t\t- Restocks the cash machine to the original pre-stock levels",
            "^R$",
            (String cmdLine)->{
                restock();
            }),
        WITHDRAW(
            "W|w <dollar amount> \t- Withdraws that amount from the cash machine (e.g. \"W $145\") ",
            "^W$",
            (String cmdLine)->{
                withdraw(cmdLine);
            }),
        INQUIRY( 
            "I|i <denominations> \t- Displays the number of bills in that " + 
            "denomination present in the cash machine (e.g. I $20 $10 $1) ",
            "^I$",
            (String cmdLine)->{
                inquiry(cmdLine);
            }),
        QUIT("Q|q \t\t\t- Quits the application",
            "^Q$",
            (String cmdLine)->{
            });
        
        public final String description;
        public final String patternString;
        public final Consumer<String> action;
        private Commands(String description, String patternString, Consumer<String> action){
            this.description = description;
            this.patternString = patternString;
            this.action = action;
        }
        
        public boolean matches(String matchString){
            
            return Pattern.matches(this.patternString, matchString);
        }

    }
    
    private static final EnumSet<Commands> cmdSet = EnumSet.allOf(Commands.class);
    private static final Console con = System.console();   
    private static final CashDrawer DRAWER = new CashDrawer(10);
    
    private static void restock(){
        
        DRAWER.restock(10);
        displayBalance();
    }
    
    private static void withdraw(String cmdLine){
        
        println("Doing withdraw");
    }
    
    private static void inquiry(String cmdLine){
        println("Doing inquiry");
    }
    
    private static void displayBalance(){
        
        Map<CashDrawer.Denominations, Integer> balance = DRAWER.getBalance();
        for(Entry<CashDrawer.Denominations, Integer> entry : balance.entrySet()){
            CashDrawer.Denominations den = (CashDrawer.Denominations) entry.getKey();
            println(String.format("%1$s - %2$d", den.getDescription(), entry.getValue()));
        }
    }
    
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
                cmd.action.accept(cmdStr);
                
                if(cmd.equals(Commands.QUIT)){
                    keepRunning = false;
                }
                
                break;
            }
        }
        
        if(!matched){
            println(String.format("Invalid command: %1$s", cmdStr));
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
