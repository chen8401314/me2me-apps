package listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.core.event.ApplicationEventBus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/27
 * Time :15:20
 */
public class SnsListener {

    private final ApplicationEventBus applicationEventBus;

    @Autowired
    public SnsListener(ApplicationEventBus applicationEventBus){
        this.applicationEventBus = applicationEventBus;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void modifyCircle(SnsListener snsListener){

    }
}
