package com.gmail.com.fantasy_api;


import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcService;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.List;

@JsonRpcService("/api/jsonrcp")
public interface RcpApi {

    @JsonRpcMethod("metoda.testowa")
    List<Ass> test();

    @JsonRpcMethod("bean.springa")
    ApplicationBeansUtill.ApplicationBeans allBeans();
}
