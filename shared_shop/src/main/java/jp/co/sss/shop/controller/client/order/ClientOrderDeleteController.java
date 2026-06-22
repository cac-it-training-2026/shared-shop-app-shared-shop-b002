package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientOrderDeleteController {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	HttpSession session;

	@RequestMapping(path = "/client/order/delete/check/{id}", method = RequestMethod.POST)
	public String deleteCheck(@PathVariable Integer id, Model model) {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/client/login";
		}

		Order order = orderRepository.findByIdAndUserIdAndDeleteFlag(id, loginUser.getId(), 0);
		if (order == null) {
			return "redirect:/syserror";
		}

		session.setAttribute("orderId", id);

		return "redirect:/client/order/delete/check";
	}

	@RequestMapping(path = "/client/order/delete/check", method = RequestMethod.GET)
	public String deleteCheckView(Model model) {
		Integer orderId = (Integer) session.getAttribute("orderId");
		if (orderId == null) {
			return "redirect:/syserror";
		}

		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/client/login";
		}

		Order order = orderRepository.findByIdAndUserIdAndDeleteFlag(orderId, loginUser.getId(), 0);
		if (order == null) {
			return "redirect:/syserror";
		}

		return "client/order/delete_check";
	}

	@RequestMapping(path = "/client/order/delete/complete", method = RequestMethod.POST)
	public String deleteComplete() {
		Integer orderId = (Integer) session.getAttribute("orderId");
		if (orderId == null) {
			return "redirect:/syserror";
		}

		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/client/login";
		}

		Order order = orderRepository.findByIdAndUserIdAndDeleteFlag(orderId, loginUser.getId(), 0);
		if (order == null) {
			return "redirect:/syserror";
		}

		// 論理削除
		order.setDeleteFlag(Constant.DELETED);
		orderRepository.save(order);

		// 在庫戻し処理
		for (OrderItem orderItem : order.getOrderItemsList()) {
			Item item = orderItem.getItem();
			item.setStock(item.getStock() + orderItem.getQuantity());
			itemRepository.save(item);
		}

		session.removeAttribute("orderId");

		return "redirect:/client/order/delete/complete";
	}

	@RequestMapping(path = "/client/order/delete/complete", method = RequestMethod.GET)
	public String deleteCompleteView() {
		return "client/order/delete_complete";
	}
}
