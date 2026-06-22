package jp.co.sss.shop.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.Item;

/**
 * favoritesテーブル用リポジトリ
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	/**
	 * 会員に紐づくお気に入り情報を取得する
	 * @param user 会員情報
	 * @return お気に入り情報のリスト
	 */
	List<Favorite> findByUser(User user);

	/**
	 * 会員と商品に紐づくお気に入り情報を取得する
	 * @param user 会員情報
	 * @param item 商品情報
	 * @return お気に入り情報
	 */
	Favorite findByUserAndItem(User user, Item item);

	/**
	 * お気に入りIDと会員に紐づくお気に入り情報を取得する（所有権チェック用）
	 * @param id お気に入りID
	 * @param user 会員情報
	 * @return お気に入り情報
	 */
	Optional<Favorite> findByIdAndUser(Integer id, User user);
}
