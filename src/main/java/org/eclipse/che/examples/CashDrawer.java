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
        
        public final long value;
        private Denominations(long value){
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
    
    private final EnumMap<Denominations, Integer> ledger = new EnumMap<Denominations, Integer>(Denominations.class);
    
    public CashDrawer(int billCount){
        restock(billCount);
    }
    
    public Map<Denominations, Integer> getBillCounts(){
        
        return ledger.clone();
    }

    public int getBalance(){
        
        int ret = 0;
        for(Entry<Denominations, Integer> entry : ledger.entrySet()){
           ret += entry.getKey().value * entry.getValue(); 
        }
        
        return ret;
    }
    
    public void restock(int billCount){
        
        for(Denominations den : EnumSet.allOf(Denominations.class)){
            ledger.put(den, billCount);
        }
    }
}
