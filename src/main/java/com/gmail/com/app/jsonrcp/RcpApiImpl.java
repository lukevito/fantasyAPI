package com.gmail.com.app.jsonrcp;


import com.gmail.com.app.bean.BeansUtill;
import com.gmail.com.app.jsonrcp.pojo.Ass;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.com.app.bean.BeansController.getApplicationBeans;

@Service
@AutoJsonRpcServiceImpl
public class RcpApiImpl implements RcpApi {

    private final ConfigurableApplicationContext context;

    @Autowired
    public RcpApiImpl(ConfigurableApplicationContext context) {
        this.context = context;
    }

    //curl -H "Content-Type: application/json" --data "{\"jsonrpc\":\"2.0\",\"method\":\"metoda.testowa\",\"params\":[],\"id\":1}" http://localhost:8080/api/jsonrcp
    @Override
    public List<Ass> test() {
        ArrayList<Ass> asses = new ArrayList<>();
        asses.add(new Ass());
        asses.add(new Ass());
        return asses;
    }

    //curl -H "Content-Type: application/json" --data "{\"jsonrpc\":\"2.0\",\"method\":\"bean.springa\",\"params\":[],\"id\":1}" http://localhost:8080/api/jsonrcp
    @Override
    public BeansUtill.ApplicationBeans allBeans() {
        return getApplicationBeans(this.context);
    }

}
