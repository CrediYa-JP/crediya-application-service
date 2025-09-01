package co.com.crediya.app.model.constants;


import lombok.Getter;

@Getter
public enum ExternalService {
    AUTHENTICATION("Authentication Service");


    private final String displayName;

    ExternalService(String displayName) {
        this.displayName = displayName;
    }

}