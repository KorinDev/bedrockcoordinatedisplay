package net.korin.bedrock_coordinates_display.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.korin.bedrock_coordinates_display.BedrockCoordinatesDisplay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;

import java.awt.*;

public class BedrockCoordinatesDisplayClient implements ClientModInitializer {

    public static String MOD_ID = "bedrock_coordinates_display";

    public static int y_offset = 10;
    public static int x_offset = 10;

	@Override
	public void onInitializeClient() {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(BedrockCoordinatesDisplayClient.MOD_ID, "after_hotbar"), BedrockCoordinatesDisplayClient::extract
        );
	}

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        Player player = client.player;

        if (player == null) return;

        String coords = String.format("Position: %s, %s, %s", (int)client.player.getX(), (int)client.player.getY(), (int)client.player.getZ());

        graphics.nextStratum();

        int textWidth = font.width(coords);
        int textHeight = font.lineHeight;
        int padding = 4;

        graphics.fill(
                x_offset - padding,
                y_offset - padding,
                x_offset + textWidth + padding,
                y_offset + textHeight + padding,
                ARGB.color(200, 0, 0, 0));


        graphics.text(font, Component.literal(coords), x_offset, y_offset, ARGB.color(255, 255, 255, 255), true);

        graphics.nextStratum();
    }
}