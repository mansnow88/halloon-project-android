package com.halloon.android.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.halloon.android.api.FavAPI;
import com.halloon.android.api.LBSAPI;
import com.halloon.android.api.OtherAPI;
import com.halloon.android.api.PrivateMessageAPI;
import com.halloon.android.api.UserUpdateAPI;
import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.bean.UserBean;
import com.halloon.android.util.Constants;
import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;

public class ContentManager {
	private static ContentManager instance;
	private Context context;
	// private OAuthV1 preoauth;
	private OAuthV2 preoauth;
	private String[] sexType = { " ", "男", "女" };

	private static int REQNUM = 30;

	private ContentManager(Context context) {
		this.context = context;
		// preoauth = new OAuthV1();
		// preoauth.setOauthConsumerKey(Constants.CONSUMER_KEY);
		// preoauth.setOauthConsumerSecret(Constants.CONSUMER_SECRET);

		// preoauth.setOauthToken(SettingsManager.getInstance(context).getRequestToken());
		// preoauth.setOauthTokenSecret(SettingsManager.getInstance(context).getRequestTokenSecret());

		preoauth = (OAuthV2) ((Activity) context).getIntent().getSerializableExtra("oauth");

	}

	public static ContentManager getInstance(Context context) {
		if (instance == null) {
			instance = new ContentManager(context);
		}
		return instance;
	}

