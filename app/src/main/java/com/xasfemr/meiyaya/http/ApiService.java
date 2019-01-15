package com.xasfemr.meiyaya.http;


import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.module.college.protocol.AttentionProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CollegeDataProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CollegeEventProtocol;
import com.xasfemr.meiyaya.module.college.protocol.CommentProttocol;
import com.xasfemr.meiyaya.module.college.protocol.CourseProtocolList;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentListProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HomeInterceptionProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HomeRecommendProtocol;
import com.xasfemr.meiyaya.module.college.protocol.HotCourseOrMemberProtocol;
import com.xasfemr.meiyaya.module.college.protocol.LectureProtocol;
import com.xasfemr.meiyaya.module.college.protocol.PlaybackListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.CourseListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.HomeBannerProtocol;
import com.xasfemr.meiyaya.module.home.protocol.HomeNewsprotocol;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentDetailProtocol;
import com.xasfemr.meiyaya.module.home.protocol.InstrumentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.MemberCourseHotListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.MemberCoursePlaybackListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.PostProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruimentDetailProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RecruitmentListProtocol;
import com.xasfemr.meiyaya.module.home.protocol.RequestJobListProtocol;
import com.xasfemr.meiyaya.module.live.protocol.LiveMsgProtocol;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;


/**
 * 描述：
 * Created by sen.luo on 2017/6/30.
 */

public interface ApiService {

    //直播列表
    @POST("index.php?m=Home&c=server&a=channelList")
    Observable<BaseProtocol<CourseProtocolList>> getCourseList(@QueryMap Map<String,String> map);

    //回放列表
    @POST("index.php?m=Home&c=server&a=videolist")
    Observable<BaseProtocol<PlaybackListProtocol>>getPlaybackList(@QueryMap Map<String,String> map);

    //讲师列表
    @GET("index.php?m=Home&c=Lecturer&a=index")
    Observable<BaseProtocol<ArrayList<LectureProtocol>>> getLectureList(@QueryMap Map<String,String> map);

    // 资料列表
    @GET("index.php?m=Home&c=material&a=material")
    Observable<BaseProtocol<ArrayList<CollegeDataProtocol>>>getCollegeDataList();

    //活动列表
    @GET("index.php?m=Home&c=Activity&a=Index")
    Observable<BaseProtocol<ArrayList<CollegeEventProtocol>>> getCollegeEventList();

    //课程列表
    @GET("index.php?m=Home&c=Course&a=index")
    Observable<BaseProtocol<ArrayList<CourseListProtocol>>> getCourseDataList();

    //课程--直播列表
    @GET("index.php?m=Home&c=Server&a=channelList") //
    Observable<BaseProtocol<MemberCourseHotListProtocol>>getMemberHotList(@QueryMap Map<String,String> map);

    //课程--回放列表
    @GET("index.php?m=Home&c=Server&a=videolist")
    Observable<BaseProtocol<MemberCourseHotListProtocol>>getMemberPlaybackList(@QueryMap Map<String,String> map);

    //关注
    @GET("index.php?m=Home&c=attention&a=follow")
    Observable<BaseProtocol<AttentionProtocol>>getAttention(@QueryMap Map<String,String> map);

    //取消关注
    @GET("index.php?m=Home&c=attention&a=unfollow")
    Observable<BaseProtocol<AttentionProtocol>>cancelAttention(@QueryMap Map<String,String> map);


    //是否存在关注状态
    @GET("index.php?m=Home&c=Attention&a=Isfollow")
    Observable<BaseProtocol<AttentionProtocol>>ifAttention(@QueryMap Map<String,String> map);

    //直播页面创建聊天index.php?m=Home&c=Payapp&a=bind_channel
    @GET("index.php?m=Home&c=Payapp&a=bind_channel")
    Observable<BaseProtocol<String>>putClient(@QueryMap Map<String,String> map);

    //首页 热门课程 更多
    @GET("index.php?m=Home&c=Server&a=myvideolist&hot=1")
    Observable<BaseProtocol<HotCourseOrMemberProtocol>> getHotCourseMoreList(@QueryMap Map<String,String> map);

    //首页 会员课程 更多
    @GET("index.php?m=Home&c=server&a=myvideolist&is_member=1")
    Observable<BaseProtocol<HotCourseOrMemberProtocol>> getMemberMoreList(@QueryMap Map<String,String> map);


    //首页 会员--直播列表
    @GET("index.php?m=Home&c=server&a=channelList&is_member=1")
    Observable<BaseProtocol<MemberCourseHotListProtocol>>getMemberLiveList(@QueryMap Map<String,String> map);

