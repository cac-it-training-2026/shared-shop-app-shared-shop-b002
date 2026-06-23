package jp.co.sss.shop.validator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jp.co.sss.shop.annotation.LoginCheck;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * ログインチェックの独自検証クラス
 *
 * @author System Shared
 */
public class LoginValidator implements ConstraintValidator<LoginCheck, Object> {
	private String email;
	private String password;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Override
	public void initialize(LoginCheck annotation) {
		this.email = annotation.fieldEmail();
		this.password = annotation.fieldPassword();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		boolean isValidFlg = false;
		String emailProp = (String) beanWrapper.getPropertyValue(this.email);
		String passwordProp = (String) beanWrapper.getPropertyValue(this.password);

		User user = userRepository.findByEmailAndDeleteFlag(emailProp, Constant.NOT_DELETED);

		if (user != null && user.getIsLocked() != null && user.getIsLocked() == Constant.LOCKED) {
			// アカウントロック中の場合

			// ロック解除判定
			Timestamp lockedTime = user.getLockedTime();
			if (lockedTime != null) {
				long currentTimeMillis = System.currentTimeMillis();
				long lockedTimeMillis = lockedTime.getTime();
				long durationMillis = (long) Constant.LOCK_DURATION_MINUTES * 60 * 1000;

				if (currentTimeMillis - lockedTimeMillis >= durationMillis) {
					// 30分経過していればロック解除
					user.setIsLocked(Constant.UNLOCKED);
					user.setLoginAttemptCount(0);
					user.setLockedTime(null);
					userRepository.save(user);
				} else {
					// 30分未経過
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate("{msg.login.account.locked}")
							.addConstraintViolation();
					return false;
				}
			} else {
				// 時刻が記録されていない場合は念のためロック継続
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{msg.login.account.locked}")
						.addConstraintViolation();
				return false;
			}
		}

		if (user != null && passwordProp.equals(user.getPassword())) {
			UserBean userBean = new UserBean();

			userBean.setId(user.getId());
			userBean.setName(user.getName());
			userBean.setAuthority(user.getAuthority());

			if (user.getAuthority().intValue() == Constant.AUTH_CLIENT) {
				userBean.setRole("USER");
			} else {
				userBean.setRole("ADMIN");
			}

			userBean.setEmail(user.getEmail());

			// セッションスコープにログインしたユーザの情報を登録
			session.setAttribute("user", userBean);

			// ログイン成功時、失敗回数をリセット
			if (user.getLoginAttemptCount() != null && user.getLoginAttemptCount() > 0) {
				user.setLoginAttemptCount(0);
				userRepository.save(user);
			}

			isValidFlg = true;
		} else {
			//ユーザ認証に失敗
			if (user != null) {
				// ログイン失敗回数を加算
				int attemptCount = (user.getLoginAttemptCount() != null) ? user.getLoginAttemptCount() : 0;
				attemptCount++;
				user.setLoginAttemptCount(attemptCount);

				if (attemptCount >= Constant.MAX_LOGIN_ATTEMPTS) {
					// ロック状態にする
					user.setIsLocked(Constant.LOCKED);
					// ロック時刻を記録
					user.setLockedTime(new Timestamp(System.currentTimeMillis()));
				}
				userRepository.save(user);
			}
			isValidFlg = false;
		}
		return isValidFlg;
	}
}