	//获取 通讯录
	public ArrayList<UserBean> getContacts() {
		ArrayList<UserBean> beans = new ArrayList<UserBean>();

		FriendsAPI userAPI = new FriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int page = 1;
		while (true) {
			try {
				String response = userAPI.idollist(preoauth, "json", REQNUM
						+ "", REQNUM * (page - 1) + "", "");
				// String response = userAPI.fanslist(preoauth, "json", REQNUM +
				// "", REQNUM * (page - 1) + "", "", "0");

				JSONObject jsonObject = new JSONObject(response);
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				JSONArray jsonArray = dataJsonObject.getJSONArray("info");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jObject = jsonArray.getJSONObject(i);
					/* if (jObject.getBoolean("isidol")) { */
					UserBean userBean = new UserBean();
					userBean.setOpenId(jObject.getString("openid"));
					userBean.setName(jObject.getString("name"));
					userBean.setNick(jObject.getString("nick"));
					userBean.setHead(jObject.getString("head"));
					userBean.setSex(sexType[jObject.getInt("sex")]);

					JSONArray tweetArray = jObject.getJSONArray("tweet");
					for (int j = 0; j < tweetArray.length(); j++) {
						JSONObject tweetObject = tweetArray.getJSONObject(j);
						TweetBean tweetBean = new TweetBean();
						tweetBean.setId(tweetObject.getString("id"));
						tweetBean.setText(tweetObject.getString("text"));
						tweetBean.setFrom(tweetObject.getString("from"));
						tweetBean.setTimestamp(tweetObject
								.getString("timestamp"));
						userBean.setTweetBean(tweetBean);
					}
					beans.add(userBean);
					// }

				}
			} catch (Exception e) {
				Log.d(Constants.LOG_TAG, "GET_CONTACTS_ERROR:" + e);
				return beans;
			}
			page++;
		}

	}

	//获取他人资料
	public ProfileBean getOtherProfile(String name, String id) {
		ProfileBean profileBean = new ProfileBean();
		UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);

		try {
			String otherInfo = userAPI.otherInfo(preoauth, "json", name, id);

			JSONObject jsonObject = new JSONObject(otherInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");

			profileBean.setNick(dataJsonObject.getString("nick"));
			profileBean.setName(dataJsonObject.getString("name"));
			profileBean.setHead(dataJsonObject.getString("head"));
			profileBean.setSex(sexType[dataJsonObject.getInt("sex")]);
			profileBean.setOpenId(dataJsonObject.getString("openid"));
			profileBean.setIntroduction(dataJsonObject.getString("introduction"));
			profileBean.setLocation(dataJsonObject.getString("location"));
			profileBean.setTweetNum(dataJsonObject.getString("tweetnum"));
			profileBean.setFansNum(dataJsonObject.getString("fansnum"));
			profileBean.setFavNum(dataJsonObject.getString("favnum"));
			profileBean.setIdolNum(dataJsonObject.getString("idolnum"));

			JSONArray tagArray = dataJsonObject.optJSONArray("tag");
			String temp_tag = "";
			for (int i = 0; i < tagArray.length(); i++) {
				JSONObject tagObject = tagArray.getJSONObject(i);
				temp_tag += tagObject.getString("name") + " ";
			}
			profileBean.setTag(temp_tag);

			TweetBean tweetBean = new TweetBean();
			JSONArray tweetArray = dataJsonObject.getJSONArray("tweetinfo");
			JSONObject tweetJsonObject = tweetArray.getJSONObject(0);

			tweetBean.setText(tweetJsonObject.getString("text"));
			tweetBean.setId(tweetJsonObject.getString("id"));
			tweetBean.setFrom(tweetJsonObject.getString("from"));
			tweetBean.setTimestamp(tweetJsonObject.getString("timestamp"));

			profileBean.setTweetBean(tweetBean);

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_OTHER_INFO_ERROR" + e);
		}

		return profileBean;
	}

	//获取本人资料
	public ProfileBean getMyProfile() {
		ProfileBean profileBean = new ProfileBean();
		UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);

		try {
			String myInfo = userAPI.info(preoauth, "json");

			JSONObject jsonObject = new JSONObject(myInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");

			profileBean.setNick(dataJsonObject.getString("nick"));
			profileBean.setName(dataJsonObject.getString("name"));
			profileBean.setHead(dataJsonObject.getString("head"));
			profileBean.setSex(sexType[dataJsonObject.getInt("sex")]);
			profileBean.setOpenId(dataJsonObject.getString("openid"));
			profileBean.setIntroduction(dataJsonObject
					.getString("introduction"));
			profileBean.setLocation(dataJsonObject.getString("location"));
			profileBean.setTweetNum(dataJsonObject.getString("tweetnum"));
			profileBean.setFansNum(dataJsonObject.getString("fansnum"));
			profileBean.setFavNum(dataJsonObject.getString("favnum"));
			profileBean.setIdolNum(dataJsonObject.getString("idolnum"));

			JSONArray tagArray = dataJsonObject.optJSONArray("tag");
			String temp_tag = "";
			for (int i = 0; i < tagArray.length(); i++) {
				JSONObject tagObject = tagArray.getJSONObject(i);
				temp_tag += tagObject.getString("name") + " ";
			}
			profileBean.setTag(temp_tag);

			TweetBean tweetBean = new TweetBean();
			JSONArray tweetArray = dataJsonObject.getJSONArray("tweetinfo");
			JSONObject tweetJsonObject = tweetArray.getJSONObject(0);

			tweetBean.setText(tweetJsonObject.getString("text"));
			tweetBean.setId(tweetJsonObject.getString("id"));
			tweetBean.setFrom(tweetJsonObject.getString("from"));
			tweetBean.setTimestamp(tweetJsonObject.getString("timestamp"));

			profileBean.setTweetBean(tweetBean);

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PROFILE_ERROR:" + e);
		}

		return profileBean;
	}
	
	public int[] updateProfile(String nick, String sex, String year, String month, String day, String countryCode, String provinceCode, String cityCode, String introduction){
		UserUpdateAPI userUpdateApi = new UserUpdateAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInt = new int[2];
		try{
			JSONObject feedBack = new JSONObject(userUpdateApi.updateProfile(preoauth, "json", nick, sex, year, month, day, countryCode, provinceCode, cityCode, introduction));
			
			returnInt[0] = feedBack.getInt("ret");
			returnInt[1] = feedBack.getInt("errcode");
		}catch(Exception e){
			Log.d(Constants.LOG_TAG, "UPDATE_PROFILE_ERROR:" + e);
		}
		
		return returnInt;
	}
	
	public int[] updateEducation(String fieldId, String year, String schoolId, String departmentId, String level){
		UserUpdateAPI userUpdateApi = new UserUpdateAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInt = new int[2];
		try{
			JSONObject feedBack = new JSONObject(userUpdateApi.updateEducation(preoauth, "json", fieldId, year, schoolId, departmentId, level));
			
			returnInt[0] = feedBack.getInt("ret");
			returnInt[1] = feedBack.getInt("errcode");
		}catch(Exception e){
			Log.d(Constants.LOG_TAG, "UPDATE_EDUCATION_ERROR:" + e);
		}
		
		return returnInt;
	}
	
	public int[] updateHead(String pic){
		UserUpdateAPI userUpdateApi = new UserUpdateAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInt = new int[2];
		try{
			JSONObject feedBack = new JSONObject(userUpdateApi.updateHead(preoauth, "json", pic));
			
			returnInt[0] = feedBack.getInt("ret");
			returnInt[1] = feedBack.getInt("errcode");
		}catch(Exception e){
			Log.d(Constants.LOG_TAG, "UPDATE_HEAD_ERROR:" + e);
		}
		
		return returnInt;
	}

	/**
	 * 获取主页时间线
	 * 
	 * @param pageFlag
	 *            分页标识 (0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页起始时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70)
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，则type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 *            建议不使用contentType为1的类型，如果要拉取只有文本的微博，建议使用0x80
	 * @return
	 */
	public ArrayList<TweetBean> getHomeTimeLineTweet(String pageFlag,
			String pageTime, int requestNum, String type, String contentType) {
		StatusesAPI statusesAPI = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.homeTimeline(preoauth, "json", pageFlag,
					pageTime, requestNum + "", type, contentType);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			JSONObject userInfoObject = dataJsonObject.optJSONObject("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				TweetBean tb = getTweetFromJSON(tweetInfoObject);
				tb.setMentionedUser(userInfoObject);
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_HOME_TIME_LINE_TWEET_ERROR:" + e);
		}

		return tweetContainer;

	}

	/**
	 * 获取个人时间线
	 * 
	 * @param pageFlag
	 *            分页标识 (0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页起始时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-200)
	 * @param lastId
	 *            和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，则type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤 填零表示所有类型 1-带文本 2-带链接 4图片 8-带视频 0x10-带音频
	 * @return
	 */
	public ArrayList<TweetBean> getBroadcastTimeLine(String pageFlag,
			String pageTime, int requestNum, String lastId, String type,
			String contentType) {
		StatusesAPI statusesAPI = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.broadcastTimeline(preoauth, "json",
					pageFlag, pageTime, requestNum + "", lastId, type,
					contentType);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			JSONObject userInfoObject = dataJsonObject.optJSONObject("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				TweetBean tb = getTweetFromJSON(tweetInfoObject);
				tb.setMentionedUser(userInfoObject);
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_BROADCAST_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}

	/**
	 * 获取他人微博时间线
	 * 
	 * @param pageFlag
	 *            分页标识(0x0:第一页,0x1向下翻页,0x2向上翻页)
	 * @param pageTime
	 *            本页其实时间(第一页填:0x0,向上翻页:填上一次请求返回的第一条记录时间,向下翻页:填上一次请求返回的最后一条记录时间)
	 * @param requestNum
	 *            每次请求的记录条数(1-70);
	 * @param lastId
	 *            和pagetime配合使用(第一页:填0,向上翻页:填上一次请求返回的第一条记录id,向下翻页:
	 *            填上一次请求返回的最后一条记录id)
	 * @param name
	 *            你需要赌球的用户的用户名
	 * @param fopenId
	 *            你需要读取的用户ID name和id至少选一个 如果同时存在则以name为主
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 * @return
	 */
	public ArrayList<TweetBean> getOtherTimeLine(String pageFlag,
			String pageTime, int requestNum, String lastId, String name,
			String fopenId, String type, String contentType) {
		StatusesAPI statusesAPI = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetContainer = new ArrayList<TweetBean>(
				requestNum);
		try {
			String tweetInfo;
			tweetInfo = statusesAPI.userTimeline(preoauth, "json", pageFlag,
					pageTime, requestNum + "", lastId, name, fopenId, type,
					contentType);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray tweetInfoArray = dataJsonObject.getJSONArray("info");
			JSONObject userInfoObject = dataJsonObject.optJSONObject("user");
			for (int i = 0; i < tweetInfoArray.length(); i++) {
				JSONObject tweetInfoObject = tweetInfoArray.getJSONObject(i);
				TweetBean tb = getTweetFromJSON(tweetInfoObject);
				tb.setMentionedUser(userInfoObject);
				tweetContainer.add(tb);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_USER_TIMELINE_ERROR:" + e);
		}

		return tweetContainer;
	}

	/**
	 * 从微博唯一ID获取单条微博
	 * 
	 * @param id
	 *            微博唯一ID
	 * @return TweetBean
	 */
	public TweetBean getTweetFromId(String id) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		TweetBean temp_tweet = new TweetBean();

		try {
			String tweetInfo = tApi.show(preoauth, "json", id);
			Log.d(Constants.LOG_TAG, tweetInfo);

			JSONObject jsonObject = new JSONObject(tweetInfo);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONObject userInfoObject = dataJsonObject.optJSONObject("user");
			temp_tweet = getTweetFromJSON(dataJsonObject);
			temp_tweet.setMentionedUser(userInfoObject);
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_TWEET_FROM_ID_ERROR:" + e);
		}

		return temp_tweet;
	}

	/**
	 * 从微博唯一ID获取回复或者转播
	 * 
	 * @param id
	 * @return
	 */
	public ArrayList<TweetBean> getTweetCommentFromId(String id, String type, String pageFlag, String pageTime, String requestNum) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> commentTweetBean = new ArrayList<TweetBean>();
		try {
			String commentInfo = tApi.reList(preoauth, "json", type, id, pageFlag, pageTime, requestNum, "0");

			JSONObject jsonObject = new JSONObject(commentInfo);
			if (jsonObject.getString("msg") == "have no tweet") {
				return null;
			} else {
				JSONObject dataJsonObject = jsonObject.getJSONObject("data");
				JSONArray commentInfoArray = dataJsonObject.getJSONArray("info");
				JSONObject userInfoObject = dataJsonObject.optJSONObject("user");
				for (int i = 0; i < commentInfoArray.length(); i++) {
					TweetBean temp_comment = new TweetBean();
					JSONObject commentInfoObject = commentInfoArray.getJSONObject(i);
					temp_comment.setNick(commentInfoObject.getString("nick"));
					temp_comment.setName(commentInfoObject.getString("name"));
					temp_comment.setHead(commentInfoObject.getString("head"));
					temp_comment.setText(commentInfoObject.getString("text"));
					temp_comment.setTimestamp(commentInfoObject.getString("timestamp"));
					temp_comment.setTweetImage(commentInfoObject.optJSONArray("image"));
					temp_comment.setFrom(commentInfoObject.getString("from"));
					temp_comment.setId(commentInfoObject.getString("id"));
					temp_comment.setOpenId(commentInfoObject.getString("openid"));
					temp_comment.setMentionedUser(userInfoObject);
					Log.i(Constants.LOG_TAG, temp_comment.getMentionedUser().toString());
					if (commentInfoObject.optJSONObject("source") != null) {
						TweetBean temp_source = new TweetBean();
						JSONObject temp_source_object = commentInfoObject.getJSONObject("source");
						temp_source.setId(temp_source_object.getString("id"));
						temp_source.setOpenId(temp_source_object.getString("openid"));
						temp_source.setText(temp_source_object.getString("text"));
						temp_source.setHead(temp_source_object.getString("head"));
						temp_source.setNick(temp_source_object.getString("nick"));
						temp_source.setName(temp_source_object.getString("name"));
						temp_source.setTimestamp(temp_source_object.getString("timestamp"));
						temp_source.setTweetImage(temp_source_object.optJSONArray("image"));
						temp_source.setFrom(temp_source_object.getString("from"));
						temp_source.setMentionedUser(userInfoObject);
						temp_comment.setSource(temp_source);
					}
					commentTweetBean.add(temp_comment);
				}
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_TWEET_COMMENT_ERROR:" + e);
		}

		return commentTweetBean;

	}

	private TweetBean getTweetFromJSON(JSONObject tweetInfoObject) {
		TweetBean temp_tweet = new TweetBean();

		try {
			temp_tweet.setOpenId(tweetInfoObject.getString("openid"));
			temp_tweet.setId(tweetInfoObject.getString("id"));
			temp_tweet.setHead(tweetInfoObject.getString("head"));
			temp_tweet.setNick(tweetInfoObject.getString("nick"));
			temp_tweet.setName(tweetInfoObject.getString("name"));
			temp_tweet.setText(tweetInfoObject.getString("text"));
			temp_tweet.setTweetImage(tweetInfoObject.optJSONArray("image"));
			temp_tweet.setLongitude(tweetInfoObject.optString("longitude"));
			temp_tweet.setLatitude(tweetInfoObject.optString("latitude"));
			System.out.println(temp_tweet.getLongitude() + ","
					+ temp_tweet.getLatitude());
			temp_tweet.setGeo(tweetInfoObject.getString("geo"));

			JSONObject videoObject = tweetInfoObject.optJSONObject("video");
			if (videoObject != null) {
				temp_tweet.setVideoImage(videoObject.getString("picurl"));
				temp_tweet.setVideoPlayer(videoObject.getString("player"));
				temp_tweet.setVideoUrl(videoObject.getString("shorturl"));
			}

			JSONObject sourceTweetObject = tweetInfoObject
					.optJSONObject("source");
			if (sourceTweetObject != null && sourceTweetObject.length() > 0) {
				TweetBean sourceTweetBean = new TweetBean();

				sourceTweetBean.setHead(sourceTweetObject.getString("head"));
				sourceTweetBean.setNick(sourceTweetObject.getString("nick"));
				sourceTweetBean.setName(sourceTweetObject.getString("name"));
				sourceTweetBean.setText(sourceTweetObject.getString("text"));
				sourceTweetBean.setTweetImage(sourceTweetObject
						.optJSONArray("image"));
				sourceTweetBean.setLongitude(sourceTweetObject
						.optString("longitude"));
				sourceTweetBean.setLatitude(sourceTweetObject
						.optString("latitude"));
				System.out.println(sourceTweetBean.getLongitude() + ","
						+ sourceTweetBean.getLatitude());
				sourceTweetBean.setGeo(sourceTweetObject.getString("geo"));

				JSONObject sourceVideoObject = sourceTweetObject
						.optJSONObject("video");
				if (sourceVideoObject != null) {
					sourceTweetBean.setVideoImage(sourceVideoObject
							.getString("picurl"));
					sourceTweetBean.setVideoPlayer(sourceVideoObject
							.getString("player"));
					sourceTweetBean.setVideoUrl(sourceVideoObject
							.getString("shorturl"));
				}

				temp_tweet.setSource(sourceTweetBean);
			} else {
				temp_tweet.setSource(null);
			}

			temp_tweet.setId(tweetInfoObject.getString("id"));
			temp_tweet.setFrom(tweetInfoObject.getString("from"));
			temp_tweet.setTimestamp(tweetInfoObject.getString("timestamp"));
			temp_tweet.setCount(tweetInfoObject.getString("count"));
			temp_tweet.setMCount(tweetInfoObject.getString("mcount"));
			temp_tweet.setIsVip(tweetInfoObject.getInt("isvip"));

		} catch (JSONException e) {
			Log.d(Constants.LOG_TAG, "GET_TWEET_FROM_JSON_ERROR:" + e);
		}

		return temp_tweet;

	}

	/**
	 * 获取附近的人
	 * 
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param pageinfo
	 *            翻页参数,由上次请求返回(第一次请求时请填空)
	 * @param num 人数
	 * @param gender 性别, 0x0全部, 0x1男, 0x2女
	 * @return
	 */
	public ArrayList<UserBean> getAroundPeople(String longitude, String latitude, String pageinfo, String num, String gender) {
		LBSAPI lbsApi = new LBSAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<UserBean> userBeans = new ArrayList<UserBean>();

		try {
			String lbsAround = lbsApi.getAroundPeople(preoauth, "json", longitude, latitude, pageinfo, num, gender);
			JSONObject jsonObject = new JSONObject(lbsAround);
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			JSONArray userInfoArray = dataJsonObject.getJSONArray("info");
			for (int i = 0; i < userInfoArray.length(); i++) {
				UserBean userBean = new UserBean();

				JSONObject userInfoObject = userInfoArray.getJSONObject(i);
				userBean.setHead(userInfoObject.getString("head"));
				userBean.setName(userInfoObject.getString("name"));
				userBean.setNick(userInfoObject.getString("nick"));
				userBean.setOpenId(userInfoObject.getString("openid"));
				userBean.setSex(sexType[Integer.valueOf(gender)]);

				userBeans.add(userBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_AROUND_PEOPLE_ERROR:" + e);
		}

		return userBeans;
	}

	/**
	 * 发布纯文字微博
	 * 
	 * @param text
	 * @param ip
	 */
	public int[] addText(String text, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.add(preoauth, "json",
					text, ip));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "TEXT_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 发布带经纬度微博
	 * 
	 * @param text
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @param syncFlag
	 *            微博同步到空间分享标记（可选，0-同步，1-不同步，默认为0）
	 */
	public int[] addText(String text, String ip, String longitude,
			String latitude, String syncFlag) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.add(preoauth, "json",
					text, ip, longitude, latitude, syncFlag));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_TEXT_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 发布图片微博
	 * 
	 * @param text
	 * @param imagePath
	 * @param ip
	 */
	public int[] addImageTweet(String text, String imagePath, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.addPic(preoauth, "json",
					text, ip, imagePath));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "IMAGE_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 发布带经纬度 图片微博
	 * 
	 * @param text
	 * @param imagePath
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @param syncFlag
	 *            微博同步到空间分享标记（可选，0-同步，1-不同步，默认为0）
	 */
	public int[] addImageTweet(String text, String imagePath, String ip,
			String longitude, String latitude, String syncFlag) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.addPic(preoauth, "json",
					text, ip, longitude, latitude, imagePath, syncFlag));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_IMAGE_TWEET_PUBLISH_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 删除单条微博
	 * 
	 * @param id
	 *            微博ID
	 */
	public int[] delTweet(String id) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.del(preoauth, "json", id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "DEL_TWEET_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 回复微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @return
	 */
	public int[] comment(String content, String id, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.comment(preoauth, "json",
					content, ip, id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "COMMENT_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 回复带经纬度微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public int[] comment(String content, String id, String ip,
			Double longitude, Double latitude) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.comment(preoauth, "json",
					content, ip, String.valueOf(longitude),
					String.valueOf(latitude), id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_COMMENT_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 转发微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @return
	 */
	public int[] retweet(String content, String id, String ip) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.reAdd(preoauth, "json",
					content, ip, id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "RETWEET_ERROR:" + e);
		}

		return returnInts;
	}

	/**
	 * 转发带经纬度微博
	 * 
	 * @param content
	 * @param id
	 * @param ip
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public int[] retweet(String content, String id, String ip,
			Double longitude, Double latitude) {
		TAPI tApi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try {
			JSONObject feedBack = new JSONObject(tApi.reAdd(preoauth, "json",
					content, ip, String.valueOf(longitude),
					String.valueOf(latitude), id));
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "LOCATION_RETWEET_ERROR:" + e);
		}

		return returnInts;
	}
	
	public int[] addFav(String id){
		FavAPI favApi = new FavAPI(OAuthConstants.OAUTH_VERSION_2_A);
		int[] returnInts = new int[2];
		try{
			JSONObject feedBack = new JSONObject(favApi.addFav(preoauth, "json", id));
			
			returnInts[0] = feedBack.getInt("ret");
			returnInts[1] = feedBack.getInt("errcode");
		}catch(Exception e){
			Log.d(Constants.LOG_TAG, "ADD_FAV_ERROR:" + e);
		}
		
		return returnInts;
	}
	
	/**
	 *  获取未读计数
	 * @param op 请求的方式 0：仅获取数据更新的条数 1.后去完毕后将相应的技术清零
	 * @param type 需获取的更新数据的类型 5：首页未读消息计数 6.@页未读消息计数 7.私信页未读消息计数 8.新增听众数 9。首页新增的原创广播数 op=0时不输入type 返回所有类型计数 op=1时需输入type 返回所有类型计数 同时清除该type类型的技术
	 * @return
	 */
	public HashMap<String, String> getUnreadCount(String op, String type){
		OtherAPI otherApi = new OtherAPI(OAuthConstants.OAUTH_VERSION_2_A);
		HashMap<String, String> tmpHash = new HashMap<String, String>();
		try{
			String count = otherApi.getUpdateCount(preoauth, "json", op, type);
			JSONObject countJson = new JSONObject(count);
			JSONObject data = countJson.getJSONObject("data");
			tmpHash.put("home", data.getString("home"));
			tmpHash.put("private", data.getString("private"));
			tmpHash.put("fans", data.getString("fans"));
			tmpHash.put("mentions",  data.getString("mentions"));
			tmpHash.put("create",  data.getString("create"));
		}catch(Exception e){
			Log.d(Constants.LOG_TAG, "GET_COUNT_ERROR:" + e);
		}
		
		return tmpHash;
	}

	/**
	 * 获取私信的首页会话列表，包括未读信息数、会话人账号等信息。
	 * 
	 * @param pageFlag
	 *            分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pageTime
	 *            本页起始时间，用于翻页，和pageflag、lastid配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，
	 *            向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param requestNum
	 *            每次请求记录的条数（最多70条）
	 * @param lastId
	 *            用于翻页，和pageflag、pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @return
	 */
	public ArrayList<PrivateDataBean> getPrivateHomeTimeLine(String pageFlag, String pageTime, String requestNum, String lastId) {
		PrivateMessageAPI privateApi = new PrivateMessageAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<PrivateDataBean> privateDataBeans = new ArrayList<PrivateDataBean>();
		try {
			String privateHomeTimeLine = privateApi.homeTimeline(preoauth,
					"json", pageFlag, pageTime, requestNum, lastId);
			JSONObject privateMessageJsonObject = new JSONObject(
					privateHomeTimeLine);
			JSONObject dataJsonObject = privateMessageJsonObject
					.getJSONObject("data");
			JSONArray privateInfoArray = dataJsonObject.getJSONArray("info");
			for (int i = 0; i < privateInfoArray.length(); i++) {
				JSONObject privateDataObject = privateInfoArray
						.getJSONObject(i);
				PrivateDataBean privateDataBean = new PrivateDataBean();
				privateDataBean.setHead(privateDataObject.getString("tohead"));
				privateDataBean.setName(privateDataObject.getString("toname"));
				privateDataBean.setNick(privateDataObject.getString("tonick"));
				privateDataBean.setImage(privateDataObject.optJSONArray("image"));
				privateDataBean.setIsVip(privateDataObject.getInt("toisvip"));
				privateDataBean.setTotalCount(privateDataObject.getString("totalcnt"));
				privateDataBean.setUnReadCount(privateDataObject.getString("unreadcnt"));
				privateDataBean.setOpenId(privateDataObject.getString("toopenid"));
				privateDataBean.setTweeted(privateDataObject.getString("tweetid"));
				privateDataBean.setPubTime(privateDataObject.getString("pubtime"));
				privateDataBean.setMsgBox(privateDataObject.getInt("msgbox"));
				privateDataBean.setReadFlag(privateDataObject.getInt("readflag"));
				privateDataBean.setRoomId(privateDataObject.optString("roomid"));
				privateDataBean.setText(privateDataObject.getString("text"));

				privateDataBeans.add(privateDataBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_PRIVATE_HOME_TIME_LINE_ERROR:" + e);
		}

		return privateDataBeans;
	}
	
	public ArrayList<PrivateDataBean> getPrivateConversation(String pageFlag, String pageTime, String requestNum, String lastId, String name, String fopenId){
		PrivateMessageAPI privateApi = new PrivateMessageAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<PrivateDataBean> privateDataBeans = new ArrayList<PrivateDataBean>();
		try{
			String privateConversation = privateApi.userTimeline(preoauth, "json", pageFlag, pageTime, requestNum, lastId, name, fopenId);
			JSONObject privateConversationJsonObject = new JSONObject(privateConversation);
			JSONObject dataJsonObject = privateConversationJsonObject.getJSONObject("data");
			
            
			
			JSONArray privateInfoArray = dataJsonObject.getJSONArray("info");
			
			for(int i = 0; i < privateInfoArray.length(); i++){
				JSONObject privateDataObject = privateInfoArray.getJSONObject(i);
				PrivateDataBean privateDataBean = new PrivateDataBean();
				
				privateDataBean.setHead(privateConversationJsonObject.getString("tohead"));
				privateDataBean.setMyHead(privateConversationJsonObject.getString("head"));
				privateDataBean.setName(privateConversationJsonObject.getString("toname"));
				privateDataBean.setNick(privateConversationJsonObject.getString("tonick"));
				privateDataBean.setOpenId(privateConversationJsonObject.getString("toopenid"));
				privateDataBean.setIsVip(privateConversationJsonObject.getInt("isvip"));
				
				privateDataBean.setPubTime(privateDataObject.getString("pubtime"));
				privateDataBean.setMsgBox(privateDataObject.getInt("msgbox"));
				privateDataBean.setText(privateDataObject.getString("text"));
				privateDataBean.setTweeted(privateDataObject.getString("tweetid"));
				privateDataBean.setReadFlag(privateDataObject.getInt("readflag"));
				
				privateDataBeans.add(0, privateDataBean);
			}
			
		}catch(Exception e){
			Log.d(Constants.LOG_TAG, "GET_PRIVATE_USER_TIME_LINE_ERROR:" + e);
		}
		
		return privateDataBeans;
	}

	/**
	 * 用户提及时间线
	 * 
	 * @param pageFlag
	 *            分页标识（0：第一页，1：向下翻页，2向上翻页）
	 * @param pageTime
	 *            本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param requestNum
	 *            每次请求记录的条数（1-100条）
	 * @param lastId
	 *            和pagetime配合使用（第一页：填0，向上翻页：填上一次请求返回的第一条记录id，向下翻页：
	 *            填上一次请求返回的最后一条记录id）
	 * @param type
	 *            拉取类型 0x1 原创发表 0x2 转载 0x8 回复 0x10 空回 0x20 提及 0x40 点评
	 *            如需拉取多个类型请使用|，如(0x1|0x2)得到3，此时type=3即可，填零表示拉取所有类型
	 * @param contentType
	 *            内容过滤。0-表示所有类型，1-带文本，2-带链接，4-带图片，8-带视频，0x10-带音频
	 * @return
	 */
	public ArrayList<TweetBean> getMentionsHomeTimeLine(String pageFlag,
			String pageTime, String requestNum, String lastId, String type,
			String contentType) {
		StatusesAPI statusesApi = new StatusesAPI(
				OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetBeans = new ArrayList<TweetBean>();
		try {
			String mentionsHomeTimeLine = statusesApi.mentionsTimeline(
					preoauth, "json", pageFlag, pageTime, requestNum, lastId,
					type, contentType);
			JSONObject mentionsJsonObject = new JSONObject(mentionsHomeTimeLine);
			JSONObject dataJsonObject = mentionsJsonObject
					.getJSONObject("data");
			JSONArray mentionsJsonArray = dataJsonObject.getJSONArray("info");
			JSONObject userJsonObject = dataJsonObject.optJSONObject("user");
			for (int i = 0; i < mentionsJsonArray.length(); i++) {
				TweetBean tweetBean = new TweetBean();
				JSONObject mentionsObject = mentionsJsonArray.getJSONObject(i);
				tweetBean = getTweetFromJSON(mentionsObject);
				tweetBean.setMentionedUser(userJsonObject);

				tweetBeans.add(tweetBean);
			}

		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_MENTIONS_HOME_LINE_ERROR:" + e);
		}

		return tweetBeans;
	}

	/**
	 * 获取周边微博
	 * 
	 * @param longitude
	 * @param latitude
	 * @param pageInfo
	 *            翻页参数,由上次请求返回(第一次请求时请填空)
	 * @param pageSize
	 *            请求的每页个数(1-50),建议25
	 * @return
	 */
	public ArrayList<TweetBean> getAroundTweet(String longitude,
			String latitude, String pageInfo, String pageSize) {
		LBSAPI LBSApi = new LBSAPI(OAuthConstants.OAUTH_VERSION_2_A);
		ArrayList<TweetBean> tweetBeans = new ArrayList<TweetBean>();
		try {
			String aroundTweet = LBSApi.getAroundNew(preoauth, "json",
					longitude, latitude, pageInfo, pageSize);
			JSONObject aroundTweetJsonObject = new JSONObject(aroundTweet);
			JSONObject dataJsonObject = aroundTweetJsonObject
					.getJSONObject("data");
			JSONArray aroundJsonArray = dataJsonObject.getJSONArray("info");
			JSONObject userJsonObject = dataJsonObject.optJSONObject("user");
			for (int i = 0; i < aroundJsonArray.length(); i++) {
				TweetBean tweetBean = new TweetBean();
				JSONObject aroundObject = aroundJsonArray.getJSONObject(i);
				tweetBean = getTweetFromJSON(aroundObject);
				tweetBean.setMentionedUser(userJsonObject);

				tweetBeans.add(tweetBean);
			}
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "GET_AROUND_NEW_ERROR:" + e);
		}

		return tweetBeans;
	}
}
