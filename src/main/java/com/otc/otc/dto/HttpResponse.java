package com.otc.otc.dto;

public class HttpResponse {
    private final int statusCode;
    private String body;

    private HttpResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    // 정적 팩토리 메서드: 200 OK 응답 생성
    public static HttpResponse ok() {
        return new HttpResponse(200);
    }

    // 정적 팩토리 메서드: 404 Not Found 응답 생성
    public static HttpResponse notFound() {
        return new HttpResponse(404);
    }

    // 응답 본문 추가
    public HttpResponse body(String body) {
        this.body = body;
        return this;
    }

    // 응답 객체를 문자열로 변환
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusCode).append(" ");
        switch (statusCode) {
            case 200:
                sb.append("OK");
                break;
            case 404:
                sb.append("Not Found");
                break;
            default:
                sb.append("Unknown");
        }
        sb.append("\r\n");
        if (body != null) {
            sb.append("Content-Length: ").append(body.getBytes().length).append("\r\n");
            sb.append("\r\n");
            sb.append(body);
        }
        return sb.toString();
    }
}
