package com.util;

/**
 * @author Jun
 * @time 2017/1/8  21:39
 * @desc
 */
public class Constance {

    public static final String NORMAL_GOODS = "normal_goods";
    public static final String JUGAO_GOODS = "juhao_goods";
    public static final String ACCOUNT_CONFLICT_INT = "ACCOUNT_CONFLICT_INT";
    public static final int REQUEST_CODE = 400;
    public static final String BLUETOOTH_LIGHT_COUNT = "BLUETOOTH_LIGHT_COUNT";
    public static String OK="0";

    public static String USERCODE = "USERCODE";

    public static String USERCODEID = "USERCODEID";

    public static String all_attr_list="all_attr_list";

    public static String attrVal="attrVal";

    public static String attr_value="attr_value";

    public static String scenelist="scenelist";

    public static String scene="scene";

    public static String original_img="original_img";

    public static String name="name";

    public static String address="address";

    public static String logistics="logistics";

    public static String UpdateModele="UpdateModele";

    public static String mobile="mobile";

    public static String regions="regions";

    public static String img_url="img_url";

    public static String goodsList="products";

    public static String consignees="consignees";

    public static final String TITLE = "title";

    public static final String VALUE = "value";

    public static final String CODE = "code";

    public static String[] SEXTYPE= new String[]{"男", "女"};

    public static String[] CAMERTYPE= new String[]{"拍照", "从手机相册选择"};

    public static String[] LOGTYPE= new String[]{"顺丰速运", "圆通快递","京东快递","申通快递","韵达快递","汇通快递","中通快递","宅急送","天天快递","E邮宝"};

    /**
     * 拍照
     */
    public static final int FLAG_UPLOAD_TAKE_PICTURE = 10;
    /**
     * 选择图片
     */
    public static final int FLAG_UPLOAD_CHOOICE_IMAGE = 12;
    /**
     * 剪切
     */

    public static final String CAMERA_PATH = "juhao/camera";//拍照保存在sd卡下的文件夹名称

    public static final int PHOTO_WITH_DATA = 1; // 从SD卡中得到图片

    public static final int PHOTO_WITH_CAMERA = 2;// 拍摄照片

    public static final int FLAG_UPLOAD_IMAGE_CUT = 13;

    public static int MESSAGE=101;

    public static final int FROMDIY = 0X001;

    public static final int FROMDIY02 = 0X002;

    public static final int FROMADDRESS = 0X003;

    public static final int FROMLOGISTICS = 0X004;

    public static final int FROMLOG= 0X005;

    public static final int FROMFILTER= 0X006;

    public static final int FROMSCHEME= 0X007;

    public static final int CARTCOUNT = 0X008;

    public static final int  PROPERTY= 0X009;

    public static final int  PROPCAMERA= 0X010;

    public static final int  FROMCONSIGNMENT= 0X011;

    public static final String ISSELECTGOODS = "isselectgoods";

    public static final String ISSELECTSCRENES = "isselectscrenes";

    public static final String error_desc="error_desc";

    public static final String isSELECTADDRESS="isSELECTADDRESS";

    public static final String isSelectLogistice="isSelectLogistice";

    public static final String USERNAME="userName";

    public static final String username="username";

    public static final String TOKEN="token";

    public static final String banners="banners";

    public static final String photo="photo";

    public static final String FROMPHOTO="FROMPHOTO";

    public static final String large="large";

    public static final String link="link";

    public static final String default_photo="default_photo";

    public static final String price="price";

    public static final String current_price="current_price";

    public static final String avatar="avatar";

    public static final String nickname="nickname";

    public static final String gender="gender";

    public static final String age="age";

    public static final String birthday="birthday";

    public static final String user="user";

    public static final String id="id";

    public static final String consignee="consignee";

    public static final String product="product";

    public static final String properties="properties";

    public static final String property="property";

    public static final String attachments="attachments";

    public static final String attrs="attrs";

    public static final String attr_name="attr_name";

    public static final String goods_desc="goods_desc";

    public static final String photos="photos";

    public static final String is_liked="is_liked";

    public static final String categories="categories";

    public static final String SCENE="scene";

    public static final String attr_price="attr_price";

    public static final String amount="amount";

    public static final String goods_groups="goods_groups";

    public static String goods="goods";

    public static String SCENE_URL="http://zhongshuo.bocang.cc/Public/uploads/scene/";

    public static String is_default="is_default";

    public static String goodNums="goodNums";

    public static String money="money";

    public static String tel="tel";

    /**
     * 本地数据库名称
     */
    public static final String DB_NAME = "local.db";

    /**
     * 本地数据库版本号
     */
    public static final int DB_VERSION = 1;

    public static String thumbs="thumbs";

    public static String thumb="thumb";

    public static String goods_attr_list="goods_attr_list";

    public static String filter_attr_name="filter_attr_name";

    public static String attr_list="attr_list";

    public static String filter_attr="filter_attr";

    public static String server="server";

    public static String cell_phone="cell_phone";

    public static String yaoqing="yaoqing";

    public static String articles="articles";

    public static String title="title";

    public static String url="url";

    public static String notices="notices";

