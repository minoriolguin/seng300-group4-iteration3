package com.thelocalmarketplace.software;

public class ActionBlocker{
    private boolean isBlocked = false;

    // Method to block customer interaction
    public void blockInteraction() {
        isBlocked = true;
    }

    // Method to unblock customer interaction
    public void unblockInteraction() {
        isBlocked = false;
    }

    // Check if interaction is blocked
    public boolean isInteractionBlocked() {
        return isBlocked;
    }
}
