package project.backend.service;

import project.backend.dto.LoginRegisterDto;

public interface UserManagementService {

    /**
     * Sign up a new user.
     *
     * @param signUpDto the user to sign up
     * @return the JWT, if successful
     */
    String signUp(LoginRegisterDto signUpDto);

    /**
     * Log in a user.
     *
     * @param loginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(LoginRegisterDto loginDto);

    /**
     * Delete a user by ID.
     * @param userId the ID of the user to delete
     */
    void deleteUser(long userId);
}
