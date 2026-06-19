package jp.co.sss.shop.controller.client.review;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.util.Constant;

/**
 * レビュー投稿機能のコントローラクラス
 */
@Controller
public class ClientReviewController {

	/**
	 * レビューリポジトリ
	 */
	@Autowired
	ReviewRepository reviewRepository;

	/**
	 * 商品リポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 注文商品リポジトリ
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * レビュー投稿入力画面表示処理
	 *
	 * @param orderItemId 注文商品ID
	 * @param model       Viewとの値受渡し
	 * @return "client/review/regist_input" レビュー投稿入力画面
	 */
	@RequestMapping(path = "/client/review/regist/input", method = RequestMethod.POST)
	public String registReviewInput(Integer orderItemId, Model model) {

		// ログインユーザー情報の取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null || userBean.getAuthority() != Constant.AUTH_CLIENT) {
			return "redirect:/login";
		}

		// 注文商品情報の取得
		Optional<OrderItem> orderItemOpt = orderItemRepository.findById(orderItemId);
		if (!orderItemOpt.isPresent()) {
			return "redirect:/syserror";
		}
		OrderItem orderItem = orderItemOpt.get();

		// 注文がログインユーザーのものかチェック
		if (!orderItem.getOrder().getUser().getId().equals(userBean.getId())) {
			return "redirect:/syserror";
		}

		// 既にレビュー済みでないかチェック
		if (reviewRepository.findByOrderItemId(orderItemId) != null) {
			return "redirect:/client/order/detail/" + orderItem.getOrder().getId();
		}

		ReviewForm form = new ReviewForm();
		form.setOrderItemId(orderItemId);
		form.setItemId(orderItem.getItem().getId());
		model.addAttribute("reviewForm", form);
		model.addAttribute("item", orderItem.getItem());

		return "client/review/regist_input";
	}

	/**
	 * レビュー投稿処理
	 *
	 * @param form               レビューフォーム
	 * @param result             入力チェック結果
	 * @param model              Viewとの値受渡し
	 * @return 商品詳細画面へのリダイレクト
	 */
	@RequestMapping(path = "/client/review/regist", method = RequestMethod.POST)
	public String registReview(@Valid @ModelAttribute ReviewForm form, BindingResult result, Model model) {

		// ログインユーザー情報の取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null || userBean.getAuthority() != Constant.AUTH_CLIENT) {
			return "redirect:/login";
		}

		if (result.hasErrors()) {
			// 商品情報を再取得してモデルに追加
			Optional<Item> itemOpt = itemRepository.findById(form.getItemId());
			model.addAttribute("item", itemOpt.orElse(null));
			return "client/review/regist_input";
		}

		// 注文商品情報の取得
		Optional<OrderItem> orderItemOpt = orderItemRepository.findById(form.getOrderItemId());
		if (!orderItemOpt.isPresent()) {
			return "redirect:/syserror";
		}
		OrderItem orderItem = orderItemOpt.get();

		// 既にレビュー済みでないかチェック（二重送信対策）
		if (reviewRepository.findByOrderItemId(form.getOrderItemId()) != null) {
			return "redirect:/client/item/detail/" + form.getItemId();
		}

		// レビューエンティティの作成と保存
		Review review = new Review();
		review.setRating(form.getRating());
		review.setTitle(form.getTitle());
		review.setContent(form.getContent());
		review.setItem(orderItem.getItem());
		review.setOrderItem(orderItem);

		User user = new User();
		user.setId(userBean.getId());
		review.setUser(user);

		reviewRepository.save(review);

		return "redirect:/client/item/detail/" + form.getItemId();
	}
}
