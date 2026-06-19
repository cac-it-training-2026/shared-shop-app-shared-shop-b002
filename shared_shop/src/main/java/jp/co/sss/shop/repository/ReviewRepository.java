package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewsテーブル用リポジトリ
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品IDに基づき登録日の降順でレビューを取得
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId);

	/**
	 * 注文明細IDに基づきレビューを取得
	 * @param orderItemId 注文明細ID
	 * @return レビューエンティティ
	 */
	Review findByOrderItemId(Integer orderItemId);
}
