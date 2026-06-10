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

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)

	public String deleteUsercheck() {

		//セッションからログインユーザーを取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {
			return "redirect:/syserror";
		}
		//DBから最新情報取得
		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);

		if (user == null) {
			return "redirect:/syserror";
		}
		UserForm form = new UserForm();
		BeanUtils.copyProperties(user, form);

		//セッションに保存
		session.setAttribute("userForm", form);

		return "redirect:/client/user/delete/check";
	}

	//退会確認画面表示
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String updatedeleteUser(Model model) {

		UserForm form = (UserForm) session.getAttribute("userForm");

		if (form == null) {
			return "redirect:/syserror";
		}
		model.addAttribute("userForm", form);

		return "client/user/delete_check";
	}

	@RequestMapping(path = "/client/user/detail", method = RequestMethod.POST)
	public String backToUserDetailPost() {
	    // そのまま詳細画面へ戻す（必要ならセッションチェック）
	    UserBean loginUser = (UserBean) session.getAttribute("user");

	    if (loginUser == null) {
	        return "redirect:/syserror";
	    }

	    return "redirect:/client/user/detail";
	}
	//退会実行
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String deleteUsercomplete() {
		UserForm form = (UserForm) session.getAttribute("userForm");
		if (form == null) {

			return "redirect:/syserror";
		}
		//DBから取得
		User user = userRepository.findByIdAndDeleteFlag(form.getId(), Constant.NOT_DELETED);
		if (user == null) {
			return "redirect:/syserror";
		}

		//論理削除
		user.setDeleteFlag(Constant.DELETED);

		//DB更新
		userRepository.save(user);

		session.invalidate();
		return "redirect:/client/user/delete/complete";

	}

	//退会完了画面
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteUsercomplet() {
		return "client/user/delete_complete";
	}
}
