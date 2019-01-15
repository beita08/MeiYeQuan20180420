package com.xasfemr.meiyaya.global;

import com.xasfemr.meiyaya.BuildConfig;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/1 0001 14:14
 */

public class GlobalConstants {

    public static final String URL_HOME = "http://www.xasfemr.com/";


    private static final boolean isLive = BuildConfig.MieYeQuan_LIVE;

    public static final String LIVE_BASE_URL = "http://app.xasfemr.com/";
    public static final String TEST_BASE_URL = "http://testapp.xasfemr.com/";


    public static final String BASE_URL = isLive ? LIVE_BASE_URL : TEST_BASE_URL;

    /*-----------------------------H5相关页面--------------------------------*/
    public static final String H5_APP_PROTOCOL    = BASE_URL + "index.php?m=Home&c=Safety&a=safety";
    public static final String H5_ABOUT_OURS      = BASE_URL + "index.php?m=Home&c=aboutus&a=aboutus";
    public static final String H5_GRADE_PRIVILEGE = BASE_URL + "index.php?m=Home&c=classrules&a=classrules";
    public static final String H5_ABOUT_GOLD      = BASE_URL + "index.php?m=Home&c=safety&a=jinbi";
    public static final String H5_SHARE_MEIYAYA   = BASE_URL + "index.php?m=Home&c=Download&a=download";
    public static final String H5_SHARE_DYNAMIC   = BASE_URL + "index.php?m=Home&c=Dynamic&a=dynamic_share";
    public static final String H5_SHARE_COURSE    = BASE_URL + "index.php?m=home&c=Reviewseeding&a=seeding_share";


    public static final String URL_RANKING_LIST   = BASE_URL + "index.php?m=Home&c=ranking&a=ranking";
    public static final String URL_RANKING_ACTIVE = BASE_URL + "index.php?m=home&c=Ranking&a=active";
    public static final String URL_RANKING_MONEY  = BASE_URL + "index.php?m=home&c=Ranking&a=treasure";

    public static final String URL_HOME_BANNER        = BASE_URL + "index.php?m=Home&c=Index&a=banner";
    public static final String URL_HOME_FOUR_VIDEO    = BASE_URL + "index.php?m=Home&c=Server&a=recommend";
    public static final String URL_INDUSTRY_NEWS_HOME = BASE_URL + "index.php?m=Home&c=Index&a=test";
    public static final String URL_HOME_NEWS_DETAILS  = BASE_URL + "index.php?m=Home&c=index&a=info";
    public static final String URL_INDUSTRY_NEWS_MORE = BASE_URL + "index.php?m=Home&c=More&a=test";
    public static final String URL_MORE_NEWS_DETAILS  = BASE_URL + "index.php?m=Home&c=More&a=info";

    /*-------------------华丽的分割线2017年10月13日-----------------------------*/
    public static final String URL_LOGIN               = BASE_URL + "index.php?m=Home&c=User&a=login";
    public static final String URL_LOGOUT              = BASE_URL + "index.php?m=Home&c=User&a=userexit";
    public static final String URL_REGISTER            = BASE_URL + "index.php?m=Home&c=User&a=register";
    public static final String URL_CHANGE_PASSWORD     = BASE_URL + "index.php?m=Home&c=user&a=changepwd";
    public static final String URL_SUGGESTION_FEEDBACK = BASE_URL + "index.php?m=Home&c=Feedback&a=feed";

    public static final String URL_SET_NEW_PWD = BASE_URL + "index.php?m=Home&c=User&a=forgetpwd";

    public static final String URL_COLLEGE_LECTURE = BASE_URL + "index.php?m=Home&c=Lecturer&a=index";
    public static final String URL_USER_ME         = BASE_URL + "index.php?m=Home&c=user&a=user";
    public static final String URL_USER_SIGNIN     = BASE_URL + "index.php?m=Home&c=sign&a=sign";
    public static final String URL_MY_MEMBER       = BASE_URL + "index.php?m=Home&c=Vip&a=index";
    public static final String URL_MY_LEARN        = BASE_URL + "index.php?m=Home&c=Collect&a=CollectList";
    public static final String URL_MY_GOLD_NUMBER  = BASE_URL + "index.php?m=Home&c=user&a=usermoney";


    /*-------------------pay----华丽的分割线2017年10月20日-----------------------------*/

    public static final String PAY_ALIPAY      = BASE_URL + "index.php?m=Home&c=Payapp&a=alipay_payopen";
    public static final String PAY_WECHATPAY   = BASE_URL + "index.php?m=Home&c=Payapp&a=weixinpay";
    public static final String PAY_GOLD_DETAIL = BASE_URL + "index.php?m=Home&c=Payapp&a=amountdetail";

    /*----------------------------------live---------------------------------------------*/
    public static final String URL_REAL_NAME_AUTH   = BASE_URL + "index.php?m=home&c=user&a=sub";
    public static final String URL_GET_LIVE_CHANNEL = BASE_URL + "index.php?m=Home&c=Server&a=channelList_free";
    public static final String URL_COURSE_CLASS     = BASE_URL + "index.php?m=Home&c=Course&a=index";

