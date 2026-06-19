package jp.co.sss.shop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidatorContext;
import jp.co.sss.shop.annotation.LoginCheck;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.validator.LoginValidator;

public class AccountLockTest {

    @InjectMocks
    private LoginValidator loginValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        LoginCheck annotation = mock(LoginCheck.class);
        when(annotation.fieldEmail()).thenReturn("email");
        when(annotation.fieldPassword()).thenReturn("password");
        loginValidator.initialize(annotation);
    }

    @Test
    public void testLoginFailureIncrementsCount() {
        String email = "test@example.com";
        String password = "wrongpassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword("correctpassword");
        user.setLoginAttemptCount(0);
        user.setIsLocked(Constant.UNLOCKED);

        when(userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED)).thenReturn(user);

        LoginFormStub form = new LoginFormStub(email, password);

        boolean result = loginValidator.isValid(form, context);

        assertFalse(result);
        assertEquals(1, user.getLoginAttemptCount());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testLoginFailureLocksAccountAfterMaxAttempts() {
        String email = "test@example.com";
        String password = "wrongpassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword("correctpassword");
        user.setLoginAttemptCount(Constant.MAX_LOGIN_ATTEMPTS - 1);
        user.setIsLocked(Constant.UNLOCKED);

        when(userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED)).thenReturn(user);

        LoginFormStub form = new LoginFormStub(email, password);

        boolean result = loginValidator.isValid(form, context);

        assertFalse(result);
        assertEquals(Constant.MAX_LOGIN_ATTEMPTS, user.getLoginAttemptCount());
        assertEquals(Constant.LOCKED, user.getIsLocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testLockedAccountCannotLogin() {
        String email = "locked@example.com";
        String password = "correctpassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setIsLocked(Constant.LOCKED);

        when(userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED)).thenReturn(user);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        LoginFormStub form = new LoginFormStub(email, password);

        boolean result = loginValidator.isValid(form, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    public void testSuccessfulLoginResetsCount() {
        String email = "test@example.com";
        String password = "correctpassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setLoginAttemptCount(3);
        user.setIsLocked(Constant.UNLOCKED);

        when(userRepository.findByEmailAndDeleteFlag(email, Constant.NOT_DELETED)).thenReturn(user);

        LoginFormStub form = new LoginFormStub(email, password);

        boolean result = loginValidator.isValid(form, context);

        assertTrue(result);
        assertEquals(0, user.getLoginAttemptCount());
        verify(userRepository, times(1)).save(user);
    }

    // BeanWrapperでプロパティ取得できるようにスタブクラスを作成
    public static class LoginFormStub {
        private String email;
        private String password;
        public LoginFormStub(String email, String password) {
            this.email = email;
            this.password = password;
        }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
}
