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
import java.util.HashMap;
import java.util.Map;

public class MembershipDatabase {
    private Map<Integer, Member> members;
    private static int idCounter = 0;

    public MembershipDatabase() {
        this.members = new HashMap<>();
    }

    // Adds a new member and returns the unique memberId
    public int addMember(String name) {
        Member newMember = new Member(++idCounter, name);
        members.put(newMember.getMemberId(), newMember);
        return newMember.getMemberId();
    }

    // Adds points to a member's account
    public void addPoints(int memberId, int points) {
        if (members.containsKey(memberId)) {
            members.get(memberId).addPoints(points);
        }
    }

    // Retrieves a member by memberId
    public Member getMember(int memberId) {
        return members.get(memberId);
    }
    
    // Method to check if a member exists in the database
    public boolean memberExists(int memberId) {
        return members.containsKey(memberId);
    }

    private static class Member {
        private int memberId;
        private String name;
        private int points;

        public Member(int memberId, String name) {
            this.memberId = memberId;
            this.name = name;
            this.points = 0;
        }

        public void addPoints(int points) {
            this.points += points;
        }

        // Getters and toString for easy display
        public int getMemberId() { return memberId; }
        public String getName() { return name; }
        public int getPoints() { return points; }
        @Override
        public String toString() {
            return "Member{" + "memberId=" + memberId + ", name='" + name + '\'' + ", points=" + points + '}';
        }
    }
}
