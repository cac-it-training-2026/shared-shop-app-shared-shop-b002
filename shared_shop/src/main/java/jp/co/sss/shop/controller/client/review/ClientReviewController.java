package jp.co.sss.shop.controller.client.review;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
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
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * レビュー投稿処理
	 *
	 * @param form               レビューフォーム
	 * @param result             入力チェック結果
	 * @param redirectAttributes リダイレクト先への属性受け渡し
	 * @return 商品詳細画面へのリダイレクト
	 */
	@RequestMapping(path = "/client/review/regist", method = RequestMethod.POST)
	public String registReview(@Valid @ModelAttribute ReviewForm form, BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			// バリデーションエラーがある場合、エラーメッセージをリダイレクト先に渡す
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reviewForm", result);
			redirectAttributes.addFlashAttribute("reviewForm", form);
			return "redirect:/client/item/detail/" + form.getItemId();
		}

		// ログインユーザー情報の取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null || userBean.getAuthority() != Constant.AUTH_CLIENT) {
			return "redirect:/login";
		}

		// 商品情報の取得
		Optional<Item> itemOpt = itemRepository.findById(form.getItemId());
		if (!itemOpt.isPresent()) {
			return "redirect:/syserror";
		}

		// レビューエンティティの作成と保存
		Review review = new Review();
		review.setRating(form.getRating());
		review.setTitle(form.getTitle());
		review.setContent(form.getContent());

		Item item = itemOpt.get();
		review.setItem(item);

		User user = new User();
		user.setId(userBean.getId());
		review.setUser(user);

		reviewRepository.save(review);

		return "redirect:/client/item/detail/" + form.getItemId();
	}
}
