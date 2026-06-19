package jp.co.sss.shop.validator;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.EmailCheck;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * メールアドレス重複チェックの独自検証クラス
 *
 * @author System Shared
 */
public class EmailValidator implements ConstraintValidator<EmailCheck, Object> {
	private String email;
	private String id;
	private String reRegist;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Override
	public void initialize(EmailCheck annotation) {
		this.email = annotation.fieldEmail();
		this.id = annotation.fieldId();
		this.reRegist = "reRegist"; // UserFormのフィールド名
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);

		String emailProp = (String) beanWrapper.getPropertyValue(this.email);
		Integer idProp = (Integer) beanWrapper.getPropertyValue(this.id);
		Boolean reRegistProp = (Boolean) beanWrapper.getPropertyValue(this.reRegist);
		User user = userRepository.findByEmail(emailProp);

		if (user == null) {
			if (Boolean.TRUE.equals(reRegistProp)) {
				// 再登録モードなのにユーザーが見つからない
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{userRegist.notFound.reRegist.message}")
						.addConstraintViolation();
				return false;
			}
			return true;
		}

		if (user.getId().equals(idProp)) {
			// プロフィール更新時は、自分自身のメールアドレスならOK
			return true;
		}

		Integer deleteFlag = user.getDeleteFlag();
		if (deleteFlag != null && deleteFlag == Constant.DELETED) {
			if (Boolean.TRUE.equals(reRegistProp)) {
				// 再登録モードで、削除済みユーザーが見つかった -> 許可
				return true;
			} else {
				// 新規登録モードで、削除済みユーザーが見つかった -> 再登録リンクへ誘導
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{userRegist.deleted.regist.message}")
						.addConstraintViolation();
				return false;
			}
		} else {
			// 生存ユーザー（deleteFlag=0 または null）が見つかった
			if (Boolean.TRUE.equals(reRegistProp)) {
				// 再登録モードなのに生存ユーザーがいる -> ログインを促すメッセージなど
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{userRegist.notDeleted.reRegist.message}")
						.addConstraintViolation();
				return false;
			} else {
				// 新規登録モードで生存ユーザーがいる -> 重複エラー
				return false; // デフォルトメッセージ（userRegist.duplicate.message）を使用
			}
		}
	}

}
