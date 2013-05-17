package com.halloon.android.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.halloon.android.bean.PrivateDataBean;
import com.halloon.android.bean.ProfileBean;
import com.halloon.android.bean.TweetBean;
import com.halloon.android.bean.UserBean;
import com.halloon.android.util.Constants;
import com.lhws.android.uitl.StringMatcher;

public class DBManager {
	private static Context context;
	private static DBManager _instance;
	SQLiteDatabase db;

	//private DatabaseHelper dbHelper;

	private static final int DATABASE_VERSION = 16;

	public final static int CONTACT_STATUS_INIT = 0;
	public final static int CONTACT_STATUS_READY = 1;

	public final static int PROFILE_STATUS_INIT = 0;
	public final static int PROFILE_STATUS_READY = 1;

	public final static int TWEET_LIST_STATUS_INIT = 0;
	public final static int TWEET_LIST_STATUS_READY = 1;

	public final static int PRIVATE_MESSAGE_STATUS_INIT = 0;
	public final static int PRIVATE_MESSAGE_STATUS_READY = 1;

	public final static int AT_LIST_STATUS_INIT = 0;
	public final static int AT_LIST_STATUS_READY = 1;

	// Database Name
	private static final String HALLOON_DATABASE = "halloon_data";

	// Contacts table name
	private static final String TABLE_CONTACTS_ME = "contacts_me";
	private static final String TABLE_MY_PROFILE = "my_profile";
	private static final String TABLE_TWEET_LIST = "tweet_list";
	private static final String TABLE_PRIVATE_LIST = "private_list";
	private static final String TABLE_AT_LIST = "at_list";

	// Contacts Table Columns names
	private static final String KEY_OPEN_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_NICK = "nick";
	private static final String KEY_NAME_FORMATTED = "name_formatted";
	private static final String KEY_HEAD = "head";
	private static final String KEY_SEX = "sex";
	private static final String KEY_TWEET = "tweet";

	private static final String PROFILE_OPEN_ID = "id";
	private static final String PROFILE_NICK = "nick";
	private static final String PROFILE_NAME = "name";
	private static final String PROFILE_HEAD = "head";
	private static final String PROFILE_SEX = "sex";
	private static final String PROFILE_INTRO = "introduction";
	private static final String PROFILE_LOCATION = "location";
	private static final String PROFILE_TAG = "tag";
	private static final String PROFILE_TWEETNUM = "tweet_num";
	private static final String PROFILE_IDOLNUM = "idol_num";
	private static final String PROFILE_FANSNUM = "fans_num";
	private static final String PROFILE_FAVNUM = "fav_num";

	private static final String TWEET_USER_ID = "openid";
	private static final String TWEET_ID = "id";
	private static final String TWEET_HEAD = "head";
	private static final String TWEET_NICK = "nick";
	private static final String TWEET_NAME = "name";
	private static final String TWEET_TIMESTAMP = "timestamp";
	private static final String TWEET_FROM = "send_from";
	private static final String TWEET_CONTENT = "tweet_content";
	private static final String TWEET_IMAGE = "tweet_image";
	private static final String TWEET_VIDEO_IMAGE = "tweet_video_image";
	private static final String TWEET_VIDEO_PLAYER = "tweet_video_player";
	private static final String TWEET_VIDEO_URL = "tweet_video_url";
	private static final String COMMENT_COUNT = "comment_count";
	private static final String R_COUNT = "r_count";
	private static final String SOURCE_NICK = "source_nick";
	private static final String SOURCE_CONTENT = "source_content";
	private static final String SOURCE_IMAGE = "source_image";
	private static final String SOURCE_VIDEO_IMAGE = "source_video_image";
	private static final String SOURCE_VIDEO_PLAYER = "source_video_player";
	private static final String SOURCE_VIDEO_URL = "source_video_url";
	private static final String SOURCE_LONGITUDE = "source_longitude";
	private static final String SOURCE_LATITUDE = "source_latitude";
	private static final String SOURCE_GEO = "source_geo";
	private static final String TWEET_MUSER = "mentioned_user";
	private static final String TWEET_IS_VIP = "is_vip";
	private static final String TWEET_LONGITUDE = "tweet_longitude";
	private static final String TWEET_LATITUDE = "tweet_latitude";
	private static final String TWEET_GEO = "tweet_geo";

	private static final String PRIVATE_ID = "id";
	private static final String PRIVATE_HEAD = "head";
	private static final String PRIVATE_NAME = "name";
	private static final String PRIVATE_NICK = "nick";
	private static final String PRIVATE_CONTENT = "content";
	private static final String PRIVATE_IMAGE = "image";
	private static final String PRIVATE_TIMESTAMP = "timestamp";

	private static final String AT_ID = "id";
	private static final String AT_HEAD = "head";
	private static final String AT_NAME = "name";
	private static final String AT_NICK = "nick";
	private static final String AT_FROM = "at_from";
	private static final String AT_IMAGE = "at_image";
	private static final String AT_VIDEO_IMAGE = "at_video_image";
	private static final String AT_VIDEO_PLAYER = "at_video_player";
	private static final String AT_VIDEO_URL = "at_video_url";
	private static final String AT_TIMESTAMP = "timestamp";
	private static final String AT_CONTENT = "at_content";
	private static final String AT_SOURCE_NAME = "at_source_name";
	private static final String AT_SOURCE_NICK = "at_source_nick";
	private static final String AT_SOURCE_CONTENT = "at_source_content";
	private static final String AT_SOURCE_IMAGE = "at_source_image";
	private static final String AT_SOURCE_VIDEO_IMAGE = "at_source_video_image";
	private static final String AT_SOURCE_VIDEO_PLAYER = "at_source_video_player";
	private static final String AT_SOURCE_VIDEO_URL = "at_source_video_url";
	private static final String AT_R_COUNT = "at_r_count";
	private static final String AT_COMMENT_COUNT = "at_comment_count";
	private static final String AT_MUSER = "at_muser";

