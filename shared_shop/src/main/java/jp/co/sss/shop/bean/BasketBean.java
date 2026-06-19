package jp.co.sss.shop.bean;

import jp.co.sss.shop.entity.Coupon;

/**
 * 買い物かご内の商品情報クラス
 *
 * @author SystemShared
 */

public class BasketBean {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 商品在庫数
	 */
	private Integer stock;

	/**
	 * 商品注文個数 初期値 1
	 */
	private Integer orderNum = 1;

	/**
	 * コンストラクタ
	 */
	public BasketBean() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param id  商品ID
	 * @param name  商品名
	 * @param stock 商品在庫数
	 */
	public BasketBean(Integer id, String name, Integer stock) {
		this.id = id;
		this.name = name;
		this.stock = stock;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param id  商品ID
	 * @param name  商品名
	 * @param stock  商品在庫数
	 * @param orderNum  注文個数
	 */
	public BasketBean(Integer id, String name, Integer stock, Integer orderNum) {
		this.id = id;
		this.name = name;
		this.stock = stock;
		this.orderNum = orderNum;
	}

	/**
	 * 商品IDの取得
	 * @return 商品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 商品IDのセット
	 * @param id 商品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 商品名の取得
	 * @return 商品名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 商品名のセット
	 * @param name 商品名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 商品の在庫数の取得
	 * @return 在庫数
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * 商品の在庫数のセット
	 * @param stock 在庫数
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	/**
	 * 買い物かごに入れている商品個数の取得
	 * @return 注文個数
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	/**
	 * 適用されたクーポン
	 */
	private Coupon appliedCoupon;

	/**
	 * 割引額
	 */
	private Integer discountAmount;

	/**
	 * 買い物かごに入れる商品個数のセット
	 * @param orderNum 注文個数
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 適用されたクーポンの取得
	 * @return 適用されたクーポン
	 */
	public Coupon getAppliedCoupon() {
		return appliedCoupon;
	}

	/**
	 * 適用されたクーポンのセット
	 * @param appliedCoupon 適用されたクーポン
	 */
	public void setAppliedCoupon(Coupon appliedCoupon) {
		this.appliedCoupon = appliedCoupon;
	}

	/**
	 * 割引額の取得
	 * @return 割引額
	 */
	public Integer getDiscountAmount() {
		return discountAmount;
	}

	/**
	 * 割引額のセット
	 * @param discountAmount 割引額
	 */
	public void setDiscountAmount(Integer discountAmount) {
		this.discountAmount = discountAmount;
	}

}
