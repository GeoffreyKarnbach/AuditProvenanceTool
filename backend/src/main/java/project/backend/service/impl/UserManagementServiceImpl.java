package project.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.backend.dto.LoginRegisterDto;
import project.backend.entity.UserEntity;
import project.backend.repository.UserRepository;
import project.backend.service.UserManagementService;
import project.backend.service.validator.UserValidator;
import project.backend.security.JwtTokenizer;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;


    @Override
    public String signUp(LoginRegisterDto signUpDto) {

        userValidator.validateSignUpUser(signUpDto);

        var entity = UserEntity.builder()
            .username(signUpDto.getUsername())
            .password(passwordEncoder.encode(signUpDto.getPassword()))
            .build();

        userRepository.save(entity);

        return login(signUpDto);
    }

    @Override
    public String login(LoginRegisterDto loginDto) {
        Optional<UserEntity> user = userRepository.findByUsername(loginDto.getUsername());

        if (user.isPresent() && passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
            return jwtTokenizer.getAuthToken(String.valueOf(user.get().getId()), List.of("ROLE_USER", "ROLE_ADMIN"));
        }

        throw new BadCredentialsException("Bad credentials");
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
