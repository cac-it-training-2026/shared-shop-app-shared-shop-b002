package jp.co.sss.shop.validator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

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
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{msg.login.account.locked}")
					.addConstraintViolation();
			return false;
		}

		if (user != null && passwordProp.equals(user.getPassword())) {
			UserBean userBean = new UserBean();

			userBean.setId(user.getId());
			userBean.setName(user.getName());
			userBean.setAuthority(user.getAuthority());
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
				}
				userRepository.save(user);
			}
			isValidFlg = false;
		}
		return isValidFlg;
	}
}
