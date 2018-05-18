package com.hpe.sseclient;

import okhttp3.Request;
import okhttp3.Response;
import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import java.util.concurrent.TimeUnit;

public class TestSSE {

    private final static String URL = "http://123.206.56.201:20017/v2/events";

    private static final long TEST_TIMEOUT = TimeUnit.SECONDS.toMillis(60);

    private final com.here.oksse.OkSse okSse = new com.here.oksse.OkSse();

    private boolean isAlive;
    private boolean hasOpened;
    private boolean hasReceivedMessage;

    public void testNewServerSentEventConnection() throws Exception {
        isAlive = true;
        Request request = new Request.Builder().url(URL).build();
        ServerSentEvent sse = okSse.newServerSentEvent(request, new ServerSentEvent.Listener() {
            @Override
            public void onOpen(ServerSentEvent sse, Response response) {
                System.out.println("OkSse opened: " + response.message());
                hasOpened = true;
            }

            @Override
            public void onMessage(ServerSentEvent sse, String id, String event, String message) {
                System.out.println("New OkSse message id=" + id + " event=" + event + " message=" + message);
                assert message.equals("{\"name\":\"oksse\",\"test\":1}");
                hasReceivedMessage = true;
            }

            @Override
            public void onComment(ServerSentEvent sse, String comment) {
                System.out.println("New OkSse comment " + comment);
            }

            @Override
            public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
                System.out.println("OkSse sends retry time " + milliseconds + " milliseconds");
                return true;
            }

            @Override
            public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
                throw new RuntimeException(throwable);
            }

            @Override
            public void onClosed(ServerSentEvent sse) {
                isAlive = false;
                System.out.println("OkSse connection closed");
            }

            @Override
            public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
                throw new RuntimeException("No retry was expected");
            }

        });

        long startTime = System.currentTimeMillis();
        while (isAlive) {
            try {
                Thread.sleep(1000*1000);
                if (System.currentTimeMillis() - startTime > TEST_TIMEOUT || (hasOpened && hasReceivedMessage)) {
                    sse.close();
                }
            } catch (InterruptedException e) {
                isAlive = false;
            }
        }

        assert hasOpened;
        assert hasReceivedMessage;
    }

}