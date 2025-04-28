package project.backend.endpoint;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.backend.dto.LoginRegisterDto;
import project.backend.service.UserManagementService;

@RestController
@RequestMapping(value = "/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserManagementEndpoint {

    private final UserManagementService userManagementService;

    @PermitAll
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@Valid @RequestBody LoginRegisterDto signUpDto) {
        log.info("Received sign up request for user {}", signUpDto.getUsername());
        return userManagementService.signUp(signUpDto);
    }

    @PermitAll
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@Valid @RequestBody LoginRegisterDto loginDto) {
        log.info("Received login request for user {}", loginDto.getUsername());
        return userManagementService.login(loginDto);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser() {
        log.info("DELETE /api/v1/user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        long userId = Long.parseLong(authentication.getPrincipal().toString());

        userManagementService.deleteUser(userId);
    }
}
