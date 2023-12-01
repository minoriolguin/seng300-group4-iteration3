package com.thelocalmarketplace.software;

import com.thelocalmarketplace.software.MembershipDatabase;

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
        int memberId = Integer.parseInt(number);
        if (!database.memberExists(memberId)) {
            System.out.println("Error: Membership number not found.");
            return false;
        }

        return true;
    }
}
