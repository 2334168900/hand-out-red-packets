package org.example.controller;

import cn.hutool.core.util.IdUtil;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class redoscontroller {
    public static final String RED_PACKAGE_KEY = "redpackage:";
    public static final String RED_PACKAGE_CONSUME_KEY = "redpackage:consume:";
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/send")
    public String sendRedPackage(int money,int number)
    {
        Integer[] splitrp = splitRed(money,number);
        String key = RED_PACKAGE_KEY+IdUtil.simpleUUID();
        redisTemplate.opsForList().leftPushAll(key,splitrp);
        redisTemplate.expire(key,1, TimeUnit.DAYS);
        return key+"\t"+ Ints.asList(Arrays.stream(splitrp).mapToInt(Integer::valueOf).toArray());
    }

    @RequestMapping(value = "/rob")
    public String robRedPackage(String key,String userid)
    {
        Object redp = redisTemplate.opsForHash().get(RED_PACKAGE_CONSUME_KEY + key, userid);
        if(null == redp)
        {
            Object partrp = redisTemplate.opsForList().leftPop(RED_PACKAGE_KEY + key);
            if(partrp != null)
            {
                redisTemplate.opsForHash().put(RED_PACKAGE_CONSUME_KEY+key,userid,partrp);
                System.out.println("用户:"+userid+"\t 抢到了多少钱的红包："+partrp);
                return String.valueOf(partrp);
            }
            return "errorCode:-1,红包抢完了";
        }
        return "errorCode:-2,   message:"+userid+"\t"+"你已经抢过红包了，不能重新抢";
    }


    private Integer[] splitRed(int money,int number){
        Integer[] redpackage = new Integer[number];

        int useMoney=0;
        for(int i=0;i<number;i++){
            if(i == number-1){
                redpackage[i] = money - useMoney;
            }else {
                int avgmoney = ((money - useMoney)/(number - i))*2;
                redpackage[i] = 1+new Random().nextInt(avgmoney);
            }
            useMoney = useMoney + redpackage[i];
        }
        return redpackage;
    }


}
