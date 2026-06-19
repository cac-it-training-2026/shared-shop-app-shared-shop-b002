package jp.co.sss.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Order;

/**
 * ordersテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository

public interface OrderRepository extends JpaRepository<Order, Integer> {

	/**
	 * 注文日付降順で注文情報すべてを検索(管理者機能で利用)
	 * @param pageable ページング情報
	 * @return 注文エンティティのページオブジェクト
	 */
	@Query("SELECT o FROM Order o ORDER BY o.insertDate DESC,o.id DESC")
	Page<Order> findAllOrderByInsertdateDescIdDesc(Pageable pageable);

	// ログインユーザーの注文のみ取得
	Page<Order> findByUserIdOrderByInsertDateDescIdDesc(
			Integer userId,
			Pageable pageable);

	/**
	 * 注文IDとユーザーIDに合致する注文情報を取得
	 * @param id 注文ID
	 * @param userId ユーザーID
	 * @return 注文エンティティ
	 */
	Order findByIdAndUserId(Integer id, Integer userId);

}
