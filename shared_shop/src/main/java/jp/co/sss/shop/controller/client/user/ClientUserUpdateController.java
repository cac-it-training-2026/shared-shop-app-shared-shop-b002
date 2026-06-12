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

@Controller
public class ClientUserUpdateController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 処理1
	 * (変更ボタン 押下時処理) 、(確認画面-戻るボタン 押下時処理)
	 */

	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateUserInputPost(HttpSession session) {

		// 安全のためのログインチェック（セッションからユーザー情報を取得）
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/";
		}

		// ・セッションスコープに入力フォーム情報があるかを確認
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// 、なければ下記の処理を実施
		if (userForm == null) {
			// ・セッションに保存されたIDを使用し、変更対象のデータをDBから取得
			User user = userRepository.findById(loginUser.getId()).orElse(null);

			// ・取得データを元に入力画面初期表示用の入力フォーム情報を新規生成
			userForm = new UserForm();
			if (user != null) {
				BeanUtils.copyProperties(user, userForm);
			}
		}

		// ・入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", userForm);

		// ・変更入力画面表示処理へリダイレクト
		// リダイレクト: "/～/update/input"
		return "redirect:/client/user/update/input";
	}

	/**
	 * 処理2
	 * (変更入力画面表示処理)
	 */

	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateUserInputGet(HttpSession session, Model model) {

		// ・セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// ・入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// ・セッションスコープに入力エラー情報がある場合
		String errorKey = "org.springframework.validation.BindingResult.userForm";
		if (session.getAttribute(errorKey) != null) {

			// - 取得した入力エラー情報をリクエストスコープに設定
			model.addAttribute(errorKey, session.getAttribute(errorKey));

			// - セッションスコープから、入力エラー情報を削除
			session.removeAttribute(errorKey);
		}

		// ・変更入力画面表示
		// フォワード : “～/update_input”
		return "client/user/update_input";
	}

	/**
	 * 処理3
	 * (確認ボタン 押下時処理)
	 */
	// path = "/～/update/check", method = RequestMethod.POST
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateUserCheck(
			@Valid @ModelAttribute UserForm form, BindingResult result, HttpSession session) {

		// ・セッションスコープからフォーム情報を取得
		UserForm sessionForm = (UserForm) session.getAttribute("userForm");

		// ・入力フォーム情報に不足がある場合、セッションスコープから取得した値をセット
		// （画面から送られてきたformにIDや権限がなければ、セッションの元の値で補う）
		if (form.getId() == null && sessionForm != null) {
			form.setId(sessionForm.getId());
		}
		if (form.getAuthority() == null && sessionForm != null) {
			form.setAuthority(sessionForm.getAuthority());
		}

		// ・画面から入力された入力フォームを、セッションスコープに入力フォーム情報として保存
		session.setAttribute("userForm", form);

		// ・BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			// - 入力エラー情報をセッションスコープに設定
			String errorKey = "org.springframework.validation.BindingResult.userForm";
			session.setAttribute(errorKey, result);

			// - 変更入力画面表示処理にリダイレクト（リダイレクト: “～/update/input”）
			return "redirect:/client/user/update/input";
		}

		// ・入力エラーがない場合
		// - 変更確認画面表示処理にリダイレクト（リダイレクト: “/～/update/check”）
		return "redirect:/client/user/update/check";
	}

	/**
	 * 処理4
	 * (変更確認画面表示処理)
	 */
	// path = "/～/update/check", method = RequestMethod.GET
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateUserCheckGet(HttpSession session, Model model) {

		// ・セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// ・入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// ・登録確認画面表示（フォワード : “～/update_check”）
		return "client/user/update_check";
	}

	/**
	 * 処理5
	 * (登録ボタン 押下時処理)
	 */
	// path = "/～/update/complete", method = RequestMethod.POST
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateUserComplete(HttpSession session) {

		// 安全チェックのためにセッションからログインユーザー情報を取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// ・セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		if (loginUser != null && form != null) {
			// ・入力フォーム情報を元にDB登録用エンティティオブジェクトを生成
			// （まずは現在のユーザー情報をDBから取得）
			User user = userRepository.findById(loginUser.getId()).orElse(null);

			if (user != null) {
				// フォームからエンティティへデータを詰め替える
				BeanUtils.copyProperties(form, user);

				// ・DB更新実施
				userRepository.save(user);

				// ・処理内容に応じて、セッションスコープの内容を書き換える
				// - ログインユーザの会員変更の場合、セッションスコープの会員情報を更新
				loginUser.setName(user.getName());
				loginUser.setEmail(user.getEmail());
				session.setAttribute("user", loginUser);

				// ・セッションスコープの入力フォーム情報削除
				session.removeAttribute("userForm");
			}
		}

		// ・変更完了画面表示処理にリダイレクト
		// リダイレクト : "/～/update/complete"
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 処理6
	 * (変更完了画面表示処理)
	 */
	// path = "/～/update/complete", method = RequestMethod.GET
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateUserCompleteView() {

		// ・登録完了画面表示
		// フォワード : “client/user/update_complete”
		return "client/user/update_complete";
	}
}