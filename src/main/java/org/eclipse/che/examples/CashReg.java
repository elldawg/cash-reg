package org.eclipse.che.examples;

import java.io.Console;
import java.lang.NumberFormatException;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.Map;

public class CashReg {
    
    private enum Commands {
        RESTOCK(
            "^R$",
            (String cmdLine)->{
                restock();
            }),
        WITHDRAW(
            "^W\\s\\$[0-9]+$",
            (String cmdLine)->{
                withdraw(cmdLine);
            }),
        INQUIRY( 
            "^I(\\s\\$(100|50|20|10|5|1))+$",
            (String cmdLine)->{
                inquiry(cmdLine);
            }),
        QUIT(
            "^Q$",
            (String cmdLine)->{
            });
        
        public final String patternString;
        public final Consumer<String> action;
        private Commands(String patternString, Consumer<String> action){
            this.patternString = patternString;
            this.action = action;
        }
        
        public boolean matches(String matchString){
            
            return Pattern.matches(this.patternString, matchString);
        }

    }
    
    private static final EnumSet<Commands> CMD_SET = EnumSet.allOf(Commands.class);
    private static final Console CONSOLE = System.console();   
    private static final CashDrawer DRAWER = new CashDrawer(10);
    private static final String FAILURE_FORMAT = "Failure: %1$s\n";
    
    private static void restock(){
        
        DRAWER.restock(10);
        displayBillCounts();
    }
    
    private static void withdraw(String cmdLine){
        
        
        String [] args = cmdLine.split("\\$");
        
        int amount = -1;
        
        try{
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            reportFailure("Invalid command");
            return;
        }
        
        if(!DRAWER.withdraw(amount)){
            reportFailure("Insufficient funds");
        } else {
            println(String.format("Success: Dispensed $%1$d\n", amount));
            println("Machine balance:\n");
            displayBillCounts();
        }
    }
    
    private static void reportFailure(String message){
        println(String.format(FAILURE_FORMAT, message));
    }
    
    private static void inquiry(String cmdLine){
        
        Pattern pat = Pattern.compile("\\s");
        String [] tokens = pat.split(cmdLine);
        
        Map<CashDrawer.Denominations, Integer> balance = DRAWER.getBillCounts();
        
        for(String tokes : tokens){
            CashDrawer.Denominations den = CashDrawer.Denominations.get(tokes);
            if(null == den){
                continue;
            }
            
            println(String.format("%1$s - %2$d\n", den.getDescription(), balance.get(den)));
        }
    }
  
    private static void displayBillCounts(){
        
        Map<CashDrawer.Denominations, Integer> balance = DRAWER.getBillCounts();
        for(Entry<CashDrawer.Denominations, Integer> entry : balance.entrySet()){
            CashDrawer.Denominations den = (CashDrawer.Denominations) entry.getKey();
            println(String.format("%1$s - %2$d\n", den.getDescription(), entry.getValue()));
        }
    }
    
    public static void main(String[] argvs) {
        
        while (true == handleCommand()){
        };
    }
    
    private static boolean handleCommand(){
        
        String cmdStr = CONSOLE.readLine().trim().toUpperCase();
        println("");
        boolean keepRunning = true;
        boolean matched = false;
        
        for(Commands cmd: CMD_SET){
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
            reportFailure("Invalid command");
        }
        
        return keepRunning;
    }
    
    private static void println(String line){
        CONSOLE.printf("%1$s\n", line);
    }
}
