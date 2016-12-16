package com.cse.utils.readparam;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;

/**
 * 读取配置文件
 * @author wycheng
 * @date 2015年11月17日
 */
public final class ReadConfParam {

    private static Configuration config;

    static {
        try {
            ConfigurationFactory factory = new ConfigurationFactory();
            factory.setConfigurationURL(ReadConfParam.class.getResource("/properties/configuration.xml"));
            config = factory.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件值
     * @param key 键
     * @return 值
     */
    public static String getMessage(String key) {

        if (!config.containsKey(key)) {
            return null;
        }
        String value = config.getString(key);
        return value;
    }

    /**
     * 读取配置的list值
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static List getMessageList(String key) {

        if (!config.containsKey(key)) {
            return null;
        }
        List value = config.getList(key);
        return value;
    }

    private ReadConfParam() {
    }

}
