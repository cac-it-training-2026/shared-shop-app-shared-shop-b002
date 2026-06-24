package jp.co.sss.shop.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.OrderItem;

/**
 * order_itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	/**
	 * 商品IDと注文日（指定日以降）を条件に注文合計数量を検索
	 * @param itemId 商品ID
	 * @param date 指定日
	 * @return 注文合計数量
	 */
	@Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.item.id = :itemId AND oi.order.insertDate > :date")
	Long countQuantityByItemIdAndOrderInsertDateAfter(@Param("itemId") Integer itemId, @Param("date") Date date);

	/**
	 * 複数の商品IDと注文日（指定日以降）を条件に各商品の注文合計数量を検索
	 * @param itemIds 商品IDのリスト
	 * @param date 指定日
	 * @return 各商品のIDと注文合計数量のリスト
	 */
	@Query("SELECT oi.item.id, SUM(oi.quantity) FROM OrderItem oi WHERE oi.item.id IN :itemIds AND oi.order.insertDate > :date GROUP BY oi.item.id")
	List<Object[]> countQuantitiesByItemIdsAndOrderInsertDateAfter(@Param("itemIds") List<Integer> itemIds, @Param("date") Date date);
}
