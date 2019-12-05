package org.fdns;

import com.google.gson.Gson;
import org.fdns.callbacks.ErrorCallback;
import org.fdns.callbacks.RegistrationSuccessCallback;
import org.fdns.callbacks.ServeCallback;
import org.fdns.callbacks.SuccessCallback;
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
    private ServeCallback serveCallback;
    private List<String> selfDomains = new ArrayList<>();

    public Instance(Network network, String selfIp, ServeCallback serveCallback) {
        this.network = network;
        this.selfIp = selfIp;
        this.serveCallback = serveCallback;
    }

    private void send(String ip, Request request) throws ConnectException {
        network.send(ip, json.toJson(request));
    }

    public void post(String domain, String data, SuccessCallback success, ErrorCallback error) {
        removeExpiredRequests();
        Integer requestId = random.nextInt();
        RequestData requestData = new RequestData(requestId, new Date().getTime(), data, success, error);
        requestsData.put(requestId, requestData);
        sendProxyPathRequest(requestId, domain);
    }

    public void removeExpiredRequests() {
        /*Long expireTime = new Date().getTime() - CONNECTION_TIMEOUT;
        ArrayList<RequestData> expiredRequests = new ArrayList<>();
        for (RequestData requestData : requestsData.values())
            if (requestData.startTime < expireTime)
                expiredRequests.add(requestData);*/
        for (RequestData requestData : requestsData.values())
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

    List<String> findSimilarDomains(String requestDomain) {
        // TODO add limit of similar results
        int difference = Integer.MAX_VALUE;
        List<String> similarDomains = new ArrayList<>();
        for (String domain : domains) {
            int currentDifference = StringComparator.compare(requestDomain, domain);
            if (currentDifference < difference) {
                difference = currentDifference;
                similarDomains.clear();
                similarDomains.add(domain);
            } else if (currentDifference == difference) {
                similarDomains.add(domain);
            }
        }
        if (difference == 0 && similarDomains.indexOf(requestDomain) != -1) {
            similarDomains.clear();
            similarDomains.add(requestDomain);
        }
        // TODO sort by speed
        return similarDomains;
    }

    void filterPreviousDomains(List<String> similarDomains, List<String> trace) {
        similarDomains.removeIf(domain -> trace.indexOf(owners.get(domain).ip) != -1);
    }

    void sendProxyPathRequest(Integer requestId, String domain) {
        PathRequest pathRequest = new PathRequest(domain);
        pathRequest.requestId = requestId;
        onPathRequest(pathRequest);
    }

    private void onPathRequest(PathRequest request) {
        if (selfDomains.indexOf(request.findDomain) == -1) {
            List<String> similarDomains = findSimilarDomains(request.findDomain);
            filterPreviousDomains(similarDomains, request.trace);
            request.trace.add(selfIp);
            if (similarDomains.size() == 0) {
                request.isFail = true;
                request.requestType = PathBackRequest.class.getSimpleName();
                onPathBackRequest(request);
            } else {
                for (String domain : similarDomains) {
                    try {
                        send(owners.get(domain).ip, request);
                        return;
                    } catch (ConnectException ignored) {
                    }
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
                    // trace hosts are disconnect
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
        if (pathRequest.isFail == null) {
            RequestData requestData = requestsData.get(pathRequest.requestId);
            DataRequest dataRequest = new DataRequest(pathRequest.requestId, requestData.data, new Date().getTime());
            Collections.reverse(pathRequest.backtrace);
            dataRequest.path = pathRequest.backtrace;
            try {
                send(dataRequest.path.get(1), dataRequest);
            } catch (ConnectException e) {
                onError(pathRequest.requestId, "data request connection error to " + dataRequest.path.get(1));
            }
        } else {
            onError(pathRequest.requestId, "domain is not available");
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

    private void onDataRequestFinish(DataRequest dataRequest) {
        if (dataRequest.responseTime == null) {
            dataRequest.responseTime = new Date().getTime();
            dataRequest.data = serveCallback.onMessage(dataRequest.data);
            Collections.reverse(dataRequest.path);
            try {
                send(dataRequest.path.get(1), dataRequest);
            } catch (ConnectException ignore) {
            }
        } else {
            RequestData requestData = requestsData.get(dataRequest.requestId);
            requestData.success.run(dataRequest.data);
        }
    }

    String randomBase64String(int length) {
        Random random = ThreadLocalRandom.current();
        byte[] r = new byte[length];
        random.nextBytes(r);
        return new String(Base64.getEncoder().encode(r));
    }

    public  void registration(String registrationDomain, RegistrationSuccessCallback success, ErrorCallback error) {
        registration(registrationDomain, null, success, error);
    }

    private void registration(String registrationDomain, String reassignToken, RegistrationSuccessCallback success, ErrorCallback error) {
        post(registrationDomain,
                null,
                data -> {
                    if (error != null) error.run("domain busy");
                },
                message -> {
                    String nextReassignToken = randomBase64String(16);
                    String nextReassignTokenHash = MD5.encode(nextReassignToken);
                    Owner newOwner = new Owner(registrationDomain, reassignToken, nextReassignTokenHash, selfIp);
                    RegistrationRequest registrationRequest = new RegistrationRequest(newOwner);
                    for (String domain : findSimilarDomains(registrationDomain)) {
                        try {
                            send(owners.get(domain).ip, registrationRequest);
                            selfDomains.add(registrationDomain);
                            if (success != null) success.onSuccess(nextReassignToken);
                            return;
                        } catch (ConnectException ignored) {
                        }
                    }
                    if (error != null) error.run("domain table do not have similar domains");
                });
    }

    void onRegistrationRequest(RegistrationRequest request) {
        addOwner(request.owner);
    }

    void addOwner(Owner owner) {
        domains.add(owner.domain);
        owners.put(owner.domain, owner);
    }

    public void reassign(String domain, String reassignToken, RegistrationSuccessCallback success, ErrorCallback error) {
        registration(reassignToken, nextDomain -> {
            registration(domain, reassignToken, success, error);
        }, error);
    }

    public Instance proxy(String ip) {
        Owner owner = new Owner("proxy", null,"proxy", ip);
        addOwner(owner);
        return this;
    }
}
