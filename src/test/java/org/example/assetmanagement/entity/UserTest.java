package org.example.assetmanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User")
class UserTest {

    @Nested
    @DisplayName("constructor")
    class ConstructorTest {

        @Test
        void constructor_有効な値を指定した場合_ユーザーを生成できる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            assertThat(user.getUsername()).isEqualTo("john");
            assertThat(user.getPassword()).isEqualTo("encoded-password");
            assertThat(user.getEmail()).isEqualTo("john@example.com");
            assertThat(user.getFullName()).isEqualTo("John Doe");
            assertThat(user.getRole()).isEqualTo("USER");
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void constructor_usernameがnullまたは空の場合_IllegalArgumentExceptionをスローする(String invalidUsername) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new User(invalidUsername, "encoded-password", "john@example.com", "John Doe", "USER"));
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTest {

        @Test
        void updateProfile_有効な値を指定した場合_プロフィールを更新できる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.updateProfile("john2", "john2@example.com", "John Doe 2", "ADMIN");

            assertThat(user.getUsername()).isEqualTo("john2");
            assertThat(user.getEmail()).isEqualTo("john2@example.com");
            assertThat(user.getFullName()).isEqualTo("John Doe 2");
            assertThat(user.getRole()).isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("password")
    class PasswordTest {

        @Test
        void updatePassword_有効な値を指定した場合_パスワードを更新できる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.updatePassword("new-encoded-password");

            assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        }
    }

    @Nested
    @DisplayName("status")
    class ActiveTest {

        @Test
        void deactivate_呼び出した場合_INACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.deactivate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        void activate_呼び出した場合_ACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate();

            user.activate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("status")
    class StatusTest {

        @Test
        void deactivate_ACTIVE状態から呼び出した場合_INACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            user.deactivate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        void deactivate_すでにINACTIVE状態から呼び出した場合_IllegalStateExceptionをスローする() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate(); // 1回目で INACTIVE にする

            // 例外メッセージの包含条件（contains）も綺麗にチェーンできます
            assertThatIllegalStateException()
                    .isThrownBy(user::deactivate)
                    .withMessageContaining("Invalid user status transition");
        }

        @Test
        void activate_INACTIVE状態から呼び出した場合_ACTIVEになる() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");
            user.deactivate();

            user.activate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void activate_すでにACTIVE状態から呼び出した場合_IllegalStateExceptionをスローする() {
            User user = new User("john", "encoded-password", "john@example.com", "John Doe", "USER");

            assertThatIllegalStateException()
                    .isThrownBy(user::activate)
                    .withMessageContaining("Invalid user status transition");
        }
    }
}