package com.me2me.cache.service;

import com.me2me.core.cache.JedisTemplate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.*;

import javax.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/12.
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Value("#{app.redisHost}")
    private String host;
    @Value("#{app.redisPort}")
    private int port;
    @Value("#{app.redisTimeout}")
    private int timeout;
    @Value("#{app.redisMaxTotal}")
    private int maxTotal;
    @Value("#{app.redisMaxWaitMillis}")
    private int maxWaitMillis;
    @Value("#{app.redisMaxIdle}")
    private int maxIdle;

    @Autowired
    private JedisTemplate jedisTemplate;

//    @Bean
//    private HttpInvokerProxyFactoryBean initBean(){
//        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
//        httpInvokerProxyFactoryBean.setServiceUrl("");
//        httpInvokerProxyFactoryBean.setServiceInterface(null);
//        return httpInvokerProxyFactoryBean;
//    }


    @PostConstruct
    public void initPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxIdle(maxIdle);
        JedisPool jedisPool = new JedisPool(poolConfig,host,port,timeout);
        this.jedisTemplate.setJedisPool(jedisPool);
        log.info("init redis pool from server {} at port {} ",host,port);
    }

    @Override
    public void set(final String key, final String value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.set(key,value);
            }
        });
    }

    @Override
    public void del(final String key) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.del(key);
            }
        });
    }

    @Override
    public void setex(final String key, final String value, final int timeout) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.setex(key,timeout,value);
            }
        });
    }

    @Override
    public String get(final String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.get(key);
            }
        });
    }

    @Override
    public void sadd(final String key, final String ... values) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.sadd(key,values);
            }
        });
    }

    @Override
    public Set<String> smembers(final String key){
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.smembers(key);
            }
        });
    }
    
    @Override
    public void srem(final String key, final String... values){
    	jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.srem(key,values);
            }
        });
    }

    @Override
    public void flushDB() {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.flushDB();
            }
        });
    }

    @Override
    public void lPush(final String key, final String ... value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.lpush(key,value);
            }
        });
    }

    @Override
    public void rPush(final String key, final String... value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.rpush(key, value);
            }
        });
    }

    @Override
    public String lPop(final String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.lpop(key);
            }
        });
    }

    @Override
    public void expire(final String key, final int timeout) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.expire(key, timeout);
            }
        });
    }

    public void hSet(final String key,final String field,final String value){
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.hset(key, field,value);
            }
        });
    }

    @Override
    public String hGet(final String key, final String field) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.hget(key,field);
            }
        });
    }

    @Override
    public void hDel(final String key, final String ... field) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.hdel(key, field);
            }
        });
    }

    @Override
    public Map<String,String> hGetAll(final String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.hgetAll(key);
            }
        });
    }

    @Override
    public void hSetAll(final String key, final Map<String, String> stringMap) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.hmset(key,stringMap);
            }
        });
    }

    @Override
    public List<String> lrange(final String key, final int start, int end) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.lrange(key,start,end);
            }
        });
    }

    @Override
    public void lrem(String key, long count, String value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.lrem(key, count,value);
            }
        });
    }

    @Override
    public long incr(String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.incr(key);
            }
        });
    }
    
    @Override
    public long decr(String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.decr(key);
            }
        });
    }
    
    @Override
    public long incrby(String key, long num) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.incrBy(key, num);
            }
        });
    }
    
    @Override
    public long decrby(String key, long num) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.decrBy(key, num);
            }
        });
    }

    @Override
    public Set<String> keys(String pattern) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.keys(pattern);
            }
        });
    }
    public void cacheJavaObject(String key,Object obj){
    	List<Exception> exceptions =new ArrayList<>();
    	jedisTemplate.execute(new JedisTemplate.JedisAction() {		// 这方法太坑，有异常居然不抛出来,使用时自己注意看LOG日志
			
			@Override
			public void action(Jedis jedis) {
				try {
					byte[] data = serialize(obj);
					jedis.set(key.getBytes(), data);
				} catch (Exception e) {
					exceptions.add(e);
				}
			}
		});
    	if(exceptions.size()>0){
    		throw new RuntimeException("存java对象失败",exceptions.get(0));
    	}
    	
    }
    public void cacheJavaObject(String key,Object obj,int seconds){
    	List<Exception> exceptions =new ArrayList<>();
    	jedisTemplate.execute(new JedisTemplate.JedisAction() {
			
			@Override
			public void action(Jedis jedis) {
				try {
					byte[] keybytes= key.getBytes();
					byte[] data= serialize(obj);
					jedis.set(keybytes, data);
					jedis.expire(keybytes, seconds);
				} catch (Exception e) {
					exceptions.add(e);
				}
			}
		});
    	if(exceptions.size()>0){
    		throw new RuntimeException("存java对象失败",exceptions.get(0));
    	}
    }
    public Object getJavaObject(String key){
    	return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
			@Override
			public Object actionResult(Jedis jedis) {
				try{
					byte[] keybytes= key.getBytes();
					byte[] data = jedis.get(keybytes);
					Object obj = null;
					if(null != data){
						obj = unserialize(data);
					}
					return obj;
				} catch (Exception e) {
					throw new RuntimeException("取java对象失败",e);
				}
			}
		});
    }
	public static byte[] serialize(Object object) throws Exception {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			throw e;
		}finally {
			if(oos!=null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(baos!=null){
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Object unserialize(byte[] bytes) throws Exception {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois=null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
			
		} catch (Exception e) {
			throw e;
		}finally {
			if(bais!=null){
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(ois!=null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
