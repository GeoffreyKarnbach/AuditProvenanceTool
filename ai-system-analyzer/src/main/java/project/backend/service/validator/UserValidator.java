package project.backend.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.backend.dto.LoginRegisterDto;
import project.backend.dto.ValidationErrorDto;
import project.backend.dto.ValidationErrorRestDto;
import project.backend.entity.UserEntity;
import project.backend.repository.UserRepository;
import project.backend.exception.ValidationException;
import project.backend.exception.ConflictException;


import java.util.ArrayList;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateSignUpUser(LoginRegisterDto signUpDto) {
        var validationErrors = new ArrayList<ValidationErrorDto>();
        var conflictErrors = new ArrayList<ValidationErrorDto>();

        if (signUpDto.getUsername().isBlank()) {
            addError(validationErrors, "Username cannot be empty");
        }

        if (signUpDto.getPassword().isBlank()) {
            addError(validationErrors, "Password cannot be empty");
        }

        if (signUpDto.getUsername() != null) {
            var username = signUpDto.getUsername();
            if (username.length() > 100) {
                addError(validationErrors, "Username cannot be longer than 100 characters");
            }
        }

        Optional<UserEntity> user = userRepository.findByUsername(signUpDto.getUsername());
        if (user.isPresent()) {
            addError(conflictErrors, "Username already in use");
        }

        if (signUpDto.getPassword().length() < 8) {
            addError(validationErrors, "Password must be at least 4 characters long");
        }

        if (signUpDto.getPassword().length() > 50) {
            addError(validationErrors, "Password cannot be longer than 50 characters");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(
                ValidationErrorRestDto.builder()
                    .message("Validation error while creating user")
                    .errors(validationErrors)
                    .build()
            );
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException(
                ValidationErrorRestDto.builder()
                    .message("Conflict error while creating user")
                    .errors(conflictErrors)
                    .build()
            );
        }
    }

    private void addError(ArrayList<ValidationErrorDto> list, String message) {
        list.add(ValidationErrorDto.builder()
            .message(String.format(message))
            .build());
    }
}
