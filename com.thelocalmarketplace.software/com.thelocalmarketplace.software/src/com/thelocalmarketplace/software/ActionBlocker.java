/**
* Jon Mulyk (UCID: 30093143)
* Elizabeth Szentmiklossy (UCID: 30165216)
* Ahmed Ibrahim Mohamed Seifledin Hadsan (UCID: 30174024)
* Arthur Huan (UCID: 30197354)
* Jaden Myers (UCID: 30152504)
* Jane Magai (UCID: 30180119)
* Ahmed Elshabasi (UCID: 30188386)
* Jincheng Li (UCID: 30172907)
* Sina Salahshour (UCID: 30177165)
* Anthony Tolentino (UCID: 30081427) */

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
