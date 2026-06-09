package jp.co.sss.shop.controller.client.order;

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
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientOrderRegistController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String inputAddress(@ModelAttribute OrderForm form, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		User user = userRepository.findById(loginUser.getId()).orElse(null);

		form.setName(user.getName());
		form.setPostalCode(user.getPostalCode());
		form.setAddress(user.getAddress());
		form.setPhoneNumber(user.getPhoneNumber());
		form.setPayMethod(1);

		session.setAttribute("orderForm", form);

		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String inputAdress2(OrderForm form, Model model) {

		form = (OrderForm) session.getAttribute("orderForm");
		/*確認用*/
		if (form == null) {
			form = new OrderForm();
		}
		model.addAttribute("orderForm", form);

		Object result = session.getAttribute("result");

		if (result != null) {
			model.addAttribute("result", result);
			session.removeAttribute("result");
		}

		return "client/order/address_input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String errorcheck(@Valid @ModelAttribute OrderForm form, BindingResult result) {

		session.setAttribute("orderForm", form);

		return "";
	}

}
