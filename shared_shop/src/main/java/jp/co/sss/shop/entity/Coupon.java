package jp.co.sss.shop.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * クーポン情報のエンティティクラス
 */
@Entity
@Table(name = "coupon")
public class Coupon {
	/**
	 * クーポンID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_coupon_gen")
	@SequenceGenerator(name = "seq_coupon_gen", sequenceName = "seq_coupon", allocationSize = 1)
	private Integer couponId;

	/**
	 * クーポンコード
	 */
	@Column(unique = true, nullable = false)
	private String couponCode;

	/**
	 * クーポン名
	 */
	@Column
	private String couponName;

	/**
	 * 割引種別 (FIXED / PERCENT)
	 */
	@Column(nullable = false)
	private String discountType;

	/**
	 * 割引値
	 */
	@Column(nullable = false)
	private Integer discountValue;

	/**
	 * 利用開始日時
	 */
	@Column
	private Timestamp startDate;

	/**
	 * 利用終了日時
	 */
	@Column
	private Timestamp endDate;

	/**
	 * 有効フラグ (1:有効, 0:無効)
	 */
	@Column(insertable = false)
	private Integer enabled;

	/**
	 * 作成日時
	 */
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	/**
	 * クーポンIDの取得
	 * @return クーポンID
	 */
	public Integer getCouponId() {
		return couponId;
	}

	/**
	 * クーポンIDのセット
	 * @param couponId クーポンID
	 */
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	/**
	 * クーポンコードの取得
	 * @return クーポンコード
	 */
	public String getCouponCode() {
		return couponCode;
	}

	/**
	 * クーポンコードのセット
	 * @param couponCode クーポンコード
	 */
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * クーポン名の取得
	 * @return クーポン名
	 */
	public String getCouponName() {
		return couponName;
	}

	/**
	 * クーポン名のセット
	 * @param couponName クーポン名
	 */
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	/**
	 * 割引種別の取得
	 * @return 割引種別
	 */
	public String getDiscountType() {
		return discountType;
	}

	/**
	 * 割引種別のセット
	 * @param discountType 割引種別
	 */
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	/**
	 * 割引値の取得
	 * @return 割引値
	 */
	public Integer getDiscountValue() {
		return discountValue;
	}

	/**
	 * 割引値のセット
	 * @param discountValue 割引値
	 */
	public void setDiscountValue(Integer discountValue) {
		this.discountValue = discountValue;
	}

	/**
	 * 利用開始日時の取得
	 * @return 利用開始日時
	 */
	public Timestamp getStartDate() {
		return startDate;
	}

	/**
	 * 利用開始日時のセット
	 * @param startDate 利用開始日時
	 */
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * 利用終了日時の取得
	 * @return 利用終了日時
	 */
	public Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * 利用終了日時のセット
	 * @param endDate 利用終了日時
	 */
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	/**
	 * 有効フラグの取得
	 * @return 有効フラグ
	 */
	public Integer getEnabled() {
		return enabled;
	}

	/**
	 * 有効フラグのセット
	 * @param enabled 有効フラグ
	 */
	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	/**
	 * 作成日時の取得
	 * @return 作成日時
	 */
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	/**
	 * 作成日時のセット
	 * @param createdAt 作成日時
	 */
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
