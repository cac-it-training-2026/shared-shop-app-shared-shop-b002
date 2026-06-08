package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import jp.co.sss.shop.form.UserForm;

@Controller
public class ClientUserRegistController {

	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String inputUser(@ModelAttribute UserForm form) {
		return "regist_input";
	}

	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String checkUser(@Valid @ModelAttribute UserForm form, Model model) {
		return "regist_check";
	}

	@RequestMapping("/shared_shop")
	public String completeUser() {
		return "regist_complete";
	}

}
