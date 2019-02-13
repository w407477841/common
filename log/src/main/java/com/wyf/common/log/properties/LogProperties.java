package com.wyf.common.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 16:24 2019/2/12
 * Modified By : wangyifei
 */
@ConfigurationProperties(prefix = "wyf.log")
@Data
public class LogProperties {
    private String level = "debug";
}
