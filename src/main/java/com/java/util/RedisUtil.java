package com.java.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.dbsun.entity.system.PageData;
/**
 * @author Wolf
 *
 */
@Service
public class RedisUtil<T> {

	/**微信地址*/
	/**获取二维码*/
	public final static String GETSCEURL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";//兑换二维码不需要登录，ticket进行UrlEncode
	
	/**拉取用户信息(需scope为 snsapi_userinfo)*/
	public final static String GETUSERINFOURL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	/**拉取用户信息(关注后获取用户信息)*/
	public final static String GETUSERGZINFOURL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN ";
	/**获取code*/
	public final static String GETWXUSERMSGURL = "https://open.weixin.qq.com/connect/oauth2/authorize?"
			+ "appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";//获取code
	/**获取微信用户信息包括openID*/
	public final static String GETPAGEAKURL = "https://api.weixin.qq.com/sns/oauth2/access_token?"
			+ "appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	/**创建菜单*/
	public final static String CREATEMENUURL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	/**创建二维码ticket*/
	public final static String GETTKT_URL="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
	/**获取access_token*/
	public final static String ACCESS_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?" +
			"grant_type=client_credential&appid=APPID&secret=APPSECRET";
	/**获取jsapi_ticket*/
	public final static String JSAPI_TICKET_URL="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	/**获取关注列表*/
	public final static String GZ_URL="https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=";
	/**APPID **/
	public final static String weixin_appid = "wxa0844dadcdcf1727";
	/**SECRET*/
	public final static String weixin_secret = "cd6ff5003f2c2b8f7b98815c2fb49f9b";
	/**微信令牌*/
	public final static String weixin_token = "fhadmin";
	/**获取临时素材*/
	public final static String SCLS_URL="https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	
	/**模板消息==================================*/
	
	/**设置所属行业*/
	public final static String TEMP_API_SET_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
	/**获取设置的行业信息*/
	public final static String TEMP_GET_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=ACCESS_TOKEN";
	/**获得模板ID*/
	public final static String API_ADD_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
	/**获取模板列表*/
	public final static String GET_ALL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=ACCESS_TOKEN";
	/**删除模板*/
	public final static String DEL_PRIVATE_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=ACCESS_TOKEN";
	/**发送模板消息*/
	public final static String TEMP_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	
	/**模板消息==================================*/
	
	/**自动回复*/
	public final static String GET_CURRENT_AUTOREPLY_INFO = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=ACCESS_TOKEN"; 

	/**客服消息**********************************/
	
	/**添加客服帐号*/
	public final static String KFACCOUNT_ADD = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN";
	
	/**修改客服账号*/
	public final static String KFACCOUNT_UPDATE = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=ACCESS_TOKEN";
	
	/**删除客服账号*/
	public final static String KFACCOUNT_DEL = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=ACCESS_TOKEN";
	
	/**设置客服帐号的头像*/
	public final static String KFACCOUNT_UPLOADHEADIMG = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT";
	
	/**获取所有客服账号*/
	public final static String CUSTOMSERVICE_GETKFLIST = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN";
	
	/**客服接口-发消息*/
	public final static String CUSTOM_SEND = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	
	/**客服输入状态*/
	public final static String CUSTOM_TYPING = "https://api.weixin.qq.com/cgi-bin/message/custom/typing?access_token=ACCESS_TOKEN";
	
	
	
	/**客服消息**********************************/
	
    @Autowired 
    public RedisTemplate redisTemplate;

     /**
 	 * 读取微信token从缓存读取
 	 * token缓存的构成
 	 * weixinToken={token:xxx,token_timp:xxx,token_num:xxx}
 	 * token值
 	 * token_timp获取时间搓
 	 * token_num:当天获取次数
 	 * @param filePath
 	 * @return
 	 */
 	public  String readWxToken() {
 		return readTxtFile("c:/access_token.txt");
 	}
 	public String readTxtFile(String filePath) {
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					//System.out.println(lineTxt);
					return lineTxt;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return "";
	}
 	
