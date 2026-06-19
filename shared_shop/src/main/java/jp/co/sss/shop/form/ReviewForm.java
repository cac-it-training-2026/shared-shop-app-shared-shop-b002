package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * レビュー投稿用のフォームクラス
 */
public class ReviewForm implements Serializable {

	/**
	 * 商品ID
	 */
	private Integer itemId;

	/**
	 * 評価 (1-5)
	 */
	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;

	/**
	 * タイトル
	 */
	@NotBlank
	@Size(max = 50)
	private String title;

	/**
	 * 内容
	 */
	@NotBlank
	@Size(max = 200)
	private String content;

	/**
	 * 商品ID取得
	 * @return 商品ID
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * 商品IDセット
	 * @param itemId 商品ID
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 * 評価取得
	 * @return 評価
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * 評価セット
	 * @param rating 評価
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * タイトル取得
	 * @return タイトル
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * タイトルセット
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 内容取得
	 * @return 内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 内容セット
	 * @param content 内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
}
