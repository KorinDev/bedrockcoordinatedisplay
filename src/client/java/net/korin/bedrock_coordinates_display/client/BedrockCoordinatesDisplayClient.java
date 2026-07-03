package net.korin.bedrock_coordinates_display.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.korin.bedrock_coordinates_display.client.command.NoteCommand;
import net.korin.bedrock_coordinates_display.client.config.ModConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class BedrockCoordinatesDisplayClient implements ClientModInitializer {

    record OrderedLine(
            String label,
            String value,
            ValidatedColor labelColor,
            ValidatedColor valueColor) {
        public String fullText() {
            return label + value;
        }
    }

    public static final String MOD_ID = "bedrock_coordinates_display";

    public static ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new, RegisterType.CLIENT);


    public static int y_offset = 10;
    public static int x_offset = 10;

    public static boolean visibilityToggle = true;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(BedrockCoordinatesDisplayClient.MOD_ID, "keybinds")
        );

        KeyMapping toggleVisibilityKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.bedrock_coordinates_display.toggle_visibility",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSPACE,
                CATEGORY
        ));

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(BedrockCoordinatesDisplayClient.MOD_ID, "after_chat"), BedrockCoordinatesDisplayClient::extract
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleVisibilityKey.consumeClick()) {
                if (client.player != null) {
                    visibilityToggle = !visibilityToggle;
                }
            }
        });

        NoteCommand.register();
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {

        if (!CONFIG.enabled) return;
        if (!visibilityToggle) return;

        x_offset = CONFIG.offsetX.get();
        y_offset = CONFIG.offsetY.get();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        Font font = client.font;
        Player player = client.player;

        if (player == null) return;
        if (level == null) return;

        String posLabel = CONFIG.positionDisplay.text + ": ";
        String posValue = String.format("%s, %s, %s",
                (int)player.getX(),
                (int)player.getY(),
                (int)player.getZ());

        String dayLabel = CONFIG.dayDisplay.text + ": ";
        String dayValue = String.valueOf(level.getOverworldClockTime() / 24000L);

        String timeLabel = CONFIG.timeDisplay.text + ": ";
        String timeValue = getTimeString(level, player);

        String biomeLabel = CONFIG.biomeDisplay.text + ": ";
        String biomeValue = getBiomeString(level, player);

        String fpsLabel = CONFIG.framerateDisplay.text + ": ";
        String fpsValue = String.valueOf(client.getFps());

        String speedLabel = CONFIG.speedDisplay.text + ": ";
        String speedValue = getSpeedString(player);

        String noteValue = CONFIG.noteText;

        List<OrderedLine> lines = new ArrayList<>();

        for (ModConfig.DisplayModule module : CONFIG.displayOrder) {
            switch (module) {
                case POSITION -> lines.add(new OrderedLine(
                        posLabel, posValue,
                        CONFIG.positionDisplay.colorText,
                        CONFIG.positionDisplay.colorValue
                ));
                case DAY -> lines.add(new OrderedLine(
                        dayLabel, dayValue,
                        CONFIG.dayDisplay.colorText,
                        CONFIG.dayDisplay.colorValue
                ));
                case TIME -> lines.add(new OrderedLine(
                        timeLabel, timeValue,
                        CONFIG.timeDisplay.colorText,
                        CONFIG.timeDisplay.colorValue
                ));
                case BIOME -> lines.add(new OrderedLine(
                        biomeLabel, biomeValue,
                        CONFIG.biomeDisplay.colorText,
                        CONFIG.biomeDisplay.colorValue
                ));
                case FRAMERATE -> lines.add(new OrderedLine(
                        fpsLabel, fpsValue,
                        CONFIG.framerateDisplay.colorText,
                        CONFIG.framerateDisplay.colorValue
                ));
                case SPEED -> lines.add(new OrderedLine(
                        speedLabel, speedValue,
                        CONFIG.speedDisplay.colorText,
                        CONFIG.speedDisplay.colorValue
                ));
            }
        }

        if (!noteValue.isEmpty()) {
            lines.add(new OrderedLine(
                    "Note: ", noteValue,
                    new ValidatedColor(Color.WHITE),
                    new ValidatedColor(Color.WHITE)
            ));
        }
        if (lines.isEmpty()) return;



        graphics.nextStratum();

        int padding = CONFIG.padding.get();
        int opacity = CONFIG.backgroundOpacity.get();
        int lineSpacing = CONFIG.lineSpacing.get();


        if (CONFIG.useChatBackgroundOpacity) {
            double opacityFloat = client.options.textBackgroundOpacity().get();
            int opacityAlpha = (int) Math.round(opacityFloat * 255.0);
            opacity = Math.min(255, Math.max(0, opacityAlpha));
        }

        float scale = CONFIG.scale.get();



        int lineHeight = font.lineHeight - 1;
        int maxTextWidth = 0;
        for (OrderedLine line : lines) {
            maxTextWidth = Math.max(maxTextWidth, font.width(line.fullText()));
        }

        int totalHeight = (lines.size() * lineHeight) + (Math.max(0, lines.size() - 1) * lineSpacing);


        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(scale);

        graphics.fill(
                (int)(x_offset / scale) - padding,
                (int)(y_offset / scale) - padding,
                (int)(x_offset / scale) + maxTextWidth + padding,
                (int)(y_offset / scale) + totalHeight + padding,
                ARGB.color(opacity, 0, 0, 0));

        for (int i = 0; i < lines.size(); i++) {
            int yPos = (int)(y_offset / scale) + (i * (lineHeight + lineSpacing));
            OrderedLine line = lines.get(i);
            if (!line.label.isEmpty()) {
                graphics.text(
                        font,
                        line.label,
                        (int)(x_offset / scale),
                        yPos,
                        ARGB.color(255, line.labelColor.r(), line.labelColor.g(), line.labelColor.b()),
                        true
                );
            }

            if (!line.value.isEmpty()) {
                int xPos = (int)(x_offset / scale) + font.width(line.label);
                graphics.text(
                        font,
                        line.value,
                        xPos,
                        yPos,
                        ARGB.color(255, line.valueColor.r(), line.valueColor.g(), line.valueColor.b()),
                        true
                );
            }
        }

        pose.popMatrix();
        graphics.nextStratum();
    }

    private static String getTimeString(ClientLevel level, Player player) {
        long worldTime = level.getOverworldClockTime();
        int worldHour = (int)((worldTime / 1000) % 24);
        int realHour = (worldHour + 6) % 24;
        int minutes = (int)((worldTime % 1000) / (1000f / 60f));
        if (player.level().dimension() != Level.OVERWORLD && !CONFIG.timeDisplay.forceInAllDimensions)
            return "???";

        if (CONFIG.timeDisplay.ampm) {
            int ampmHour = realHour % 12;
            if (ampmHour == 0) ampmHour = 12;
            String ampm = (realHour < 12) ? "AM" : "PM";
            return String.format("%d:@02d %s", ampmHour, minutes, ampm);
        } else {
            return String.format("%02d:%02d", realHour, minutes);
        }
    }

    private static String getBiomeString(ClientLevel level, Player player) {
        Holder<Biome> biomeHolder = level.getBiome(player.blockPosition());
        String biomeName = level.registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .getKey(biomeHolder.value())
                .getPath();

        if (CONFIG.biomeDisplay.prettifyBiome) {
            String[] split = biomeName.split("_");
            StringBuilder prettified = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                if (i > 0) prettified.append(" ");
                String word = split[i];
                if (word.length() > 0)
                    prettified.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1).toLowerCase());
            }
            return prettified.toString();
        }
        return biomeName;
    }

    private static String getSpeedString(Player player) {
        Vec3 delta = player.getDeltaMovement();
        double _speed;
        if (player.onGround()) {
            _speed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        } else {
            _speed = delta.length();
        }
        double bps = _speed * 20;
        return String.format("%.1fb/s", bps);
    }
}