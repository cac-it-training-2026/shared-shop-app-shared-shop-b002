package jp.co.sss.shop.bean;

/**
 * レビュー情報クラス
 */
public class ReviewBean {

	/**
	 * レビューID
	 */
	private Integer id;

	/**
	 * 評価 (1-5)
	 */
	private Integer rating;

	/**
	 * タイトル
	 */
	private String title;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 登録日付
	 */
	private String insertDate;

	/**
	 * 投稿者名
	 */
	private String userName;

	/**
	 * レビューID取得
	 * @return レビューID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * レビューIDセット
	 * @param id レビューID
	 */
	public void setId(Integer id) {
		this.id = id;
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

	/**
	 * 登録日付取得
	 * @return 登録日付
	 */
	public String getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付セット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * 投稿者名取得
	 * @return 投稿者名
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 投稿者名セット
	 * @param userName 投稿者名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
