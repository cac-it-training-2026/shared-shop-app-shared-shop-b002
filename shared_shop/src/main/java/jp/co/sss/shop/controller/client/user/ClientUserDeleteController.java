package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員削除(退会)機能のコントローラクラス
 *
 * @author kanamik 
 */

@Controller
public class ClientUserDeleteController {

	/**
	 * 会員情報 リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 会員情報削除確認処理
	 *
	 * @return "redirect:/client/user/delete/check" 削除確認画面 表示
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)

	public String deleteUsercheck() {

		// セッションからログインユーザーを取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {
			// 対象が無い場合、エラー
			return "redirect:/login";
		}
		// DBから最新情報取得
		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);

		if (user == null) {
			return "redirect:/login";
		}
		// 取得情報から表示フォーム情報を生成
		UserForm form = new UserForm();
		BeanUtils.copyProperties(user, form);

		// 情報フォームをセッションに保持
		session.setAttribute("userForm", form);
		// 削除確認画面　表示
		return "redirect:/client/user/delete/check";
	}

	/**
	 * 確認画面　表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/delete_check" 確認画面 表示
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String updateInput(Model model) {

		// セッションから入力フォーム取得
		UserForm form = (UserForm) session.getAttribute("userForm");
		if (form == null) {
			// セッション情報がない場合、エラー
			return "redirect:/login";
		}
		// 入力フォーム情報を画面表示設定
		model.addAttribute("userForm", form);

		// 削除確認画面　表示
		return "client/user/delete_check";
	}

	/**
	 * 会員情報を論理削除し、削除完了画面へリダイレクト
	 *
	 * @return "redirect:/client/user/delete/complete" 会員情報 削除完了画面へ
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String deleteUsercomplete() {
		UserForm form = (UserForm) session.getAttribute("userForm");
		if (form == null) {

			return "redirect:/login";
		}
		// DBから取得
		User user = userRepository.findByIdAndDeleteFlag(form.getId(), Constant.NOT_DELETED);
		if (user == null) {
			return "redirect:/syserror";
		}

		// 論理削除。削除フラグを立てる
		user.setDeleteFlag(Constant.DELETED);

		// DB更新。会員情報を保存
		userRepository.save(user);
		// セッションの削除対象情報を削除
		session.invalidate();
		// 削除完了画面　表示処理
		return "redirect:/client/user/delete/complete";

	}

	/**
	 * 会員情報削除完了画面を表示
	 *
	 * @return "client/user/delete_complete" 会員情報 削除完了画面へ
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteUsercomplet() {
		return "client/user/delete_complete";
	}

}
