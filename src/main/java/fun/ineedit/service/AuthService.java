package fun.ineedit.service;

import fun.ineedit.dto.Dto;
import fun.ineedit.entity.Category;
import fun.ineedit.entity.User;
import fun.ineedit.repository.CategoryRepository;
import fun.ineedit.repository.UserRepository;
import fun.ineedit.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Transactional
    public Dto.AuthResponse register(Dto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .name(req.name())
                .email(req.email().toLowerCase())
                .password(passwordEncoder.encode(req.password()))
                .build();
        user = userRepository.save(user);

        // Seed single default category
        categoryRepository.save(Category.builder().name("Needs").user(user).build());

        String token = jwtService.generateToken(user);
        return new Dto.AuthResponse(token, toUserInfo(user));
    }

    public Dto.AuthResponse login(Dto.LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password()));

        User user = userRepository.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return new Dto.AuthResponse(token, toUserInfo(user));
    }

    private Dto.UserInfo toUserInfo(User user) {
        return new Dto.UserInfo(user.getId(), user.getName(), user.getEmail());
    }
}
