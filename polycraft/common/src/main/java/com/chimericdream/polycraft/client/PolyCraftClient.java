package com.chimericdream.polycraft.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;

/** Client-side setup. Called from each loader's client entrypoint, never from common init. */
public final class PolyCraftClient {
    private PolyCraftClient() {
    }

    public static void init() {
        ClientGuiEvent.INIT_POST.register((screen, access) -> ConflictOverlay.onInit(screen));

        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register((minecraft, screen, event, doubleClick) ->
            ConflictOverlay.onMouseClicked(screen, event.x(), event.y(), event.button()) ? EventResult.interruptTrue() : EventResult.pass());
        ClientScreenInputEvent.MOUSE_SCROLLED_PRE.register((minecraft, screen, mouseX, mouseY, scrollX, scrollY) ->
            ConflictOverlay.onMouseScrolled(screen, mouseX, mouseY, scrollY) ? EventResult.interruptTrue() : EventResult.pass());
        ClientScreenInputEvent.KEY_PRESSED_PRE.register((minecraft, screen, event) ->
            ConflictOverlay.onKeyPressed(screen, event.key()) ? EventResult.interruptTrue() : EventResult.pass());
    }
}
