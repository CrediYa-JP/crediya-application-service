package co.com.crediya.app.consumer.mapper;

import co.com.crediya.app.consumer.dto.UserResponse;
import co.com.crediya.app.model.user.User;

public class UserMapConsumer {

    public static User mapToUser(UserResponse UserResponse){
        return User.builder()
                .email(UserResponse.getEmail())
                .firstName(UserResponse.getFirstName())
                .lastName(UserResponse.getLastName())
                .baseSalary(UserResponse.getBaseSalary())
                .identityDocument(UserResponse.getIdentityDocument())
                .build();
    }
}
