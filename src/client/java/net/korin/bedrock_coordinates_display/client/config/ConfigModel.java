package net.korin.bedrock_coordinates_display.client.config;

import io.wispforest.owo.config.annotation.*;


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

    @RangeConstraint(min = 0, max = 16)
    public int lineSpacing = 2;

    @Nest public PositionDisplay positionDisplay = new PositionDisplay();

    public static class PositionDisplay {
        public boolean enablePosition = true;
    }

    @Nest public DayDisplay dayDisplay = new DayDisplay();

    public static class DayDisplay {
        public boolean enableDay = false;
    }

    @Nest public BiomeDisplay biomeDisplay = new BiomeDisplay();

    public static class BiomeDisplay {
        public boolean enableBiome = false;
        public boolean prettifyBiome = true;
    }



    public boolean useChatBackgroundOpacity = false;

    @RangeConstraint(min = 0, max = 255)
    public int backgroundOpacity = 150;
}
