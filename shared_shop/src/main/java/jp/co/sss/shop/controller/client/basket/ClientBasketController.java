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

	/**
	 * 買い物かご一覧画面を表示する。
	 * セッションから買い物かご情報を取得し、
	 * リクエストスコープへ登録する。
	 *
	 * @param session セッション
	 * @param model モデル
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String showBasketlist(HttpSession session, Model model) {
		//セッションから買い物かごを取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		//リクエストスコープへ登録
		model.addAttribute("basketBeans", basketBeans);

		return "client/basket/list";
	}

	/**
	 * 商品を買い物かごへ追加する。
	 * 未ログイン時はログイン画面へ遷移する。
	 * 同一商品が既に存在する場合は数量を増やし、
	 * 存在しない場合は新たに追加する。
	 * 在庫不足・在庫切れの場合はエラーメッセージを表示する。
	 *
	 * @param form 注文情報
	 * @param session セッション
	 * @param model モデル
	 * @return 買い物かご一覧画面
	 */
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
		List<String> itemNameListZero = new ArrayList<>(); //在庫切れ
		List<String> itemNameListLessThan = new ArrayList<>(); //在庫不足

		//同じ商品があるか確認(拡張for文)
		for (BasketBean basket : basketBeans) {
			if (basket.getId().equals(form.getId())) {
				//在庫切れ
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

	/**
	 * 買い物かごから指定商品を削除する。
	 * 商品IDをもとに買い物かご内の商品を削除し、
	 * 買い物かご一覧画面へ遷移する。
	 *
	 * @param form 注文情報
	 * @param session セッション
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket(@ModelAttribute OrderForm form, HttpSession session) {

		//セッションから買い物かごを取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		//削除候補リストの作成
		List<BasketBean> removeList = new ArrayList<>();
		//拡張for文で買い物かごの中身を１つずつチェック
		if (basketBeans != null) {
			//拡張for文で買い物かごの中身を１つずつチェック
			for (BasketBean basket : basketBeans) {
				//削除したい商品IDと一致した商品を削除候補リストに追加
				if (basket.getId().equals(form.getId())) {
					removeList.add(basket);
					break;
				}
			}
			//ループ終了後にまとめて削除(安全)
			basketBeans.removeAll(removeList);
		}
		session.setAttribute("basketBeans", basketBeans);
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごを空にする。
	 * セッションに保存されている買い物かご情報を削除し、
	 * 買い物かご一覧画面へ遷移する。
	 *
	 * @param session セッション
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String alldeleteBasket(HttpSession session) {
		session.removeAttribute("basketBeans");
		return "redirect:/client/basket/list";
	}

}
