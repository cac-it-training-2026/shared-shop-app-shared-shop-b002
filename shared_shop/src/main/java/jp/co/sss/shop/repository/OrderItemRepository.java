package jp.co.sss.shop.repository;

import java.sql.Date;

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
}
