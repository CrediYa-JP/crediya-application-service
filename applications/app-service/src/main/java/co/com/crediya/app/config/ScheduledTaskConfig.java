package co.com.crediya.app.config;

import co.com.crediya.app.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskConfig {

    private final LoanApplicationUseCase loanApplicationUseCase;

    @Scheduled(fixedDelay = 5000)
    public void processCapacityResponses() {
        loanApplicationUseCase.processCapacityResponses()
                .doOnError(error -> log.error("ERROR in scheduled capacity response processing: {}", error.getMessage()))
                .subscribe();
    }
}