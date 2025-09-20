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
    /**
     * Calcula cuota mensual usando fórmula de amortización francesa
     * Para tasas mensuales (no anuales)
     * Fórmula: Cuota = P * [i * (1+i)^n] / [(1+i)^n - 1]
     *
     * @param amount Monto del préstamo
     * @param monthlyRate Tasa de interés mensual (ej: 0.025 = 2.5% mensual)
     * @param termInMonths Plazo en meses
     * @return Cuota mensual fija
     */
    public static BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                                     BigDecimal monthlyRate,
                                                     Integer termInMonths) {
        // Validar inputs - montos negativos o plazo inválido retornan cero
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || termInMonths <= 0) {
            return BigDecimal.ZERO;
        }

        // Caso especial: tasa cero significa sin intereses, solo dividir el monto
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(termInMonths), 2, RoundingMode.HALF_UP);
        }

        // Calcular (1 + tasa_mensual)^numero_meses - base para la amortización
        BigDecimal onePlusRatePowerN = monthlyRate.add(BigDecimal.ONE).pow(termInMonths);

        // Numerador: tasa_mensual * (1 + tasa_mensual)^numero_meses
        BigDecimal numerator = monthlyRate.multiply(onePlusRatePowerN);

        // Denominador: (1 + tasa_mensual)^numero_meses - 1
        BigDecimal denominator = onePlusRatePowerN.subtract(BigDecimal.ONE);

        // Cuota final: monto * numerador / denominador
        return amount.multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }
}