    //首页 会员--回放列表
    @GET("index.php?m=Home&c=server&a=myvideolist&is_member=1")
    Observable<BaseProtocol<MemberCourseHotListProtocol>>getMemberbackList(@QueryMap Map<String,String> map);

    //提现
    @POST("index.php?m=Home&c=Payapp&a=lectureMoney")
    Observable<BaseProtocol<String>> getWithDraw(@QueryMap Map<String ,String> map);

    //精品课程列表
    @GET("index.php?m=Home&c=Server&a=videoBest")
    Observable<BaseProtocol<ArrayList<ExcellentListProtocol>>> getExcellentList(@QueryMap Map<String ,String> map);

    //精品课程收藏 /取消收藏
    @POST("index.php?m=Home&c=Collect&a=Addcollect")
    Observable<BaseProtocol<String>>getCollectData(@QueryMap Map<String,String> map);

    //直播间聊天发送消息
    @POST("index.php?m=Home&c=Payapp&a=message_channel")
    Observable<BaseProtocol<String>>liveSendMessage(@QueryMap Map<String,String> map);

    //获取直播间所有信息
    @POST("index.php?m=Home&c=Server&a=channelStats")
    Observable<BaseProtocol<LiveMsgProtocol>> getLiveMsg(@QueryMap Map<String,String> map);

    //直播间分享
    @POST("index.php?m=Home&c=Server&a=share")
    Observable<BaseProtocol<Object>> getLiveShareSuccess(@QueryMap Map<String,String>map);

    //资讯分享
    @POST("index.php?m=home&c=Index&a=share")
    Observable<BaseProtocol<Object>> getNewsShareSuccess(@QueryMap Map<String,String> map);

    //视频评论列表
    @POST("/index.php?m=home&c=Reviewseeding&a=Review")
    Observable<BaseProtocol<ArrayList<CommentProttocol>>> getCommentList(@QueryMap Map<String,String> map);

    //视频发表评论
    @POST("index.php?m=home&c=Reviewseeding&a=reviewseeding")
    Observable<BaseProtocol<Object>> sendComment(@QueryMap Map<String,String>map);

    //视频删除评论
    @POST("index.php?m=home&c=Reviewseeding&a=delreview")
    Observable<BaseProtocol<Object>> deleteComment(@QueryMap Map<String,String> map);

    //获取首页banner 和活动
    @GET("index.php?m=Home&c=Index&a=banner")
    Observable<BaseProtocol<HomeBannerProtocol>>getHomeBanner();

    //获取首页资讯
    @GET("index.php?m=Home&c=Index&a=test")
    Observable<BaseProtocol<ArrayList<HomeNewsprotocol>>> getHomeNews();


    //获取某个分类下的筛选项
    @GET("index.php?m=info&c=category&a=categoryList")
    Observable<BaseProtocol<PostProtocol>> getFilterList(@QueryMap Map<String,String> map);  //传参catId  =5招聘  =6求职 =8店铺转让 =9店铺求租  =2求购仪器 =3转让仪器


    //获取招聘列表
    @GET("index.php?m=info&c=Info&a=InfoList")
    Observable<BaseProtocol<ArrayList<RecruitmentListProtocol>>> getRecruitmentList(@QueryMap Map<String,String> map);

    //获取求职列表
    @GET("index.php?m=info&c=Info&a=InfoList")
    Observable<BaseProtocol<ArrayList<RequestJobListProtocol>>> getRequestJobList(@QueryMap Map<String,String> map);

    //获取仪器转让列表
    @POST("index.php?m=info&c=Info&a=InfoList")
    Observable<BaseProtocol<ArrayList<InstrumentListProtocol>>> getInstrumentList(@QueryMap Map<String,String> map);

    //获取招聘详情
    @GET("index.php?m=info&c=Info&a=InfoDetail")
    Observable<BaseProtocol<RecruimentDetailProtocol>> getRecruimentDetail(@QueryMap Map<String,String> map);


    //获取仪器转让详情
    @GET("index.php?m=info&c=Info&a=InfoDetail")
    Observable<BaseProtocol<InstrumentDetailProtocol>> getInstrumentDetail(@QueryMap Map<String,String> map);

    //获取首页推荐热门信息
    @POST("index.php?m=info&c=index&a=index")
    Observable<BaseProtocol<ArrayList<HomeInterceptionProtocol>>> getHomeRecommendList(@QueryMap Map<String,String> map);

//    //获取求职详情
//    @POST("")



//    //信息发布  catId=5招聘  6求职 3仪器转让
//    @FormUrlEncoded
//    @POST("index.php?m=info&c=Info&a=infoAdd")
//    Observable<BaseProtocol<Object>> putMsgRelease(@QueryMap Map<String,String> map);
}
