package de.unknowncity.papertemplateplugin;

import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import de.unknowncity.papertemplateplugin.config.ExampleConfiguration;
import dev.chojo.ocular.Configurations;
import dev.chojo.ocular.dataformats.YamlDataFormat;
import dev.chojo.ocular.key.Key;

import java.nio.file.Path;

public class PaperTemplatePlugin extends PaperAstraPlugin {
    private ServiceRegistry<PaperTemplatePlugin> serviceRegistry;
    private PaperMessenger messenger;
    private Configurations<ExampleConfiguration> config;

    @Override
    public void onPluginEnable() {
        initConfiguration();

        initializeMessenger();
    }

    @Override
    public void onPluginDisable() {

    }

    public void initConfiguration() {
        var configKey = Key.builder(Path.of("config.yml"), ExampleConfiguration::new).build();
        config = Configurations.builder(configKey, new YamlDataFormat())
                .setBase(Path.of(""))
                .build();
    }

    private void initializeMessenger() {
        var defaultLang = languageService.getDefaultLanguage();

        var localization = Localization.builder(getDataPath().resolve("lang")).buildAndLoad();

        this.messenger = PaperMessenger.builder(localization, getPluginMeta())
                .withDefaultLanguage(defaultLang)
                .withLanguageService(languageService)
                .withPlaceHolderAPI(hookRegistry.getRegistered(PlaceholderApiHook.class).orElse(null))
                .build();
    }

    public ServiceRegistry<PaperTemplatePlugin> serviceRegistry() {
        return serviceRegistry;
    }

    public PaperMessenger messenger() {
        return messenger;
    }

    public Configurations<ExampleConfiguration> config() {
        return config;
    }
}