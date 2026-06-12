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

	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateUserInput(HttpSession session, Model model) {

		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {
			return "redirect:/";
		}

		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			User user = userRepository.findById(loginUser.getId()).orElse(null);
			userForm = new UserForm();
			if (user != null) {
				BeanUtils.copyProperties(user, userForm);
			}
		}

		model.addAttribute("userForm", userForm);

		String errorKey = "org.springframework.validation.BindingResult.userForm";
		if (session.getAttribute(errorKey) != null) {
			model.addAttribute(errorKey, session.getAttribute(errorKey));
			session.removeAttribute(errorKey);
		}

		return "client/user/update_input";//ここを詳細設計通り、リダイレクトにするとエラーになる//
	}

	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateUserCheck(@Valid @ModelAttribute UserForm form, BindingResult result, Model model,
			HttpSession session) {

		session.setAttribute("userForm", form);

		if (result.hasErrors()) {

			session.setAttribute("org.springframework.validation.BindingResult.userForm", result);

			return "redirect:/client/user/update/input";
		}

		return "client/user/update_check";
	}

	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateUserComplete(HttpSession session) {

		UserBean loginUser = (UserBean) session.getAttribute("user");
		UserForm form = (UserForm) session.getAttribute("userForm");

		if (loginUser != null && form != null) {
			User user = userRepository.findById(loginUser.getId()).orElse(null);

			if (user != null) {

				user.setName(form.getName());
				user.setEmail(form.getEmail());
				user.setPostalCode(form.getPostalCode());
				user.setAddress(form.getAddress());
				user.setPhoneNumber(form.getPhoneNumber());

				userRepository.save(user);

				loginUser.setName(user.getName());
				loginUser.setEmail(user.getEmail());

				session.setAttribute("user", loginUser);

				session.removeAttribute("userForm");
			}
		}

		return "client/user/update_complete";//リダイレクトにするとメンテ表示
	}

	//新しく追加 6/11 完了画面表示用のGETメソッド
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateUserCompleteView() {

		return "client/user/update_complete";
	}

}