package net.korin.bedrock_coordinates_display.client.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = "bedrock_coordinates_display")
@Config(name = "bedrock_coordinates_display", wrapperName = "BedrockCoordinatesDisplayConfig")
public class ConfigModel {
    public boolean enabled = true;

    @RangeConstraint(min = 0, max = 128)
    public int offsetX = 10;

    @RangeConstraint(min = 0, max = 128)
    public int offsetY = 10;

    @RangeConstraint(min = 0, max = 16)
    public int padding = 4;

    @RangeConstraint(min = 0, max = 255)
    public int backgroundOpacity = 150;
}
