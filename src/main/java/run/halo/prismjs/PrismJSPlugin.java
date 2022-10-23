package run.halo.prismjs;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * prism js 集成
 *
 * @author liuzhihang
 * @date 2022/10/23
 */
@Component
public class PrismJSPlugin extends BasePlugin {

    public PrismJSPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