 	/**
 	 * 读取微信ticket从缓存读取
 	 * ticket缓存的构成
 	 * weixinTicket={ticket:xxx,ticket_timp:xxx,ticket_num:xxx}
 	 * ticket值
 	 * ticket_timp获取时间搓
 	 * ticket_num:当天获取次数
 	 * @param filePath
 	 * @return
 	 */
 	public  String readWxTicket() {
 		return readTxtFile("c:/access_ticket.txt");
 		
 	}
 	/**
 	 * 读取微信用户的个人基本信息包含openid
 	 */
 	public  PageData readWxOpenIdAndInfo(String openid) {
 		String openidStr = openid;
 		if(Tools.isEmpty(openid)){
 			openidStr = "openid";
 		}
 		PageData map = (PageData)getCacheObject(openidStr);
 		if(map != null){
 			return map;
 		}else{
 			return null;
 		}
 	}1
 	
 	/**
 	 * 写入微信用户的个人基本信息包含openid
 	 */
 	public  boolean writeWxOpenIdAndInfo(PageData pd){
 		
 		setCacheObject(pd.getString("YWU010"),pd);
 		return true;
 	}
 	/**
 	 * 微信token写入缓存
 	 * token缓存的构成
 	 * weixinToken={token:xxx,token_timp:xxx,token_num:xxx}
 	 * token值
 	 * token_timp获取时间搓
 	 * token_num:当天获取次数
 	 * @param filePath
 	 * @return
 	 */
 	public  boolean writeWxToken(String token) {
 		
 		PrintWriter pw;
		try {
			pw = new PrintWriter( new FileWriter( "c:/access_token.txt" ) );
			pw.print(token);
	        pw.close();
	         
		} catch (IOException e) {
			e.printStackTrace();
		}
// 		Map<String,Object> map = new HashMap();
// 		map.put("token", token);
// 		map.put("token_timp",DateUtil.get1000ParsedDate());
// 		map.put("token_num", "");
// 		setCacheObject("weixinToken", map);
 		return true;
 	}
 	
 	/**
 	 * 微信ticket写入缓存
 	 * ticket缓存的构成
 	 * weixinToken={token:xxx,token_timp:xxx,token_num:xxx}
 	 * token值
 	 * token_timp获取时间搓
 	 * token_num:当天获取次数
 	 * @param filePath
 	 * @return
 	 */
 	public  boolean writeWxTicket(String ticket) {
 		PrintWriter pw;
		try {
			pw = new PrintWriter( new FileWriter( "c:/access_ticket.txt" ) );
			pw.print(ticket);
	        pw.close();
	         
		} catch (IOException e) {
			e.printStackTrace();
		}
// 		Map<String,Object> map = new HashMap();
// 		map.put("ticket", ticket);
// 		map.put("ticket_timp",DateUtil.get1000ParsedDate());
// 		map.put("ticket_num", "");
// 		setCacheObject("weixinTicket", map);
 		return true;
 	}
 	
 	/**
 	 * 微信jssdk:ticket写入缓存
 	 * 
 	 */
 	public  boolean writeWxJSSDKTicket(String ticket) {
 		PrintWriter pw;
		try {
			pw = new PrintWriter( new FileWriter( "c:/access_jssdkticket.txt" ) );
			pw.print(ticket);
	        pw.close();
	         
		} catch (IOException e) {
			e.printStackTrace();
		}
// 		Map<String,Object> map = new HashMap();
// 		map.put("jssdkTicket", ticket);
// 		map.put("ticket_timp",DateUtil.get1000ParsedDate());
// 		map.put("ticket_num", "");
// 		setCacheObject("jssdkTicket", map);
 		return true;
 	}
 	
 	/**
 	 * 读取微信jssdk:ticket从缓存读取
 	 */
 	public  String readWxJSSDKTicket() {
 		return readTxtFile("c:/access_jssdkticket.txt");
 	}

     /**
      * 缓存基本的对象，Integer、String、实体类等
      * @param key 缓存的键值
      * @param value 缓存的值
      * @return  缓存的对象
      */
     public <T> ValueOperations<String,T> setCacheObject(String key,T value)
     {
      System.out.println(key+"*****"+value.toString());
      ValueOperations<String,T> operation = redisTemplate.opsForValue(); 
      operation.set(key,value);
      return operation;
     }

