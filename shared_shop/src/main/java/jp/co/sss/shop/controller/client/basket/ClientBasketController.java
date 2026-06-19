package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Coupon;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.CouponService;

/**
 * 買い物かごの基本クラス
 * @author miyuta
 */
@Controller
public class ClientBasketController {
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CouponService couponService;

	/**
	 * 買い物かご一覧画面を表示する。
	 * セッションから買い物かご情報を取得し、
	 * リクエストスコープへ登録する。
	 * 在庫切れ・不足の場合は
	 * エラーメッセージを表示する。
	 *
	 * @author miyuta
	 * @param session 買い物かご情報
	 * @param model モデル
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.GET)
	public String showBasketlist(HttpSession session, Model model) {
		// ログインしていない場合（nullチェック）
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		// セッションから買い物かごを取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		// 在庫切れメッセージ表示用
		List<String> itemNameListZero = new ArrayList<>();
		// 在庫不足メッセージ表示用
		List<String> itemNameListLessThan = new ArrayList<>();
		// 買い物かごに商品があるか確認
		Integer total = 0;
		if (basketBeans != null) {
			// 拡張for文でかご内の商品を1つずつ確認
			for (BasketBean basket : basketBeans) {
				// 商品IDでDBから最新の情報を取得
				Item item = itemRepository.findByIdAndDeleteFlag(basket.getId(), 0);
				// 商品が見つからなかった場合
				if (item == null) {
					continue;
				}
				// BasketBeanの在庫を更新
				basket.setStock(item.getStock());
				// 在庫切れ
				if (item.getStock().equals(0)) {
					itemNameListZero.add(item.getName());
				}
				// 在庫不足
				else if (item.getStock() < basket.getOrderNum()) {
					itemNameListLessThan.add(item.getName());
				}
				// 合計金額の加算
				if (item != null) {
					total += item.getPrice() * basket.getOrderNum();
				}
			}
		}

		Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
		Integer discountAmount = couponService.calculateDiscount(appliedCoupon, total);
		session.setAttribute("discountAmount", discountAmount);

		// リクエストスコープへ登録
		model.addAttribute("itemNameListZero", itemNameListZero);
		model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		model.addAttribute("basketBeans", basketBeans);
		model.addAttribute("total", total);

		return "client/basket/list";
	}

	/**
	 * クーポンを適用する。
	 * @param couponCode クーポンコード
	 * @param session セッション
	 * @param model モデル
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/coupon/apply", method = RequestMethod.POST)
	public String applyCoupon(@RequestParam String couponCode, HttpSession session, Model model) {
		Coupon coupon = couponService.validateCoupon(couponCode);
		if (coupon == null) {
			session.removeAttribute("appliedCoupon");
			model.addAttribute("couponError", "入力されたクーポンコードは利用できません。");
		} else {
			session.setAttribute("appliedCoupon", coupon);
		}
		return showBasketlist(session, model);
	}

	/**
	 * 商品を買い物かごへ追加する。
	 * 未ログイン時はログイン画面へ遷移する。
	 * 同一商品が既に存在する場合は数量を増やし、
	 * 存在しない場合は新たに追加する。
	 * 在庫不足の場合は
	 * エラーメッセージを表示する。
	 *
	 * @author miyuta
	 * @param form 在庫情報
	 * @param session 買い物かご情報
	 * @param model モデル
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(@ModelAttribute OrderForm form, HttpSession session, Model model) {

		// ログインしていない場合（nullチェック）
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		// セッションから買い物かごを取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		// かごがnullならリストを生成する
		if (basketBeans == null) {
			basketBeans = new ArrayList<>();
		}
		// 在庫不足メッセージ表示用
		List<String> itemNameListLessThan = new ArrayList<>();
		// 同じ商品があるか確認(拡張for文)
		for (BasketBean basket : basketBeans) {
			if (basket.getId().equals(form.getId())) {
				// DBから最新在庫取得
				Item item = itemRepository.findByIdAndDeleteFlag(basket.getId(), 0);
				// BasketBeanの在庫を更新
				basket.setStock(item.getStock());
				// 在庫不足
				if (basket.getStock() <= (basket.getOrderNum())) {
					itemNameListLessThan.add(basket.getName());

					model.addAttribute("basketBeans", basketBeans);
					model.addAttribute("itemNameListLessThan", itemNameListLessThan);
					return "client/basket/list";
				}
				// 同じ商品は個数を増加
				basket.setOrderNum(basket.getOrderNum() + 1);
				session.setAttribute("basketBeans", basketBeans);
				return "redirect:/client/basket/list";
			}
		}
		// DBから最新在庫取得
		Item item = itemRepository.findByIdAndDeleteFlag(form.getId(), 0);
		// BasketBeanオブジェクト生成
		BasketBean basketBean = new BasketBean();
		// Item→BasketBean
		basketBean.setId(item.getId());
		basketBean.setName(item.getName());
		basketBean.setStock(item.getStock());

		// リストへ追加
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
	 * @author miyuta
	 * @param form 削除対象商品のID
	 * @param session 買い物かご情報
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket(@ModelAttribute OrderForm form, HttpSession session) {

		// セッションから買い物かごを取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		boolean deleteFlag = false;
		// 拡張for文で買い物かごの中身を１つずつチェック
		if (basketBeans != null) {
			// 拡張for文で買い物かごの中身を１つずつチェック
			for (BasketBean basket : basketBeans) {
				// 削除したい商品IDと一致した商品を削除候補リストに追加
				if (basket.getId().equals(form.getId())) {
					// 数量が2以上なら1減らす
					if (basket.getOrderNum() > 1) {
						basket.setOrderNum(basket.getOrderNum() - 1);
						// 数量が1なら削除
					} else {
						deleteFlag = true;
					}
					break;
				}
			}
			// for文の外で削除
			if (deleteFlag) {
				basketBeans.removeIf(
						basketBean -> basketBean.getId().equals(form.getId()));
			}

			// かごが空の場合セッションから削除
			if (basketBeans.isEmpty()) {
				session.removeAttribute("basketBeans");
				// かごに他の商品が残っている場合
				// 新の買い物かご情報をセッションに保存
			} else {
				session.setAttribute("basketBeans", basketBeans);
			}
		}
		return "redirect:/client/basket/list";
	}

	/**
	 * 買い物かごを空にする。
	 * セッションに保存されている買い物かご情報を削除し、
	 * 買い物かご一覧画面へ遷移する。
	 *
	 * @author miyuta
	 * @param session 買い物かご情報
	 * @return 買い物かご一覧画面
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String alldeleteBasket(HttpSession session) {
		// セッションから削除
		session.removeAttribute("basketBeans");
		return "redirect:/client/basket/list";
	}

}
