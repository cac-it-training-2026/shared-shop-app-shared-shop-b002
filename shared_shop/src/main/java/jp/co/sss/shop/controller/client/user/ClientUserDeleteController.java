package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

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

	@RequestMapping(path = "/client/user/delete/check/{id}", method = RequestMethod.POST)
	public String deleteCheckString(@PathVariable Integer id) {
		User user = userRepository.findByIdAndDeleteFlag(id, 1);
		if (user == null) {

			return "redirect:/syserror";
		}
		UserForm form = new UserForm();
		form.setId(user.getId());
		form.setName(user.getName());

		//セッションに保存
		session.setAttribute("userForm", form);

		return "redirect:/client/user/delete/check";
	}

	//削除確認画面表示
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String updatedelete(Model model) {

		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			return "redirect:/syserror";
		}
		model.addAttribute("userForm", userForm);
		return "/client/user/delete_check";
	}

	//退会
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String deleteComplete(HttpSession session) {
		UserForm form = (UserForm) session.getAttribute("userForm");
		if (form == null) {

			return "redirect:/syserror";
		}
		//DBから取得
		User user = userRepository.findByIdAndDeleteFlag(form.getId(), 1);

		user.setDeleteFlag(0);
		userRepository.save(user);

		session.removeAttribute("userForm");

		session.invalidate();
		return "redirect:user/delete/complete";
	}

	//退会完了画面
	@RequestMapping(path = "/user/delete/complete", method = RequestMethod.GET)
	public String deleteComplet() {
		return "/client/user/delete_complete";
	}
}
