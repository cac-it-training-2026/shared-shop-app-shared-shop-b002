package jp.co.sss.shop.controller.client.basket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ClientBasketController {

	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket() {

		return "redirect :/client/basket/list";
	}

	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket() {

		return "redirect:/client/basket/list ";
	}

	@RequestMapping(path = "/basket/allDelete", method = RequestMethod.POST)
	public String alldeleteBasket() {

		return "redirect: /client/basket/list";
	}

}
