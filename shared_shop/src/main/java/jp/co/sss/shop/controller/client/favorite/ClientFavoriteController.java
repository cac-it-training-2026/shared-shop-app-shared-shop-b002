package jp.co.sss.shop.controller.client.favorite;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.FavoriteRepository;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * お気に入り機能のコントローラクラス
 */
@Controller
public class ClientFavoriteController {

	/**
	 * お気に入り情報
	 */
	@Autowired
	FavoriteRepository favoriteRepository;

	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * お気に入り一覧表示
	 *
	 * @param model モデル
	 * @return "client/favorite/list" お気に入り一覧画面表示
	 */
	@RequestMapping(path = "/client/favorite/list", method = RequestMethod.GET)
	public String showFavoriteList(Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}
		User user = new User();
		user.setId(userBean.getId());

		List<Favorite> favorites = favoriteRepository.findByUser(user);
		model.addAttribute("favorites", favorites);

		return "client/favorite/list";
	}

	/**
	 * お気に入り登録
	 *
	 * @param itemId 商品ID
	 * @return "redirect:/client/favorite/list" お気に入り一覧表示にリダイレクト
	 */
	@RequestMapping(path = "/client/favorite/add/{itemId}", method = RequestMethod.POST)
	public String addFavorite(@PathVariable Integer itemId) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}
		User user = new User();
		user.setId(userBean.getId());

		Item item = itemRepository.getReferenceById(itemId);

		// 重複チェック
		Favorite existingFavorite = favoriteRepository.findByUserAndItem(user, item);
		if (existingFavorite == null) {
			Favorite favorite = new Favorite();
			favorite.setUser(user);
			favorite.setItem(item);
			favoriteRepository.save(favorite);
		}

		return "redirect:/client/favorite/list";
	}

	/**
	 * お気に入り削除
	 *
	 * @param id お気に入りID
	 * @return "redirect:/client/favorite/list" お気に入り一覧表示にリダイレクト
	 */
	@RequestMapping(path = "/client/favorite/delete/{id}", method = RequestMethod.POST)
	public String deleteFavorite(@PathVariable Integer id) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}
		User user = new User();
		user.setId(userBean.getId());

		// 所有権チェック
		Optional<Favorite> favorite = favoriteRepository.findByIdAndUser(id, user);
		if (favorite.isPresent()) {
			favoriteRepository.deleteById(id);
		}

		return "redirect:/client/favorite/list";
	}
}
