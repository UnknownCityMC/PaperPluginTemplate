package de.unknowncity.papertemplateplugin.configuration.serializer;

import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.papertemplateplugin.configuration.TemplateConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class TemplateConfigurationSerializer implements TypeSerializer<TemplateConfiguration> {
    @Override
    public TemplateConfiguration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var dataBaseSettings = node.get(DataBaseSetting.class);

        return new TemplateConfiguration(
                dataBaseSettings
        );
    }

    @Override
    public void serialize(Type type, @Nullable TemplateConfiguration obj, ConfigurationNode node) throws SerializationException {
        // Ignore
    }
}
