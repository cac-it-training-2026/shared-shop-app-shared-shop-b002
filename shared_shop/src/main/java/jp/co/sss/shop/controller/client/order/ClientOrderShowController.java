package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
public class ClientOrderShowController {

	/**
	 * 注文情報 あああ
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 合計金額計算サービス
	 */
	@Autowired
	PriceCalc priceCalc;

	/**
	 * Entity、Form、Bean間のデータ生成、コピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 一覧取得、一覧画面表示　処理
	 *
	 * @param model Viewとの値受渡し
	 * @param pageable ページング情報
	 * @return "/order/list" 注文情報 一覧画面へ
	 */

	//注文一覧
	@RequestMapping(path = "/client/order/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showOrderList(Model model, Pageable pageable) {
		//ログインユーザー取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/client/login";
		}
		// すべての注文情報を取得(注文日降順)
		//表示画面でページングが必要なため、ページ情報付きの検索を行う
		Page<Order> orderList = orderRepository.findByUserIdOrderByInsertDateDescIdDesc(loginUser.getId(), pageable);

		// 注文情報リストを生成
		List<OrderBean> orderBeanList = new ArrayList<>();
		for (Order order : orderList) {
			OrderBean orderBean = beanTools.copyEntityToOrderBean(order);
			//orderレコードから紐づくOrderItemのListを取り出す
			List<OrderItem> orderItemList = order.getOrderItemsList();
			int total = priceCalc.orderItemPriceTotal(orderItemList);
			//合計金額のセット
			orderBean.setTotal(total);
			orderBeanList.add(orderBean);

		}
		//注文情報リストをVIEWへ渡す
		model.addAttribute("pages", orderList);
		model.addAttribute("orders", orderBeanList);

		return "client/order/list";

	}

	//注文詳細
	@RequestMapping(path = "/client/order/detail/{id}")

	public String showDetailOrder(@PathVariable Integer id, Model model) {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/syserror";
		}
		// 指定された注文IDとログインユーザーに紐づく注文情報を取得
		Order order = orderRepository.getReferenceById(id);

		if (order == null) {
			return "redirect:/syserror";
		}
		OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

		List<OrderItemBean> itemBeans = beanTools.generateOrderItemBeanList(order.getOrderItemsList());
		//合計金額を算出
		int total = priceCalc.orderItemBeanPriceTotalUseSubtotal(itemBeans);

		//注文情報をViewへ渡す
		model.addAttribute("order", orderBean);
		model.addAttribute("orderItemBeans", itemBeans);
		model.addAttribute("total", total);

		return "client/order/detail";
	}

}
