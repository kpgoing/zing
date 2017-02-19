package club.xubowei.rpc.serviceinfoclient.registry;

import org.zxc.zing.common.entity.ProviderInfo;

import java.util.Map;
import java.util.Set;

/**
 * Created by xubowei on 16/02/2017.
 */
public interface RegistryServiceInfoLoader {
    Map<String, Set<ProviderInfo>> getAllServicesInfo();

    Set<ProviderInfo> getProviderInfosByServiceName(String serviceName);

}
