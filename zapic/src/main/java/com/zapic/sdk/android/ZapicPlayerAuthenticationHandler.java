package com.zapic.sdk.android;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * Represents a callback used to notify the application when the player has logged in or logged out.
 *
 * @author Kyle Dodson
 * @since 1.2.0
 * @deprecated Replaced with {@link ZapicAuthenticationCallback}.
 */
public interface ZapicPlayerAuthenticationHandler {
    /**
     * Called when the player has logged in.
     * <p>
     * The game may use this notification to enable features reserved for authenticated players. The game may also use
     * this notification to load player-specific data.
     *
     * @param player The current player.
     * @since 1.2.0
     */
    @MainThread
    void onLogin(@NonNull ZapicPlayer player);

    /**
     * Called when the player has logged out.
     * <p>
     * The game may use this notification to disable features reserved for authenticated players. The game may also use
     * this notification to unload player-specific data.
     *
     * @param player The previous player.
     * @since 1.2.0
     */
    @MainThread
    void onLogout(@NonNull ZapicPlayer player);
}
