package eberware.api.listeners;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class EndpointListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger _logger = Logger.getLogger(EndpointListener.class.getSimpleName());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods()
                .forEach((endpoint,handlerMethod) -> {
                    _logger.log(Level.INFO, endpoint.toString());
                });
    }
}
