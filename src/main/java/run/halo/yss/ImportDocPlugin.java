package run.halo.yss;

import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.PluginContext;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

@Component
public class ImportDocPlugin extends BasePlugin {
    private final SchemeManager schemeManager;

    public ImportDocPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        this.schemeManager.register(ImportSchema.class);
        System.out.println("Import doc 插件装载");
    }

    @Override
    public void stop() {
        this.schemeManager.unregister(schemeManager.get(ImportSchema.class));
        System.out.println("Import doc 插件卸载");
    }
}