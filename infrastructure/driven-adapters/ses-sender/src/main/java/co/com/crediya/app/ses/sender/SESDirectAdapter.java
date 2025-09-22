package co.com.crediya.app.ses.sender;

import co.com.crediya.app.model.capacityevaluation.PaymentDetail;
import co.com.crediya.app.model.notifications.gateways.DirectEmailGateway;
import co.com.crediya.app.ses.sender.config.SESProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.model.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Component
@Slf4j
public class SESDirectAdapter implements DirectEmailGateway {

    private final SesAsyncClient sesClient;
    private final SESProperties properties;
    private final ObjectMapper objectMapper;

    public SESDirectAdapter(@Qualifier("directEmailSesClient") SesAsyncClient sesClient,
                            SESProperties properties,
                            ObjectMapper objectMapper) {
        this.sesClient = sesClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> sendLoanDecisionWithPaymentPlan(String email, String userName,
                                                      String decision, BigDecimal amount, Integer term, List<PaymentDetail> paymentPlan) {

        return Mono.fromCallable(() -> buildTemplateData(userName, decision, amount, term, paymentPlan))
                .flatMap(templateData -> sendTemplatedEmail(email, templateData))
                .doOnSuccess(messageId -> log.info("SES_EMAIL_SENT email={}, messageId={}", email, messageId))
                .doOnError(error -> log.error("SES_EMAIL_FAILED email={}, error={}", email, error.getMessage()))
                .then();
    }

    private Mono<String> sendTemplatedEmail(String email, String templateData) {
        return Mono.fromFuture(() ->
                sesClient.sendEmail(SendEmailRequest.builder()
                        .source(properties.sourceEmail())
                        .destination(Destination.builder().toAddresses(email).build())
                        .message(Message.builder()
                                .subject(Content.builder()
                                        .data("Decision sobre su solicitud de crédito")
                                        .charset("UTF-8")
                                        .build())
                                .body(Body.builder()
                                        .html(Content.builder()
                                                .data(buildHtmlBody(templateData))
                                                .charset("UTF-8")
                                                .build())
                                        .build())
                                .build())
                        .build())
        ).map(SendEmailResponse::messageId);
    }

    // ✅ Método para formatear números
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0.00";
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(amount);
    }

    private String buildHtmlBody(String templateDataJson) {
        try {
            Map<String, Object> data = objectMapper.readValue(templateDataJson, Map.class);

            String userName = (String) data.get("userName");
            String decision = (String) data.get("decision");
            String amount = (String) data.get("amount");
            String term = (String) data.get("term");
            List<Map<String, Object>> paymentPlan = (List<Map<String, Object>>) data.get("paymentPlan");

            StringBuilder html = new StringBuilder();

            html.append("<!DOCTYPE html>");
            html.append("<html><head>");
            html.append("<meta charset=\"UTF-8\">");
            html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            html.append("</head>");
            html.append("<body style='font-family: Arial, sans-serif;'>");

            html.append("<h2 style='color: #2E86AB;'>CrediYa - Decision de Solicitud</h2>");
            html.append("<p>Estimado/a ").append(userName).append(",</p>");

            if ("APPROVED".equals(decision)) {
                html.append("<p><strong style='color: green;'¡Felicitaciones! Su solicitud ha sido APROBADA.</strong></p>");
                html.append("<p>Monto aprobado: <strong>$").append(formatCurrency(new BigDecimal(amount))).append("</strong></p>");
                html.append("<p>Plazo: <strong>").append(term).append(" meses</strong></p>");

                if (paymentPlan != null && !paymentPlan.isEmpty()) {
                    html.append("<h3>Plan de Pagos:</h3>");
                    html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
                    html.append("<tr style='background-color: #f2f2f2;'>");
                    html.append("<th style='padding: 8px;'>Mes</th>");
                    html.append("<th style='padding: 8px;'>Pago Total</th>");
                    html.append("<th style='padding: 8px;'>Capital</th>");
                    html.append("<th style='padding: 8px;'>Interes</th>");
                    html.append("<th style='padding: 8px;'>Saldo</th>");
                    html.append("</tr>");

                    for (Map<String, Object> payment : paymentPlan) {
                        html.append("<tr>");
                        html.append("<td style='padding: 8px; text-align: center;'>").append(payment.get("month")).append("</td>");
                        html.append("<td style='padding: 8px; text-align: right;'>$")
                                .append(formatCurrency(new BigDecimal(payment.get("totalPayment").toString()))).append("</td>");
                        html.append("<td style='padding: 8px; text-align: right;'>$")
                                .append(formatCurrency(new BigDecimal(payment.get("principalPayment").toString()))).append("</td>");
                        html.append("<td style='padding: 8px; text-align: right;'>$")
                                .append(formatCurrency(new BigDecimal(payment.get("interestPayment").toString()))).append("</td>");
                        html.append("<td style='padding: 8px; text-align: right;'>$")
                                .append(formatCurrency(new BigDecimal(payment.get("remainingBalance").toString()))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</table>");
                }
            } else {
                html.append("<p><strong style='color: red;'>Lo sentimos, su solicitud ha sido RECHAZADA.</strong></p>");
                html.append("<p>Para más informacion, contáctenos.</p>");
            }

            html.append("<br><p>Atentamente,<br><strong>Equipo CrediYa</strong></p>");
            html.append("</body></html>");

            return html.toString();

        } catch (Exception e) {
            log.error("Error building HTML body", e);
            return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body><h2>Error en el template de email</h2></body></html>";
        }
    }

    private String buildTemplateData(String userName, String decision,
                                     BigDecimal amount, Integer term, List<PaymentDetail> paymentPlan) {
        try {
            Map<String, Object> data = new HashMap<>();

            data.put("userName", Objects.toString(userName, ""));
            data.put("decision", Objects.toString(decision, ""));
            data.put("amount", amount != null ? amount.toString() : "0");
            data.put("term", term != null ? term.toString() : "0");

            List<Map<String, Object>> simplifiedPaymentPlan = simplifyPaymentPlan(paymentPlan);
            data.put("paymentPlan", simplifiedPaymentPlan);

            return objectMapper.writeValueAsString(data);

        } catch (Exception e) {
            log.error("Error serializing template data", e);
            throw new RuntimeException("Failed to serialize email template data: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> simplifyPaymentPlan(List<PaymentDetail> paymentPlan) {
        return Optional.ofNullable(paymentPlan)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::paymentDetailToMap)
                .toList();
    }

    private Map<String, Object> paymentDetailToMap(PaymentDetail detail) {
        Map<String, Object> map = new HashMap<>();
        map.put("month", detail.getMonth() != null ? detail.getMonth() : 0);
        map.put("totalPayment", detail.getTotalPayment() != null ? detail.getTotalPayment().toString() : "0");
        map.put("principalPayment", detail.getPrincipalPayment() != null ? detail.getPrincipalPayment().toString() : "0");
        map.put("interestPayment", detail.getInterestPayment() != null ? detail.getInterestPayment().toString() : "0");
        map.put("remainingBalance", detail.getRemainingBalance() != null ? detail.getRemainingBalance().toString() : "0");
        return map;
    }
}
