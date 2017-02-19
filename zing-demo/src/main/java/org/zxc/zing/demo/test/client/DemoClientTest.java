package org.zxc.zing.demo.test.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zxc.zing.demo.api.DemoService;
import org.zxc.zing.demo.api.MessageDTO;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xuanchen.zhao on 15-12-11.
 */
public class DemoClientTest {

    private static DemoService demoService;

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:config/spring/applicationContext-client.xml");
        demoService = (DemoService) context.getBean("demoService");
        test();
        testLoadMessageObject();
        testBatchInvocation();
    }

    public static void test() throws InterruptedException {
        String result = demoService.echo("Hello World!");
        System.out.println(result);
    }

    public static void testLoadMessageObject(){
        MessageDTO messageDTO = demoService.loadObject(1);
        System.out.println(messageDTO);
    }

    public static void testBatchInvocation() throws InterruptedException {
        final int THREAD_COUNT = 50;
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < THREAD_COUNT; i++) {
//            MockServiceInvocation invocation = new MockServiceInvocation(demoService, latch);
            threadPool.submit(() -> {
                String result = demoService.echo("Hello!");
                System.out.println(result);
                latch.countDown();
            });
        }
        latch.await();
    }

    private static class MockServiceInvocation implements Runnable {

        private CountDownLatch latch;
        private DemoService demoService;

        private MockServiceInvocation(DemoService demoService, CountDownLatch latch) {
            this.demoService = demoService;
            this.latch = latch;
        }

        @Override
        public void run() {
            String result = demoService.echo("Hello!");
            System.out.println(result);
            latch.countDown();
        }
    }
}
