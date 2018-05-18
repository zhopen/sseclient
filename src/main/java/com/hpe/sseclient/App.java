package com.hpe.sseclient;

import okhttp3.Request;
import okhttp3.Response;
import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import java.util.concurrent.TimeUnit;
/**
 * Hello world!
 *
 */



public class App
{

        public static void main( String[] args )throws Exception {
            System.out.println("Hello World!");

            TestSSE test = new TestSSE();
            test.testNewServerSentEventConnection();

        }
}