    public static final String URL_ADD_FOLLOW    = BASE_URL + "index.php?m=Home&c=attention&a=follow";
    public static final String URL_CANCEL_FOLLOW = BASE_URL + "index.php?m=Home&c=attention&a=unfollow";

    //直播页面创建聊天
    public static final String URL_GET_LIVE_CHAT   = BASE_URL + "index.php?m=Home&c=Payapp&a=bind_channel";
    //直播间发送礼物
    public static final String LIVE_CHAT_SEND_GIFT = BASE_URL + "index.php?m=Home&c=Payapp&a=message_channelreward";
    //关闭直播间
    public static final String URL_CLOSE_LIVE_CHAT = BASE_URL + "index.php?m=Home&c=Payapp&a=unbind_channel";
    //直播页面发送消息
    public static final String URL_SEND_LIVE_MSG   = BASE_URL + "index.php?m=Home&c=Payapp&a=message_channel";

    //主播结束直播
    public static final String URL_LIVE_OVER  = BASE_URL + "index.php?m=Home&c=Payapp&a=channel_over";
    //版本更新
    public static final String UPDATE_VERSION = BASE_URL + "index.php?m=Home&c=Version&a=updateInfo";

    //获取直播间分享信息
    public static final String GET_LIVE_SHARE_DATA = BASE_URL + "index.php/Home/Server/channel_share/cid/";


    /*-------------------------聊天相关URL----------------------------*/
    public static final String URL_BIND_CLIENT_USER  = BASE_URL + "index.php?m=Home&c=Payapp&a=bind";
    public static final String URL_SEND_CHAT_CONTENT = BASE_URL + "index.php?m=Home&c=Payapp&a=message";
    public static final String URL_CHAT_MESSAGE_LIST = BASE_URL + "index.php?m=Home&c=Payapp&a=user_messagelist";
    public static final String URL_CHAT_READ_MESSAGE = BASE_URL + "index.php?m=Home&c=Payapp&a=user_read";
    public static final String URL_UNREAD_MSG_COUNT  = BASE_URL + "index.php?m=Home&c=Payapp&a=user_message";


    /*-------------------------动态相关URL,2017年11月13日-------------------------------*/
    public static final String URL_DYNAMIC_ADD     = BASE_URL + "index.php?m=Home&c=dynamic&a=indynamic";
    public static final String URL_DYNAMIC_GET     = BASE_URL + "index.php?m=Home&c=dynamic&a=index";
    public static final String URL_DYNAMIC_DELETE  = BASE_URL + "index.php?m=Home&c=dynamic&a=deldynamic";
    public static final String URL_DYNAMIC_SHARE   = BASE_URL + "index.php?m=Home&c=Dynamic&a=share";
    public static final String URL_DYNAMIC_LIKES   = BASE_URL + "index.php?m=Home&c=Dynamic&a=dynamiclike";
    public static final String URL_DYNAMIC_COMMENT = BASE_URL + "index.php?m=home&c=Dynamic&a=Review";
    public static final String URL_COMMENT_ADD     = BASE_URL + "index.php?m=Home&c=Dynamic&a=Reviewlevel";

    public static final String URL_FRIEND_FOLLOW = BASE_URL + "index.php?m=Home&c=attention&a=attention";
    public static final String URL_FRIEND_FANS   = BASE_URL + "index.php?m=Home&c=attention&a=fans";
    public static final String URL_FRIENDS_ALL   = BASE_URL + "index.php?m=home&c=Attention&a=myfriends";

    /*---------------------------个人主页相关URL2017年11月21日-----------------------------------------------------*/
    public static final String URL_LOOK_USER_PAGER    = BASE_URL + "index.php?m=home&c=user&a=usermessage";
    public static final String URL_LOOK_USER_COURSE   = BASE_URL + "index.php?m=home&c=Mycourse&a=index";
    public static final String URL_EDIT_MY_INFO       = BASE_URL + "index.php?m=Home&c=user&a=edituser";
    public static final String URL_REPORT_ILLEGAL     = BASE_URL + "index.php?m=home&c=user&a=report";
    public static final String URL_ABOUT_WITHDRAW     = BASE_URL + "index.php?m=Home&c=Safety&a=withdraw";
    public static final String URL_DELETE_MY_VIDEO    = BASE_URL + "index.php?m=Home&c=Server&a=videoDelete";
    public static final String URL_DELETE_POST_INFO   = BASE_URL + "index.php?m=info&c=publish&a=publishDelete";
    public static final String URL_USER_POST_INFO     = BASE_URL + "index.php?m=info&c=publish&a=index";
    public static final String URL_CHANGE_INFO_SWITCH = BASE_URL + "index.php?m=info&c=publish";
    public static final String URL_USER_DYNAMIC       = BASE_URL + "index.php?m=info&c=publish&a=dynamicList";
    public static final String URL_USER_VIDEO         = BASE_URL + "index.php?m=info&c=publish&a=myvideolist";