     /**
      * 获得缓存的基本对象。
      * @param key  缓存键值
      * @param operation
      * @return   缓存键值对应的数据
      */
     public <T> T getCacheObject(String key/*,ValueOperations<String,T> operation*/)
     {
      ValueOperations<String,T> operation = redisTemplate.opsForValue(); 
      return operation.get(key);
     }

     /**
      * 缓存List数据
      * @param key  缓存的键值
      * @param dataList 待缓存的List数据
      * @return   缓存的对象
      */
     public <T> ListOperations<String, T> setCacheList(String key,List<T> dataList)
     {
      ListOperations listOperation = redisTemplate.opsForList();
      if(null != dataList)
      {
       int size = dataList.size();
       for(int i = 0; i < size ; i ++)
       {

        listOperation.rightPush(key,dataList.get(i));
       }
      }

      return listOperation;
     }

     /**
      * 获得缓存的list对象
      * @param key 缓存的键值
      * @return  缓存键值对应的数据
      */
     public <T> List<T> getCacheList(String key)
     {
      List<T> dataList = new ArrayList<T>();
      ListOperations<String,T> listOperation = redisTemplate.opsForList();
      Long size = listOperation.size(key);

      for(int i = 0 ; i < size ; i ++)
      {
       dataList.add((T) listOperation.leftPop(key));
      }

      return dataList;
     }

     /**
      * 缓存Set
      * @param key  缓存键值
      * @param dataSet 缓存的数据
      * @return   缓存数据的对象
      */
     public <T> BoundSetOperations<String,T> setCacheSet(String key,Set<T> dataSet)
     {
      BoundSetOperations<String,T> setOperation = redisTemplate.boundSetOps(key); 
      /*T[] t = (T[]) dataSet.toArray();
        setOperation.add(t);*/


      Iterator<T> it = dataSet.iterator();
      while(it.hasNext())
      {
       setOperation.add(it.next());
      }

      return setOperation;
     }

     /**
      * 获得缓存的set
      * @param key
      * @param operation
      * @return
      */
     public Set<T> getCacheSet(String key/*,BoundSetOperations<String,T> operation*/)
     {
      Set<T> dataSet = new HashSet<T>();
      BoundSetOperations<String,T> operation = redisTemplate.boundSetOps(key); 

      Long size = operation.size();
      for(int i = 0 ; i < size ; i++)
      {
       dataSet.add(operation.pop());
      }
      return dataSet;
     }

     /**
      * 缓存Map
      * @param key
      * @param dataMap
      * @return
      */
     public <T> HashOperations<String,String,T> setCacheMap(String key,Map<String,T> dataMap)
     {

      HashOperations hashOperations = redisTemplate.opsForHash();
      if(null != dataMap)
      {

       for (Map.Entry<String, T> entry : dataMap.entrySet()) { 

        /*System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); */
        hashOperations.put(key,entry.getKey(),entry.getValue());
       } 

      }

      return hashOperations;
     }

     /**
      * 获得缓存的Map
      * @param key
      * @param hashOperation
      * @return
      */
     public <T> Map<String,T> getCacheMap(String key/*,HashOperations<String,String,T> hashOperation*/)
     {
      Map<String, T> map = redisTemplate.opsForHash().entries(key);
      /*Map<String, T> map = hashOperation.entries(key);*/
      return map;
     }

     /**
      * 缓存Map
      * @param key
      * @param dataMap
      * @return
      */
     public <T> HashOperations<String,Integer,T> setCacheIntegerMap(String key,Map<Integer,T> dataMap)
     {
      HashOperations hashOperations = redisTemplate.opsForHash();
      if(null != dataMap)
      {

       for (Map.Entry<Integer, T> entry : dataMap.entrySet()) { 

        /*System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); */
        hashOperations.put(key,entry.getKey(),entry.getValue());
       } 

      }

      return hashOperations;
     }

     /**
      * 获得缓存的Map
      * @param key
      * @param hashOperation
      * @return
      */
     public <T> Map<Integer,T> getCacheIntegerMap(String key/*,HashOperations<String,String,T> hashOperation*/)
     {
      Map<Integer, T> map = redisTemplate.opsForHash().entries(key);
      /*Map<String, T> map = hashOperation.entries(key);*/
      return map;
     }

}
