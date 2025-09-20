package co.com.crediya.app.api;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.response.LoanApplicationResponse;
import co.com.crediya.app.api.exception.ApiErrorResponse;
import co.com.crediya.app.model.common.PagedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
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

    private static final String API_V1_APPLICATIONS = "/api/v1/applications";

    @Bean
    @RouterOperations({
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
            ),
            @RouterOperation(
                    path = "/api/v1/applications",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getApplicationsForReview",
                            summary = "Get loan applications for review",
                            description = "Retrieve paginated list of loan applications pending advisor review",
                            tags = {"Loan Application Review"},
                            parameters = {
                                    @Parameter(
                                            name = "page",
                                            description = "Page number (0-based)",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            name = "size",
                                            description = "Page size",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "10")
                                    ),
                                    @Parameter(
                                            name = "status",
                                            description = "Filter by application status",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "string", allowableValues = {"pending", "rejected", "manual"})
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Applications retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = PagedResult.class))),
                                    @ApiResponse(responseCode = "403", description = "Access denied - Advisor role required")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(API_V1_APPLICATIONS)
                        .and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)),
                handler::registerLoanApplication)
                .andRoute(GET(API_V1_APPLICATIONS), handler::getApplicationsForReview)
                .andRoute(PUT(API_V1_APPLICATIONS + "/{id}")
                                .and(accept(MediaType.APPLICATION_JSON))
                                .and(contentType(MediaType.APPLICATION_JSON)),
                        handler::updateApplicationStatus);
    }
}