	public static DBManager getInstance(Context context) {
		DBManager.context = context;
		if (_instance == null) {
			_instance = new DBManager(context);
		}

		return _instance;
	}

	private DBManager(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		//dbHelper.close();
		Log.d(Constants.LOG_TAG, "DBManager construstor");

	}

	public void upgradeProfile(ProfileBean profileBean) {
		ContentValues values = new ContentValues();
		values.put(PROFILE_OPEN_ID, profileBean.getOpenId());
		values.put(PROFILE_NICK, profileBean.getNick());
		values.put(PROFILE_NAME, profileBean.getName());
		values.put(PROFILE_HEAD, profileBean.getHead());
		values.put(PROFILE_SEX, profileBean.getSex());
		values.put(PROFILE_INTRO, profileBean.getIntroduction());
		values.put(PROFILE_LOCATION, profileBean.getLocation());
		values.put(PROFILE_TAG, profileBean.getTag());
		values.put(PROFILE_TWEETNUM, profileBean.getTweetNum());
		values.put(PROFILE_IDOLNUM, profileBean.getIdolNum());
		values.put(PROFILE_FANSNUM, profileBean.getFansNum());
		values.put(PROFILE_FAVNUM, profileBean.getFavNum());

		db.replace(TABLE_MY_PROFILE, null, values);

		SettingsManager.getInstance(context).setProfileStatus(
				PROFILE_STATUS_READY);

	}

	public ProfileBean getProfile() {
		Cursor cursor = db.query(TABLE_MY_PROFILE, new String[] {
				PROFILE_OPEN_ID, PROFILE_NICK, PROFILE_NAME, PROFILE_HEAD,
				PROFILE_SEX, PROFILE_INTRO, PROFILE_LOCATION, PROFILE_TAG,
				PROFILE_TWEETNUM, PROFILE_IDOLNUM, PROFILE_FANSNUM,
				PROFILE_FAVNUM }, null, null, null, null, null);

		ProfileBean profileBean = new ProfileBean();

		if (cursor != null && cursor.moveToFirst()) {

			profileBean.setOpenId(cursor.getString(0));
			profileBean.setNick(cursor.getString(1));
			profileBean.setName(cursor.getString(2));
			profileBean.setHead(cursor.getString(3));
			profileBean.setSex(cursor.getString(4));
			profileBean.setIntroduction(cursor.getString(5));
			profileBean.setLocation(cursor.getString(6));
			profileBean.setTag(cursor.getString(7));
			profileBean.setTweetNum(cursor.getString(8));
			profileBean.setIdolNum(cursor.getString(9));
			profileBean.setFansNum(cursor.getString(10));
			profileBean.setFavNum(cursor.getString(11));
		}
		if (cursor != null && !cursor.isClosed()) 
			cursor.close();
		
		Log.i(Constants.LOG_TAG, profileBean.toString());

		return profileBean;
	}

