package com.jgkj.bxxc.tools;

/**
 * Created by tongshoujun on 2017/5/11.
 */

public class Urls {

    //私教
    public static int  private_education = 1;

    //VIP
    public static int  vip_education = 2;

    //至尊
    public static int  extreme_education = 3;

    //预约教练
    public static String ptcourse = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/ptcourse";

    // 剩余学时
    public static String Hours = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/Hours";

    // 取消预约
    public static String quxiaoApply = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/quxiaoApply";

    // 套餐信息
    public static String packages = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/package";

    // 购买学时
    public static String buyClassHour = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/buyClassHour";

    // 确定预约
    public static String stuAppointmentpackage = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/stuAppointmentpackage";

    // 设置支付密码
    public static String setPayPwd = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/setPayPwd";

    // 验证旧的支付密码
    public static String verPayPwd = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/verPayPwd";

    // 修改支付密码
    public static String savePayPwd = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/savePayPwd";

    // 发送验证码
    public static String remsg = "http://www.baixinxueche.com/index.php/Home/Apitokenmsg/remsg";

    // 验证验证码
    public static String judgePayPwdmsg = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/judgePayPwdmsgAndroid";

    // 忘记支付密码
    public static String forgetSavePayPassword = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/forgetSavePayPassword";

    // 重置密码
    public static String repassword = "http://www.baixinxueche.com/index.php/Home/Apialltoken/repassword";

    // 获取银行卡信息
    public static String getBank = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/getBank";

    // 添加银行卡信息
    public static String addBankAndroid = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/addBankAndroid";

    //我的教练
//    public static String myCoachUrl = "http://www.baixinxueche.com/index.php/Home/Apitokenpt/myCoach";
    public static String myCoachUrl="http://www.baixinxueche.com/index.php/Home/Hotsearch/myCoach";
    //微信支付
    public static String wxapppay = "http://www.baixinxueche.com/index.php/Home/wxapppay/pay";

    //提现
    public static String tixian = "http://www.baixinxueche.com/index.php/Home/Apitokenupdata/tixian";

    //首页轮播图
    public static String bannerpicsands = "http://www.baixinxueche.com/index.php/Home/Apitoken/bannerpicsandsfx";

    //陪驾
    public static String peijia = "http://www.baixinxueche.com/index.php/Home/Apitokenpj/peijia";

    //陪练的教练详情页
    public static String pjcoach = "http://www.baixinxueche.com/index.php/Home/Apitokenpj/pjcoach";

    //陪驾记录
    public static String pjShowApply = "http://www.baixinxueche.com/index.php/Home/Apitokenpj/pjShowApply";

    //未陪驾记录
    public static String pjShowApplyNotTo = "http://www.baixinxueche.com/index.php/Home/Apitokenpj/pjShowApplyNotTo";

    //支付宝直接购买学时
    public static String alibuyclass = "http://www.baixinxueche.com/index.php/Home/wxapppay/alibuyclass";

    //微信直接购买学时
    public static String wxbuyclass = "http://www.baixinxueche.com/index.php/Home/wxapppay/wxbuyclass";

    //银联购买学时
    public static String unionbuyclass = "http://www.baixinxueche.com/index.php/Home/Unionpay/unionBuyClass";

    //科目一题目
    public static String sub = "http://www.baixinxueche.com/index.php/Home/Apitokenptchoose/sub";

    //科目四题目
    public static String subfour = "http://www.baixinxueche.com/index.php/Home/Apitokenptchoose/subfour";

    //题库版本
    public static String subjectVersion = "http://www.baixinxueche.com/index.php/Home/Apitokenptchoose/subjectVersion";

    //测试
//    public static String alibuyclass="http://www.baixinxueche.com/index.php/Home/wxapppay/alibuyclassExam";
//    public static String wxbuyclass="http://www.baixinxueche.com/index.php/Home/wxapppay/wxbuyclassExam";


    //私教中心请求约课数据
    public static String priteamptcourse = "http://www.baixinxueche.com/index.php/Home/Hotsearch/ptcourse ";

    //私教中心确定约课
    /**
     * * uid
     * token
     * time_slot 时间段
     * day 时间
     * cid 教练的id
     * tid 私教中心的id
     * class_style 私教 or 陪练
     * package_id  套餐的类型；  私教班是 1 2 3 4 四种套餐类型    经典班 是 5
     */
    public static String priteamSurecourse = "http://www.baixinxueche.com/index.php/Home/Hotsearch/appoint";
    /**
     * 私教中心取消约课
     *  * uid
     * token
     * time_slot 时间段
     * day 时间
     * cid 教练的id
     * tid 私教中心的id
     */
    public static String priteamCancelcourse="http://www.baixinxueche.com/index.php/Home/Hotsearch/cancelAppoint";

    //银联支付充值
    public  static String unionRecharge="http://www.baixinxueche.com/index.php/Home/Unionpay/unionChongZhi";


}
