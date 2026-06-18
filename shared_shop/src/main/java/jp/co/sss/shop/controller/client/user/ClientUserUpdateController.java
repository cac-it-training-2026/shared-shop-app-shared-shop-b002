package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員情報更新用コントローラー
 */
@Controller
public class ClientUserUpdateController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 変更ボタン押下時、および確認画面からの戻り時の処理
	 *
	 * @param session HTTPセッション
	 * @return 入力画面リダイレクト
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateUserInputPost(HttpSession session) {

		// ログインチェック（セッションからユーザー情報を取得）
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/";
		}

		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// 入力フォーム情報がない場合、DBからデータを取得して初期化
		if (userForm == null) {
			User user = userRepository.findById(loginUser.getId()).orElse(null);

			userForm = new UserForm();
			if (user != null) {
				BeanUtils.copyProperties(user, userForm);
			}
		}

		// 入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", userForm);

		// 入力画面表示処理へリダイレクト
		return "redirect:/client/user/update/input";
	}

	/**
	 * 変更入力画面の表示処理
	 *
	 * @param session HTTPセッション
	 * @param model モデル
	 * @return 入力画面表示
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateUserInputGet(HttpSession session, Model model) {

		// セッションから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// 入力エラー情報がある場合はリクエストスコープに設定してセッションから削除
		String errorKey = "org.springframework.validation.BindingResult.userForm";
		if (session.getAttribute(errorKey) != null) {
			model.addAttribute(errorKey, session.getAttribute(errorKey));
			session.removeAttribute(errorKey);
		}

		// 変更入力画面を表示
		return "client/user/update_input";
	}

	/**
	 * 確認ボタン押下時の処理
	 *
	 * @param form 入力フォーム
	 * @param result バリデーション結果
	 * @param session HTTPセッション
	 * @return 確認画面リダイレクト
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateUserCheck(
			@Valid @ModelAttribute UserForm form, BindingResult result, HttpSession session) {

		// セッションからフォーム情報を取得
		UserForm sessionForm = (UserForm) session.getAttribute("userForm");

		// フォームに欠損しているIDや権限情報を補完
		if (form.getId() == null && sessionForm != null) {
			form.setId(sessionForm.getId());
		}
		if (form.getAuthority() == null && sessionForm != null) {
			form.setAuthority(sessionForm.getAuthority());
		}

		// 入力フォームをセッションに保存
		session.setAttribute("userForm", form);

		// 入力エラーがある場合は入力画面へリダイレクト
		if (result.hasErrors()) {
			String errorKey = "org.springframework.validation.BindingResult.userForm";
			session.setAttribute(errorKey, result);
			return "redirect:/client/user/update/input";
		}

		// 入力エラーがない場合は確認画面へリダイレクト
		return "redirect:/client/user/update/check";
	}

	/**
	 * 変更確認画面の表示処理
	 *
	 * @param session HTTPセッション
	 * @param model モデル
	 * @return 確認画面表示
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateUserCheckGet(HttpSession session, Model model) {

		// セッションから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// 確認画面を表示
		return "client/user/update_check";
	}

	/**
	 * 登録ボタン押下時の処理
	 *
	 * @param session HTTPセッション
	 * @return 完了画面リダイレクト
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateUserComplete(HttpSession session) {

		// セッションからログインユーザー情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// セッションからフォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		// ログイン済みかつフォーム情報がある場合に更新を実行
		if (loginUser != null && form != null) {
			User user = userRepository.findById(loginUser.getId()).orElse(null);

			if (user != null) {
				// エンティティへ値をコピー
				BeanUtils.copyProperties(form, user);

				// DBを更新
				userRepository.save(user);

				// セッションのログイン情報を最新化
				loginUser.setName(user.getName());
				loginUser.setEmail(user.getEmail());
				session.setAttribute("user", loginUser);

				// 不要になったフォーム情報を削除
				session.removeAttribute("userForm");
			}
		}

		// 完了画面へリダイレクト
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 変更完了画面の表示処理
	 *
	 * @return 完了画面表示
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateUserCompleteView() {

		// 完了画面を表示
		return "client/user/update_complete";
	}
}