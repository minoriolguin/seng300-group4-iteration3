/**
 * # seng300-group4-iteration3

Anthony Chan / anthonyych4n / 30174703

Farida Elogueil/ Farida152005 / 30171114

Marvellous Chukwukelu/ M-Chukwukelu / 30197270

Lydia Swiegers / LSTofu / 30174059

Steven Huang / stevenhuang007 / 30145866

Althea Non / altheanon / 30172442

Minori Olguin / minoriolguin / 30035923

Sofia Rubio / sofiarubio / 30113733

Elizabeth Szentmiklossy / eliza / 30165216

Kelly Osena / osenak / 30074352

Arvin Bolbolanardestani /  / 30165484

Ahmed Elshabasi /  / 30188386

Shawn Hanlon / Shawn426566 /  10021510

Nada Mohamed /  / 30183972

Jon Mulyk /  / 30093143

Muhib Qureshi /  / 30076351

Muzammil Saleem /  / 30180889

Steven Susorov /  / 30197973

Anthony Tolentino /  / 30081427

Johnny Tran /  / 30140472

Kaylee Xiao /  /  30173778

 */

package com.thelocalmarketplace.software;

public class DisableStation {

    private Software software;

    public DisableStation(Software software) {
        this.software = software;
    }

    /**
     * Disables the customer station for maintenance.
     * If a session is active, the disabling is set to pending until the session completes.
     */
    public void disableForMaintenance() {
        if (software.isSessionActive()) {
            software.setPendingMaintenance(true);
            System.out.println("Disabling pending: Session is currently active.");
        } else {
            software.blockCustomerStation();
            System.out.println("Station disabled for maintenance. 'Out of order' displayed.");
        }
    }

    /**
     * Method to be called when a session ends to check if maintenance is pending.
     * This method is integrated into the session end logic in the Software class.
     */
    public void checkAndPerformPendingMaintenance() {
        if (software.isPendingMaintenance() && !software.isSessionActive()) {
            software.blockCustomerStation();
            software.setPendingMaintenance(false);
            System.out.println("Pending maintenance performed. Station disabled.");
        }
    }

    /**
     * Enables the customer station if it is currently blocked for maintenance.
     */
    public void enableStation() {
        if (software.isCustomerStationBlocked()) {
            software.unblockCustomerStation();
            System.out.println("Station enabled for use.");
        } else {
            System.out.println("Station is not currently blocked.");
        }
    }
}
