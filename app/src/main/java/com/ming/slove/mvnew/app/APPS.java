package com.ming.slove.mvnew.app;

import android.os.Environment;

/**
 * 用于管理所有全局变量
 * Created by Ming on 2016/11/30.
 */

public class APPS {
    public static final String CHAT_BASE_URL = "http://push.traimo.com/source/";//聊天资源文件base_url
    public static final String DEFAULT_HEAD = "/Public/head/default.png";//服务器提供的默认头像，这里是为了方便替换为本地图片
    public static final String LIVE_SHARE_BASE_URL = "http://html1.yibanke.com/live/live.php?url_m3u8=";//直播分享的base_url
    //SharedPreferences相关参数C
    public static final String KEY_IS_TEST = "key_is_test";
    public static final String KEY_BASE_URL = "key_base_url";//正式服务器与测试服务器的切换，默认为正式
    public static final String KEY_LOGIN_NAME = "login_name";//登录名
    public static final String KEY_LOGIN_PASSWORD = "login_password";//登录密码
    public static final String IS_REMEMBER_PASSWORD = "is_remember_password";//是否记住密码
    public static final String IS_FIRST_RUN = "is_first_run";//是否首次运行
    public static final String IS_UPDATA_MY_INFO = "is_updata_myInfo";//是否更新信息
    public static final String USER_AUTH = "user_auth";//用户认证信息
    public static final String ME_UID = "me_id";//登录用户的uid
    public static final String ME_HEAD = "me_head";//登录用户的头像
    public static final String ME_NAME = "me_name";//登录用户的昵称
    public static final String IS_SHOW_YINGSHAN = "is_show_yingshan";//是否为县长，1是0不是
    public static final String IS_SHOP_OWNER = "is_shop_owner";//是否为店长，1是0不是
    public static final String FRIEND_LIST_UID = "friend_list_uid";//好友uid，用于判定是否为好友。
    public static final String APPLY_INFO = "apply_info_";//储存申请店长人的信息
    public static final String APPLY_INFO_VID = "apply_vid_";//储存申请店长,管理的村
    public static final String SELECTED_CARD = "selected_card";//存储用户的默认提现银行卡，注销登录时须清空。
    public static final String MANAGER_ADDRESS = "village_manager_address";//存储店长用户的村店地址，注销登录时须清空。
    public static final String MANAGER_VID = "manager_village_id";//存储店长用户的村店地址，注销登录时须清空。
    //存储目录路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/MingAppk/";
    public static final String FILE_PATH_HTTPCACHE = FILE_PATH + "HttpCache/";
    public static final String FILE_PATH_GLIDECACHE = FILE_PATH + "GlideCache/";
    public static final String FILE_PATH_CAMERACACHE = FILE_PATH + "CameraCache/";//自定义相机缓存
    public static final String FILE_PATH_DOWNLOAD = FILE_PATH + "Download/Pictures/";
    public static final String FILE_PATH_WEBCACHE = FILE_PATH + "WebCache/";
    public static final String FILE_PATH_DBCACHE = FILE_PATH + "DbCache/";
    public static final String FILE_PATH_DATABASE = FILE_PATH + "DataBase/";//数据库
    //API地址接口
    public static String BASE_URL;//API接口的主机地址
    public static String NEW_FRIEND = "new friend";//用于判定，动态页面显示“新的朋友”图标
    public static String WEB_APP_USER_INFO = "web_app_user_info";//用于web页面获取用户信息
}