    public static String fangan="fangan";

    public static String error_code="error_code";

    public static String style="style";

    public static String space="space";

    public static String content="content";

    public static String path="path";

    public static String goodsInfo="goodsInfo";

    public static final String MESSAGE_RECEIVED_ACTION = "bc.juhao.com.MESSAGE_RECEIVED_ACTION";

    public static final String KEY_MESSAGE = "message";

    public static final String KEY_EXTRAS = "extras";

    public static String orders="orders";

    public static String status="status";

    public static String sn="sn";

    public static String total_price="total_price";

    public static String created_at="created_at";

    public static String total="total";

    public static String total_amount="total_amount";

    public static String product_price="product_price";

    public static String order="order";

    public static String order_type="order_type";

    public static String VIPSUBJECT="奥克特商城";

    public static String partner="partner";

    public static String seller_id="seller_id";

    public static String private_key="private_key";

    public static String notify_url="notify_url";

    public static String alipay="alipay";

    public static String app_img="app_img";

    public static String phone_img="phone_img";

    public static String custom="custom";

    public static String share_url="share_url";

    public static String JSON="JSON";

    public static String img="img";

    public static String ISFIRSTISTART="ISFIRSTISTART";

    public static String data="data";

    public static String user_name="user_name";

    public static String phone="phone";

    public static String qq="qq";

    public static String weixin="weixin";

    public static String shop="shop";

    public static String latval="latval";

    public static String longval="longval";

    public static String user_id="user_id";

    public static String SHARE_PATH="SHARE_PATH";

    public static String SHARE_IMG_PATH="SHARE_IMG_PATH";

    public static String img_path="img_path";

    public static String productshape="productshape";

    public static String productlist="productlist";

    public static String scaleType="scaleType";

    public static String SHOWHELP="SHOWHELP";

    public static String level="level";


    public static String MESSAGE_SOUND="message_sound";

    public static String MESSAGE_NOTIFICATION="MESSAGE_NOTIFICATION";

    public static String MESSAGE_VIBRATE="MESSAGE_VIBRATE";

    public static final int CHATTYPE_SINGLE = 1;

    public static final int CHATTYPE_GROUP = 2;

    public static final int CHATTYPE_CHATROOM = 3;

    public static final String ACCOUNT_REMOVED = "帐号已经被移除";

    public static final String ACCOUNT_CONFLICT = "帐号在其他设备登录";

    public static final String ACCOUNT_FORBIDDEN="当前网络不可用，请检查网络设置";

    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";

    public static final String GROUP_USERNAME = "item_groups";

    public static final String CHAT_ROBOT = "item_robots";

    public static final String CHAT_ROOM = "item_chatroom";

    public static String USER_NICE="user_nice";

    public static String USER_ICON="user_icon";

    public static String User_ID="user_id";

    public static String SEARCH_ORDER="search_order";

    public static String EMREGIEST="EMREGIEST";

    public static String USERID="USERID";

    public static String ALIPAY="ALIPAY";

    public static String ALIPAYNAME="ALIPAYNAME";

    public static String state="state";

    public static String isbuy="isbuy";

    public static String add_time="add_time";

    public static String admin_note="admin_note";

    public static String process_type="process_type";

    public static String commission="commission";

    public static String account="account";

    public static String is_paid="is_paid";

    public static String postscript="postscript";

    public static String pdfType="pdfType";

    public static String FROMTYPE="FROMTYPE";

    public static String sort="sort";

    public static String type="type";

    public static String goods_id="goods_id";

    public static String parent_name="parent_name";

    public static String date="date";

    public static String shipping="shipping";

    public static String customer="customer";

    public static String customer_level="customer_level";

    public static String article_type="article_type";

    public static String joined_at="joined_at";

    public static String parent="parent";

    public static String invite_code="invite_code";

    public static String ISYIJI="ISYIJI";

    public static String original_price="original_price";

    public static String discount="discount";

    public static String reviews="reviews";

    public static String author="author";

    public static String updated_at="updated_at";

    public static String clike="clike";

    public static String order_id="order_id";

    public static String IMAGESHOW="IMAGESHOW";

    public static String IMAGEPOSITION="IMAGEPOSITION";

    public static String group_buy="group_buy";

    public static String products="products";

    public static String end_time="end_time";

    public static String ext_info="ext_info";

    public static String restrict_amount="restrict_amount";

    public static String start_time="start_time";

    public static String price_ladder="price_ladder";

    public static String ad_code="ad_code";
    public static String is_xiangou="is_xiangou";
    public static String category="category";
    public static String is_finished="is_finished";
    public static String is_jh="is_jh";
    public static String version="version";
    public static String text="text";
    public static String push_control="push_control";
    public static String thumbs2="thumbs2";
    public static String is_discount="is_discount";
    // appid
    public static final String APP_ID = "wxe5dbf8785c4ec928";// yum

    // 商户号
    public static final String MCH_ID = "1495147682";// yum

    // API密钥，在商户平台设置
    public static final String API_KEY = "51b49eb6c84cd25c58392c8164906968";// yum

    //微信统一下单接口
    public static final String UNIFIED_ORDER ="https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static String rank="rank";

