package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClientUserUpdateController {

	@RequestMapping(path = "/client/user/update/input")
	public String updateUserinput() {

		return "user/update_input";
	}

}
