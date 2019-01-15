package com.xasfemr.meiyaya.module.home.protocol;

import com.xasfemr.meiyaya.base.protocol.BaseProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 */

public class CourseListProtocol extends BaseProtocol{


    /**
     * id : 1
     * cname : 美业分析
     * list : [{"id":"1","cname":"标准划分","images":"http://app.xasfemr.com/Public/Uploads/2017-11-13/5a08ff0a1780f.png","desc":"按照严格标准准确划分美容行业的不同盈利种类，为经营者科普支撑店面正常运营的基础理论知识，整理思路，全面了解行业内信息，知己知彼，百战不殆。","net_typeid":"132858","url":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=videolist&type=132858","url_channel":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=channelList&type=132858"},{"id":"2","cname":"战略分析","images":"http://app.xasfemr.com/Public/Uploads/2017-11-13/5a08ff2199a3e.png","desc":"根据专业知识和从业经验帮助美容院分析现状、展望未来。秉持可持续发展理念，具体问题具体分析，通过对美容院目前发展现状的分析制定长远发展战略，培养美容院的全局观，助其打赢每一场没有硝烟的战争。","net_typeid":"132857","url":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=videolist&type=132857","url_channel":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=channelList&type=132857"},{"id":"3","cname":"产品质量","images":"http://app.xasfemr.com/Public/Uploads/2017-11-13/5a08ff2e7d3d2.png","desc":"为美容院提供鉴别产品质量以及挑选产品的小技巧，教授如何更好的将产品的功能作用发挥到最大化，通过产品吸引顾客，留住顾客。","net_typeid":"135019","url":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=videolist&type=135019","url_channel":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=channelList&type=135019"},{"id":"4","cname":"品牌形象","images":"http://app.xasfemr.com/Public/Uploads/2017-11-13/5a08ff3cd4cee.png","desc":"根据美容院的具体情况设计符合现状的形象宣传，增加店面特点，树立品牌形象，提高辨识度；由此吸引大量精准消费群体。","net_typeid":"132856","url":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=videolist&type=132856","url_channel":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=channelList&type=132856"},{"id":"5","cname":"国际美容","images":"http://app.xasfemr.com/Public/Uploads/2017-11-13/5a08ff4b9c75a.png","desc":"宏观分析国内外美业现状，了接美容行业目前在国内外的整体发展水平，借鉴国内外的成功案例，引进先进的发展理念与经营策略，帮助美容院迅速成长。","net_typeid":"132855","url":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=videolist&type=132855","url_channel":"http://app.xasfemr.com/index.php?m=Home&c=Server&a=channelList&type=132855"}]
     */

    public String id;
    public String cname;
    public ArrayList<CourseDataProtocol> list;



}
