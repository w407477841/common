package com.wyf.common.log;

import com.alibaba.fastjson.JSON;
import com.wyf.common.log.annotations.OpenLog;
import com.wyf.common.log.properties.LogProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 16:22 2019/2/12
 * Modified By : wangyifei
 */
@Configuration
@ConditionalOnProperty(prefix = "wyf.log",value = "enabled",matchIfMissing = true)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("log");

    private static final  String execution = "@annotation(openLog)" ;

    private final LogProperties logProperties;

    public LogAutoConfig(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Component
    @Aspect
    public class LogAop {
        public  LogAop() {
            System.out.println("---------------------------------------------------------");
            System.out.println("----------------------志拦截器已启动---------------------");
            System.out.println("---------------------------------------------------------");
        }
        @Around(value = execution)
        public Object aroundAdvice(ProceedingJoinPoint pjp, OpenLog openLog)throws Throwable{
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            //防止不是http请求的方法，例如：scheduled
            if (ra == null || sra == null) {
                String class_method = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
                String request_args = JSON.toJSONString(pjp.getArgs());
                String response_data ="";
                long spend;
                long startTime = System.currentTimeMillis();
                String error_msg = "";
                try {
                    Object response = pjp.proceed();
                    // 3.出参打印
                    response_data = response != null ? JSON.toJSONString(response) : "";
                    return response;
                } catch (Throwable e) {
                    error_msg = e.getMessage() ;
                    throw e;
                } finally {
                    long endTime = System.currentTimeMillis();
                    spend  = (endTime - startTime);
                    if(error_msg!=null&&!error_msg.equals("")){
                        log("error"," CLASS_METHOD <{}> REQUEST_ARGS <{}> | RESPONSE <{}> | ERROR <{}> | SPEND <{}ms>",class_method,request_args,response_data,error_msg,spend+"");
                    }else {
                        log("info"," CLASS_METHOD <{}> REQUEST_ARGS <{}> | RESPONSE <{}> | ERROR <{}> | SPEND <{}ms>",class_method,request_args,response_data,error_msg,spend+"");
                    }

                }

            }
            HttpServletRequest request = sra.getRequest();
            String url = request.getRequestURL().toString();
            String http_method = request.getMethod();
            String ip = request.getRemoteAddr() ;
            String class_method = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
            String request_args = JSON.toJSONString(pjp.getArgs());
            String response_data ="";
            String error_msg = "";
            long spend;
            long startTime = System.currentTimeMillis();
            try {
                Object response = pjp.proceed();
                // 3.出参打印
                response_data = response != null ? JSON.toJSONString(response) : "";
                return response;
            } catch (Throwable e) {
                error_msg = e.getMessage() ;
                throw e;
            } finally {
                long endTime = System.currentTimeMillis();
                spend  = (endTime - startTime);
                if(error_msg!=null&&!error_msg.equals("")){
                    log("error","URL <{}> | HTTP_METHOD <{}> | IP <{}> | CLASS_METHOD <{}> REQUEST_ARGS <{}> | RESPONSE <{}> | ERROR <{}> | SPEND <{}ms>",url,http_method,ip,class_method,request_args,response_data,error_msg,spend+"");
                }else{
                    log("info","URL <{}> | HTTP_METHOD <{}> | IP <{}> | CLASS_METHOD <{}> REQUEST_ARGS <{}> | RESPONSE <{}> | ERROR <{}> | SPEND <{}ms>",url,http_method,ip,class_method,request_args,response_data,error_msg,spend+"");
                }

            }

        }

        private void log(String level,String msg, String ...args){
            switch (level){
                case "info" :
                    LOGGER.info(msg,args);
                    break;
                case "error":
                    LOGGER.error(msg,args);
                    break;
                default:
                    LOGGER.info(msg,args);
                    break;
            }
        }


    }


}
