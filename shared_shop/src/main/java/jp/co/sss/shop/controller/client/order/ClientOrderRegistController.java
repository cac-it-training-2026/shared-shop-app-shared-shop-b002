package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Coupon;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.service.CouponService;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 注文機能の基本クラス
 * @author kido
 * 
 */
@Controller
public class ClientOrderRegistController {

	/**
	 * ユーザー情報を操作するリポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * 商品情報を操作するリポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 注文情報を操作するリポジトリ
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * 注文商品の明細情報を操作するリポジトリ
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * クーポン関連の処理を行うサービス
	 */
	@Autowired
	CouponService couponService;

	/**
	 * セッション情報を管理するオブジェクト
	 * ログインユーザー情報などの保持に使用する
	 */
	@Autowired
	HttpSession session;

	/** ご注文のお手続きボタン 押下時処理
	 * 
	 * @param form formの情報
	 * @return リダイレクト先のURL
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String inputAddress(@ModelAttribute OrderForm form) {
		UserBean loginUser = (UserBean) session.getAttribute("user");

		// セッションが切れていた場合はログイン画面へ戻す
		if (loginUser == null) {
			return "redirect:/login";
		}
		// loginUser(UserBean) の ID を使って、「User」エンティティを取得する
		User user = userRepository.findById(loginUser.getId()).orElse(null);

		// DBから取得した最新の会員情報をフォームにセットする（userがnullでないことを確認）
		if (user != null) {
			form.setName(user.getName());
			form.setPostalCode(user.getPostalCode());
			form.setAddress(user.getAddress());
			form.setPhoneNumber(user.getPhoneNumber());
		}
		form.setPayMethod(1);

		session.setAttribute("orderForm", form);
		return "redirect:/client/order/address/input";
	}

	/** 届け先入力画面表示処理
	 * 
	 * @param form formの情報
	 * @param model モデル
	 * @return 住所入力画面に遷移
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String inputAdressView(OrderForm form, Model model) {

		form = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", form);

		BindingResult result = (BindingResult) session.getAttribute("result");

		if (result != null) {
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "orderForm", result);
			session.removeAttribute("result");
		}

		return "client/order/address_input";
	}

	/** 届け先入力画面 次へボタン 押下時処理
	 * 
	 * @param form formの情報
	 * @param result 入力チェック（バリデーション）結果
	 * @return 支払方法選択画面へのリダイレクトURL
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String errorCheck(@Valid @ModelAttribute OrderForm form, BindingResult result) {

		session.setAttribute("orderForm", form);

		// エラーある場合
		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/client/order/address/input";

		}
		// 支払方法選択画面表示処理にリダイレクト
		return "redirect:/client/order/payment/input";
	}

	/**
	 * 届け先入力画面 戻るボタン 押下時処理
	 * @return 買い物かご画面へのリダイレクトURL
	 */
	@RequestMapping(path = "/client/basket/list", method = RequestMethod.POST)
	public String backToBasket() {

		// 買い物かご画面（表示処理）へリダイレクトする
		return "redirect:/client/basket/list";
	}

