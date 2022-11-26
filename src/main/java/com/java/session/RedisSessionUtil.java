package com.java.session;

import com.dbsun.entity.system.PageData;
import com.dbsun.mapper.SysUserMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Redis Session 会话管理
 *
 * @author zhao yi
 * @create 2018-06-05 20:59
 * @desc
 **/
@Component
public class RedisSessionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RedisSessionUtil.class);

    @Autowired
    @Qualifier("sessionRedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private SysUserMapper userMapper;

    /**
     * The default prefix for each key and channel in Redis used by Spring Session.
     */
    static final String SPRING_SESSION_REDIS_PREFIX = "spring:session:";
    /**
     * 主要 Session Key 主键
     */
    static final String SPRING_SESSION_PREFIX = "spring:session:sessions:";
    /**
     * 到期时间前缀
     */
    static final String SESSIONS_EXPIRES__PREFIX = "spring:session:sessions:expires:";
    /**
     * 超时时间前缀
     */
    static final String SESSIONS_EXPIRATIONS_PREFIX = "spring:session:expirations:";

    /**
     * session 创建时间 spring:session:sessions: + [sessionId] +creationTime
     * The key in the Hash representing
     * {@link org.springframework.session.ExpiringSession#getCreationTime()}.
     */
    static final String CREATION_TIME_ATTR = "creationTime";

    /**
     * The key in the Hash representing
     * {@link org.springframework.session.ExpiringSession#getMaxInactiveIntervalInSeconds()}
     * .
     */
    static final String MAX_INACTIVE_ATTR = "maxInactiveInterval";

    /**
     * session 最后访问时间啊in
     * The key in the Hash representing
     * {@link org.springframework.session.ExpiringSession#getLastAccessedTime()}.
     */
    static final String LAST_ACCESSED_ATTR = "lastAccessedTime";

    /**
     * The prefix of the key for used for session attributes. The suffix is the name of
     * the session attribute. For example, if the session contained an attribute named
     * attributeName, then there would be an entry in the hash named
     * sessionAttr:attributeName that mapped to its value.
     */
    static final String SESSION_ATTR_PREFIX = "sessionAttr:";


    /***
     * 获得所有会话 sessionAll
     * @return
     */
    public Set<Object> getSessionAll() {
        return getRedisSessions(SPRING_SESSION_PREFIX);
    }

    /***
     * 获得所有会话的数量 [包括未登录的人员]
     * @return
     * @throws IOException
     */
    public int getSessionSize() {
        return getSessionAll().size();
    }


    /***
     *获得所有的Session
     * @param sessionRedisPrefix redis 存储默认方式前缀 [spring:session:]
     * @return
     */
    private Set<Object> getRedisSessions(String sessionRedisPrefix) {
        String likeSession = sessionRedisPrefix + "*";//通配符
        Set<Object> keys = redisTemplate.keys(likeSession);
        Iterator<Object> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next != null && next.toString().contains(SESSIONS_EXPIRES__PREFIX)) {
                iterator.remove();
            }
        }
        return keys;
    }


    /***
     * 获得当前 在线用户
     * @return
     */
    public JSONArray getLoginUsers() {
        //获得所有session
        Set<Object> keys = getSessionAll();
        //重新组装
        JSONArray array = new JSONArray();
        //过滤  非在线用户
        Iterator<Object> iterator2 = keys.iterator();
        while (iterator2.hasNext()) {
            String key = iterator2.next().toString();
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            Iterator<Map.Entry<Object, Object>> iterator = entries.entrySet().iterator();

            if (entries.containsKey(SESSION_ATTR_PREFIX + "user")) {// 如果包含 user 判断为登录用户
                JSONObject temp = new JSONObject();

                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> next = iterator.next();
                    String k = next.getKey().toString();

                    if ((SESSION_ATTR_PREFIX + "user").equals(k)) { //如果包含user登录用户认定为登录用户
                        PageData user = (PageData) next.getValue();
                        temp.put("sessionId", key.substring(key.lastIndexOf(":") + 1));//sesionId
                        temp.put("NAME", user.getString("NAME"));
                        temp.put("NUMBER", user.getString("NUMBER"));
                        temp.put("DEPT_ID", user.getString("DEPT_ID"));
                        temp.put("USERNAME", user.getString("USERNAME"));
                        final String version = user.getString("version");
                        temp.put("version", user.getString("version"));
                    }
                    if ((CREATION_TIME_ATTR).equals(k)) {//获得创建时间
                        Long creationTime = Long.parseLong(next.getValue().toString());
                        temp.put(CREATION_TIME_ATTR, DateUtil.fomatDateTime(new Date(creationTime)));
                        temp.put("onlineTime", DateUtil.getDifferenceTime(System.currentTimeMillis(), creationTime)); //计算在线时间
                    }
                    if ((LAST_ACCESSED_ATTR).equals(k)) {//获得最近访问时间
                        Long lastAccessedTime = Long.parseLong(next.getValue().toString());
                        temp.put(LAST_ACCESSED_ATTR, DateUtil.fomatDateTime(new Date(lastAccessedTime)));
                    }
                }
                array.add(temp);
            }
        }

        return array;
    }

    /***
     * 获得当前 在线用户 + 增加部门信息
     * @return
     */
    public JSONArray getSessionIsLoginUsers() {
        return newGetSessionIsLoginUsers(this.getLoginUsers());
    }


    /**
     * 组装添加部门
     *
     * @lick getSessionIsLoginUsers()
     * @Desc 为在线用户添加部门名字, 然后按造部门名字排序
     */
    private JSONArray newGetSessionIsLoginUsers(JSONArray userList) {
        //获得部门 数据
        List<PageData> deptLs = userMapper.getDeptAllDeptLst(null);
        for (PageData dep : deptLs) {
            for (int i = 0; i < userList.size(); i++) {
                JSONObject user = (JSONObject) userList.get(i);
                if (dep.getString("DEPT_ID").equals(user.getString("DEPT_ID"))) {
                    final String dept_name = dep.getString("DEPT_NAME");
                    System.out.println(dept_name);
                    user.put("DEPT_ID", dep.getString("DEPT_NAME"));
                }
            }
        }
        //按造部门排序
        Collections.sort(userList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o1.getString("DEPT_ID").compareToIgnoreCase(o2.getString("DEPT_ID"));
            }
        });
        return userList;
    }

    /***
     * 注销 session
     * @param sessionId
     * @Desc 根据sessionId ,如果存在 删除Redis会话 (记得要注销session)
     */
    public void delUserSession(String sessionId) {
        if (StringUtils.isBlank(sessionId)) return;

        Set<Object> keys = getSessionAll();
        Iterator<Object> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String redisSessionKey = (String) iterator.next();
            if ((SPRING_SESSION_PREFIX + sessionId).equals(redisSessionKey)) {
                redisTemplate.delete(SPRING_SESSION_PREFIX + sessionId);
            }
        }

    }

    /***
     * 强制下线 user
     * @param sessionId
     * 根据sessionId ,如果存在
     * 清除session 的所有数据,更新初始时间,浏览器如果没有关闭 cookie:sessionId 是一直储存在客户端的(删除是没有用的)
     */
    public void forcedOfflineUser(String sessionId) {
        if (StringUtils.isBlank(sessionId)) return;

        Set<Object> keys = getSessionAll();
        Iterator<Object> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String redisSessionKey = (String) iterator.next();
            if ((SPRING_SESSION_PREFIX + sessionId).equals(redisSessionKey)) {
                redisTemplate.opsForHash().delete(SPRING_SESSION_PREFIX + sessionId, SESSION_ATTR_PREFIX + "user", SESSION_ATTR_PREFIX + "menulst");
                redisTemplate.opsForHash().put(SPRING_SESSION_PREFIX + sessionId, CREATION_TIME_ATTR, System.currentTimeMillis());
            }
        }

    }


    /**
     * sessionId 谦容浏览器
     * 查看缓存 判断用户是否登录 (谦容跨浏览器)
     *
     * @param username 账号
     *                 1. 遍历在线的的登录用户.判断账号是否存在,如果存在代表账户已经登录
     *                 2. 清除用户信息
     *                 3. 用户强制下线
     */
    public boolean checkUserIsLoggin(String username) {
        if (StringUtils.isNotBlank(username)) {
            JSONArray loginUsers = getLoginUsers();
            for (int i = 0; i < loginUsers.size(); i++) {
                final JSONObject user = (JSONObject) loginUsers.get(i);
                if (username.equals(user.getString("USERNAME"))) {
                    this.forcedOfflineUser(user.getString("sessionId"));//下线
                    return true;
                }
            }
        }
        return false;
    }

}
