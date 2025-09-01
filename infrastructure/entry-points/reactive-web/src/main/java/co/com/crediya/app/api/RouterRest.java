package co.com.crediya.app.api;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.response.LoanApplicationResponse;
import co.com.crediya.app.api.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String API_V1_SOLICITUDES = "/api/v1/applications";

    @Bean
    @RouterOperation(
            path = "/api/v1/applications",
            method = RequestMethod.POST,
            operation = @Operation(
                    operationId = "registerLoanApplication",
                    summary = "Register new loan application",
                    description = "Creates a new loan application in the system with pending review status",
                    tags = {"Loan Applications"},
                    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = RegisterLoanApplicationRequest.class))
                    ),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Loan application created successfully",
                                    content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))),
                            @ApiResponse(responseCode = "400", description = "Validation error",
                                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                            @ApiResponse(responseCode = "404", description = "Invalid loan type",
                                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                            @ApiResponse(responseCode = "404", description = "User not found",
                                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
                            @ApiResponse(responseCode = "500", description = "External service unavailable",
                                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
                    }
            )
    )
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(API_V1_SOLICITUDES)
                        .and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)),
                handler::registerLoanApplication);
    }
}