package Stables;

import java.util.Currency;

public class CurrentCurrency {
    private static String currency;

    public static boolean validateCurrencyCode(String code){
        boolean check=true;
        try {
            Currency curr = Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            check=false;
        }

        return check;
    }

    public static String get(){
        if(currency==null){
            currency="";
        }

        return currency;
    }

    public static void set(String currencyVal){
        currency=currencyVal;
    }
}
