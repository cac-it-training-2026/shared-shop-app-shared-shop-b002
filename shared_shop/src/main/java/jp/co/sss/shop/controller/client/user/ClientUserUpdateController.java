package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class ClientUserUpdateController {

	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateUserinput(Model model, HttpSession session) {

		return "user/update_input";
	}

	//	@RequestMapping(path ="/client/user/update/check",method = RequestMethod.POST)
	//	public String updateUsercheck()
	//	
	//	
	//
}
