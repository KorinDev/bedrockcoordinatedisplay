package net.korin.bedrock_coordinates_display.client.config;

import io.wispforest.owo.config.annotation.*;

import io.wispforest.owo.ui.core.Color;


@Modmenu(modId = "bedrock_coordinates_display")
@Config(name = "bedrock_coordinates_display", wrapperName = "BedrockCoordinatesDisplayConfig")
public class ConfigModel {
    @SectionHeader("mod")
    public boolean enabled = true;

    @RangeConstraint(min = 0.1f, max = 2.0f)
    public float scale = 1.0f;



    @RangeConstraint(min = 0, max = 128)
    public int offsetX = 10;

    @RangeConstraint(min = 0, max = 128)
    public int offsetY = 10;

    @RangeConstraint(min = 0, max = 16)
    public int padding = 4;

    @RangeConstraint(min = 0, max = 16)
    public int lineSpacing = 2;

    public boolean useChatBackgroundOpacity = false;

    @RangeConstraint(min = 0, max = 255)
    public int backgroundOpacity = 150;

    /*@SectionHeader("displayOrder")
    public String displayOrder = "position, day, biome, framerate, speed, time";*/

    @SectionHeader("modules")
    @Nest public PositionDisplay positionDisplay = new PositionDisplay();

    public static class PositionDisplay {
        public boolean enabled = true;
        public Color colorText = Color.WHITE;
        public Color colorValue = Color.WHITE;
        public String text = "Position";
    }

    @Nest public DayDisplay dayDisplay = new DayDisplay();

    public static class DayDisplay {
        public boolean enabled = false;
        public Color colorText = Color.WHITE;
        public Color colorValue = Color.WHITE;
        public String text = "Days played";
    }

    @Nest public TimeDisplay timeDisplay = new TimeDisplay();

    public static class TimeDisplay {
        public boolean enabled = false;
        public Color colorText = Color.WHITE;
        public Color colorValue = Color.WHITE;
        public String text = "Time";
        public boolean ampm = false;
        public boolean forceInAllDimensions = false;
    }

    @Nest public BiomeDisplay biomeDisplay = new BiomeDisplay();

    public static class BiomeDisplay {
        public boolean enabled = false;
        public Color colorText = Color.WHITE;
        public Color colorValue = Color.WHITE;
        public String text = "Biome";
        public boolean prettifyBiome = true;
    }

    @Nest public FramerateDisplay framerateDisplay = new FramerateDisplay();

    public static class FramerateDisplay {
        public boolean enabled = false;
        public Color colorText = Color.WHITE;
        public Color colorValue = Color.WHITE;
        public String text = "Framerate";
    }

    @Nest public SpeedDisplay speedDisplay = new SpeedDisplay();

    public static class SpeedDisplay {
        public boolean enabled = false;
        public Color colorText = Color.WHITE;
        public Color colorValue = Color.WHITE;
        public String text = "Speed";
    }



    @ExcludeFromScreen
    public String noteText = "";




}