	/**
	 * 支払方法選択画面表示処理
	 * @param model モデル
	 * @return 支払方法選択画面のビュー名
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String inputPayment(Model model) {
		OrderForm form = (OrderForm) session.getAttribute("orderForm");

		model.addAttribute("orderForm", form);
		// 初期値をクレジットカードに設定
		if (form != null) {
			model.addAttribute("payMethod", form.getPayMethod());
		}

		return "client/order/payment_input";
	}

	/**
	 * 支払方法選択画面 次へボタン 押下時処理
	 * @param form formの情報
	 * @return 注文確認画面へのリダイレクトURL
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String pushNext(@ModelAttribute OrderForm form) {
		OrderForm sessionform = (OrderForm) session.getAttribute("orderForm");
		if (sessionform == null) {
			return "redirect:/client/order/address/input";
		}

		sessionform.setPayMethod(form.getPayMethod());
		session.setAttribute("orderForm", sessionform);

		return "redirect:/client/order/check";
	}

	/**
	 * 注文確認画面表示処理
	 * @param model モデル
	 * @return 注文確認画面のビュー名
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String checkOrder(Model model) {

		// セッションスコープから注文情報を取得
		OrderForm form = (OrderForm) session.getAttribute("orderForm");
		if (form == null) {
			return "redirect:/client/order/address/input";
		}

		// セッションスコープから買い物かご情報を取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		int totalAmount = 0;//合計金額用

		List<BasketBean> updatedBasketBeans = new ArrayList<>();
		List<String> itemNameListLessThan = new ArrayList<>();
		List<String> itemNameListZero = new ArrayList<>();
		List<Map<String, Object>> displayItems = new ArrayList<>();

		if (basketBeans != null) {
			for (BasketBean basket : basketBeans) {
				// 注文商品の最新情報をDBから取得し、商品の在庫チェックを行う
				Item dbItem = itemRepository.findByIdAndDeleteFlag(basket.getId(), 0);

				// ①商品が存在しない、または在庫切れの場合
				if (dbItem == null || dbItem.getStock() == 0) {
					itemNameListZero.add(basket.getName());
					continue;
				}
				// ②在庫不足の場合：注文数を在庫数と同じにする
				if (basket.getOrderNum() > dbItem.getStock()) {
					itemNameListLessThan.add(basket.getName());
					basket.setOrderNum(dbItem.getStock());
				}

				// 在庫数を最新の状態に更新
				basket.setStock(dbItem.getStock());
				updatedBasketBeans.add(basket);
				// 買い物かご情報から、商品ごとの金額小計を算出し、注文商品情報リストに保存
				int subTotal = dbItem.getPrice() * basket.getOrderNum();
				totalAmount += subTotal;

				// 注文商品情報リスト（Mapのリスト）にデータを格納
				Map<String, Object> orderItemMap = new HashMap<>();
				orderItemMap.put("name", dbItem.getName());
				orderItemMap.put("image", dbItem.getImage());
				orderItemMap.put("price", dbItem.getPrice());
				orderItemMap.put("orderNum", basket.getOrderNum());
				orderItemMap.put("subtotal", subTotal);

				displayItems.add(orderItemMap);
			}
		}
		// 在庫状況を反映した買い物かご情報をセッションに保存
		session.setAttribute("basketBeans", updatedBasketBeans);

		// 在庫不足、在庫切れがあった場合の警告メッセージ
		if (!itemNameListLessThan.isEmpty()) {
			model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		}
		if (!itemNameListZero.isEmpty()) {
			model.addAttribute("itemNameListZero", itemNameListZero);
		}

		// 注文商品情報リストをリクエストスコープに設定
		if (updatedBasketBeans != null && !updatedBasketBeans.isEmpty()) {
			model.addAttribute("orderItemBeans", displayItems);
		} else {
			model.addAttribute("orderItemBeans", null);
		}

		// 割引計算
		Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
		Integer discountAmount = couponService.calculateDiscount(appliedCoupon, totalAmount);

		// 各種情報をリクエストスコープ（Model）に設定
		model.addAttribute("total", totalAmount); //合計金額
		model.addAttribute("discountAmount", discountAmount); //割引額
		model.addAttribute("finalTotal", totalAmount - discountAmount); //最終合計
		model.addAttribute("orderForm", form); //注文入力フォーム情報

		return "client/order/check";
	}

	/**
	 * 注文確認画面で、戻るボタン押下処理
	 * @return 届け先入力画面へのリダイレクトURL
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String backPayment() {

		return "redirect:/client/order/address/input";
	}

	/**
	 * ご注文の確定ボタン 押下時処理
	 * @return 注文完了画面へのリダイレクトURL
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String pushFinal() {
		// ログインユーザー情報取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		// セッションスコープから注文情報を取得
		OrderForm form = (OrderForm) session.getAttribute("orderForm");

		// セッションスコープから買い物かご情報を取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		if (form == null || basketBeans == null || basketBeans.isEmpty()) {
			return "redirect:/client/basket/list";
		}

		// 注文の在庫チェック
		for (BasketBean basket : basketBeans) {
			// 注文商品の最新情報をDBから取得し、商品の在庫チェックを行う
			Item dbItem = itemRepository.findByIdAndDeleteFlag(basket.getId(), 0);

			// ①商品が存在しない、または在庫切れの場合
			if (dbItem == null || dbItem.getStock() == 0 || basket.getOrderNum() > dbItem.getStock()) {
				// 注文確認画面へリダイレクト
				return "redirect:/client/order/check";

			}
		}
		// 注文情報情報を元にDB登録用エンティティオブジェクトを生成
		Order order = new Order();

		// ログインしているユーザーのエンティティを生成してセット
		User user = userRepository.findById(userBean.getId()).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}
		order.setUser(user);

		// フォームから住所などの情報を移し替える
		order.setPostalCode(form.getPostalCode());
		order.setAddress(form.getAddress());
		order.setName(form.getName());
		order.setPhoneNumber(form.getPhoneNumber());
		order.setPayMethod(form.getPayMethod());

		// クーポン情報の取得
		Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
		if (appliedCoupon != null) {
			// 商品の合計金額を再計算
			int totalAmount = 0;
			for (BasketBean basket : basketBeans) {
				Item dbItem = itemRepository.findByIdAndDeleteFlag(basket.getId(), 0);
				if (dbItem != null) {
					totalAmount += dbItem.getPrice() * basket.getOrderNum();
				}
			}
			Integer discountAmount = couponService.calculateDiscount(appliedCoupon, totalAmount);

			order.setCouponCode(appliedCoupon.getCouponCode());
			order.setDiscountAmount(discountAmount);
			order.setDiscountedTotal(totalAmount - discountAmount);
		}

		// 注文テーブルのDB登録実施
		Order savedOrder = orderRepository.save(order);

		// 注文商品テーブルのDB登録実施
		for (BasketBean basket : basketBeans) {
			// DBから最新の商品情報を再度取得
			Item dbItem = itemRepository.findByIdAndDeleteFlag(basket.getId(), 0);

			// 注文商品（明細）エンティティの生成
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(savedOrder);
			orderItem.setItem(dbItem);

			orderItem.setQuantity(basket.getOrderNum());

			orderItem.setPrice(dbItem.getPrice());

			// 注文商品テーブルのDB登録実施
			orderItemRepository.save(orderItem);

			// 在庫数の引き算処理（DBの在庫を実際に減らす）
			int newStock = dbItem.getStock() - basket.getOrderNum();
			dbItem.setStock(newStock);
			itemRepository.save(dbItem);
		}

		// セッションスコープの注文入力フォーム情報、買い物かご情報、クーポン情報を削除
		session.removeAttribute("orderForm");
		session.removeAttribute("basketBeans");
		session.removeAttribute("appliedCoupon");
		session.removeAttribute("discountAmount");

		return "redirect:/client/order/complete";
	}

	/**
	 * 注文完了画面表示処理
	 * @return 注文完了画面のビュー名
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String completeOrder() {

		return "client/order/complete";
	}

}
