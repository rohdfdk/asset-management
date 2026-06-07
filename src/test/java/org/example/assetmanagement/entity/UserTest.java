package org.example.assetmanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

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
            assertEquals(UserStatus.ACTIVE, user.getStatus());
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
    @DisplayName("status")
    class ActiveTest {

        @Test
        void deactivate_呼び出した場合_INACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.deactivate();

            assertEquals(UserStatus.INACTIVE, user.getStatus());
        }

        @Test
        void activate_呼び出した場合_ACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate();

            user.activate();

            assertEquals(UserStatus.ACTIVE, user.getStatus());
        }
    }

    @Nested
    @DisplayName("status")
    class StatusTest {

        @Test
        void deactivate_ACTIVE状態から呼び出した場合_INACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.deactivate();

            assertEquals(UserStatus.INACTIVE, user.getStatus());
        }

        @Test
        void deactivate_すでにINACTIVE状態から呼び出した場合_IllegalStateExceptionをスローする() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate(); // 1回目で INACTIVE にする

            // 2回目は遷移不可なので例外が発生することを検証
            IllegalStateException exception = assertThrows(IllegalStateException.class, user::deactivate);

            assertTrue(exception.getMessage().contains("Invalid user status transition"));
        }

        @Test
        void activate_INACTIVE状態から呼び出した場合_ACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate();

            user.activate();

            assertEquals(UserStatus.ACTIVE, user.getStatus());
        }

        @Test
        void activate_すでにACTIVE状態から呼び出した場合_IllegalStateExceptionをスローする() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            // 初期状態がすでに対象の状態 (ACTIVE)

            // 遷移不可なので例外が発生することを検証
            IllegalStateException exception = assertThrows(IllegalStateException.class, user::activate);

            assertTrue(exception.getMessage().contains("Invalid user status transition"));
        }
    }
}