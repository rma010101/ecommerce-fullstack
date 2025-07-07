package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "query_logs")
public class QueryLog {
    @Id
    private String id;
    private LocalDateTime timestamp;
    private String clientIp;
    private String httpMethod;
    private String requestUri;
    private String queryString;
    private String controllerMethod;
    private String methodArguments;
    private String userAgent;
    private int responseStatus;
    private long responseSize;
    private long executionTimeMs;
    private boolean success;
    private String errorMessage;

    // Constructors
    public QueryLog() {}

    public QueryLog(LocalDateTime timestamp, String clientIp, String httpMethod, String requestUri) {
        this.timestamp = timestamp;
        this.clientIp = clientIp;
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getControllerMethod() {
        return controllerMethod;
    }

    public void setControllerMethod(String controllerMethod) {
        this.controllerMethod = controllerMethod;
    }

    public String getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(String methodArguments) {
        this.methodArguments = methodArguments;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(long responseSize) {
        this.responseSize = responseSize;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "QueryLog{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", clientIp='" + clientIp + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", controllerMethod='" + controllerMethod + '\'' +
                ", responseStatus=" + responseStatus +
                ", executionTimeMs=" + executionTimeMs +
                ", success=" + success +
                '}';
    }
}
