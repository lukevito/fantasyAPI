package com.gmail.com.fantasy_api;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NeoUtil {

    private final NeoConfiguration neoConfiguration;

    @Autowired
    public NeoUtil(NeoConfiguration neoConfiguration) {
        this.neoConfiguration = neoConfiguration;
    }

    public JSONObject getNeo4jConf() {
        JSONObject o = new JSONObject();
        o.put("server_url", neoConfiguration.getUrl());
        o.put("server_user", neoConfiguration.getUser());
        o.put("server_password", neoConfiguration.getPassword());
        return o;
    }
}
