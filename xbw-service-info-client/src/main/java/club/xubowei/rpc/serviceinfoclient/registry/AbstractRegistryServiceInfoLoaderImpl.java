package club.xubowei.rpc.serviceinfoclient.registry;

import club.xubowei.rpc.serviceinfoclient.registry.remote.NettyServer;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zxc.zing.common.config.ConfigManager;
import org.zxc.zing.common.constant.Constants;
import org.zxc.zing.common.entity.ProviderInfo;
import org.zxc.zing.common.registry.function.ServerStr2ProviderInfoTransformer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by xubowei on 16/02/2017.
 */
public class AbstractRegistryServiceInfoLoaderImpl {

    private static final Logger log = LoggerFactory.getLogger(AbstractRegistryServiceInfoLoaderImpl.class);

    private static CuratorFramework client;

    private static volatile boolean started = false;

    private static Map<String, Set<ProviderInfo>> serviceInfos;

    static {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void start() throws Exception {
        log.info("try bootstrap state:" + started);
        if (!started) {
            synchronized (AbstractRegistryServiceInfoLoaderImpl.class) {
                if (!started) {
                    doStart();
                    NettyServer.start(8081);
                }
            }
        }
        log.info("started");
    }

    private static void doStart() throws Exception {


        serviceInfos = new ConcurrentHashMap<>();

//      String zookeeperAddress = ConfigManager.getInstance().getProperty(Constants.ZOOKEEPER_ADDRESS);
        String zookeeperAddress = "127.0.0.1:2181";

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperAddress, retryPolicy);

        client.start();

        List<String> paths = client.getChildren().watched().forPath(Constants.SERVICE_ZK_PATH_PREFIX);

        System.out.println("paths = " + paths);

        for (String path : paths) {
            List<String> stringList = client.getChildren().watched().forPath(String.format(Constants.SERVICE_ZK_PATH_FORMAT, path));
            serviceInfos.put(path, new HashSet<>(Lists.transform(stringList, ServerStr2ProviderInfoTransformer.INSTANCE)));
//                        ArrayList<String> serviceNameAndInfo = Lists.newArrayList(Splitter.on(Constants.ZOOKEEPER_PATH_SEPARATOR).split(path));
//                        if (CollectionUtils.isNotEmpty(serviceNameAndInfo) && serviceNameAndInfo.size() == 2) {
//                        }
        }

        started = client.blockUntilConnected(1000, TimeUnit.MILLISECONDS);

    }


    public static Map<String, Set<ProviderInfo>> getAllServicesInfo() {
        return Collections.unmodifiableMap(serviceInfos);
    }

    public static Set<ProviderInfo> getProviderInfosByServiceName(String serviceName) {
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(AbstractRegistryServiceInfoLoaderImpl.getAllServicesInfo().toString());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
