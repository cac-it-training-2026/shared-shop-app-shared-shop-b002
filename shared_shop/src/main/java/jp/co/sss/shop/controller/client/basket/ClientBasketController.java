package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;

@Controller
public class ClientBasketController {
	@Autowired
	private ItemRepository itemRepository;

	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String showBasketlist(HttpSession session, Model model) {
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		model.addAttribute("basketBeans", basketBeans);

		return "client/basket/list";
	}

	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(@ModelAttribute OrderForm form, HttpSession session, Model model) {

		// ログインしていない場合
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		//セッションから買い物かごを取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		//リストがnullならリストを生成する
		if (basketBeans == null) {
			basketBeans = new ArrayList<>();
		}
		//メッセージの表示用
		List<String> itemNameListZero = new ArrayList<>(); //在庫なし
		List<String> itemNameListLessThan = new ArrayList<>(); //在庫不足

		//同じ商品があるか確認(拡張for文)
		for (BasketBean basket : basketBeans) {
			if (basket.getId().equals(form.getId())) {
				//在庫なし
				if (basket.getStock().equals(0)) {
					itemNameListZero.add(basket.getName());
					model.addAttribute("itemNameListZero", itemNameListZero);
					return "client/basket/list";
				}
				//在庫不足
				if (basket.getStock().equals(basket.getOrderNum())) {
					itemNameListLessThan.add(basket.getName());
					model.addAttribute("itemNameListLessThan", itemNameListLessThan);
					return "client/basket/list";
				}

				//同じ商品は個数を増加
				basket.setOrderNum(basket.getOrderNum() + 1);
				session.setAttribute("basketBeans", basketBeans);
				return "redirect:/client/basket/list";
			}
		}
		//BasketBeanオブジェクト生成
		BasketBean basketBean = new BasketBean();

		//主キー検索
		Item item = itemRepository.findByIdAndDeleteFlag(form.getId(), 0);
		if (item == null) {
			return "redirect:/client/item/list/newest";
		}

		//Item→BasketBean
		basketBean.setId(item.getId());
		basketBean.setName(item.getName());
		basketBean.setStock(item.getStock());

		//リストへ追加
		basketBeans.add(basketBean);
		//セッションへ保存
		session.setAttribute("basketBeans", basketBeans);

		return "redirect:/client/basket/list";
	}

	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket(@ModelAttribute OrderForm form, HttpSession session) {

		return "redirect:/client/basket/list ";
	}

	@RequestMapping(path = "/basket/allDelete", method = RequestMethod.POST)
	public String alldeleteBasket(HttpSession session) {

		return "redirect: /client/basket/list";
	}

}
