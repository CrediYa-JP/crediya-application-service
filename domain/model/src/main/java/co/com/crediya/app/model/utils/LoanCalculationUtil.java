package co.com.crediya.app.model.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class LoanCalculationUtil {

    private LoanCalculationUtil() {
        // Utility class
    }

    /**
     * Calcula cuota mensual usando fórmula de amortización
     * R = A * i / (1 – 1 / (1 + i)^n)
     *
     * @param amount Monto del préstamo (A)
     * @param annualRate Tasa anual (se convierte a mensual)
     * @param termInMonths Plazo en meses (n)
     * @return Cuota mensual (R)
     */
    public static BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                                     BigDecimal annualRate,
                                                     Integer termInMonths) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || termInMonths <= 0) {
            return BigDecimal.ZERO;
        }

        // Convertir tasa anual a mensual: i = annualRate / 12
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        // Si tasa es 0, simple división
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(termInMonths), 2, RoundingMode.HALF_UP);
        }

        // (1 + i)^n
        BigDecimal onePlusRatePowerN = monthlyRate.add(BigDecimal.ONE)
                .pow(termInMonths);

        // i * (1 + i)^n
        BigDecimal numerator = monthlyRate.multiply(onePlusRatePowerN);

        // (1 + i)^n - 1
        BigDecimal denominator = onePlusRatePowerN.subtract(BigDecimal.ONE);

        // A * [i * (1 + i)^n] / [(1 + i)^n - 1]
        return amount.multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }
}