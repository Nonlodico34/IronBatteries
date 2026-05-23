package it.nonlodico34.ironbatteries.bigleagues;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BigIntHelper {
    private static final String[] NUMBER_CHARS = new String[] {"K", "M", "G", "T", "P", "E"};

    public static int bigIntegerToInt(BigInteger num) {
        try {
            return num.intValueExact();
        } catch (ArithmeticException ignored) {
            return num.signum() > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
    }

    public static String formatBigInteger(BigInteger value, int decimals, boolean keepTrailingZeros) {
        if (value.compareTo(BigInteger.valueOf(1000)) <= 0) {
            DecimalFormat simpleFormatter = new DecimalFormat("###,###,###,###,###");
            return simpleFormatter.format(value);
        }

        BigDecimal num = new BigDecimal(value);
        BigDecimal thousand = BigDecimal.valueOf(1000);
        int cnt = 0;

        while (num.compareTo(thousand) >= 0 && cnt < NUMBER_CHARS.length) {
            num = num.divide(thousand, decimals + 3, RoundingMode.HALF_UP);
            cnt++;
        }

        num = num.setScale(decimals, RoundingMode.HALF_UP);

        if (!keepTrailingZeros) {
            num = num.stripTrailingZeros();
        }

        DecimalFormat formatter = new DecimalFormat("###,###,###,###,##0");
        formatter.setMaximumFractionDigits(decimals);
        formatter.setMinimumFractionDigits(keepTrailingZeros ? decimals : 0);

        return formatter.format(num) + NUMBER_CHARS[cnt - 1];
    }

    public static String formatInt(int value, int decimals, boolean keepTrailingZeros) {
        return formatBigInteger(BigInteger.valueOf(value), decimals, keepTrailingZeros);
    }
}