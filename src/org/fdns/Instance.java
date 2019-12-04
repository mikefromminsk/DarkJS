package org.fdns;

import com.google.gson.Gson;
import org.fdns.requests.*;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Instance {

    private Random random = new Random();
    private Gson json = new Gson();
    private ArrayList<String> domains = new ArrayList<>();
    private Map<String, Owner> owners = new HashMap<>();
    private Map<Integer, RequestData> requestsData = new LinkedHashMap<>();
    private Long CONNECTION_TIMEOUT = 10000L; // milliseconds

    private Network network;
    private String selfIp;
    private List<String> selfDomains = new ArrayList<>();

    public Instance(Network network, String selfIp) {
        this.network = network;
        this.selfIp = selfIp;
    }

    private void send(String ip, Request request) throws ConnectException {
        network.send(ip, json.toJson(request));
    }

    void onDataRequestFinish(DataRequest data) {

    }

    public interface SuccessCallback {
        void run(String data);
    }

    public interface ErrorCallback {
        void run(String message);
    }

    public void post(String domain, String data, SuccessCallback success, ErrorCallback error) {
        removeExpiredRequests();
        Integer requestId = random.nextInt();
        RequestData requestData = new RequestData(requestId, new Date().getTime(), data, success, error);
        requestsData.put(requestId, requestData);
        sendProxyPathRequest(domain);
    }

    private void removeExpiredRequests() {
        Long expireTime = new Date().getTime() - CONNECTION_TIMEOUT;
        ArrayList<RequestData> expiredRequests = new ArrayList<>();
        for (RequestData requestData : requestsData.values())
            if (requestData.startTime < expireTime)
                expiredRequests.add(requestData);
        for (RequestData requestData : expiredRequests)
            onError(requestData.requestId, "connection timeout");
    }

    public void onReceive(String data) {
        Request request = json.fromJson(data, Request.class);
        String requestType = request.requestType;
        if (requestType.equals(PathRequest.class.getSimpleName())) {
            onPathRequest(json.fromJson(data, PathRequest.class));
        } else if (requestType.equals(PathBackRequest.class.getSimpleName())) {
            onPathBackRequest(json.fromJson(data, PathRequest.class));
        } else if (requestType.equals(DataRequest.class.getSimpleName())) {
            onDataRequest(json.fromJson(data, DataRequest.class));
        } else if (requestType.equals(RegistrationRequest.class.getSimpleName())) {
            onRegistrationRequest(json.fromJson(data, RegistrationRequest.class));
        }
    }

    private void onError(Integer requestId, String message) {
        RequestData requestData = requestsData.get(requestId);
        if (requestData != null && requestData.error != null)
            requestData.error.run(message);
        requestsData.remove(requestId);
    }

    List<String> findSimilarDomains(String domain) {
        // TODO add limit of similar results
        int difference = Integer.MAX_VALUE;
        List<String> similarDomains = new ArrayList<>();
        for (String currentDomain : domains) {
            int currentDifference = StringComparator.compare(domain, currentDomain);
            if (currentDifference < difference) {
                difference = currentDifference;
                similarDomains.clear();
                similarDomains.add(currentDomain);
            } else if (currentDifference == difference) {
                similarDomains.add(currentDomain);
            }
        }
        if (difference == 0 && similarDomains.indexOf(domain) != -1) {
            similarDomains.clear();
            similarDomains.add(domain);
        }
        // TODO sort by speed
        return similarDomains;
    }

    void filterPreviousDomains(List<String> similarDomains, List<String> domains) {
        // TODO develop
    }

    void sendProxyPathRequest(String domain) {
        onPathRequest(new PathRequest(domain));
    }

    private void onPathRequest(PathRequest request) {
        if (selfDomains.indexOf(request.findDomain) == -1) {
            List<String> similarDomains = findSimilarDomains(request.findDomain);
            filterPreviousDomains(similarDomains, request.trace);
            request.trace.add(selfIp);
            for (String target : similarDomains) {
                try {
                    send(target, request);
                    return;
                } catch (ConnectException ignored) {
                }
            }
        } else {
            request.requestType = PathBackRequest.class.getSimpleName();
            onPathBackRequest(request);
        }
    }

    private void onPathBackRequest(PathRequest request) {
        request.backtrace.add(selfIp);
        for (int i = 0; i < request.trace.size(); i++) {
            String traceIp = request.trace.get(i);
            if (traceIp.equals(selfIp)) {
                if (i == 0) {
                    onProxyPathFinish(request);
                    break;
                } else {
                    // trace items are disconnect
                    break;
                }
            } else {
                try {
                    send(traceIp, request);
                    break;
                } catch (ConnectException ignore) {
                }
            }
        }
    }

    private void onProxyPathFinish(PathRequest pathRequest) {
        RequestData requestData = requestsData.get(pathRequest.requestId);
        DataRequest dataRequest = new DataRequest(pathRequest.requestId, requestData.data);
        Collections.reverse(pathRequest.backtrace);
        dataRequest.path = pathRequest.backtrace;
        try {
            send(dataRequest.path.get(1), dataRequest);
        } catch (ConnectException e) {
            onError(pathRequest.requestId, "data request connection error to " + dataRequest.path.get(1));
        }
    }

    private void onDataRequest(DataRequest dataRequest) {
        int selfIndex = dataRequest.path.indexOf(selfIp);
        if (selfIndex == dataRequest.path.size() - 1) {
            onDataRequestFinish(dataRequest);
        } else {
            try {
                send(dataRequest.path.get(selfIndex + 1), dataRequest);
            } catch (ConnectException ignore) {
            }
        }
    }


    String randomBase64String(int length) {
        Random random = ThreadLocalRandom.current();
        byte[] r = new byte[length];
        random.nextBytes(r);
        return new String(Base64.getEncoder().encode(r));
    }

    public String registration(String domain) {
        String nextOwnerDomain = randomBase64String(16);
        post(domain, null, data -> onRegistrationError(domain),
                message -> {
                    String nextOwnerDomainHash = MD5.encode(nextOwnerDomain);
                    Owner newOwner = new Owner(domain, nextOwnerDomainHash, selfIp);
                    RegistrationRequest registrationRequest = new RegistrationRequest(newOwner);
                    for (String target : findSimilarDomains(domain)) {
                        try {
                            send(target, registrationRequest);
                            return;
                        } catch (ConnectException ignored) {
                        }
                    }
                    onRegistrationError(domain);
                });
        return nextOwnerDomain;
    }

    private void onRegistrationError(String unregisteredDomain) {
        String nextOwnerDomainHash = MD5.encode(unregisteredDomain);
        for (String selfDomain : selfDomains) {
            Owner owner = owners.get(selfDomain);
            if (owner.nextOwnerDomainHash.equals(nextOwnerDomainHash)) {
                UpdateRequest updateRequest = new UpdateRequest(owner.domain, unregisteredDomain);
                float successRequests = 0;
                List<String> similarDomains = findSimilarDomains(owner.domain);
                for (String target : similarDomains) {
                    try {
                        send(target, updateRequest);
                        successRequests++;
                    } catch (ConnectException ignored) {
                    }
                }
                if (successRequests / similarDomains.size() > 0.1f) {
                    onUpdateSuccess(owner.domain);
                } else {
                    onUpdateError(owner.domain);
                }
            }
        }

    }

    private void onUpdateSuccess(String domain) {

    }

    private void onUpdateError(String domain) {

    }

    void onRegistrationRequest(RegistrationRequest request) {
        domains.add(request.owner.domain);
        owners.put(request.owner.domain, request.owner);
    }

    public void update(String nextOwnerDomain, Runnable success, Runnable error) {
        registration(nextOwnerDomain);
    }

    public Instance addProxy(String proxyIp) {
        Owner owner = new Owner("proxy", "proxy", proxyIp);
        owners.put(owner.domain, owner);
        return this;
    }
}
