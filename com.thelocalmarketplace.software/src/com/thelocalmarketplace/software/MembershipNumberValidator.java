 /**
 *Project, Iteration 3, Group 4
 *  Group Members:
 * - Arvin Bolbolanardestani / 30165484
 * - Anthony Chan / 30174703
 * - Marvellous Chukwukelu / 30197270
 * - Farida Elogueil / 30171114
 * - Ahmed Elshabasi / 30188386
 * - Shawn Hanlon / 10021510
 * - Steven Huang / 30145866
 * - Nada Mohamed / 30183972
 * - Jon Mulyk / 30093143
 * - Althea Non / 30172442
 * - Minori Olguin / 30035923
 * - Kelly Osena / 30074352
 * - Muhib Qureshi / 30076351
 * - Sofia Rubio / 30113733
 * - Muzammil Saleem / 30180889
 * - Steven Susorov / 30197973
 * - Lydia Swiegers / 30174059
 * - Elizabeth Szentmiklossy / 30165216
 * - Anthony Tolentino / 30081427
 * - Johnny Tran / 30140472
 * - Kaylee Xiao / 30173778 
 **/

package com.thelocalmarketplace.software;

public class MembershipNumberValidator {
    private static final int EXPECTED_LENGTH = 8;
    private MembershipDatabase database;

    public MembershipNumberValidator(MembershipDatabase database) {
        this.database = database;
    }

    public boolean isValid(String number) {
        if (number == null || !number.matches("\\d{" + EXPECTED_LENGTH + "}")) {
            return false;
        }

        // Check if the number exists in the database
        if (!database.memberExists(number)) {
            System.out.println("Error: Membership number not found.");
            return false;
        }

        return true;
    }
}
