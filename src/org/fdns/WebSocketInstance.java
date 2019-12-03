package org.fdns;

import com.google.gson.Gson;
import org.fdns.requests.InitializeRequest;
import org.fdns.requests.ProxyPathRequest;
import org.fdns.requests.Request;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WebSocketInstance {

    Gson json = new Gson();
    Map<String, Host> similarHosts = new LinkedHashMap<>();
    Map<String, Host> fastHosts = new LinkedHashMap<>();
    Map<String, String> selfDomains = new LinkedHashMap<>(); //  domain -> nextOwnerDomain

    int similarHostsCount = 20;
    int dissimilarHostsCount = 20;
    Random random = new Random();

    void send(String domain, Request request) {
        WebSocketNetwork.send(domain, json.toJson(request));
    }

    public static String randomBase64String(int length) {
        Random random = ThreadLocalRandom.current();
        byte[] r = new byte[length];
        random.nextBytes(r);
        return new String(Base64.getEncoder().encode(r));
    }

    void sendToRandomHosts(Map<String, Host> hosts, int hostCount, Request request) {
        for (int i = 0; i < hostCount; i++) {
            int randomSimilarHostIndex = random.nextInt() % hosts.size();
            String randomSimilarHostDomain = new ArrayList<>(hosts.keySet()).get(randomSimilarHostIndex);
            send(randomSimilarHostDomain, request);
        }
    }

    public void onReceive(String data) {
        Request request = json.fromJson(data, Request.class);
        String requestType = request.requestType;
        if (requestType.equals(InitializeRequest.class.getSimpleName())) {
            onInitializeRequest(json.fromJson(data, InitializeRequest.class));
        } else if (requestType.equals(ProxyPathRequest.class.getSimpleName())){
            onProxyPathRequest(json.fromJson(data, ProxyPathRequest.class));
        }
    }

    public void initRegistration(String domain) {
        String nextOwnerDomain = randomBase64String(16);
        String nextOwnerDomainHash = MD5.encode(nextOwnerDomain);
        selfDomains.put(domain, nextOwnerDomainHash);
        Host newHost = new Host(domain, nextOwnerDomainHash);
        InitializeRequest request = new InitializeRequest(newHost);
        sendToRandomHosts(fastHosts, 10, request);
    }

    private void onInitializeRequest(InitializeRequest request) {
        if (new ArrayList<>(similarHosts.keySet()).indexOf(request.host.domain) == -1) {

        }
    }

    List<String> getProxyPath(String domain) {
        ProxyPathRequest request = new ProxyPathRequest();
        send(domain, request);
        return null;
    }

    private void onProxyPathRequest(ProxyPathRequest request) {
    }

}