    public static String wxpay="wxpay";

    public static final int WEISUCCESS = 0X010;

    public static final int WEIFAIL = 0X011;

//    public static final int  PROPERTY= 0X009;
    public static String remark="remark";
    public static String cproperty="cproperty";
    public static String curl="curl";
    public static String warn_number="warn_number";
    public static String scene_id="scene_id";
    public static String sku="sku";
//    public static String shop_moblie="shop_moblie";
    public static String shop_mobile="shop_mobile";
    public static String paged="paged";
    public static String page="page";
    public static String apply_remember="apply_remember";
    public static String is_look="is_look";
    public static String movie="movie";
    public static String file_url="file_url";
    public static String display="display";
    public static String desc="desc";
    public static String app_key="24938534";
    public static String iotId="iotId";
    public static String statu="statu";
    public static String PowerSwitch="PowerSwitch";
    public static String value="value";
    public static String night="night";
    public static String nightswitch="nightswitch";
    public static String socket="socket";
    public static String lock="lock";
    public static String pwd="pwd";
    public static String islogin="islogin";
    public static String days="days";
    public static String hour="hour";
    public static String minute="minute";
    public static String PowerSwitch_1="PowerSwitch_1";
    public static String PowerSwitch_2="PowerSwitch_2";
    public static String PowerSwitch_3="PowerSwitch_3";
    public static String PowerSwitch_4="PowerSwitch_4";
    public static String ColorTemperature="ColorTemperature";
    public static String success="success";
    public static String count_down_json="count_down_json";
    public static String LightSwitch="LightSwitch";
    public static String actions="actions";
    public static String bluetooth="bluetooth";
    public static String isbluetooth="isbluetooth";
    public static String identityId="identityId";
    public static String RecordList="RecordList";
    public static String is_national="is_national";
    public static String dnGuideIcon="dnGuideIcon";
    public static String helpCopywriting="helpCopywriting";
    public static String linkIndentityId="linkIndentityId";
    public static String list="list";
    public static String is_first="isfirst";
    public static String is_not_first="isnotfirst";
    public static String is_open="isopen";
    public static String uri="uri";
    public static String propertyName="propertyName";
    public static String propertyValue="propertyValue";
    public static String propertyName_desc="propertyName_desc";
    public static String propertyValue_desc="propertyValue_desc";
    public static String icon="icon";
    public static String color_img="color_img";
    public static String color="color";
    public static String is_edit="is_edit";
    public static String scene_type="scene_type";
    public static String iconColor="iconColor";
    public static String description="description";
    public static String identifier="identifier";
    public static String localizedProductName="localizedProductName";
    public static String productImage="productImage";
    public static String localizedPropertyName="localizedPropertyName";
    public static String deviceNickName="deviceNickName";
    public static String localizedCompareValueName="localizedCompareValueName";
    public static String productKey="productKey";
    public static String propertyItems="propertyItems";
    public static String actionsJson="actionsJson";
    public static String params="params";
    public static String second="second";
    public static String is_auto="isauto";
    public static String is_condition="iscondition";
    public static String timing_str="timing_str";
    public static String timing="timing";
    public static String tmiming_day="timing_day";
    public static String timing_cron="timing_cron";
    public static String timing_time="timing_time";
    public static String timing_date="timing_date";
    public static String bg_pic="bg_pic";
    public static String actionPic="actionPic";
    public static String conditionPic="conditionPic";
    public static String deviceName="deviceName";
    public static String zdhimg="zdhimg";
    public static String caConditionsJson="caConditionsJson";
    public static String cron="cron";
    public static String weekStr="weekStr";
    public static String position="postion";
    public static String actionsUrl="actionsUrl";
    public static String dev_nums="dev_nums";
    public static String beginDate="beginDate";
    public static String endDate="endDate";
    public static String repeat="repeat";
    public static String format="format";
    public static String showinhome="showinhome";
    public static String column="column";
    public static String compareValue="compareValue";
    public static String sceneId="sceneId";
    public static String valid="valid";
    public static String dataType="dataType";
    public static String specs="specs";
    public static String functionList="functionList";
    public static String msg="msg";
    public static String Doorbell="Doorbell";
    public static String Random="Random";
    public static String OpenLock="OpenLock";
    public static String BatteryPercentage="BatteryPercentage";
/*
            {
    {
        "goods_attr_list": [
        {
            "filter_attr_name": "类型",
                "index": 0,
                "attr_list": [
            {
                "attr_value": "全部",
                    "id": 0
            },
            {
                "attr_value": "台灯",
                    "id": 6009
            },
            {
                "attr_value": "吊灯",
                    "id": 121
            },
            {
                "attr_value": "吸顶灯",
                    "id": 156
            },
            {
                "attr_value": "壁灯",
                    "id": 5486
            },
            {
                "attr_value": "落地灯",
                    "id": 1331
            },
            {
                "attr_value": "镜前灯",
                    "id": 6306
            }
      ]
        },
        {
            "filter_attr_name": "空间",
                "index": 1,
                "attr_list": [
            {
                "attr_value": "全部",
                    "id": 0
            },
            {
                "attr_value": "书房",
                    "id": 243
            },
            {
                "attr_value": "儿童房",
                    "id": 38681
            },
            {
                "attr_value": "卧室",
                    "id": 130
            },
                "attr_value": "卫浴间",
                    "id": 26718
            },
            {
                "attr_value": "客厅",
                    "id": 128
            },
            {
                "attr_value": "楼梯\/拐角",
                    "id": 20767
            },
            {
                "attr_value": "玄关\/过道\/廊道",
                    "id": 152
            },
            {
                "attr_value": "阳台",
                    "id": 20766
            },
            {
                "attr_value": "餐厅",
                    "id": 129
            },
            {
                "attr_value": "高层\/复式\/别墅",
                    "id": 26346
            }
      ]
        },
        {
            "filter_attr_name": "材质",
                "index": 2,
                "attr_list": [
            {
                "attr_value": "全部",
                    "id": 0
            },
            {
                "attr_value": "ABS",
                    "id": 32938
            },
            {
                "attr_value": "ABS+铝合金",
                    "id": 18069
            },
            {
                "attr_value": "ABS航空塑料",
                    "id": 39389
            },
            {
                "attr_value": "PC",
                    "id": 32503
            },
            {
                "attr_value": "PP",
                    "id": 18675
            },
            {
                "attr_value": "PP料",
                    "id": 29476
            },
            {
                "attr_value": "PP料+亚克力",
                    "id": 18192
            },
            {
                "attr_value": "PVC",
                    "id": 46098
            },
            {
                "attr_value": "PVC+亚克力",
                    "id": 46543
            },
            {
                "attr_value": "S金+双色亚克力",
                    "id": 25523
            },
            {
                "attr_value": "S金不锈钢+亚克力+磨砂玻璃罩",
                    "id": 44153
            },
            {
                "attr_value": "不锈钢",
                    "id": 26297
            },
            {
                "attr_value": "不锈钢+粘沙亚克力+K9水晶方珠",
                    "id": 44760
            },
            {
                "attr_value": "不锈钢+超白钢化玻璃",
                    "id": 25784
            },
            {
                "attr_value": "不锈钢+超白钢化玻璃+K9干邑色水晶方珠+水晶沙",
                    "id": 44286
            },
            {
                "attr_value": "不锈钢+超白钢化玻璃+K9水晶",
                    "id": 44523
            },
            {
                "attr_value": "不锈钢+超白钢化玻璃+K9水晶八角珠",
                    "id": 44461
            },
            {
                "attr_value": "不锈钢+超白钢化玻璃+K9水晶方珠",
                    "id": 44670
            },
            {
                "attr_value": "不锈钢切割+粘沙亚克力+K9水晶方珠",
                    "id": 44419
            },
            {
                "attr_value": "云石",
                    "id": 26326
            },
            {
                "attr_value": "五金",
                    "id": 29769
            },
            {
                "attr_value": "亚克力",
                    "id": 395
            },
            {
                "attr_value": "亚克力+S金不锈钢片+K9水晶中柱+磨砂灯罩",
                    "id": 44092
            },
            {
                "attr_value": "亚力克",
                    "id": 26924
            },
            {
                "attr_value": "优质金属",
                    "id": 42002
            },
            {
                "attr_value": "优质铜材灯体+玻璃灯罩",
                    "id": 46776
            },
            {
                "attr_value": "全铜",
                    "id": 38130
            },
            {
                "attr_value": "压克力",
                    "id": 32026
            },
            {
                "attr_value": "压铸铝",
                    "id": 33131
            },
            {
                "attr_value": "合金",
                    "id": 2710
            },
            {
                "attr_value": "布艺",
                    "id": 26445
            },
            {
                "attr_value": "异形不锈钢",
                    "id": 25905
            },
            {
                "attr_value": "异形不锈钢+异形亚克力+K9水晶+水晶沙",
                    "id": 44910
            },
            {
                "attr_value": "扫金",
                    "id": 41593
            },
            {
                "attr_value": "木",
                    "id": 160
            },
            {
                "attr_value": "木+PVC",
                    "id": 46183
            },
            {
                "attr_value": "木+亚克力",
                    "id": 46591
            },
            {
                "attr_value": "木+布",
                    "id": 46671
            },
            {
                "attr_value": "木+玻璃",
                    "id": 46415
            },
            {
                "attr_value": "木+铁",
                    "id": 46383
            },
            {
                "attr_value": "木艺+玻璃",
                    "id": 46034
            },
            {
                "attr_value": "树脂",
                    "id": 29584
            },
            {
                "attr_value": "水晶",
                    "id": 2712
            },
            {
                "attr_value": "烤漆",
                    "id": 29770
            },
            {
                "attr_value": "玉石",
                    "id": 26537
            },
            {
                "attr_value": "玉石\/云石",
                    "id": 2711
            },
            {
                "attr_value": "玻璃",
                    "id": 26300
            },
            {
                "attr_value": "玻璃灯罩",
                    "id": 43350
            },
            {
                "attr_value": "玻璃管",
                    "id": 33732
            },
            {
                "attr_value": "玻璃罩",
                    "id": 37771
            },
            {
                "attr_value": "电镀",
                    "id": 29796
            },
            {
                "attr_value": "碳素钢",
                    "id": 32502
            },
            {
                "attr_value": "磨砂灯罩",
                    "id": 38132
            },
            {
                "attr_value": "磨砂玻璃",
                    "id": 2748
            },
            {
                "attr_value": "磨砂罩",
                    "id": 29795
            },
            {
                "attr_value": "红木",
                    "id": 42147
            },
            {
                "attr_value": "金箔",
                    "id": 29743
            },
            {
                "attr_value": "金铁盘",
                    "id": 33533
            },
            {
                "attr_value": "钢化玻璃",
                    "id": 26684
            },
            {
                "attr_value": "铁",
                    "id": 16528
            },
            {
                "attr_value": "铁+PVC",
                    "id": 46130
            },
            {
                "attr_value": "铁+布",
                    "id": 46271
            },
            {
                "attr_value": "铁+木+PVC",
                    "id": 46134
            },
            {
                "attr_value": "铁+木+布",
                    "id": 46207
            },
            {
                "attr_value": "铁+玻璃",
                    "id": 18854
            },
            {
                "attr_value": "铁艺",
                    "id": 2747
            },
            {
                "attr_value": "铁艺+亚克力",
                    "id": 45088
            },
            {
                "attr_value": "铁艺+橡木+磨砂玻璃罩",
                    "id": 44170
            },
            {
                "attr_value": "铁艺+玻璃",
                    "id": 18532
            },
            {
                "attr_value": "铜",
                    "id": 18145
            },
            {
                "attr_value": "铜+玉石+白玉玻璃",
                    "id": 20037
            },
            {
                "attr_value": "铜+玻璃",
                    "id": 20274
            },
            {
                "attr_value": "铜+玻璃+玉石",
                    "id": 27509
            },
            {
                "attr_value": "铜+玻璃罩",
                    "id": 19220
            },
            {
                "attr_value": "铜+陶瓷+磨砂玻璃",
                    "id": 27864
            },
            {
                "attr_value": "铝",
                    "id": 31782
            },
            {
                "attr_value": "铝合金",
                    "id": 32501
            },
            {
                "attr_value": "铝材",
                    "id": 34995
            },
            {
                "attr_value": "锌合金",
                    "id": 26611
            },
            {
                "attr_value": "锌合金+K9国产一级水晶",
                    "id": 44991
            },
            {
                "attr_value": "锌合金+刻纹磨砂玻璃罩",
                    "id": 44204
            },
            {
                "attr_value": "锌合金+实木+玻璃罩",
                    "id": 22609
            },
            {
                "attr_value": "锌合金+水晶",
                    "id": 45012
            },
            {
                "attr_value": "锌合金+玻璃",
                    "id": 17333
            },
            {
                "attr_value": "锌合金+玻璃罩",
                    "id": 45001
            },
            {
                "attr_value": "陶\6440"
            },
            {
                "attr_value ": "激光切割 + 手工制品 ",
                    "id ": 18195
            },
            {
                "attr_value ": " 							烤漆 + 亚克力 ",
                    " 							id ": 32022
            },
            {
                " 							attr_value ": " 							烤雅白漆 ",
                    " 							id ": 35386
            },
            {
                " 							attr_value ": " 							玫瑰金 ",
                    " 							id ": 18113
            },
            {
                " 							attr_value ": " 							电镀 + 烤漆 ",
                    " 							id ": 44165
            },
            {
                " 							attr_value ": " 							电镀 + 烤漆 + 压铸 ",
                    " 							id ": 44201
            },
            {
                " 							attr_value ": " 							白 + 粉 ",
                    " 							id ": 48181
            },
            {
                " 							attr_value ": " 							白色 ",
                    " 							id ": 32763
            },
            {
                " 							attr_value ": " 							白色\ / 黑色 ",
                    " 							id ": 32272
            },
            {
                " 							attr_value ": " 							砂白 ",
                    " 							id ": 32522
            },
            {
                " 							attr_value ": " 							砂白 + 金色 ",
                    " 							id ": 35941
            },
            {
                " 							attr_value ": " 							砂金 ",
                    " 							id ": 34862
            },
            {
                " 							attr_value ": " 							砂黑\ / 砂金 ",
                    " 							id ": 32823
            },
            {
                " 							attr_value ": " 							粉红 ",
                    " 							id ": 45907
            },
            {
                " 							attr_value ": " 							粉蓝 ",
                    " 							id ": 45925
            },
            {
                " 							attr_value ": " 							红 ",
                    " 							id ": 27332
            },
            {
                " 							attr_value ": " 							纯白 ",
                    " 							id ": 40694
            },
            {
                " 							attr_value ": " 							蓝扫金 ",
                    " 							id ": 48120
            },
            {
                " 							attr_value ": " 							金色 + 奶白 ",
                    " 							id ": 35734
            },
            {
                " 							attr_value ": " 							铁艺 ",
                    " 							id ": 44974
            },
            {
                " 							attr_value ": " 							铁艺烤漆 ",
                    " 							id ": 26779
            },
            {
                " 							attr_value ": " 							铁艺烤漆 + 亚克力 ",
                    " 							id ": 27447
            },
            {
                " 							attr_value ": " 							铁艺黑拉丝 + 玻璃罩 ",
                    " 							id ": 21775
            },
            {
                " 							attr_value ": " 							铜本色 + 土豪金 ",
                    " 							id ": 26045
            },
            {
                " 							attr_value ": " 							铬色 ",
                    " 							id ": 40400
            },
            {
                " 							attr_value ": " 							银色 ",
                    " 							id ": 33451
            },
            {
                " 							attr_value ": " 							阳光金 ",
                    " 							id ": 41091
            },
            {
                " 							attr_value ": " 							香槟色 ",
                    " 							id ": 18071
            },
            {
                " 							attr_value ": " 							香槟金 ",
                    " 							id ": 33530
            },
            {
                " 							attr_value ": " 							高光白 ",
                    " 							id ": 34433
            },
            {
                " 							attr_value ": " 							黑\ / 白\ / 红 ",
                    " 							id ": 32458
            },
            {
                " 							attr_value ": " 							黑拉丝 + 扫金边 ",
                    " 							id ": 21353
            },
            {
                " 							attr_value ": " 							黑拉丝 + 扫金边E27 ",
                    " 							id ": 21461
            },
            {
                " 							attr_value ": " 							黑拉丝描金边E14 ",
                    " 							id ": 21646
            },
            {
                " 							attr_value ": " 							黑擦金加铜本色 + 玻璃灯罩 ",
                    " 							id ": 22164
            },
            {
                " 							attr_value ": " 							黑色 ",
                    " 							id ": 31823
            },
            {
                " 							attr_value ": " 							黑色 + 玫瑰金 ",
                    " 							id ": 45637
            },
            {
                " 							attr_value ": " 							黑色\ / 白色 + 玫瑰金 ",
                    " 							id ": 45979
            }
      ]
        },
        {
            " 							filter_attr_name ": " 							风格 ",
                " 							index ": 4,
                " 							attr_list ": [
            {
                " 							attr_value ": " 							全部 ",
                    " 							id ": 0
            },
            {
                " 							attr_value ": " 							中式 ",
                    " 							id ": 161
            },
            {
                " 							attr_value ": " 							后现代奢华 ",
                    " 							id ": 25806
            },
            {
                " 							attr_value ": " 							宜家 ",
                    " 							id ": 26772
            },
            {
                " 							attr_value ": " 							工业风 ",
                    " 							id ": 45420
            },
            {
                " 							attr_value ": " 							新中式 ",
                    " 							id ": 21352
            },
            {
                " 							attr_value ": " 							欧式 ",
                    " 							id ": 124
            },
            {
                " 							attr_value ": " 							现代简约 ",
                    " 							id ": 3250
            },
            {
                " 							attr_value ": " 							田园 ",
                    " 							id ": 29583
            },
            {
                " 							attr_value ": " 							美式 ",
                    " 							id ": 2746
            }
      ]
        }
  ]

    }*/

