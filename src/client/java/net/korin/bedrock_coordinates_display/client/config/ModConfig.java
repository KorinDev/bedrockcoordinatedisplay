package net.korin.bedrock_coordinates_display.client.config;

import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.korin.bedrock_coordinates_display.client.BedrockCoordinatesDisplayClient;
import net.minecraft.resources.Identifier;

import java.awt.*;


public class ModConfig extends Config {
    public ModConfig() {
        super(Identifier.fromNamespaceAndPath(BedrockCoordinatesDisplayClient.MOD_ID, "modconfig"));
    }

    public boolean enabled = true;

    public ValidatedFloat scale = new ValidatedFloat(1.0f, 2.0f, 0.1f);

    public ValidatedInt offsetX = new ValidatedInt(10, 256, 0);
    public ValidatedInt offsetY = new ValidatedInt(10, 256, 0);
    public ValidatedInt padding = new ValidatedInt(4, 16, 0);
    public ValidatedInt lineSpacing = new ValidatedInt(2, 16, 0);
    public boolean useChatBackgroundOpacity = false;
    public ValidatedInt backgroundOpacity = new ValidatedInt(150, 255, 0);


    public PositionDisplay positionDisplay = new PositionDisplay();
    public DayDisplay dayDisplay = new DayDisplay();
    public TimeDisplay timeDisplay = new TimeDisplay();
    public BiomeDisplay biomeDisplay = new BiomeDisplay();
    public FramerateDisplay framerateDisplay = new FramerateDisplay();
    public SpeedDisplay speedDisplay = new SpeedDisplay();


    public static class PositionDisplay extends ConfigSection {
        public PositionDisplay(){super();}
        public boolean enabled = true;
        public ValidatedColor colorText = new ValidatedColor(Color.WHITE, false);
        public ValidatedColor colorValue = new ValidatedColor(Color.WHITE, false);
        public String text = "Position";
    }

    public static class DayDisplay extends ConfigSection {
        public DayDisplay() {super();}
        public boolean enabled = true;
        public ValidatedColor colorText = new ValidatedColor(Color.WHITE, false);
        public ValidatedColor colorValue = new ValidatedColor(Color.WHITE, false);
        public String text = "Days played";
    }

    public static class TimeDisplay extends ConfigSection {
        public TimeDisplay() {super();}
        public boolean enabled = true;
        public ValidatedColor colorText = new ValidatedColor(Color.WHITE, false);
        public ValidatedColor colorValue = new ValidatedColor(Color.WHITE, false);
        public String text = "Time";
        public boolean ampm = false;
        public boolean forceInAllDimensions = false;
    }

    public static class BiomeDisplay extends ConfigSection {
        public BiomeDisplay() {super();}
        public boolean enabled = false;
        public ValidatedColor colorText = new ValidatedColor(Color.WHITE, false);
        public ValidatedColor colorValue = new ValidatedColor(Color.WHITE, false);
        public String text = "Biome";
        public boolean prettifyBiome = true;
    }

    public static class FramerateDisplay extends ConfigSection {
        public FramerateDisplay() {super();}
        public boolean enabled = true;
        public ValidatedColor colorText = new ValidatedColor(Color.WHITE, false);
        public ValidatedColor colorValue = new ValidatedColor(Color.WHITE, false);
        public String text = "Framerate";
    }

    public static class SpeedDisplay extends ConfigSection {
        public SpeedDisplay() {super();}
        public boolean enabled = true;
        public ValidatedColor colorText = new ValidatedColor(Color.WHITE, false);
        public ValidatedColor colorValue = new ValidatedColor(Color.WHITE, false);
        public String text = "Speed";
    }

    public String noteText = "";
}
