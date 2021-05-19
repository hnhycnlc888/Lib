package yzkf.enums;

/**
 * 活跃行为上报功能行为枚举
 * 
 * @author qiulw
 *
 */
public enum ActiveOperation {
	/** [0]无业务行为**/
    Nothing(0),
    /** [2001]推荐/邀请**/
    Recommend(2001),
    /** [2002]抽奖并中奖**/
    LotterySuccess(2002),
    /** [2003]抽奖未中奖**/
    lotteryFail(2003),
    /** [2004]领奖/兑奖**/
    GetLottery(2004),
    /** [2005]传递**/
    Transfer(2005),
    /** [2006]答题**/
    Answer(2006),
    /** [2007]猜奖**/
    GuessLottery(2007),
    /** [2008]投票**/
    votes(2008),
    /** [2009]分享 （分享内容，分享微博）**/
    Share(2009),
    /** [2010]领取优惠劵**/
    GetCoupon(2010),
    /** [2011]阅读账单**/
    ReadBill(2011),
    /** [2012]增加活动积分**/
    AddIntegral(2012),
    /** [2013]短信登录**/
    SMSLogin(2013),
    /** [3001]MM数据业务**/
    MMOperation(3001),
    /** [3002]飞信数据业务**/
    FetionOperation(3002),
    /** [3003]飞聊数据业务**/
    FeiliaoOperation(3003),
    /** [3004]移动微博数据业务**/
    WeiboOperation(3004),
    /** [3005]移动通信录数据业务**/
    AdrOperation(3005),
    /** [3006]PE数据业务**/
    PEOperation(3006);
	private int value;
	ActiveOperation(int value){
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}
}
