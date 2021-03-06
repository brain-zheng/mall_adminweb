package com.mall.adminweb.common;


public class Constants {

    public static final int RESULT_CODE_SUCCESS = 200;  // 成功处理请求
    public static final int RESULT_CODE_BAD_REQUEST = 412;  // 请求错误
    public static final int RESULT_CODE_PARAM_ERROR = 406;  // 传参错误
    public static final int RESULT_CODE_SERVER_ERROR = 500;  // 服务器错误

    public final static int SELL_STATUS_UP = 0;//商品上架状态
    public final static int SELL_STATUS_DOWN = 1;//商品下架状态

    public final static String FILE_UPLOAD_DIC = "/Users/nicomama/Desktop/gradDesign/adminweb/src/main/resources/static/admin/dist/img/";//上传文件的默认url前缀，根据部署设置自行修改

}
