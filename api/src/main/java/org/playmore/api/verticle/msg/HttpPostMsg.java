package org.playmore.api.verticle.msg;


import java.util.Map;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:56
 * @description TODO
 */
public class HttpPostMsg {
    private final String url;
    private String postBody;
    private Map<String, String> postMap;
    private Map<String, String> headerMap;
    private byte[] protobuf;

    public HttpPostMsg(String url, Map<String, String> headerMap) {
        this.url = url;
        this.headerMap = headerMap;
    }

    public HttpPostMsg(String url, String postBody, Map<String, String> headerMap) {
        this.url = url;
        this.postBody = postBody;
        this.headerMap = headerMap;
    }

    public HttpPostMsg(String url, Map<String, String> postMap, Map<String, String> headerMap) {
        this.url = url;
        this.postMap = postMap;
        this.headerMap = headerMap;
    }

    public HttpPostMsg(String url, byte[] protobuf) {
        this.url = url;
        this.protobuf = protobuf;
    }
}
