
package com.dd.datastatistics.biz;

public class StatFunctions{

    public static String spliter = "|";

    public static String get_log_string(Object... params){
        String action_key = (String) params[0];
        int[] params_int = {0,0,0,0};
        String[] params_str = {"","","","","",""};
        int int_pk = 0;
        int str_pk = 0;
        for (int i=1; i<params.length; i++) {
            Object param = params[i];
            if(param == null){
                str_pk++;
           }else if( param instanceof String ){
                String p = (String)param;
                params_str[str_pk] = p.replace("|", "%7c");
                str_pk++;
            }else{
                params_int[int_pk] = (Integer)param;
                int_pk++;
            }
        }

        String log_string_format = "%s|%d|%d|%d|%d|%s|%s|%s|%s|%s|%s";
        String log_string = String.format(log_string_format, action_key, params_int[0], params_int[1], params_int[2], params_int[3],
            params_str[0], params_str[1], params_str[2], params_str[3], params_str[4], params_str[5]);

        return log_string;
    }
    public class StatActionKeys{

        public static final String CLICK_WAREINVTALK_COMMENT = "click_wareinvtalk_comment";
        public static final String CLICK_WAREINVTALK_PRESALE = "click_wareinvtalk_presale";
        public static final String CLICK_WAREINVTALK_APPRAISAL = "click_wareinvtalk_appraisal";
        public static final String CLICK_WAREINVTALK_BUY = "click_wareinvtalk_buy";
        public static final String CLICK_WAREINVTALK_CHOOSE = "click_wareinvtalk_choose";
        public static final String CLICK_USERHOME_PERIODICON = "click_userhome_periodicon";
        public static final String CLICK_CHECKINHOROSCOPE_RECOMMEND = "click_checkinhoroscope_recommend";
        public static final String CLICK_CHECKINHOROSCOPE_CHECKINAGAIN = "click_checkinhoroscope_checkinagain";
        public static final String CLICK_CHECKINHOROSCOPE_SHARE = "click_checkinhoroscope_share";
        public static final String CLICK_CIRCLEINDEX_TOTOPICON = "click_circleindex_totopicon";
        public static final String CLICK_CIRCLEINDEX_ADDVTALKICON = "click_circleindex_addvtalkicon";
        public static final String CLICK_CIRCLEINDEX_VTALK = "click_circleindex_vtalk";
        public static final String CLICK_CIRCLEINDEX_CIRCLEMORE = "click_circleindex_circlemore";
        public static final String CLICK_CIRCLEINDEX_ADDCIRCLE = "click_circleindex_addcircle";
        public static final String CLICK_CIRCLEINDEX_CIRCLEICON = "click_circleindex_circleicon";
        public static final String CLICK_CIRCLEINDEX_ENTRYPOST = "click_circleindex_entrypost";
        public static final String CLICK_CIRCLEINDEX_ENTRYRANKING = "click_circleindex_entryranking";
        public static final String CLICK_CIRCLEINDEX_ENTRYSTAR = "click_circleindex_entrystar";
        public static final String CLICK_INDEX_BANNER = "click_index_banner";
        public static final String CLICK_USERHOME_FEED = "click_userhome_feed";
        public static final String CLICK_USERHOME_COINICON = "click_userhome_coinicon";
        public static final String CLICK_USERHOME_SCOREICON = "click_userhome_scoreicon";
        public static final String CLICK_USERHOME_MESSAGEICON = "click_userhome_messageicon";
        public static final String CLICK_USERHOME_AVATAR = "click_userhome_avatar";
        public static final String CLICK_USERHOME_CHECKINBUTTON = "click_userhome_checkinbutton";
        public static final String CLICK_USERHOME_SETTINGSBUTTON = "click_userhome_settingsbutton";
        public static final String CLICK_PRODUCTINDEX_MALLMORE = "click_productindex_mallmore";
        public static final String CLICK_PRODUCTINDEX_MALLPRODUCT = "click_productindex_mallproduct";
        public static final String CLICK_PRODUCTINDEX_FREETRYMORE = "click_productindex_freetrymore";
        public static final String CLICK_PRODUCTINDEX_FREETRYPRODUCT = "click_productindex_freetryproduct";
        public static final String CLICK_PRODUCTINDEX_ADDPRODUCT = "click_productindex_addproduct";
        public static final String CLICK_PRODUCTINDEX_CATEGORY = "click_productindex_category";
        public static final String CLICK_PRODUCTINDEX_QRCODE = "click_productindex_qrcode";
        public static final String CLICK_PRODUCTINDEX_SEARCHINPUT = "click_productindex_searchinput";
        public static final String CLICK_PRODUCTINDEX_ICONBUTTON = "click_productindex_iconbutton";
        public static final String CLICK_STARTUPBANNER_SKIP = "click_startupbanner_skip";
        public static final String CRASH_INDEX_ALL = "crash_index_all";
        public static final String CLICK_INDEX_TOTOPBUTTON = "click_index_totopbutton";
        public static final String CLICK_INDEX_ICONBUTTON = "click_index_iconbutton";
        public static final String CLICK_VTALKDETAIL_PRODUCTUSER = "click_vtalkdetail_productuser";
        public static final String CLICK_VTALKDETAIL_PRODUCTMENTIONED = "click_vtalkdetail_productmentioned";
        public static final String CLICK_VTALKDETAIL_PRODUCTCREATOR = "click_vtalkdetail_productcreator";
        public static final String CLICK_PRODUCTDETAIL_MALL = "click_productdetail_mall";
        public static final String CLICK_PRODUCTLISTSEARCH_MALL = "click_productlistsearch_mall";
        public static final String CLICK_PRODUCTLISTSEARCH_PRODUCT = "click_productlistsearch_product";
        public static final String CLICK_INDEX_USERINFO = "click_index_userinfo";
        public static final String CLICK_INDEX_FEEDITEM = "click_index_feeditem";
        public static final String CLICK_INDEX_SEARCHINPUT = "click_index_searchinput";
        public static final String CLICK_INDEX_TAGFILER = "click_index_tagfiler";
        public static final String CLICK_INDEX_BUTTONMORE = "click_index_buttonmore";
    }
    /*
    * 涔板鐐硅瘎鐐瑰嚮鏁�
    * @ware_slug 鍟嗗搧鐨剆lug鍊�
    */
    public static void log_click_wareinvtalk_comment(String ware_slug) {
        String log_string = get_log_string(StatActionKeys.CLICK_WAREINVTALK_COMMENT, ware_slug);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鏄庢棩棰勫憡鐐瑰嚮鏁�
    * @ware_slug 鍟嗗搧鐨剆lug鍊�
    */
    public static void log_click_wareinvtalk_presale(String ware_slug) {
        String log_string = get_log_string(StatActionKeys.CLICK_WAREINVTALK_PRESALE, ware_slug);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 灏忕紪閴村畾鐐瑰嚮鏁�
    * @ware_slug 鍟嗗搧鐨剆lug鍊�
    */
    public static void log_click_wareinvtalk_appraisal(String ware_slug) {
        String log_string = get_log_string(StatActionKeys.CLICK_WAREINVTALK_APPRAISAL, ware_slug);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 璐拱鎸夐挳鐐瑰嚮鏁�
    * @ware_slug 鍟嗗搧鐨剆lug鍊�
    */
    public static void log_click_wareinvtalk_buy(String ware_slug) {
        String log_string = get_log_string(StatActionKeys.CLICK_WAREINVTALK_BUY, ware_slug);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 閫夋嫨灏虹爜鐐瑰嚮鏁�
    * @ware_slug 鍟嗗搧鐨剆lug鍊�
    */
    public static void log_click_wareinvtalk_choose(String ware_slug) {
        String log_string = get_log_string(StatActionKeys.CLICK_WAREINVTALK_CHOOSE, ware_slug);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鈥滈偅鍑犲ぉ鈥濈偣鍑荤粺璁�
    */
    public static void log_click_userhome_periodicon() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_PERIODICON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 搴曢儴缇庡暒鎺ㄨ崘鐐瑰嚮缁熻
    */
    public static void log_click_checkinhoroscope_recommend() {
        String log_string = get_log_string(StatActionKeys.CLICK_CHECKINHOROSCOPE_RECOMMEND);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 琛ョ鎸夐挳鐐瑰嚮缁熻
    */
    public static void log_click_checkinhoroscope_checkinagain() {
        String log_string = get_log_string(StatActionKeys.CLICK_CHECKINHOROSCOPE_CHECKINAGAIN);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鍒嗕韩鎸夐挳鐐瑰嚮缁熻
    */
    public static void log_click_checkinhoroscope_share() {
        String log_string = get_log_string(StatActionKeys.CLICK_CHECKINHOROSCOPE_SHARE);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 涓�敭缃《鎸夐挳鐨勭偣鍑荤粺璁�
    */
    public static void log_click_circleindex_totopicon() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_TOTOPICON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 搴曢儴鍙戣〃璇濋鎸夐挳鐨勭偣鍑荤粺璁�
    */
    public static void log_click_circleindex_addvtalkicon() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_ADDVTALKICON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鏈�柊璇濋鍒楄〃鐨勭偣鍑荤粺璁�
    * @vtalk_slug 璇濋鐨剆lug鍊�
    * @vtalk_title 璇濋鐨勬爣棰�
    */
    public static void log_click_circleindex_vtalk(String vtalk_slug, String vtalk_title) {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_VTALK, vtalk_slug, vtalk_title);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 灞曞紑鎸夐挳鐨勭偣鍑荤粺璁�
    */
    public static void log_click_circleindex_circlemore() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_CIRCLEMORE);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 娣诲姞鍦堝瓙鐨勭偣鍑荤粺璁�
    */
    public static void log_click_circleindex_addcircle() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_ADDCIRCLE);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鍦堝瓙鍖哄煙鐨勭偣鍑荤粺璁�
    * @circle_slug 鍦堝瓙鐨剆lug鍊�
    * @circle_title 鍦堝瓙鏍囬
    */
    public static void log_click_circleindex_circleicon(String circle_slug, String circle_title) {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_CIRCLEICON, circle_slug, circle_title);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 澶撮儴鍙戣瘽棰樼殑鐐瑰嚮缁熻
    */
    public static void log_click_circleindex_entrypost() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_ENTRYPOST);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 娲昏穬姒滅殑鐐瑰嚮缁熻
    */
    public static void log_click_circleindex_entryranking() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_ENTRYRANKING);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鍚嶄汉棣嗙殑鐐瑰嚮缁熻
    */
    public static void log_click_circleindex_entrystar() {
        String log_string = get_log_string(StatActionKeys.CLICK_CIRCLEINDEX_ENTRYSTAR);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鎵�湁banner鐨勭偣鍑荤粺璁�
    * @jump_label 閫氱敤璺宠浆label
    * @jump_data 閫氱敤璺宠浆data
    * @slug banner鐨剆lug鍊�
    * @type banner鐨勭被鍨嬶紝鐢盿pp鎺ュ彛杩斿洖鐨勯偅涓�
    */
    public static void log_click_index_banner(String jump_label, String jump_data, String slug, String type) {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_BANNER, jump_label, jump_data, slug, type);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鍔ㄦ�娴佺殑鐐瑰嚮缁熻
    * @jump_label 鍔ㄦ�鍐呭鐐瑰嚮鍚庣殑閫氱敤璺宠浆label
    * @jump_data 鍔ㄦ�鍐呭鐐瑰嚮鍚庣殑閫氱敤璺宠浆data
    * @title 鍔ㄦ�鐨勬爣棰�
    */
    public static void log_click_userhome_feed(String jump_label, String jump_data, String title) {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_FEED, jump_label, jump_data, title);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 缇庡竵鐨勭偣鍑荤粺璁�
    */
    public static void log_click_userhome_coinicon() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_COINICON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 缇庣悍鐨勭偣鍑荤粺璁�
    */
    public static void log_click_userhome_scoreicon() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_SCOREICON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 娑堟伅鎸夐挳鐨勭偣鍑荤粺璁�
    */
    public static void log_click_userhome_messageicon() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_MESSAGEICON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 澶村儚鐨勭偣鍑荤粺璁�
    */
    public static void log_click_userhome_avatar() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_AVATAR);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鏄熷骇绛惧埌鐨勭偣鍑荤粺璁�
    */
    public static void log_click_userhome_checkinbutton() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_CHECKINBUTTON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 璁剧疆鎸夐挳鐨勭偣鍑荤粺璁�
    */
    public static void log_click_userhome_settingsbutton() {
        String log_string = get_log_string(StatActionKeys.CLICK_USERHOME_SETTINGSBUTTON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 搴曢儴鏌ョ湅鍏ㄩ儴鍏戞崲鐨勭偣鍑�
    */
    public static void log_click_productindex_mallmore() {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_MALLMORE);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 缇庡竵鍟嗗煄鎴戣鍏戞崲鐨勭偣鍑荤粺璁�
    * @product_slug 浜у搧slug
    * @product_name 浜у搧鍚嶇О
    */
    public static void log_click_productindex_mallproduct(String product_slug, String product_name) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_MALLPRODUCT, product_slug, product_name);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鏌ョ湅鍏ㄩ儴鍏嶈垂璇曠敤鐨勭偣鍑�
    */
    public static void log_click_productindex_freetrymore() {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_FREETRYMORE);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鍏嶈垂璇曠敤鐢宠button鐨勭偣鍑荤粺璁�
    * @freetry_slug 鍏嶈垂璇曠敤鐨剆lug
    * @product_slug 浜у搧鐨剆lug
    * @product_name 浜у搧鍚嶇О
    */
    public static void log_click_productindex_freetryproduct(String freetry_slug, String product_slug, String product_name) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_FREETRYPRODUCT, freetry_slug, product_slug, product_name);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鏂板浜у搧button鐨勭偣鍑荤粺璁�
    */
    public static void log_click_productindex_addproduct() {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_ADDPRODUCT);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鎶よ偆銆佸僵濡嗙瓑鍏ぇ绫荤殑鐐瑰嚮缁熻
    * @area_index 鐐瑰嚮鍖哄煙鐨勫簭鍙凤紝宸﹁竟甯﹂鑹插尯鍩熶紶1锛屽彸杈逛紶2    * @category_title 鍒嗙被鏍囬锛屽鈥滄姢鑲も�銆佲�娲侀潰鈥�
    * @category_label 鍒嗙被鐨刲abel鍙傛暟锛屽鈥渂rand鈥濄�鈥渆ffect鈥�
    * @category_data 鍒嗙被鐨刣ata鍙傛暟锛屽氨鏄痵lug鍊�
    */
    public static void log_click_productindex_category(int area_index, String category_title, String category_label, String category_data) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_CATEGORY, area_index, category_title, category_label, category_data);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鎵竴鎵玝utton鐨勭偣鍑荤粺璁�
    */
    public static void log_click_productindex_qrcode() {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_QRCODE);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鎼滅储妗嗙殑鐐瑰嚮缁熻
    */
    public static void log_click_productindex_searchinput() {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_SEARCHINPUT);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 澶撮儴涓変釜button鐨勭偣鍑�
    * @button_title 鎸夐挳涓嬮潰鐨勬爣棰樻枃鏈紝濡傗�璇勬祴瀹も�
    */
    public static void log_click_productindex_iconbutton(String button_title) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTINDEX_ICONBUTTON, button_title);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 闂睆椤佃烦杩囩殑鐐瑰嚮娆℃暟
    */
    public static void log_click_startupbanner_skip() {
        String log_string = get_log_string(StatActionKeys.CLICK_STARTUPBANNER_SKIP);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * APP宕╂簝
    * @exception_stack 寮傚父鐨勬棩蹇椾俊鎭紝璇峰皢鎹㈣绗︽浛鎹㈡帀鍐嶄笂鎶�
    */
    public static void log_crash_index_all(String exception_stack) {
        String log_string = get_log_string(StatActionKeys.CRASH_INDEX_ALL, exception_stack);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 棣栭〉涓嬮潰杩斿洖鍒伴《閮ㄧ殑鎸夐挳鐐瑰嚮
    */
    public static void log_click_index_totopbutton() {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_TOTOPBUTTON);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 棣栭〉涓婇潰閭ｄ竴鎺掑姛鑳絀CON鎸夐挳鐨勭偣鍑�
    * @jump_label 閫氱敤璺宠浆label
    * @jump_data 閫氱敤璺宠浆data
    */
    public static void log_click_index_iconbutton(String jump_label, String jump_data) {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_ICONBUTTON, jump_label, jump_data);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 闈炴ゼ涓伙紝璇勮鍖虹殑鎻掑叆浜у搧鐨勭偣鍑�
    * @product_slug 浜у搧slug鍊�
    * @product_name 浜у搧鍚嶇О
    */
    public static void log_click_vtalkdetail_productuser(String product_slug, String product_name) {
        String log_string = get_log_string(StatActionKeys.CLICK_VTALKDETAIL_PRODUCTUSER, product_slug, product_name);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 妤间富鍙戣〃鐨勮瘽棰樻垨璇勮涓紝鎻愬埌鐨勪骇鍝侊紙灏忕紪鎻掑叆鐨勶級鐨勭偣鍑�
    * @product_slug 浜у搧slug鍊�
    * @product_name 浜у搧鍚嶇О
    */
    public static void log_click_vtalkdetail_productmentioned(String product_slug, String product_name) {
        String log_string = get_log_string(StatActionKeys.CLICK_VTALKDETAIL_PRODUCTMENTIONED, product_slug, product_name);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 妤间富鍙戣瘽棰樺凡缁忚瘎璁烘彃鍏ヤ骇鍝佺殑鐐瑰嚮
    * @product_slug 浜у搧slug鍊�
    * @product_name 浜у搧鍚嶇О
    */
    public static void log_click_vtalkdetail_productcreator(String product_slug, String product_name) {
        String log_string = get_log_string(StatActionKeys.CLICK_VTALKDETAIL_PRODUCTCREATOR, product_slug, product_name);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 杈句汉鎺ㄨ崘鍟嗗鎸夐挳鐐瑰嚮
    * @mall_type 鍟嗗绫诲瀷銆俽ecommend:缇庢媺璁よ瘉鎺ㄨ崘鍟嗗;talents:杈句汉鎺ㄨ崘鍟嗗;malls:鍙傝�鍟嗗
    * @mall_name 鍟嗗鍚嶇О
    * @url url閾炬帴鍦板潃
    * @price 浠锋牸淇℃伅
    */
    public static void log_click_productdetail_mall(String mall_type, String mall_name, String url, String price) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTDETAIL_MALL, mall_type, mall_name, url, price);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 浜у搧鍒楄〃鍟嗗鐐瑰嚮
    * @channel_name 娓犻亾鍚嶅瓧
    * @url 閾炬帴鐨剈rl鍦板潃
    */
    public static void log_click_productlistsearch_mall(String channel_name, String url) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTLISTSEARCH_MALL, channel_name, url);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 浜у搧璇︽儏鐐瑰嚮
    * @product_slug 浜у搧slug鍊�
    */
    public static void log_click_productlistsearch_product(String product_slug) {
        String log_string = get_log_string(StatActionKeys.CLICK_PRODUCTLISTSEARCH_PRODUCT, product_slug);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 浠庣敤鎴峰ご鍍忔垨鐢ㄦ埛鍚嶇О杩涘叆涓汉涓婚〉鐨勭偣鍑�
    * @user_slug 鐢ㄦ埛鐨剆lug鍊�
    * @user_nickname 鐢ㄦ埛鏄电О
    */
    public static void log_click_index_userinfo(String user_slug, String user_nickname) {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_USERINFO, user_slug, user_nickname);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 浠庡ぇ鍥炬垨璇濋鏍囬杩涘叆璇濋璇︽儏椤电殑鐐瑰嚮
    * @jump_label 閫氱敤璺宠浆label
    * @jump_data 閫氱敤璺宠浆data
    * @title 鏍囬锛屽氨鏄浘鐗囦笅闈㈤偅涓�瀛�
    */
    public static void log_click_index_feeditem(String jump_label, String jump_data, String title) {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_FEEDITEM, jump_label, jump_data, title);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 椤堕儴鎼滅储妗嗙殑鐐瑰嚮缁熻
    */
    public static void log_click_index_searchinput() {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_SEARCHINPUT);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 棣栭〉涓嬮潰绛涢�鏍囩鎸夐挳鐨勭偣鍑�
    */
    public static void log_click_index_tagfiler() {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_TAGFILER);
        SaveDataTask.saveToDB(log_string);
    }
    /*
    * 鐐瑰嚮棣栭〉涓婃柟鍔熻兘ICON鎸夐挳鍙宠竟鏇村鐨勯偅涓皬鏉�
    */
    public static void log_click_index_buttonmore() {
        String log_string = get_log_string(StatActionKeys.CLICK_INDEX_BUTTONMORE);
        SaveDataTask.saveToDB(log_string);
    }
}
