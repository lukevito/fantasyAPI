package com.gmail.com.app.jsonrcp;


import com.gmail.com.app.bean.BeansUtill;
import com.gmail.com.app.jsonrcp.pojo.Ass;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcService;

import java.util.List;

@JsonRpcService("/api/jsonrcp")
public interface RcpApi {

    @JsonRpcMethod("metoda.testowa")
    List<Ass> test();

    @JsonRpcMethod("bean.springa")
    BeansUtill.ApplicationBeans allBeans();
}
