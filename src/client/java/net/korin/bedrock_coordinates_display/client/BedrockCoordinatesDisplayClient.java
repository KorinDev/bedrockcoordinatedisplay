package net.korin.bedrock_coordinates_display.client;

import com.mojang.blaze3d.platform.InputConstants;
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

        String note = String.format("Note: %s", CONFIG.noteText());

        String coords = String.format("%s: %s, %s, %s", CONFIG.positionDisplay.text(), (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ());
        String day = String.format("%s: %s", CONFIG.dayDisplay.text(), level.getOverworldClockTime() / 24000L);
        String biome = String.format("%s: %s", CONFIG.biomeDisplay.text(), biomeName);
        String fps = String.format("%s: %s", CONFIG.framerateDisplay.text(), client.getFps());
        String speed = String.format("%s: %.1fb/s", CONFIG.speedDisplay.text(), blocksPerSecond);

        List<String> lineList = new ArrayList<>();

        if (CONFIG.positionDisplay.enabled()) lineList.add(coords);
        if (CONFIG.dayDisplay.enabled()) lineList.add(day);
        if (CONFIG.biomeDisplay.enabled()) lineList.add(biome);
        if (CONFIG.framerateDisplay.enabled()) lineList.add(fps);
        if (CONFIG.speedDisplay.enabled()) lineList.add(speed);
        if (!CONFIG.noteText().isEmpty()) lineList.add(note);

        String[] lines = lineList.toArray(new String[0]);

        graphics.nextStratum();

        int padding = CONFIG.padding();
        int opacity = CONFIG.backgroundOpacity();
        int lineSpacing = CONFIG.lineSpacing();


        if (CONFIG.useChatBackgroundOpacity()) {
            double opacityFloat = client.options.textBackgroundOpacity().get();
            int opacityAlpha = (int) Math.round(opacityFloat * 255.0);
            opacity = Math.min(255, Math.max(0, opacityAlpha));
        }



        int lineHeight = font.lineHeight - 1;
        int maxTextWidth = 0;
        for (String line : lines) {
            maxTextWidth = Math.max(maxTextWidth, font.width(line));
        }

        int totalHeight = (lines.length * lineHeight) + (Math.max(0, lines.length - 1) * lineSpacing);

        graphics.fill(
                x_offset - padding,
                y_offset - padding,
                x_offset + maxTextWidth + padding,
                y_offset + totalHeight + padding,
                ARGB.color(opacity, 0, 0, 0));

        for (int i = 0; i < lines.length; i++) {
            int yPos = y_offset + (i * (lineHeight + lineSpacing));
            graphics.text(
                    font,
                    Component.literal(lines[i]),
                    x_offset,
                    yPos,
                    ARGB.color(255, 255, 255, 255),
                    true
            );
        }
        graphics.nextStratum();
    }
}