package io.fabricatedatelier.mayor.access;

/**
 * Used for entityViewPacket.
 *
 * <p>Determines if the player should get teleport after setCameraEntity method in ServerPlayerEntity class
 */
public interface ServerPlayerAccess {

    public void setWasInMayorView(boolean setWasInMayorView);

    public boolean wasInMayorView();
}
