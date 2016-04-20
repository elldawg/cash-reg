package org.eclipse.che.examples;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

public class CashDrawer {
    
    public enum Denominations {
        HUNDREDS(100),
        FIFTIES(50),
        TWENTIES(20),
        TENS(10),
        FIVES(5),
        ONES(1);
        
        public final int value;
        private Denominations(int value){
            this.value = value;
        }
        
        public String getDescription(){
            
            return String.format("$%1$s", value);
        }
        
        public static Denominations get(String desc){
            
            for(Denominations den : EnumSet.allOf(Denominations.class)){
                
                if(den.getDescription().equals(desc)){
                    return den;
                }
            }
            
            return null;
        }
    }
    
    private final EnumMap<Denominations, Integer> drawer = new EnumMap<Denominations, Integer>(Denominations.class);
    
    public CashDrawer(int billCount){
        restock(billCount);
    }
    
    public Map<Denominations, Integer> getBillCounts(){
        
        return drawer.clone();
    }

    public int getBalance(){
        
        return totalLedger(drawer);
    }
    
    private int totalLedger(Map<Denominations, Integer> totalLedger){
        
        int ret = 0;
        for(Entry<Denominations, Integer> entry : totalLedger.entrySet()){
           ret += entry.getKey().value * entry.getValue(); 
        }
        
        return ret;
    }
    
    public void restock(int billCount){
        
        for(Denominations den : EnumSet.allOf(Denominations.class)){
            drawer.put(den, billCount);
        }
    }
    
    private int withdrawDenomination(int amount, Denominations den, Map<Denominations, Integer> withdrawalLedger){
        
        if(amount < den.value){
            return amount;
        }
        
        int billCount = Integer.min(amount/den.value, drawer.get(den));
        withdrawalLedger.put(den, billCount);
        
        return amount - billCount*den.value;
    }
    
    public boolean withdraw(int amount){
        
        int balance = getBalance();
        if(amount > balance){
            return false;
        }
        
        if(amount == balance){
            
            restock(0);
            return true;
        }
        
        int tally = amount;
        Map<Denominations, Integer> withdrawalLedger = new EnumMap<Denominations, Integer>(Denominations.class);
        
        for(Denominations den : EnumSet.allOf(Denominations.class)){
            tally = withdrawDenomination(tally, den, withdrawalLedger);            
        }
        
        int ledgerTotal = totalLedger(withdrawalLedger);
        if(ledgerTotal == amount){
            removeBills(withdrawalLedger);
            return true;
        }
                
        return false;
    }
    
    private void removeBills(Map<Denominations, Integer> withdrawalLedger){
        
        for(Entry<Denominations, Integer> entry : withdrawalLedger.entrySet()){
            
            Denominations den = entry.getKey();
            int billsInDrawer = drawer.get(den);
            int billsToRemove = entry.getValue();
            drawer.put(den, billsInDrawer - billsToRemove);
        }
    }
}