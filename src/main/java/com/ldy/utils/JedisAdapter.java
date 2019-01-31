package com.ldy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool jedisPool;

/*
    public static void print(int index, Object obj) {
        System.out.println(String.format("第%d次:%s", index, obj.toString()));
    }


    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("redis://localhost:6379/9")) {
            jedis.flushDB();//删除本数据库数据

            //get set操作
            jedis.set("hello", "world");
            print(1, jedis.get("hello"));
            jedis.rename("hello", "newhello");
            print(1, jedis.get("newhello"));
            jedis.setex("hello2", 1800, "world");//设置过期时间。可以用在验证码上：1分钟内验证码有效，之后失效。缓存也可以用这个。

            //数值操作，可以用在那些变化很快的数据，如秒杀数量，阅读量，访问量等上
            jedis.set("pv", "100");
            jedis.incr("pv");//将pv值+1
            jedis.incrBy("pv", 5);
            print(2, jedis.get("pv"));
            jedis.decr("pv");
            jedis.decrBy("pv", 10);
            print(3, jedis.get("pv"));

            print(3, jedis.keys("*"));//显示数据库中所有的key

            //list操作（相当于栈结构），可以用在保存帖子关注人上
            String listname = "list";
            jedis.del(listname);

            for (int i = 0; i < 10; i++) {
                jedis.lpush(listname, "a" + String.valueOf(i));
            }

            print(4, jedis.lrange(listname, 0, 12));
            print(4, jedis.lrange(listname, 0, 3));
            print(5, jedis.llen(listname));
            print(6, jedis.lpop(listname));
            print(5, jedis.llen(listname));
            print(7, jedis.lrange(listname, 2, 6));
            print(8, jedis.lrange(listname, 2, 6));
            print(8, jedis.lindex(listname, 3));
            print(9, jedis.linsert(listname, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
            print(9, jedis.linsert(listname, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));

            //hash，相当于hashmap，“userxx"为key，value里又是几个hashmap。好处：可以频繁增删属性
            String userkey = "userxx";
            jedis.hset(userkey, "name", "jim");
            jedis.hset(userkey, "age", "12");
            jedis.hset(userkey, "phone", "1232132131");
            print(12, jedis.hget(userkey, "name"));
            print(13, jedis.hgetAll(userkey));
            jedis.hdel(userkey, "phone");
            print(14, jedis.hgetAll(userkey));
            print(15, jedis.hexists(userkey, "email"));
            print(16, jedis.hexists(userkey, "age"));
            print(17, jedis.hkeys(userkey));
            print(18, jedis.hvals(userkey));
            jedis.hsetnx(userkey, "school", "zju");//如果不存在，则设置，防止重复写入
            jedis.hsetnx(userkey, "name", "yxy");
            print(19, jedis.hgetAll(userkey));

            // set
            String likeKey1 = "commentLike1";
            String likeKey2 = "commentLike2";

            for (int i = 0; i < 10; i++) {
                jedis.sadd(likeKey1, String.valueOf(i));
                jedis.sadd(likeKey2, String.valueOf(i * i));
            }

            print(20, jedis.smembers(likeKey1));
            print(21, jedis.smembers(likeKey2));
            print(22, jedis.sunion(likeKey1, likeKey2));//求并集
            print(23, jedis.sdiff(likeKey1, likeKey2));
            print(24, jedis.sinter(likeKey1, likeKey2));//求交集，共同好友，共同关注等
            print(25, jedis.sismember(likeKey1, "12"));
            print(26, jedis.sismember(likeKey1, "16"));
            print(27, jedis.smembers(likeKey1));
            jedis.smove(likeKey2, likeKey1, "25");//将2中的25移到1中
            print(28, jedis.smembers(likeKey1));
            print(29, jedis.scard(likeKey1));

            //优先队列
            String rankKey = "rankKey";
            jedis.zadd(rankKey, 15, "jim");//15即为排序权重
            jedis.zadd(rankKey, 60, "Ben");
            jedis.zadd(rankKey, 75, "Lucy");
            jedis.zadd(rankKey, 80, "Mei");
            // 获取总个数
            print(30, jedis.zcard(rankKey));
            print(31, jedis.zcount(rankKey, 61, 100));
            print(32, jedis.zscore(rankKey, "Lucy"));
            jedis.zincrby(rankKey, 2, "Lucy");
            print(33, jedis.zscore(rankKey, "Lucy"));
            jedis.zincrby(rankKey, 2, "Luc");
            print(33, jedis.zscore(rankKey, "Luc"));
            print(34, jedis.zrange(rankKey, 0, 100));
            print(34, jedis.zrange(rankKey, 0, 10));
            print(34, jedis.zrange(rankKey, 1, 3));//默认从小到大排序，序号从0开始
            print(34, jedis.zrevrange(rankKey, 1, 3));
            //遍历打印
            for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 60, 100)) {
                print(38, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
            }

            print(39, jedis.zrank(rankKey, "Ben"));
            print(39, jedis.zrevrank(rankKey, "Ben"));

            //分值都一样时，按key的排序
            String setKey = "zset";
            jedis.zadd(setKey, 1, "a");
            jedis.zadd(setKey, 1, "b");
            jedis.zadd(setKey, 1, "b");
            jedis.zadd(setKey, 1, "d");
            jedis.zadd(setKey, 1, "e");

            print(40, jedis.zlexcount(setKey, "-", "+"));//-表示负无穷，+表示正无穷
            print(41, jedis.zlexcount(setKey, "(b", "[d"));//不包含b
            print(41, jedis.zlexcount(setKey, "[b", "[d"));
            jedis.zrem(setKey, "b");// 移除
            print(43, jedis.zrange(setKey, 0, 10));
            jedis.zremrangeByLex(setKey, "(c", "+");
            print(44, jedis.zrange(setKey, 0, 2));

            //连接池
            JedisPool pool = new JedisPool();
            for (int i = 0; i < 100; i++) {
                Jedis j = pool.getResource();
                print(45, jedis.get("pv"));
                j.close();
            }

            //利用redis做缓存，现将对象序列化，再set进去
            User user = new User();
            user.setName("xx");
            user.setPassword("ppp");
            user.setHeadUrl("a.png");
            user.setSalt("salt");
            user.setId(1);
            print(46, JSONObject.toJSONString(user));
            jedis.set("user1", JSONObject.toJSONString(user));//将user保存为jason格式放入到redis中
            //取出来
            String value = jedis.get("user1");
            User userFromJson = JSON.parseObject(value, User.class);//将Jason转换成user
            print(47, userFromJson.toString());


        } catch (Exception e) {
            logger.error("操作redis失败" + e.getMessage());
        }
    }*/


    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("redis://localhost:6379/10");
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }


    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }


    /**
     * 判断是否在set里面，1则表示在,0=则表示不存在
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }


    public List<String> brpop(int timeout, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }

    public long lpush(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }


    public List<String> lrange(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;

    }

    public long zadd(String key, double score, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }


    public Jedis getJedis() {
        return jedisPool.getResource();
    }


    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }


    /**
     * 执行事物块里面的命令
     *
     * @param tx
     * @param jedis
     * @return
     */
    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常", e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException e) {
                    logger.error("关闭redis事物发生异常" + e.getMessage());
                }
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public Set<String> zrange(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }


    public Set<String> zrevrange(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }


    public long zcard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }


    public Double zscore(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }

}












