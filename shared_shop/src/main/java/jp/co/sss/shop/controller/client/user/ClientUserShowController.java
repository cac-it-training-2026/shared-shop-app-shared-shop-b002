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

@Controller
public class ClientUserShowController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
	public String showDetailUser(HttpSession session, Model model) {

		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (loginUser == null) {

			return "redirect:/";
		}

		session.removeAttribute("userForm");

		User user = userRepository.findById(loginUser.getId()).orElse(null);

		UserBean userBean = new UserBean();
		if (user != null) {
			BeanUtils.copyProperties(user, userBean);

		}

		model.addAttribute("userBean", userBean);

		return "client/user/detail";
	}

	@RequestMapping(path = "/client/user/update/input")
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

		return "client/user/update_input";
	}
}