	public void addTweetListContent(HashMap<String, TweetBean> tweetBeans,
			boolean isUpdate) {
		TweetBean tweetBean;

		InsertHelper helper = new InsertHelper(db, TABLE_TWEET_LIST);

		int columnOpenIdIndex = helper.getColumnIndex(TWEET_USER_ID);
		int columnIdIndex = helper.getColumnIndex(TWEET_ID);
		int columnHeadIndex = helper.getColumnIndex(TWEET_HEAD);
		int columnNickIndex = helper.getColumnIndex(TWEET_NICK);
		int columnNameIndex = helper.getColumnIndex(TWEET_NAME);
		int columnTimestampIndex = helper.getColumnIndex(TWEET_TIMESTAMP);
		int columnFromIndex = helper.getColumnIndex(TWEET_FROM);
		int columnTweetContentIndex = helper.getColumnIndex(TWEET_CONTENT);
		int columnTweetImageIndex = helper.getColumnIndex(TWEET_IMAGE);
		int columnTweetVideoImageIndex = helper.getColumnIndex(TWEET_VIDEO_IMAGE);
		int columnTweetVideoPlayerIndex = helper.getColumnIndex(TWEET_VIDEO_PLAYER);
		int columnTweetVideoUrlIndex = helper.getColumnIndex(TWEET_VIDEO_URL);
		int columnMCountIndex = helper.getColumnIndex(COMMENT_COUNT);
		int columnRCountIndex = helper.getColumnIndex(R_COUNT);
		int columnSourceNickIndex = helper.getColumnIndex(SOURCE_NICK);
		int columnSourceContentIndex = helper.getColumnIndex(SOURCE_CONTENT);
		int columnSourceImageIndex = helper.getColumnIndex(SOURCE_IMAGE);
		int columnSourceVideoImageIndex = helper.getColumnIndex(SOURCE_VIDEO_IMAGE);
		int columnSourceVideoPlayerIndex = helper.getColumnIndex(SOURCE_VIDEO_PLAYER);
		int columnSourceVideoUrlIndex = helper.getColumnIndex(SOURCE_VIDEO_URL);
		int columnSourceLongitudeIndex = helper.getColumnIndex(SOURCE_LONGITUDE);
		int columnSourceLatitudeIndex = helper.getColumnIndex(SOURCE_LATITUDE);
		int columnSourceGeoIndex = helper.getColumnIndex(SOURCE_GEO);
		int columnTweetUserIndex = helper.getColumnIndex(TWEET_MUSER);
		int columnIsVipIndex = helper.getColumnIndex(TWEET_IS_VIP);
		int columnLongitudeIndex = helper.getColumnIndex(TWEET_LONGITUDE);
		int columnLatitudeIndex = helper.getColumnIndex(TWEET_LATITUDE);
		int columnGeoIndex = helper.getColumnIndex(TWEET_GEO);

		db.beginTransaction();

		try {

			Iterator<Map.Entry<String, TweetBean>> it = tweetBeans.entrySet()
					.iterator();

			if (isUpdate)
				db.delete(TABLE_TWEET_LIST, null, null);

			while (it.hasNext()) {
				Map.Entry<String, TweetBean> pairs = (Map.Entry<String, TweetBean>) it
						.next();

				tweetBean = pairs.getValue();

				if (isUpdate) {
					helper.prepareForReplace();
				} else {
					helper.prepareForInsert();
				}

				helper.bind(columnOpenIdIndex, tweetBean.getOpenId());
				helper.bind(columnIdIndex, tweetBean.getId());
				helper.bind(columnHeadIndex, tweetBean.getHead());
				helper.bind(columnNickIndex, tweetBean.getNick());
				helper.bind(columnNameIndex, tweetBean.getName());
				helper.bind(columnTimestampIndex, tweetBean.getTimestamp());
				helper.bind(columnFromIndex, tweetBean.getFrom());
				helper.bind(columnTweetContentIndex, tweetBean.getText());
				if (tweetBean.getTweetImage() != null
						&& !tweetBean.getTweetImage().toString().equals("[]")) {
					helper.bind(columnTweetImageIndex, tweetBean
							.getTweetImage().toString());
				} else {
					helper.bind(columnTweetImageIndex, "[]");
				}
				if (tweetBean.getVideoImage() != null
						&& tweetBean.getVideoImage().toString().equals("null")) {
					helper.bind(columnTweetVideoImageIndex,
							tweetBean.getVideoImage());
					helper.bind(columnTweetVideoPlayerIndex,
							tweetBean.getVideoPlayer());
					helper.bind(columnTweetVideoUrlIndex,
							tweetBean.getVideoUrl());
				} else {
					helper.bind(columnTweetVideoImageIndex, "null");
					helper.bind(columnTweetVideoPlayerIndex, "null");
					helper.bind(columnTweetVideoUrlIndex, "null");
				}
				helper.bind(columnMCountIndex, tweetBean.getMCount());
				helper.bind(columnRCountIndex, tweetBean.getCount());
				if (tweetBean.getSource() != null) {
					helper.bind(columnSourceNickIndex, tweetBean.getSource()
							.getNick());
					helper.bind(columnSourceContentIndex, tweetBean.getSource()
							.getText());
					if (tweetBean.getSource().getTweetImage() != null
							&& !tweetBean.getSource().getTweetImage()
									.toString().equals("[]")) {
						helper.bind(columnSourceImageIndex, tweetBean
								.getSource().getTweetImage().toString());
					} else {
						helper.bind(columnSourceImageIndex, "[]");
					}
					if (tweetBean.getSource().getVideoImage() != null
							&& !tweetBean.getSource().getVideoImage()
									.toString().equals("null")) {
						helper.bind(columnSourceVideoImageIndex, tweetBean
								.getSource().getVideoImage());
						helper.bind(columnSourceVideoPlayerIndex, tweetBean
								.getSource().getVideoPlayer());
						helper.bind(columnSourceVideoUrlIndex, tweetBean
								.getSource().getVideoUrl());
					} else {
						helper.bind(columnSourceVideoImageIndex, "null");
						helper.bind(columnSourceVideoPlayerIndex, "null");
						helper.bind(columnSourceVideoUrlIndex, "null");
					}
					if (tweetBean.getSource().getLongitude() != null
							&& tweetBean.getLongitude().length() > 0) {
						helper.bind(columnSourceLongitudeIndex, tweetBean
								.getSource().getLongitude());
						helper.bind(columnSourceLatitudeIndex, tweetBean
								.getSource().getLatitude());
						helper.bind(columnSourceGeoIndex, tweetBean.getSource()
								.getGeo());
					} else {
						helper.bind(columnSourceLongitudeIndex, "");
						helper.bind(columnSourceLatitudeIndex, "");
						helper.bind(columnSourceGeoIndex, "");
					}
				} else {
					helper.bind(columnSourceNickIndex, "null");
					helper.bind(columnSourceContentIndex, "null");
					helper.bind(columnSourceImageIndex, "[]");
					helper.bind(columnSourceVideoImageIndex, "null");
					helper.bind(columnSourceVideoPlayerIndex, "null");
					helper.bind(columnSourceVideoUrlIndex, "null");
					helper.bind(columnSourceLongitudeIndex, "");
					helper.bind(columnSourceLatitudeIndex, "");
					helper.bind(columnSourceGeoIndex, "");
				}
				if (tweetBean.getMentionedUser().length() > 0) {
					helper.bind(columnTweetUserIndex, tweetBean
							.getMentionedUser().toString());
				} else {
					helper.bind(columnTweetUserIndex, "{'null':null}");
				}

				helper.bind(columnIsVipIndex, tweetBean.getIsVip());

				if (tweetBean.getLongitude().length() > 0) {
					helper.bind(columnLongitudeIndex, tweetBean.getLongitude());
					helper.bind(columnLatitudeIndex, tweetBean.getLatitude());
					helper.bind(columnGeoIndex, tweetBean.getGeo());
				} else {
					helper.bind(columnLongitudeIndex, "");
					helper.bind(columnLatitudeIndex, "");
					helper.bind(columnGeoIndex, "");
				}

				helper.execute();
				Log.d(Constants.LOG_TAG, tweetBean.getText());
			}

			db.setTransactionSuccessful();

		} finally {
			helper.close();
			db.endTransaction();
		}

		SettingsManager.getInstance(context).setTweetListStatus(
				TWEET_LIST_STATUS_READY);

	}

