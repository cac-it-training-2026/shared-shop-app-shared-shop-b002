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
import jp.co.sss.shop.util.Constant;

/**
 * 会員登録機能を制御するコントローラー
 * @author kawachif
 * @see jp.co.sss.shop.form.UserForm
 * @see jp.co.sss.shop.repository.UserRepository
 */
@Controller
public class ClientUserRegistController {

	/**
	 * ユーザーフォーム情報を登録するセッションスコープ
	 */
	@Autowired
	HttpSession session;

	/**
	 * ユーザー情報に関するリポジトリ
	 */
	@Autowired
	UserRepository repository;

	/**
	 * 処理1：新規登録リンク クリック時処理
	 * @return 登録入力画面のHTMLパス("redirect:/client/user/regist/input")
	 */
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String inputInit() {
		// 入力フォーム情報を新規生成
		UserForm form = new UserForm();
		form.setReRegist(false);
		// 入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", form);
		// 登録入力画面表示処理にリダイレクト
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 会員再登録用初期化処理
	 * @return 登録入力画面のHTMLパス("redirect:/client/user/regist/input")
	 */
	@RequestMapping(path = "/client/user/reRegist/input/init", method = RequestMethod.GET)
	public String reRegistInputInit() {
		// 入力フォーム情報を新規生成
		UserForm form = new UserForm();
		form.setReRegist(true);
		// 入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", form);
		// 登録入力画面表示処理にリダイレクト
		return "redirect:/client/user/regist/input";
	}

	/**
	 *  処理2：新規登録ボタン 押下時処理、確認画面-戻るボタン 押下時処理
	 * @param form ユーザーフォーム
	 * @return 登録入力画面のHTMLパス("redirect:/client/user/regist/input")
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String inputPost(@ModelAttribute UserForm form) {
		// セッションスコープに入力フォーム情報があるかを確認
		UserForm sessionForm = (UserForm) session.getAttribute("userForm");

		// nullチェック
		if (sessionForm == null) {
			// なければ入力フォーム情報を新規生成してセッションに保存
			sessionForm = new UserForm();
			session.setAttribute("userForm", sessionForm);
		}
		// 登録入力画面表示処理にリダイレクト
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 処理3：登録画面表示処理
	 * @param model 画面へデータを渡すためのModelオブジェクト
	 * @return 登録入力画面のHTMLパス("client/user/regist_input")
	 * @throws セッションから取得した情報にエラーがあった場合
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String inputGet(Model model) {
		// セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", form);

		// セッションにエラー情報がある場合の処理（処理4でエラーがあった時用）
		BindingResult result = (BindingResult) session.getAttribute("errors");
		if (result != null) {
			// リダイレクトを挟むと画面に渡すデータ（リクエストスコープ）は一回全部消えてしまうため
			// Spring Bootが本来自動的につけるはずだった、正式な型にはめなおしている（org.springframework.validation.BindingResult.userForm）
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("errors");
		}
		// 登録画面表示（フォワード）
		return "client/user/regist_input";
	}

	/**
	 * 処理4：確認ボタン 押下時処理
	 * @param form 画面から入力された会員情報のフォームオブジェクト
	 * @param result 入力チェックのエラー結果を保持するオブジェクト
	 * @return 登録内容確認画面のHTMLパス("redirect:/client/user/regist/check")
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String checkPost(@Valid @ModelAttribute UserForm form, BindingResult result) {
		// 画面から入力された入力フォームをセッションに保存
		session.setAttribute("userForm", form);

		// BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			// 入力エラー情報と入力フォーム情報を設定してリダイレクト）
			session.setAttribute("errors", result);
			return "redirect:/client/user/regist/input";
		}
		// 入力エラーがない場合、登録確認画面表示処理にリダイレクト
		return "redirect:/client/user/regist/check";
	}

	/**
	 * 処理5：登録確認画面表示処理
	 * @param model 画面へデータを渡すためのModelオブジェクト
	 * @return 登録内容確認画面のHTMLパス("client/user/regist_check")
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.GET)
	public String checkGet(Model model) {
		// セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		// nullチェック
		if (form == null) {
			return "client/user/regist_input";
		}

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", form);

		// 登録確認画面表示（フォワード）
		return "client/user/regist_check";
	}

	/**
	 * 処理6：登録ボタン 押下時処理
	 * @return 登録完了画面のHTMLパス("redirect:/client/user/regist/complete")
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String completePost() {
		// セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		// メールアドレスに該当する会員情報を取得
		User user = repository.findByEmail(form.getEmail());

		if (user == null) {
			// 新規会員の場合
			user = new User();
		}

		// 入力フォーム情報をエンティティに移す
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		user.setName(form.getName());
		user.setPostalCode(form.getPostalCode());
		user.setAddress(form.getAddress());
		user.setPhoneNumber(form.getPhoneNumber());
		user.setAuthority(Constant.AUTH_CLIENT);
		user.setDeleteFlag(Constant.NOT_DELETED);

		repository.save(user); // リポジトリを使って保存

		// userBeanに保存
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);

		boolean reRegist = form.isReRegist();

		// セッションスコープの入力フォーム情報削除
		session.removeAttribute("userForm");
		session.setAttribute("reRegist", reRegist);

		// 未ログインでの会員登録の場合、セッションに会員（ログイン情報）をセット
		session.setAttribute("user", userBean);

		// 登録完了画面表示処理にリダイレクト
		return "redirect:/client/user/regist/complete";
	}

	/**
	 * 処理7：登録完了画面表示処理
	 * @return 登録完了画面のHTMLパス("client/user/regist_complete")
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.GET)
	public String completeGet(Model model) {
		Boolean reRegist = (Boolean) session.getAttribute("reRegist");
		model.addAttribute("reRegist", reRegist != null && reRegist);
		session.removeAttribute("reRegist");

		// 登録完了画面表示
		return "client/user/regist_complete";
	}
}
