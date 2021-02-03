package Stables;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CurrencyConvert {
    public static String Get(double val){
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}
