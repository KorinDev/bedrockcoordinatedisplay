package net.korin.bedrock_coordinates_display.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.korin.bedrock_coordinates_display.client.command.NoteCommand;
import net.korin.bedrock_coordinates_display.client.config.BedrockCoordinatesDisplayConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.ArrayList;

public class BedrockCoordinatesDisplayClient implements ClientModInitializer {

    public static final String MOD_ID = "bedrock_coordinates_display";

    public static final BedrockCoordinatesDisplayConfig CONFIG = BedrockCoordinatesDisplayConfig.createAndLoad();


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
        if (!CONFIG.enabled()) return;
        if (!visibilityToggle) return;

        x_offset = CONFIG.offsetX();
        y_offset = CONFIG.offsetY();

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        Font font = client.font;
        Player player = client.player;

        if (player == null) return;
        if (level == null) return;

        Holder<Biome> biomeHolder = level.getBiome(player.blockPosition());
        String biomeName = level.registryAccess()
                .lookupOrThrow(Registries.BIOME)
                .getKey(biomeHolder.value())
                .getPath();

        if (CONFIG.biomeDisplay.prettifyBiome()) {
            String[] split = biomeName.split("_");
            StringBuilder prettified = new StringBuilder();
            for (int i = 0; i < split.length; i ++) {
                if (i > 0) prettified.append(" ");
                String word = split[i];
                if (word.length() > 0) {
                    prettified.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1).toLowerCase());
                }
            }
            biomeName = prettified.toString();
        }

        Vec3 delta = player.getDeltaMovement();
        double _speed;
        if (player.onGround()) {
            _speed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        } else {
            _speed = delta.length();
        }

        double blocksPerSecond = _speed * 20;

        long worldTime = level.getOverworldClockTime();
        int worldHour = (int)((worldTime / 1000) % 24);
        int realHour = (worldHour + 6) % 24;
        int minutes = (int)((worldTime % 1000) / (1000f / 60f));

        String timeFormatString = "ERR";

        if (CONFIG.timeDisplay.ampm()) {
            int ampmHour = realHour % 12;
            if (ampmHour == 0) ampmHour = 12;
            String ampm = (realHour < 12) ? "AM" : "PM";
            timeFormatString = String.format("%s: %d:%02d %s", CONFIG.timeDisplay.text(), ampmHour, minutes, ampm);
        } else {
            timeFormatString = String.format("%s: %02d:%02d", CONFIG.timeDisplay.text(), realHour, minutes);
        }
        if (player.level().dimension() != Level.OVERWORLD && !CONFIG.timeDisplay.forceInAllDimensions()) {
            timeFormatString = String.format("%s: ???", CONFIG.timeDisplay.text());
        }
        String note = String.format("Note: %s", CONFIG.noteText());

        String coords = String.format("%s: %s, %s, %s", CONFIG.positionDisplay.text(), (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ());

        String day = String.format("%s: %s", CONFIG.dayDisplay.text(), level.getOverworldClockTime() / 24000L);
        String biome = String.format("%s: %s", CONFIG.biomeDisplay.text(), biomeName);
        String fps = String.format("%s: %s", CONFIG.framerateDisplay.text(), client.getFps());
        String speed = String.format("%s: %.1fb/s", CONFIG.speedDisplay.text(), blocksPerSecond);
        String time = timeFormatString;




        List<FormattedLine> lineList = new ArrayList<>();

        if (CONFIG.positionDisplay.enabled()) lineList.add(new FormattedLine(coords, CONFIG.positionDisplay.colorText(), CONFIG.positionDisplay.colorValue()));
        if (CONFIG.dayDisplay.enabled()) lineList.add(new FormattedLine(day, CONFIG.dayDisplay.colorText(), CONFIG.dayDisplay.colorValue()));
        if (CONFIG.timeDisplay.enabled()) lineList.add(new FormattedLine(time, CONFIG.timeDisplay.colorText(), CONFIG.timeDisplay.colorValue()));
        if (CONFIG.biomeDisplay.enabled()) lineList.add(new FormattedLine(biome, CONFIG.biomeDisplay.colorText(), CONFIG.biomeDisplay.colorValue()));
        if (CONFIG.framerateDisplay.enabled()) lineList.add(new FormattedLine(fps, CONFIG.framerateDisplay.colorText(), CONFIG.framerateDisplay.colorValue()));
        if (CONFIG.speedDisplay.enabled()) lineList.add(new FormattedLine(speed, CONFIG.speedDisplay.colorText(), CONFIG.speedDisplay.colorValue()));
        if (!CONFIG.noteText().isEmpty()) lineList.add(new FormattedLine(note, Color.WHITE, Color.WHITE));


        //String[] lines = lineList.toArray(new String[0]);

        graphics.nextStratum();

        int padding = CONFIG.padding();
        int opacity = CONFIG.backgroundOpacity();
        int lineSpacing = CONFIG.lineSpacing();


        if (CONFIG.useChatBackgroundOpacity()) {
            double opacityFloat = client.options.textBackgroundOpacity().get();
            int opacityAlpha = (int) Math.round(opacityFloat * 255.0);
            opacity = Math.min(255, Math.max(0, opacityAlpha));
        }

        float scale = CONFIG.scale();



        int lineHeight = font.lineHeight - 1;
        int maxTextWidth = 0;
        for (FormattedLine line : lineList) {
            maxTextWidth = Math.max(maxTextWidth, font.width(line.fullText));
        }

        int totalHeight = (lineList.size() * lineHeight) + (Math.max(0, lineList.size() - 1) * lineSpacing);

        graphics.getMatrixStack().pushMatrix();
        graphics.getMatrixStack().scale(scale, scale);


        graphics.fill(
                (int)(x_offset / scale) - padding,
                (int)(y_offset / scale) - padding,
                (int)(x_offset / scale) + maxTextWidth + padding,
                (int)(y_offset / scale) + totalHeight + padding,
                ARGB.color(opacity, 0, 0, 0));

        for (int i = 0; i < lineList.size(); i++) {
            int yPos = (int)(y_offset / scale) + (i * (lineHeight + lineSpacing));
            /*graphics.text(
                    font,
                    Component.literal(lines[i]),
                    (int)(x_offset / scale),
                    yPos,
                    ARGB.color(255, 255, 255, 255),
                    true
            );*/
            FormattedLine line = lineList.get(i);
            if (!line.label.isEmpty()) {
                graphics.text(
                        font,
                        line.label,
                        (int)(x_offset / scale),
                        yPos,
                        line.labelColor.argb(),
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
                        line.valueColor.argb(),
                        true
                );
            }
        }

        graphics.getMatrixStack().popMatrix();
        graphics.nextStratum();
    }
}

class FormattedLine {
    public final String fullText;
    public final String label;
    public final String value;
    public final io.wispforest.owo.ui.core.Color labelColor;
    public final io.wispforest.owo.ui.core.Color valueColor;
    public FormattedLine(String fullText, io.wispforest.owo.ui.core.Color labelColor, io.wispforest.owo.ui.core.Color valueColor) {
        this.fullText = fullText.toString();
        this.labelColor = labelColor;
        this.valueColor = valueColor;

        int colonIndex = fullText.indexOf(':');
        if (colonIndex != -1) {
            this.label = fullText.substring(0, colonIndex + 1);
            this.value = fullText.substring(colonIndex + 1);
        } else {
            this.label = "";
            this.value = fullText;
        }
    }
}