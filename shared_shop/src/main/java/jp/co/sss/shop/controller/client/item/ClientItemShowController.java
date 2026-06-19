package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.ReviewBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
/**
 * 
 */
/**
 * 
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * リポジトリ
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * レビューリポジトリ
	 */
	@Autowired
	ReviewRepository reviewRepository;

	@ModelAttribute("categories")
	public List<Category> getCategories() {
		List<Category> list = categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);

		return list;
	}

	/**
	 * トップ画面  表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		/*TODO 現在は全件表示を行っている
		 * これを売れ筋（注文回数が多い順）に改修する*/

		// 注文情報の商品情報を全件表示
		List<Item> itemList = itemRepository.findPopularItems();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		return "index";
	}

	/**
	 * @param sortType 表示対象sort
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" リスト表示
	 */

	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET, RequestMethod.POST })
	public String sort(@PathVariable Integer sortType,
			@RequestParam(required = false) Integer categoryId, // 画面からのカテゴリID受け取り
			Model model) {
		if (sortType != 1 && sortType != 2) {
			return "redirect:/syserror";
		}
		List<Item> itemList;

		// 【条件分岐】カテゴリ検索（指定あり）の場合
		if (categoryId != null && categoryId != 0) {
			if (sortType == 1) {
				// カテゴリ内：新着順
				itemList = itemRepository.findByCategoryIdContainingOrderByInsertDateDesc(categoryId);
			} else {
				// カテゴリ内：売れ筋順
				itemList = itemRepository.findPopularItemsByCategoryId(categoryId);
			}
		}
		// カテゴリ指定なし（全件表示）の場合
		else {
			if (sortType == 1) {
				// 全件：新着順
				itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
			} else {
				// 全件：売れ筋順
				itemList = itemRepository.findPopularItems();
			}
		}

		// JavaBeansへのコピーと画面への引き渡し
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);

		// 画面のプルダウンの選択状態（青い選択状態）を維持するために送り返す
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id      表示対象ID
	 * @param model   Viewとの値受渡し
	 * @return "client/item/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {

		// 商品IDに該当する商品情報を取得する
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		// Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		// レビュー情報の取得
		List<Review> reviewList = reviewRepository.findByItemIdOrderByInsertDateDesc(id);
		List<ReviewBean> reviewBeanList = beanTools.copyEntityListToReviewBeanList(reviewList);
		model.addAttribute("reviews", reviewBeanList);

		// 平均評価の算出
		Double averageRating = 0.0;
		String averageStar = "☆☆☆☆☆";
		if (!reviewList.isEmpty()) {
			averageRating = reviewList.stream().mapToInt(Review::getRating).average().orElse(0.0);
			averageStar = beanTools.convertRatingToStar((int) Math.round(averageRating));
		}
		model.addAttribute("averageRating", averageRating);
		model.addAttribute("averageStar", averageStar);

		return "client/item/detail";
	}

}
