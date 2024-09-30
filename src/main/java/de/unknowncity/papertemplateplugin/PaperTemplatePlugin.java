package de.unknowncity.papertemplateplugin;

import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.common.configuration.setting.serializer.DatabaseSettingSerializer;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import de.unknowncity.papertemplateplugin.configuration.serializer.TemplateConfigurationSerializer;
import de.unknowncity.papertemplateplugin.configuration.TemplateConfiguration;

public class PaperTemplatePlugin extends PaperAstraPlugin {
    private ServiceRegistry<PaperTemplatePlugin> serviceRegistry;
    private TemplateConfiguration configuration;
    private PaperMessenger messenger;

    @Override
    public void onPluginEnable() {
        initConfiguration();

        initializeMessenger();
    }

    @Override
    public void onPluginDisable() {

    }

    public void initConfiguration() {
        this.configuration = new TemplateConfiguration(
                new DataBaseSetting()
        );

        configLoader.saveDefaultConfig(configuration, getDataFolder().toPath().resolve("config.yml"), builder -> {
            builder.register(TemplateConfiguration.class, new TemplateConfigurationSerializer());
            builder.register(DataBaseSetting.class, new DatabaseSettingSerializer());
        });

        configLoader.loadConfiguration(getDataFolder().toPath().resolve("config.yml"), TemplateConfiguration.class, builder -> {
            builder.register(TemplateConfiguration.class, new TemplateConfigurationSerializer());
            builder.register(DataBaseSetting.class, new DatabaseSettingSerializer());
        }).ifPresent(templateConfiguration -> this.configuration = templateConfiguration);
    }

    private void initializeMessenger() {
        var defaultLang = languageService.getDefaultLanguage();

        var localization = Localization.builder(getDataPath().resolve("lang")).buildAndLoad();

        this.messenger = PaperMessenger.builder(localization)
                .withDefaultLanguage(defaultLang)
                .withLanguageService(languageService)
                .withPlaceHolderAPI(hookRegistry.getRegistered(PlaceholderApiHook.class))
                .build();
    }

    public ServiceRegistry<PaperTemplatePlugin> serviceRegistry() {
        return serviceRegistry;
    }

    public PaperMessenger messenger() {
        return messenger;
    }
}