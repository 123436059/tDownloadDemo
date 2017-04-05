package com.originalandtest.tx.downloaddemo.download;

public class Configuration {
    //	public static final String SDCARD_ROOT_DIR = Environment
//			.getExternalStorageDirectory().getPath() + "/hywatch/";
    public static final boolean DEBUG_VERSION = true;

//	public static final String ROOT_PATH = Configuration.SDCARD_ROOT_DIR
//			+ "music/";

//	/** the icon of the ad stored path : mnt/sdcard/soundbox/music/pictures/ */
//	public static final String PIC_ROOT_PATH = ROOT_PATH + "pictures/";
//
//	/** the icon of the ad stored path : mnt/sdcard/soundbox/music/musics/ */
//	public static final String MUSIC_ROOT_PATH = ROOT_PATH + "musics/";

    // ###################豆瓣资源##############
//	private HashMap<String, Object> buildMusicRequestParms(String channel) {
//		HashMap<String, Object> parms = new HashMap<String, Object>();
//		parms.put("app_name", Configuration.APP_NAME);
//		parms.put("scope", Configuration.SCOPE);
//		parms.put("version", Configuration.VERSION);
//		parms.put("type", "n");
//		parms.put("channel", channel);
//		parms.put("pt", "0");
//		parms.put("pb", "64");
//		parms.put("from", "atsmart");
//		parms.put("formats", "aac,mp3");
//		parms.put("kbps", "64");
//		parms.put("ADUIN", "734407108");
//		parms.put("ADSESSION", "1425864607");
//		parms.put("ADTAG", "CLIENT.QQ.5329_.0");
//		parms.put("ADPUBNO", "ADPUBNO");
//		return parms;
//	}
//
//	public static final String APP_NAME = "radio_atsmart";
//	public static final String SCOPE = "music_fm_private";
//	public static final String VERSION = "1";

    private static final String URL_BASE = "https://api.douban.com/v2/fm/";

    /**
     * 频道列表
     */
    public static final String URL_CHANEL = URL_BASE+"app_channels";

    /**
     * 音乐列表
     */
    public static final String URL_MUSIC = URL_BASE+"playlist";

    /**
     * 添加紅心歌曲
     */
    public static final String URL_LIKE = URL_BASE+"like_song";

    /**
     * 刪除紅心歌曲
     */
    public static final String URL_UNLIKE = URL_BASE+"unlike_song";

    /**
     * 标记不再播放歌曲
     */
    public static final String URL_DELETE = URL_BASE+"ban_song";

    /**
     * 标记不再播放歌曲
     */
    public static final String URL_UNDELETE = URL_BASE+"unban_song";
    // end



}