  /*  {
        "categories": [
        {
            "id": 165,
                "name": "\u7c7b\u578b",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487056803650314833.jpg",
                "photo": {
            "width": null,
                    "height": null,
                    "thumb": "http:\/\/www.juhao.com\/images\/201712\/goods_img\/143_G_1512896450386.jpg",
                    "large": "http:\/\/www.juhao.com\/images\/201712\/goods_img\/143_G_1512896450386.jpg"
        },
            "more": 1,
                "categories": [
            {
                "id": 166,
                    "name": "\u540a\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270566114824114.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 167,
                    "name": "\u58c1\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270625593630667.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 168,
                    "name": "\u53f0\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270647663118398.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 169,
                    "name": "\u843d\u5730\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270599808252843.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 170,
                    "name": "\u5438\u9876\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270610879135394.png",
                    "photo": null,
                    "more": 0,
                    "categories": [
                {
                    "id": 213,
                        "name": "\u5ba2\u5385\u706f",
                        "desc": "",
                        "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/",
                        "photo": null,
                        "more": 0,
                        "categories": [

              ],
                    "sort": 50
                }
          ],
                "sort": 50
            },
            {
                "id": 171,
                    "name": "\u955c\u524d\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270634899197479.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            }
      ],
            "sort": 1
        },
        {
            "id": 69,
                "name": "\u98ce\u683c",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487045898808925607.jpg",
                "photo": {
            "width": null,
                    "height": null,
                    "thumb": "http:\/\/www.juhao.com\/images\/201801\/goods_img\/2173_G_1515383357349.jpg",
                    "large": "http:\/\/www.juhao.com\/images\/201801\/goods_img\/2173_G_1515383357349.jpg"
        },
            "more": 1,
                "categories": [
            {
                "id": 71,
                    "name": "\u7f8e\u5f0f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270713177506941.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 72,
                    "name": "\u73b0\u4ee3\u7b80\u7ea6",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270702079470380.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 75,
                    "name": "\u4e2d\u5f0f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270731818146675.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 51
            },
            {
                "id": 74,
                    "name": "\u65b0\u4e2d\u5f0f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270741280094903.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 52
            },
            {
                "id": 200,
                    "name": "\u5de5\u4e1a\u98ce",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1511420886412921817.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 52
            },
            {
                "id": 70,
                    "name": "\u7530\u56ed",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270762080044009.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 53
            },
            {
                "id": 73,
                    "name": "\u6b27\u5f0f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270773073840795.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 54
            },
            {
                "id": 134,
                    "name": "\u540e\u73b0\u4ee3\u5962\u534e",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270783400794378.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 56
            },
            {
                "id": 135,
                    "name": "\u5b9c\u5bb6",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495270796591218234.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 57
            }
      ],
            "sort": 3
        },
        {
            "id": 76,
                "name": "\u7a7a\u95f4",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487057293641392718.jpg",
                "photo": null,
                "more": 1,
                "categories": [
            {
                "id": 77,
                    "name": "\u5ba2\u5385",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271100287789963.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 79,
                    "name": "\u4e66\u623f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271152207442624.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 78,
                    "name": "\u9910\u5385",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271083688453775.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 124,
                    "name": "\u4f1a\u8bae\u5ba4",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271164636498644.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 125,
                    "name": "\u9633\u53f0",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271191687285006.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 126,
                    "name": "\u5367\u5ba4",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271112172539221.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 136,
                    "name": "\u536b\u751f\u95f4",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271177601800274.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 137,
                    "name": "\u7384\u5173\/\u8fc7\u9053\/\u5eca\u9053",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271211410254981.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 138,
                    "name": "\u697c\u68af\/\u62d0\u89d2",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271124974738516.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 195,
                    "name": "\u9ad8\u5c42\/\u590d\u5f0f\/\u522b\u5885",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271385374140669.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 199,
                    "name": "\u513f\u7ae5\u623f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1511250843387839558.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 51
            },
            {
                "id": 139,
                    "name": "\u5176\u4ed6",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1495271411091317591.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 55
            }
      ],
            "sort": 4
        },
        {
            "id": 118,
                "name": "\u7167\u5c04\u9762\u79ef",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487056875968625677.jpg",
                "photo": null,
                "more": 1,
                "categories": [
            {
                "id": 122,
                    "name": "40\u33a1\u4ee5\u4e0a",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482923141047570724.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 119,
                    "name": "25\u33a1-40\u33a1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482923204311481684.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 51
            },
            {
                "id": 120,
                    "name": "20\u33a1-35\u33a1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482923324292882558.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 54
            },
            {
                "id": 121,
                    "name": "15\u33a1-30\u33a1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482923479580780351.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 56
            },
            {
                "id": 198,
                    "name": "10\u33a1-15\u33a1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1510899438429119919.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 56
            },
            {
                "id": 131,
                    "name": "10\u33a1-20\u33a1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482923546597258322.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 57
            },
            {
                "id": 133,
                    "name": "10\u33a1-25\u33a1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482923379585043980.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 58
            },
            {
                "id": 193,
                    "name": "5-10m\u00b2",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1493014437362213253.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 59
            }
      ],
            "sort": 7
        },
        {
            "id": 101,
                "name": "\u6750\u8d28",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487057191808845729.jpg",
                "photo": null,
                "more": 1,
                "categories": [
            {
                "id": 145,
                    "name": "\u5408\u91d1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995805558615266.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 49
            },
            {
                "id": 104,
                    "name": "\u94dd\u6750",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482996068386462459.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 105,
                    "name": "\u78e8\u7802\/\u523b\u82b1\u73bb\u7483",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995839892690440.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 103,
                    "name": "\u94c1\u827a",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995945100691781.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 102,
                    "name": "\u6c34\u6676",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995827143541211.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 107,
                    "name": "PP\u6599",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995890819782342.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 106,
                    "name": "\u4e9a\u514b\u529b",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995960547609414.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 146,
                    "name": "\u94dc",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995872887767893.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 147,
                    "name": "\u4eff\u7f8a\u76ae\/\u5e03\u827a",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995925858888581.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 148,
                    "name": "\u7389\u77f3\/\u4e91\u77f3",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995788169474603.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 149,
                    "name": "\u6811\u8102",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995855556421373.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 150,
                    "name": "\u6728",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995913944239858.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 151,
                    "name": "\u9676\u74f7",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995999584415810.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 153,
                    "name": "\u4e0d\u9508\u94a2",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482995768692130815.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            }
      ],
            "sort": 50
        },
        {
            "id": 112,
                "name": "\u4ef7\u683c",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487056983421449850.png",
                "photo": null,
                "more": 1,
                "categories": [
            {
                "id": 116,
                    "name": "0-499",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482934905252969963.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 113,
                    "name": "500-1999",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482934918593726122.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 51
            },
            {
                "id": 117,
                    "name": "2000-4999",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482934930807538639.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 52
            },
            {
                "id": 114,
                    "name": "5000-9999",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482934943303644175.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 53
            },
            {
                "id": 115,
                    "name": "10000-19999",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482934953546908321.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 54
            },
            {
                "id": 152,
                    "name": "20000\u4ee5\u4e0a",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482934963962677732.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 55
            }
      ],
            "sort": 50
        },
        {
            "id": 196,
                "name": "\u7535\u5de5\u7535\u6c14",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1508744359784836506.jpg",
                "photo": {
            "width": null,
                    "height": null,
                    "thumb": "http:\/\/www.juhao.com\/images\/201704\/goods_img\/1166_G_1492221414966.jpg",
                    "large": "http:\/\/www.juhao.com\/images\/201704\/goods_img\/1166_G_1492221414966.jpg"
        },
            "more": 1,
                "categories": [
            {
                "id": 207,
                    "name": "\u9510\u7cfb\u5217",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514338462813467904.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 5
            },
            {
                "id": 208,
                    "name": "\u60e0\u7cfb\u5217",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514338484118224373.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 10
            },
            {
                "id": 206,
                    "name": "\u5c1a\u7cfb\u5217",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514260492047166716.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 35
            },
            {
                "id": 209,
                    "name": "\u94a2\u97f5\u7cfb\u5217",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514338510582573053.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 36
            },
            {
                "id": 210,
                    "name": "G6\u81f4\u7cfb\u5217",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514338741233795134.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 40
            },
            {
                "id": 211,
                    "name": "\u81f4\u5c1a118\u7cfb\u5217",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514338769370763654.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 86,
                    "name": "\u5f00\u5173\u63d2\u5ea7",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514339449549746603.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 55
            },
            {
                "id": 201,
                    "name": "\u6d74\u9738",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1514336776887098461.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 60
            }
      ],
            "sort": 50
        },
        {
            "id": 197,
                "name": "\u5149\u6e90",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1508744378654256927.jpg",
                "photo": {
            "width": null,
                    "height": null,
                    "thumb": "http:\/\/www.juhao.com\/images\/201702\/goods_img\/657_G_1487041368165.jpg",
                    "large": "http:\/\/www.juhao.com\/images\/201702\/goods_img\/657_G_1487041368165.jpg"
        },
            "more": 1,
                "categories": [
            {
                "id": 202,
                    "name": "\u706f\u6ce1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1512096523824790488.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 203,
                    "name": "T5\u652f\u67b6",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1512096752856699127.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 205,
                    "name": "T8\u652f\u67b6",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1512098080423767649.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 81,
                    "name": "\u706f\u5e26",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1482933463715996781.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 56
            }
      ],
            "sort": 50
        },
        {
            "id": 142,
                "name": "\u5546\u7167",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/",
                "photo": {
            "width": null,
                    "height": null,
                    "thumb": "http:\/\/www.juhao.com\/images\/201704\/goods_img\/1026_G_1492070284584.jpg",
                    "large": "http:\/\/www.juhao.com\/images\/201704\/goods_img\/1026_G_1492070284584.jpg"
        },
            "more": 1,
                "categories": [
            {
                "id": 190,
                    "name": "\u529e\u516c\u7167\u660e",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487041621839030995.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 181,
                    "name": "\u7b52\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487038583277582052.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 184,
                    "name": "\u8f68\u9053\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487038611628765548.png",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 191,
                    "name": "\u5546\u4e1a\u7167\u660e",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487041798043191982.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 192,
                    "name": "\u5546\u8d85\u7167\u660e",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487041903903313409.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            }
      ],
            "sort": 55
        },
        {
            "id": 162,
                "name": "\u6237\u5916\u7167\u660e",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/",
                "photo": null,
                "more": 1,
                "categories": [
            {
                "id": 186,
                    "name": "\u8349\u576a\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487040554953190542.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 187,
                    "name": "\u666f\u89c2\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487040657614173034.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 185,
                    "name": "\u8def\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487040141465493905.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 189,
                    "name": "\u6cdb\u5149\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487040914498647800.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 188,
                    "name": "\u5730\u57cb\u706f",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1487040764847701400.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            }
      ],
            "sort": 56
        },
        {
            "id": 212,
                "name": "\u949c\u8c6a\u8d85\u5e02",
                "desc": "",
                "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/",
                "photo": {
            "width": null,
                    "height": null,
                    "thumb": "http:\/\/www.juhao.com\/images\/201801\/goods_img\/2283_G_1515058363531.jpg",
                    "large": "http:\/\/www.juhao.com\/images\/201801\/goods_img\/2283_G_1515058363531.jpg"
        },
            "more": 1,
                "categories": [
            {
                "id": 214,
                    "name": "\u949c\u8c6a\u8d35\u5bbe\u9152",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1516700856820540041.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 215,
                    "name": "\u8336\u53f6",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1516700901359796290.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            },
            {
                "id": 216,
                    "name": "\u706f\u9970\u4ea7\u54c1",
                    "desc": "",
                    "thumbs": "http:\/\/www.juhao.com\/data\/catthumb\/1516700978046591705.jpg",
                    "photo": null,
                    "more": 0,
                    "categories": [

          ],
                "sort": 50
            }
      ],
            "sort": 100
        }
  ],
        "paged": {
        "total": 11,
                "page": "1",
                "size": "20",
                "more": 0
    },
        "error_code": 0,
            "debug_id": "5a794c80e2456"
    }*/
}
