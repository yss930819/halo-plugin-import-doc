package run.halo.yss;

import run.halo.app.plugin.PluginContext;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

@Component
public class ImportDocPlugin extends BasePlugin {


    public ImportDocPlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        System.out.println("Import doc 插件装载");
    }

    @Override
    public void stop() {
        System.out.println("Import doc 插件卸载");
    }
}