package org.example.assetmanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("User")
class UserTest {

    @Nested
    @DisplayName("constructor")
    class ConstructorTest {

        @Test
        void constructor_有効な値を指定した場合_ユーザーを生成できる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            assertEquals("john", user.getUsername());
            assertEquals("encoded-password", user.getPassword());
            assertEquals("john@example.com", user.getEmail());
            assertEquals("John Doe", user.getFullName());
            assertEquals("USER", user.getRole());
            assertTrue(user.getActive());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void constructor_usernameがnullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidUsername) {
            assertThrows(IllegalArgumentException.class,
                    () -> new User(invalidUsername, "encoded-password", "john@example.com", "John Doe", "USER"));
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTest {

        @Test
        void updateProfile_有効な値を指定した場合_プロフィールを更新できる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.updateProfile("john2", "john2@example.com", "John Doe 2", "ADMIN");

            assertEquals("john2", user.getUsername());
            assertEquals("john2@example.com", user.getEmail());
            assertEquals("John Doe 2", user.getFullName());
            assertEquals("ADMIN", user.getRole());
        }
    }

    @Nested
    @DisplayName("password")
    class PasswordTest {

        @Test
        void updatePassword_有効な値を指定した場合_パスワードを更新できる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.updatePassword("new-encoded-password");

            assertEquals("new-encoded-password", user.getPassword());
        }
    }

    @Nested
    @DisplayName("active")
    class ActiveTest {

        @Test
        void deactivate_呼び出した場合_falseになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.deactivate();

            assertFalse(user.getActive());
        }

        @Test
        void activate_呼び出した場合_trueになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate();

            user.activate();

            assertTrue(user.getActive());
        }
    }
}