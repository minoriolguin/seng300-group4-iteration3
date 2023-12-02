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