	public String getTimestamp(String status) {
		Cursor cursor = db.query(TABLE_TWEET_LIST,
				new String[] { TWEET_TIMESTAMP }, null, null, null, null,
				TWEET_TIMESTAMP + " ASC");

		if (status == "FIRST")
			cursor.moveToFirst();
		else
			cursor.moveToLast();
		
		String timeStamp = cursor.getString(0);
		
		if (cursor != null && !cursor.isClosed()) 
			cursor.close();
		return timeStamp;
	}

	public ArrayList<TweetBean> getAllTweetList() {
		ArrayList<TweetBean> tweetLists = new ArrayList<TweetBean>();
		Cursor cursor = db.query(TABLE_TWEET_LIST, new String[] { TWEET_ID,
				TWEET_HEAD, TWEET_NICK, TWEET_NAME, TWEET_TIMESTAMP,
				TWEET_FROM, TWEET_CONTENT, TWEET_IMAGE, TWEET_VIDEO_IMAGE,
				TWEET_VIDEO_PLAYER, TWEET_VIDEO_URL, COMMENT_COUNT, R_COUNT,
				SOURCE_NICK, SOURCE_CONTENT, SOURCE_IMAGE, SOURCE_VIDEO_IMAGE,
				SOURCE_VIDEO_PLAYER, SOURCE_VIDEO_URL, SOURCE_LONGITUDE,
				SOURCE_LATITUDE, SOURCE_GEO, TWEET_MUSER, TWEET_IS_VIP,
				TWEET_LONGITUDE, TWEET_LATITUDE, TWEET_GEO, TWEET_USER_ID }, null, null, null,
				null, TWEET_TIMESTAMP + " DESC");

		if (cursor.moveToFirst()) {
			TweetBean tweetList;
			do {
				try {
					tweetList = new TweetBean();
					tweetList.setId(cursor.getString(0));
					tweetList.setHead(cursor.getString(1));
					tweetList.setNick(cursor.getString(2));
					tweetList.setName(cursor.getString(3));
					tweetList.setTimestamp(cursor.getString(4));
					tweetList.setFrom(cursor.getString(5));
					tweetList.setText(cursor.getString(6));
					if (!cursor.getString(7).equals("[]"))
						tweetList.setTweetImage(new JSONArray(cursor.getString(7)));
					if (!cursor.getString(8).equals("null")) {
						tweetList.setVideoImage(cursor.getString(8));
						tweetList.setVideoPlayer(cursor.getString(9));
						tweetList.setVideoUrl(cursor.getString(10));
					}
					tweetList.setMCount(cursor.getString(11));
					tweetList.setCount(cursor.getString(12));
					if (!cursor.getString(13).equals("null")) {
						TweetBean sourceTweet = new TweetBean();
						sourceTweet.setNick(cursor.getString(13));
						sourceTweet.setText(cursor.getString(14));
						if (!cursor.getString(15).equals("[]"))
							sourceTweet.setTweetImage(new JSONArray(cursor.getString(15)));
						if (!cursor.getString(16).equals("null")) {
							sourceTweet.setVideoImage(cursor.getString(16));
							sourceTweet.setVideoPlayer(cursor.getString(17));
							sourceTweet.setVideoUrl(cursor.getString(18));
						}
						if (cursor.getString(19).length() > 0) {
							sourceTweet.setLongitude(cursor.getString(19));
							sourceTweet.setLatitude(cursor.getString(20));
							sourceTweet.setGeo(cursor.getString(21));
						}
						tweetList.setSource(sourceTweet);
					}
					tweetList.setMentionedUser(new JSONObject(cursor.getString(22)));
					tweetList.setIsVip(cursor.getInt(23));
					if (cursor.getString(24).length() > 0) {
						tweetList.setLongitude(cursor.getString(24));
						tweetList.setLatitude(cursor.getString(25));
						tweetList.setGeo(cursor.getString(26));
					}
					
					tweetList.setOpenId(cursor.getString(27));

					tweetLists.add(tweetList);
				} catch (Exception e) {
					Log.d(Constants.LOG_TAG,
							"GET_ALL_TWEET_LIST_FROM_DB_ERROR:" + e);
				}
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return tweetLists;
	}
	
	public void delTweetContent(String id){
		db.delete(TABLE_TWEET_LIST, TWEET_ID + "=?", new String[]{id});
	}

	public void addPrivateMessage(
			HashMap<String, PrivateDataBean> privateBeans, boolean isUpdate) {
		PrivateDataBean privateDataBean;

		InsertHelper helper = new InsertHelper(db, TABLE_PRIVATE_LIST);

		int columnIdIndex = helper.getColumnIndex(PRIVATE_ID);
		int columnHeadIndex = helper.getColumnIndex(PRIVATE_HEAD);
		int columnNameIndex = helper.getColumnIndex(PRIVATE_NAME);
		int columnNickIndex = helper.getColumnIndex(PRIVATE_NICK);
		int columnContentIndex = helper.getColumnIndex(PRIVATE_CONTENT);
		int columnImageIndex = helper.getColumnIndex(PRIVATE_IMAGE);
		int columnTimeStampIndex = helper.getColumnIndex(PRIVATE_TIMESTAMP);

		db.beginTransaction();

		try {
			Iterator<Map.Entry<String, PrivateDataBean>> it = privateBeans
					.entrySet().iterator();

			if (isUpdate)
				db.delete(TABLE_PRIVATE_LIST, null, null);

			while (it.hasNext()) {

				Map.Entry<String, PrivateDataBean> pairs = (Map.Entry<String, PrivateDataBean>) it
						.next();
				privateDataBean = pairs.getValue();

				if (isUpdate) {
					helper.prepareForReplace();
				} else {
					helper.prepareForInsert();
				}

				helper.bind(columnIdIndex, privateDataBean.getOpenId());
				helper.bind(columnHeadIndex, privateDataBean.getHead());
				helper.bind(columnNameIndex, privateDataBean.getName());
				helper.bind(columnNickIndex, privateDataBean.getNick());
				helper.bind(columnContentIndex, privateDataBean.getText());
				if (privateDataBean.getImage() != null
						&& privateDataBean.getImage().toString().equals("[]")) {
					helper.bind(columnImageIndex, privateDataBean.getImage()
							.toString());
				} else {
					helper.bind(columnImageIndex, "[]");
				}
				helper.bind(columnTimeStampIndex, privateDataBean.getPubTime());

				helper.execute();
			}
			Log.d(Constants.LOG_TAG, String.valueOf(db.getPageSize()));

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d(Constants.LOG_TAG, "ADD_PRIVATE_MESSGAE_TO_DB_ERROR:" + e);
		} finally {
			helper.close();
			db.endTransaction();
		}

		SettingsManager.getInstance(context).setPrivateMessageStatus(
				PRIVATE_MESSAGE_STATUS_READY);

	}

	public ArrayList<PrivateDataBean> getPrivateMessage() {
		ArrayList<PrivateDataBean> privateList = new ArrayList<PrivateDataBean>();
		Cursor cursor = db.query(TABLE_PRIVATE_LIST, new String[] { PRIVATE_ID,
				PRIVATE_HEAD, PRIVATE_NAME, PRIVATE_NICK, PRIVATE_CONTENT,
				PRIVATE_IMAGE, PRIVATE_TIMESTAMP }, null, null, null, null,
				PRIVATE_TIMESTAMP + " DESC");
		if (cursor.moveToFirst()) {
			do {
				PrivateDataBean privateDataBean = new PrivateDataBean();
				privateDataBean.setOpenId(cursor.getString(0));
				privateDataBean.setHead(cursor.getString(1));
				privateDataBean.setName(cursor.getString(2));
				privateDataBean.setNick(cursor.getString(3));
				privateDataBean.setText(cursor.getString(4));
				if (!cursor.getString(5).equals("[]")) {
					try {
						privateDataBean.setImage(new JSONArray(cursor
								.getString(5)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				privateDataBean.setPubTime(cursor.getString(6));

				privateList.add(privateDataBean);

			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return privateList;
	}

	public void addAtList(HashMap<String, TweetBean> tweetBeans,
			boolean isUpdate) {
		TweetBean tweetBean;

		InsertHelper helper = new InsertHelper(db, TABLE_AT_LIST);

		int columnIdIndex = helper.getColumnIndex(AT_ID);
		int columnHeadIndex = helper.getColumnIndex(AT_HEAD);
		int columnNameIndex = helper.getColumnIndex(AT_NAME);
		int columnNickIndex = helper.getColumnIndex(AT_NICK);
		int columnFromIndex = helper.getColumnIndex(AT_FROM);
		int columnImageIndex = helper.getColumnIndex(AT_IMAGE);
		int columnVideoImageIndex = helper.getColumnIndex(AT_VIDEO_IMAGE);
		int columnVideoPlayerIndex = helper.getColumnIndex(AT_VIDEO_PLAYER);
		int columnVideoUrlIndex = helper.getColumnIndex(AT_VIDEO_URL);
		int columnTimeStampIndex = helper.getColumnIndex(AT_TIMESTAMP);
		int columnContentIndex = helper.getColumnIndex(AT_CONTENT);
		int columnSourceNameIndex = helper.getColumnIndex(AT_SOURCE_NAME);
		int columnSourceNickIndex = helper.getColumnIndex(AT_SOURCE_NICK);
		int columnSourceContentIndex = helper.getColumnIndex(AT_SOURCE_CONTENT);
		int columnSourceImageIndex = helper.getColumnIndex(AT_SOURCE_IMAGE);
		int columnSourceVideoImageIndex = helper
				.getColumnIndex(AT_SOURCE_VIDEO_IMAGE);
		int columnSourceVideoPlayerIndex = helper
				.getColumnIndex(AT_SOURCE_VIDEO_PLAYER);
		int columnSourceVideoUrlIndex = helper
				.getColumnIndex(AT_SOURCE_VIDEO_URL);
		int columnRCountIndex = helper.getColumnIndex(AT_R_COUNT);
		int columnCommentCountIndex = helper.getColumnIndex(AT_COMMENT_COUNT);
		int columnMuserIndex = helper.getColumnIndex(AT_MUSER);

		db.beginTransaction();

		try {
			Iterator<Map.Entry<String, TweetBean>> it = tweetBeans.entrySet()
					.iterator();

			if (isUpdate)
				db.delete(TABLE_AT_LIST, null, null);

			while (it.hasNext()) {
				Map.Entry<String, TweetBean> pairs = (Map.Entry<String, TweetBean>) it
						.next();
				tweetBean = pairs.getValue();

				Log.d(Constants.LOG_TAG, "it");

				if (isUpdate) {
					helper.prepareForReplace();
				} else {
					helper.prepareForInsert();
				}

				helper.bind(columnIdIndex, tweetBean.getId());
				helper.bind(columnHeadIndex, tweetBean.getHead());
				helper.bind(columnNameIndex, tweetBean.getName());
				helper.bind(columnNickIndex, tweetBean.getNick());
				helper.bind(columnFromIndex, tweetBean.getFrom());
				if (tweetBean.getTweetImage() != null
						&& tweetBean.getTweetImage().toString().equals("[]")) {
					helper.bind(columnImageIndex, tweetBean.getTweetImage()
							.toString());
				} else {
					helper.bind(columnImageIndex, "[]");
				}
				if (tweetBean.getVideoImage() != null
						&& tweetBean.getVideoImage().equals("null")) {
					helper.bind(columnVideoImageIndex,
							tweetBean.getVideoImage());
					helper.bind(columnVideoPlayerIndex,
							tweetBean.getVideoPlayer());
					helper.bind(columnVideoUrlIndex, tweetBean.getVideoUrl());
				} else {
					helper.bind(columnVideoImageIndex, "null");
					helper.bind(columnVideoPlayerIndex, "null");
					helper.bind(columnVideoUrlIndex, "null");
				}
				helper.bind(columnTimeStampIndex, tweetBean.getTimestamp());
				helper.bind(columnContentIndex, tweetBean.getText());
				if (tweetBean.getSource() != null) {
					helper.bind(columnSourceNameIndex, tweetBean.getSource()
							.getName());
					helper.bind(columnSourceNickIndex, tweetBean.getSource()
							.getNick());
					helper.bind(columnSourceContentIndex, tweetBean.getSource()
							.getText());
					if (tweetBean.getSource().getTweetImage() != null
							&& !tweetBean.getSource().getTweetImage()
									.toString().equals("[]")) {
						helper.bind(columnSourceImageIndex, tweetBean
								.getSource().getTweetImage().toString());
					} else {
						helper.bind(columnSourceImageIndex, "[]");
					}
					if (tweetBean.getSource().getVideoImage() != null
							&& !tweetBean.getSource().getVideoImage()
									.equals("null")) {
						helper.bind(columnSourceVideoImageIndex, tweetBean
								.getSource().getVideoImage());
						helper.bind(columnSourceVideoPlayerIndex, tweetBean
								.getSource().getVideoPlayer());
						helper.bind(columnSourceVideoUrlIndex, tweetBean
								.getSource().getVideoUrl());
					} else {
						helper.bind(columnSourceVideoImageIndex, "null");
						helper.bind(columnSourceVideoPlayerIndex, "null");
						helper.bind(columnSourceVideoUrlIndex, "null");
					}
				} else {
					helper.bind(columnSourceNameIndex, "null");
					helper.bind(columnSourceNickIndex, "null");
					helper.bind(columnSourceContentIndex, "null");
					helper.bind(columnSourceImageIndex, "[]");
					helper.bind(columnSourceVideoImageIndex, "null");
					helper.bind(columnSourceVideoPlayerIndex, "null");
					helper.bind(columnSourceVideoUrlIndex, "null");
				}

				helper.bind(columnRCountIndex, tweetBean.getCount());
				helper.bind(columnCommentCountIndex, tweetBean.getMCount());

				if (tweetBean.getMentionedUser().length() > 0) {
					helper.bind(columnMuserIndex, tweetBean.getMentionedUser()
							.toString());
				} else {
					helper.bind(columnMuserIndex, "{'null':null}");
				}

				helper.execute();

			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			helper.close();
			db.endTransaction();
		}

		SettingsManager.getInstance(context).setAtListStatus(
				DBManager.AT_LIST_STATUS_READY);

	}

	public ArrayList<TweetBean> getAtList() {
		ArrayList<TweetBean> tweetBeans = new ArrayList<TweetBean>();
		Cursor cursor = db.query(TABLE_AT_LIST, new String[] { AT_ID, AT_HEAD,
				AT_NAME, AT_NICK, AT_FROM, AT_IMAGE, AT_VIDEO_IMAGE,
				AT_VIDEO_PLAYER, AT_VIDEO_URL, AT_TIMESTAMP, AT_CONTENT,
				AT_SOURCE_NAME, AT_SOURCE_NICK, AT_SOURCE_CONTENT,
				AT_SOURCE_IMAGE, AT_SOURCE_VIDEO_IMAGE, AT_SOURCE_VIDEO_PLAYER,
				AT_SOURCE_VIDEO_URL, AT_R_COUNT, AT_COMMENT_COUNT, AT_MUSER },
				null, null, null, null, AT_TIMESTAMP + " DESC");
		if (cursor.moveToFirst()) {
			TweetBean tweetBean;
			do {
				try {
					tweetBean = new TweetBean();
					tweetBean.setId(cursor.getString(0));
					tweetBean.setHead(cursor.getString(1));
					tweetBean.setName(cursor.getString(2));
					tweetBean.setNick(cursor.getString(3));
					tweetBean.setFrom(cursor.getString(4));
					if (!cursor.getString(5).equals("[]"))
						tweetBean.setTweetImage(new JSONArray(cursor
								.getString(5)));
					if (!cursor.getString(6).equals("null")) {
						tweetBean.setVideoImage(cursor.getString(6));
						tweetBean.setVideoPlayer(cursor.getString(7));
						tweetBean.setVideoUrl(cursor.getString(8));
					}
					tweetBean.setTimestamp(cursor.getString(9));
					tweetBean.setText(cursor.getString(10));
					if (!cursor.getString(11).equals("null")) {
						TweetBean sourceTweetBean = new TweetBean();
						sourceTweetBean.setName(cursor.getString(11));
						sourceTweetBean.setNick(cursor.getString(12));
						sourceTweetBean.setText(cursor.getString(13));
						if (!cursor.getString(14).equals("[]"))
							sourceTweetBean.setTweetImage(new JSONArray(cursor
									.getString(14)));
						if (!cursor.getString(15).equals("null")) {
							sourceTweetBean.setVideoImage(cursor.getString(15));
							sourceTweetBean
									.setVideoPlayer(cursor.getString(16));
							sourceTweetBean.setVideoUrl(cursor.getString(17));
						}

						tweetBean.setSource(sourceTweetBean);
					}
					tweetBean.setCount(cursor.getString(18));
					tweetBean.setMCount(cursor.getString(19));
					tweetBean.setMentionedUser(new JSONObject(cursor
							.getString(20)));

					tweetBeans.add(tweetBean);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} while (cursor.moveToNext());
		}

		Log.d(Constants.LOG_TAG, tweetBeans.size() + "");
		if (cursor != null && !cursor.isClosed()) 
			cursor.close();
		return tweetBeans;
	}

	public void addContacts(HashMap<String, UserBean> userBeans,
			boolean isUpdate) {
		long a = System.currentTimeMillis();
		UserBean userBean;

		InsertHelper helper = new InsertHelper(db, TABLE_CONTACTS_ME);

		int columnIdIndex = helper.getColumnIndex(KEY_OPEN_ID);
		int columnNameIndex = helper.getColumnIndex(KEY_NAME);
		int columnNickIndex = helper.getColumnIndex(KEY_NICK);
		int columnNameFormattedIndex = helper.getColumnIndex(KEY_NAME_FORMATTED);
		int columnHeadIndex = helper.getColumnIndex(KEY_HEAD);
		int columnSexIndex = helper.getColumnIndex(KEY_SEX);
		int columnTweetIndex = helper.getColumnIndex(KEY_TWEET);

		db.beginTransaction();

		try {

			Iterator<Map.Entry<String, UserBean>> it = userBeans.entrySet()
					.iterator();

			while (it.hasNext()) {

				Map.Entry<String, UserBean> pairs = (Map.Entry<String, UserBean>) it
						.next();

				userBean = pairs.getValue();

				if (isUpdate)
					helper.prepareForReplace();
				else
					helper.prepareForInsert();

				helper.bind(columnIdIndex, userBean.getOpenId());
				helper.bind(columnNameIndex, userBean.getName());
				helper.bind(columnNickIndex, userBean.getNick());
				helper.bind(columnNameFormattedIndex,
						StringMatcher.cn2Spell(userBean.getNick()));
				helper.bind(columnHeadIndex, userBean.getHead());
				helper.bind(columnSexIndex, userBean.getSex());
				helper.bind(columnTweetIndex, userBean.getTweetBean().getText());

				helper.execute();

			}

			db.setTransactionSuccessful();

		} finally {
			helper.close();
			db.endTransaction();

		}

		SettingsManager.getInstance(context).setContactStatus(
				CONTACT_STATUS_READY);

		long b = System.currentTimeMillis();

		Log.d(Constants.LOG_TAG, "Contacts saved in : " + (b - a) + " ms, "
				+ userBeans.size() + " contacts");

	}

	// Adding new contact
	public void addContact(UserBean userBean) {
		ContentValues values = new ContentValues();
		values.put(KEY_OPEN_ID, userBean.getOpenId());
		values.put(KEY_NAME, userBean.getName());
		values.put(KEY_NICK, userBean.getNick());
		values.put(KEY_HEAD, userBean.getHead());
		values.put(KEY_SEX, userBean.getSex());
		values.put(KEY_TWEET, userBean.getTweetBean().getText());

		// Inserting Row
		db.replace(TABLE_CONTACTS_ME, null, values);

	}

	public UserBean getContact(String id) {

		Cursor cursor = db.query(TABLE_CONTACTS_ME, new String[] { KEY_OPEN_ID,
				KEY_NAME, KEY_NICK, KEY_HEAD, KEY_SEX, KEY_TWEET }, KEY_OPEN_ID
				+ "=?", new String[] { id }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		UserBean contact = new UserBean(cursor.getString(0),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getString(5));
		// return contact
		if (cursor != null && !cursor.isClosed()) 
			cursor.close();
		return contact;
	}

	public ArrayList<UserBean> getAllContacts() {
		ArrayList<UserBean> contactList = new ArrayList<UserBean>();
		// Select All Query
		Cursor cursor = db.query(TABLE_CONTACTS_ME, new String[] { KEY_OPEN_ID,
				KEY_NAME, KEY_NICK, KEY_HEAD, KEY_SEX, KEY_TWEET,
				KEY_NAME_FORMATTED }, null, null, null, null,
				KEY_NAME_FORMATTED + " COLLATE LOCALIZED COLLATE NOCASE ASC");

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UserBean contact = new UserBean(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		// return contact list
		return contactList;
	}

	public ArrayList<UserBean> getContactsSearch(String args) {
		ArrayList<UserBean> contactList = new ArrayList<UserBean>();

		String sqlQuery = "SELECT " + KEY_OPEN_ID + ", " + KEY_NAME + ", "
				+ KEY_NICK + ", " + KEY_HEAD + ", " + KEY_SEX + ", "
				+ KEY_TWEET + " FROM " + TABLE_CONTACTS_ME;
		if (args != null && args.length() > 0) {

			if (args != null && !"".equals(args))
				sqlQuery += " WHERE " + KEY_NAME_FORMATTED + " LIKE '%" + args + "%'";
			Cursor cursor = db.rawQuery(sqlQuery, null);

			if (cursor.moveToFirst()) {

				do {
					UserBean contact = new UserBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
					contactList.add(contact);
				} while (cursor.moveToNext());

			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} else {
			return getAllContacts();
		}

		return contactList;
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {

		private Context context;

		public DatabaseHelper(Context context) {
			super(context, HALLOON_DATABASE, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(Constants.LOG_TAG, "CREATE DB--" + TABLE_CONTACTS_ME + "|"
					                               + TABLE_MY_PROFILE + "|" 
					                               + TABLE_TWEET_LIST + "|"
					                               + TABLE_PRIVATE_LIST + "|" 
					                               + TABLE_AT_LIST);

			String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS_ME + "(" 
			                                                             + KEY_OPEN_ID + " TEXT PRIMARY KEY," 
			                                                             + KEY_NAME + " TEXT," 
			                                                             + KEY_NICK + " TEXT," 
			                                                             + KEY_NAME_FORMATTED + " TEXT," 
			                                                             + KEY_HEAD + " TEXT," 
			                                                             + KEY_SEX + " TEXT," 
			                                                             + KEY_TWEET + " TEXT" + ")";

			String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_MY_PROFILE + "(" + PROFILE_OPEN_ID
					+ " TEXT PRIMARY KEY," + PROFILE_NICK + " TEXT,"
					+ PROFILE_NAME + " TEXT," + PROFILE_HEAD + " TEXT,"
					+ PROFILE_SEX + " TEXT," + PROFILE_INTRO + " TEXT,"
					+ PROFILE_LOCATION + " TEXT," + PROFILE_TAG + " TEXT,"
					+ PROFILE_TWEETNUM + " TEXT," + PROFILE_IDOLNUM + " TEXT,"
					+ PROFILE_FANSNUM + " TEXT," + PROFILE_FAVNUM + " TEXT"
					+ ")";

			String CREATE_TWEET_LIST_TABLE = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_TWEET_LIST + "(" + TWEET_ID + " TEXT PRIMARY KEY,"
					+ TWEET_USER_ID + " TEXT,"
					+ TWEET_HEAD + " TEXT," + TWEET_NICK + " TEXT,"
					+ TWEET_NAME + " TEXT," + TWEET_TIMESTAMP + " INTEGER,"
					+ TWEET_FROM + " TEXT," + TWEET_CONTENT + " TEXT,"
					+ TWEET_IMAGE + " TEXT," + TWEET_VIDEO_IMAGE + " TEXT,"
					+ TWEET_VIDEO_PLAYER + " TEXT," + TWEET_VIDEO_URL
					+ " TEXT," + COMMENT_COUNT + " TEXT," + R_COUNT + " TEXT,"
					+ SOURCE_NICK + " TEXT," + SOURCE_CONTENT + " TEXT,"
					+ SOURCE_IMAGE + " TEXT," + SOURCE_VIDEO_IMAGE + " TEXT,"
					+ SOURCE_VIDEO_PLAYER + " TEXT," + SOURCE_VIDEO_URL
					+ " TEXT," + SOURCE_LONGITUDE + " TEXT," + SOURCE_LATITUDE
					+ " TEXT," + SOURCE_GEO + " TEXT," + TWEET_MUSER + " TEXT,"
					+ TWEET_IS_VIP + " TEXT," + TWEET_LONGITUDE + " TEXT,"
					+ TWEET_LATITUDE + " TEXT," + TWEET_GEO + " TEXT" + ")";

			String CREATE_PRIVATE_LIST_TABLE = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_PRIVATE_LIST + "(" + PRIVATE_ID
					+ " TEXT PRIMARY KEY," + PRIVATE_HEAD + " TEXT,"
					+ PRIVATE_NAME + " TEXT," + PRIVATE_NICK + " TEXT,"
					+ PRIVATE_CONTENT + " TEXT," + PRIVATE_IMAGE + " TEXT,"
					+ PRIVATE_TIMESTAMP + " TEXT" + ")";

			String CREATE_AT_LIST_TABLE = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_AT_LIST + "(" + AT_ID + " TEXT PRIMARY KEY,"
					+ AT_HEAD + " TEXT," + AT_NAME + " TEXT," + AT_NICK
					+ " TEXT," + AT_FROM + " TEXT," + AT_IMAGE + " TEXT,"
					+ AT_VIDEO_IMAGE + " TEXT," + AT_VIDEO_PLAYER + " TEXT,"
					+ AT_VIDEO_URL + " TEXT," + AT_TIMESTAMP + " TEXT,"
					+ AT_CONTENT + " TEXT," + AT_SOURCE_NAME + " TEXT,"
					+ AT_SOURCE_NICK + " TEXT," + AT_SOURCE_CONTENT + " TEXT,"
					+ AT_SOURCE_IMAGE + " TEXT," + AT_SOURCE_VIDEO_IMAGE
					+ " TEXT," + AT_SOURCE_VIDEO_PLAYER + " TEXT,"
					+ AT_SOURCE_VIDEO_URL + " TEXT," + AT_R_COUNT + " TEXT,"
					+ AT_COMMENT_COUNT + " TEXT," + AT_MUSER + " TEXT" + ")";

			db.execSQL(CREATE_CONTACTS_TABLE);
			SettingsManager.getInstance(context).setContactStatus(
					CONTACT_STATUS_INIT);
			db.execSQL(CREATE_PROFILE_TABLE);
			SettingsManager.getInstance(context).setProfileStatus(
					PROFILE_STATUS_INIT);
			db.execSQL(CREATE_TWEET_LIST_TABLE);
			SettingsManager.getInstance(context).setTweetListStatus(
					TWEET_LIST_STATUS_INIT);
			db.execSQL(CREATE_PRIVATE_LIST_TABLE);
			SettingsManager.getInstance(context).setPrivateMessageStatus(
					PRIVATE_MESSAGE_STATUS_INIT);
			db.execSQL(CREATE_AT_LIST_TABLE);
			SettingsManager.getInstance(context).setAtListStatus(
					AT_LIST_STATUS_INIT);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Drop older table if existed
			Log.w(Constants.LOG_TAG, "Upgrade DB Version " + DATABASE_VERSION);

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS_ME);
			SettingsManager.getInstance(context).setContactStatus(CONTACT_STATUS_INIT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_PROFILE);
			SettingsManager.getInstance(context).setProfileStatus(PROFILE_STATUS_INIT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEET_LIST);
			SettingsManager.getInstance(context).setTweetListStatus(TWEET_LIST_STATUS_INIT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRIVATE_LIST);
			SettingsManager.getInstance(context).setPrivateMessageStatus(PRIVATE_MESSAGE_STATUS_INIT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_AT_LIST);
			SettingsManager.getInstance(context).setAtListStatus(AT_LIST_STATUS_INIT);

			onCreate(db);
		}

	}

//	public void closeDatabase() {
//		if (db.isOpen())
//			db.close();
//		dbHelper.close();
//	}

}
