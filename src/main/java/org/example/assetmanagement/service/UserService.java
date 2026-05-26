package org.example.assetmanagement.service;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.dto.UserRequest;
import org.example.assetmanagement.dto.UserResponse;
import org.example.assetmanagement.entity.User;
import org.example.assetmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    // TODO:本番環境ではPasswordEncoderを使用すること
    // private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toResponse(user);
    }

    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return toResponse(user);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // TODO: 本番環境では必ずパスワードをハッシュ化すること
        // user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(request.getPassword()); // 開発用（暫定）
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setActive(true);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        // ユーザー名変更時の重複チェック
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // メールアドレス変更時の重複チェック
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPassword(request.getPassword()); // 開発用（暫定）
        }
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());

        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        user.setActive(false);
        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    @Transactional
    public UserResponse activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        user.setActive(true);
        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}