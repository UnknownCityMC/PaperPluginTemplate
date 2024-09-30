package de.unknowncity.papertemplateplugin.configuration;

import de.unknowncity.astralib.common.configuration.ApplicableAstraConfiguration;
import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;

public class TemplateConfiguration extends ApplicableAstraConfiguration  {
    private final DataBaseSetting databaseSetting;

    public TemplateConfiguration(DataBaseSetting databaseSetting) {
        this.databaseSetting = databaseSetting;
    }

    public DataBaseSetting databaseSetting() {
        return databaseSetting;
    }
}
