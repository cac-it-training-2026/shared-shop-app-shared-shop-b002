package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {

	@Autowired
	HttpSession session;
	@Autowired
	UserRepository repository;

	// 処理1：新規登録リンク クリック時処理
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String inputInit() {
		//入力フォーム情報を新規生成
		UserForm form = new UserForm();
		//入力フォーム情報をセッションスコープに保存
		session.setAttribute("userForm", form);
		//登録入力画面表示処理にリダイレクト
		return "redirect:/client/user/regist/input";
	}

	//aaaaaa
	// 処理2：新規登録ボタン 押下時処理、確認画面-戻るボタン 押下時処理
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String inputPost(@ModelAttribute UserForm form) {
		//セッションスコープに入力フォーム情報があるかを確認
		UserForm sessionForm = (UserForm) session.getAttribute("userForm");
		if (sessionForm == null) {
			//なければ入力フォーム情報を新規生成してセッションに保存
			sessionForm = new UserForm();
			session.setAttribute("userForm", sessionForm);
		}
		//登録入力画面表示処理にリダイレクト
		return "redirect:/client/user/regist/input";
	}

	// 処理3：登録画面表示処理
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String inputGet(Model model) {
		// セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", form);

		// ※セッションにエラー情報がある場合の処理（処理4でエラーがあった時用）
		BindingResult result = (BindingResult) session.getAttribute("errors");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("errors");
		}
		//登録画面表示（フォワード）
		return "client/user/regist_input";
	}

	// 処理4：確認ボタン 押下時処理
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String checkPost(@Valid @ModelAttribute UserForm form, BindingResult result) {
		//画面から入力された入力フォームをセッションに保存
		session.setAttribute("userForm", form);

		//BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			//入力エラー情報と入力フォーム情報を設定してリダイレクト）
			session.setAttribute("errors", result);
			return "redirect:/client/user/regist/input";
		}
		//入力エラーがない場合、登録確認画面表示処理にリダイレクト
		return "redirect:/client/user/regist/check";
	}

	// 処理5：登録確認画面表示処理
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.GET)
	public String checkGet(Model model) {
		//セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");
		//入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", form);

		//登録確認画面表示（フォワード）
		return "client/user/regist_check";
	}

	// 処理6：登録ボタン 押下時処理
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String completePost() {
		//セッションスコープから入力フォーム情報を取得
		UserForm form = (UserForm) session.getAttribute("userForm");

		//入力フォーム情報を元にDB登録用エンティティオブジェクトを生成
		User user = new User();
		user.setId(form.getId()); //フォームの値をエンティティに移す
		user.setEmail(form.getEmail());
		user.setPassword(form.getPassword());
		user.setName(form.getName());
		user.setPostalCode(form.getPostalCode());
		user.setAddress(form.getAddress());
		user.setPhoneNumber(form.getPhoneNumber());

		//DB登録
		repository.save(user); // リポジトリを使って保存

		//セッションスコープの入力フォーム情報削除
		session.removeAttribute("userForm");

		//未ログインでの会員登録の場合、セッションに会員（ログイン情報）をセット
		session.setAttribute("user", user);

		//登録完了画面表示処理にリダイレクト
		return "redirect:/client/user/regist/complete";
	}

	// 処理7：登録完了画面表示処理
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.GET)
	public String completeGet() {
		//登録完了画面表示
		return "client/user/regist_complete";
	}
}
