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

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

public class MembershipDatabase {
    private Map<String, Member> members;
    private static String idCounter = "00000000";
    private String MAX_MEMBERS = "99999999";

    public MembershipDatabase() {
        this.members = new HashMap<>();
    }

    // Adds a new member and returns the unique memberId
    public String addMember(String name) 
    {
    	//check that the name is not null
    	if(name == null)
    	{
    		throw new NullPointerSimulationException("Member name is null");
    	}
    	
    	//check that the name is not empty
    	if(name == "")
    	{
    		throw new RuntimeException("Member name is empty");
    	}
    	
    	//check that the maximum number of users is not going to be exceeded
    	if(idCounter.equals(MAX_MEMBERS))
    	{
    		throw new RuntimeException("Number of members exceed maximum allowed");
    	}
    	//create new member
        Member newMember = new Member(idCounter, name);
        
    	int incrementedID = Integer.parseInt(idCounter) + 1;
    	idCounter = String.format("%08d", incrementedID);

        members.put(newMember.getMemberId(), newMember);
        return newMember.getMemberId();
    }
    
    /**
     * Add points to a member's account
     * 
     * @param memberId - ID of the account to add points to
     * @param points - the amount of points to add
     */
    public void addPoints(String memberId, int points) {
        if (members.containsKey(memberId)) {
            members.get(memberId).addPoints(points);
        }
    }

    public void subtractPoints(String memberId, int points) {
        if (members.containsKey(memberId)) {
            members.get(memberId).subtractPoints(points);
        }
    }
    
    public int getPoints(String memberId)
    {
    	return members.get(memberId).getPoints();
    }

    // Retrieves a member by memberId
    public String getMemberName(String memberId) {
        return members.get(memberId).getName();
    }
    
//    public String getMemberIdByName(String memberName) {
//    	for(Member member : members.values())
//    	{
//    		if (member.getName() == memberName)
//    		{
//    			return member.getMemberId();
//    		}
//    	}
//    	
//    	return "";
//    }
    
    // Method to check if a member exists in the database
    public boolean memberExists(String memberId) {
        return members.containsKey(memberId);
    }

    private static class Member {
        private String memberId;
        private String name;
        private int points;

        public Member(String memberId, String name) {
            this.memberId = memberId;
            this.name = name;
            this.points = 0;
        }

        public void addPoints(int points) {
        	if(points > 0) {
				this.points = Math.addExact(this.points, points);
        	}
        }
        
        public void subtractPoints(int points)
        {
			points = Math.abs(points);
			int tempPoints = Math.subtractExact(this.points, points);

			if(this.points >= tempPoints)
			{
				this.points = tempPoints;
			}
			else
			{
				throw new RuntimeException("Not enought points");
			}
        }

        // Getters and toString for easy display
        public String getMemberId() { return memberId; }
        public String getName() { return name; }
        public int getPoints() { return points; }
        @Override
        public String toString() {
            return "Member{" + "memberId=" + memberId + ", name='" + name + '\'' + ", points=" + points + '}';
        }
    }
}