    //企业认证
    public static final String URL_COMPANY_APPROVE = BASE_URL + "index.php?m=Info&c=Company&a=ComApprove";
    //会议报名
    public static final String URL_MEETING_APPLY = BASE_URL + "index.php?m=info&c=Info&a=InfoComment";
    //会议详情
    public static final String URL_MEETING_DETAILS = BASE_URL + "index.php?m=info&c=Info&a=InfoDetail";
    //会议报名列表
    public static final String URL_MEETING_APPLY_LIST = BASE_URL + "index.php?m=info&c=Info&a=InfoCommentList";


    public static final String URL_INFO_ADD = BASE_URL + "index.php?m=info&c=Info&a=infoAdd";//信息发布  catId=5招聘  6求职 3仪器转让

    //手机号码的正则表达式
    public static final String STR_PHONE_REGEX1 = "^1[3-8]\\d{9}$";
    public static final String STR_PHONE_REGEX2 = "^1(3|4|5|7|8)[0-9]\\d{8}$";
    public static final String STR_PHONE_REGEX3 = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";

    public static final String YAYA_PIC                   = "https://gss3.bdstatic.com/7Po3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=54f57c850db30f24319aec03f894d192/2f738bd4b31c87018ef76502257f9e2f0608ff95.jpg";
    public static final String PIC_MEIYAYA_LOGO_RECTANGLE = BASE_URL + "Public/Home/img/myy_logo_qq.png";

    public static final int LIVE_BANNER_DELAYED_TIME = 3000;

    public static final int INTENT_DYNAMIC_LOOK_DETAIL = 0;
    public static final int INTENT_DYNAMIC_ADD_COMMENT = 1;
    public static       int webSocketConnectNumber     = 0;
    public static       int RECONNECT_DELAYED_TIME     = 500;

    public static final String isLoginState     = "isLoginState";
    public static final String phoneNumber      = "phoneNumber";
    public static final String password         = "password";
    public static final String userID           = "userID";
    public static final String CLIENT_ID        = "client_id";
    public static final String USER_NAME        = "user_name";
    public static final String USER_HEAD_IMAGE  = "user_head_image";
    public static final String USER_GOLD_NUMBER = "user_gold_number";
    public static final String PUSEH_MESSAGE    = "push_message";
    public static final String IS_APPROVE       = "is_approve";
    public static final String LECTURER_lSTATUS = "lecturer_lstatus";


    public static final String SOCKET_URL = isLive ? "ws://app.xasfemr.com:8282" : "ws://testapp.xasfemr.com:8282"; //ws://app.xasfemr.com:8282

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx9b8926da4f98936f"; //美页圈AppID wx9b8926da4f98936f

    public class EventBus {
        public static final String CHAT_MSG_RECEIVE          = "ChatMsgReceive"; //私信聊天
        public static final String CHAT_LIVE_ANCHOR          = "chat_live_anchor"; //直播主播页面聊天
        public static final String CHAT_LIVE_SEND_GIFT_BACK  = "chat_live_send_gift_back"; //直播页面发送礼物回调
        public static final String CHAT_LIVE_SEND_GIFT       = "chat_live_send_gift"; //直播页面发送礼物
        public static final String GET_ATTENTION             = "get_attention"; //发起关注取消关注
        public static final String PLAYER_COMPLETED_OR_ERROR = "player_completed_or_error"; //播放器播放结束或错误
        public static final String LIVE_COMPLETED_OR_ERROR   = "live_completed_or_error"; //直播播放结束或错误
        public static final String PUT_CLIENT_ID             = "put_client_id"; //直播播放结束或错误
        public static final String LIVE_HORSE_NUMS_CHANGE    = "live_horse_nums_change"; //直播间人数变化
        public static final String LIVE_END                  = "live_end"; //结束直播
        public static final String LIVE_GOLD_INIT            = "live_gold_init"; //视频第一帧显示
        public static final String LIVE_USER_LEAVE           = "live_user_leave"; //用户离开
        public static final String RECEIVE_MSG_UPDATA_DOT    = "receive_msg_updata_dot"; //收到消息,更新小红点

        /*微信支付返回信息*/
        public static final String WXCALLBACK = "wechat_pay_result";

        //更新用户信息
        public static final String UPDATE_USER_DATE = "update_user_date";

        public static final String CHANGE_PLAYER_PATH           = "change_player_path";//切换播放源
        public static final String UPDATE_EXCELLENT_COURSE_LIST = "update_excellent_course_list";//刷新精品课程列表

        public static final String NETWORK_CHANGE_LISTENER = "network_change_listener";//网络切换


        public static final String SHARE_NEWS = "share_news";//资讯分享
        public static final String SHARE_LIVE = "share_live";//直播间分享

        public static final String DELETE_VIDEO_comment = "delete_video_comment";//删除视频评论

        public static final String UPDATE_MESSAGE_COUNT = "update_message_count";//更新小红点


    }


}
