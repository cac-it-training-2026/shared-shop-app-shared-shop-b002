package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Coupon;

/**
 * couponテーブル用リポジトリ
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

	/**
	 * クーポンコードと有効フラグを指定してクーポン情報を検索する
	 * @param couponCode クーポンコード
	 * @param enabled 有効フラグ
	 * @return クーポン情報
	 */
	Coupon findByCouponCodeAndEnabled(String couponCode, Integer enabled);
}
