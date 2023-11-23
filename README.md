# SENG 300: Project Iteration 2

The project involves the development of the control software for a simulation of the self-checkout system desired by TheLocalMarketplace, to be produced by your organization.


## Objective
1. To expose you to issues that occur in team-based software development and 
2. To give you practice at developing non-trivial software that depends heavily on third-party code bases.


## Authors
- [Alan Yong: 30105707](https://github.com/alanyongy)
- [Atique Muhammad: 30038650](https://github.com/Atique6)
- [Ayman Momin: 30192494](https://github.com/aymanmomin)
- [Christopher Lo: 30113400](https://github.com/Reolism)
- [Ellen Bowie: 30191922](https://github.com/ebeeze1)
- [Emil Huseynov: 30171501](https://github.com/emilh7)
- [Eric George: 30173268](https://github.com/ericthegeorge)
- [Kian Sieppert: 30134666](https://github.com/givenn19)
- [Muzammil Saleem: 30180889](https://github.com/muzman123)
- [Ryan Korsrud: 30173204](https://github.com/ryankorsrud)
- [Sukhnaaz Sidhu: 30161587](https://github.com/sukh-lgtm)

## Revision History
- Version 4: The hardware team has released a new version of the hardware simulation: this corrects a significant bug in BanknoteDispensationSlot.removeDanglingBanknotes(..).

- Version 3: The hardware team has released a new version of the hardware simulation: this corrects a small bug in the method configureCoinTrayCapacity(..).

- Version 2: The hardware team has released a new version of the hardware simulation: this reduces redundancy between the three types of station by moving their commonality into an abstract base class.  A documentation error in ElectronicScaleBronze has also been corrected.

- Version 1: Initial version.


## Requirements
- You and your team must develop a portion of the control software for the self-checkout system, atop the hardware simulation provided to you in the attached ZIP file as  `com.thelocalmarketplace.hardware_0.2`. You must not alter the source code therein.
- You and your team must extend the control software for the self-checkout system to support the following use cases, according to the organization’s [use case model (v2.0)](/TheLocalMarketplace%20UC%202.pdf):
    - Payment
        - Pay with Coin [now, you have to deal with giving change]
        - Pay with Credit via Swipe
        - Pay with Debit via Swipe
        - Pay with Banknote [including making change]
    - After Payment
        - Print Receipt
    - Weight Sale
        - Handle Bulky Item [some portions of this, like attendant feedback, cannot be handled yet]
    - Adding/Removing of items
        - Add Item via Handheld Scan
        - Add Own Bags
        - Remove Item [this will have to be handled via a programmatic interface for now]

    This project iteration is cumulative: you must continue to provide support for the use cases mentioned in earlier iterations.  If the details of the requirements have changed, you need to make the necessary adjustments.  **Note that there are now three kinds of customer station: Gold, Silver, and Bronze with varying levels of accuracy (and cost) and their internal components sometimes differ; you must support all three kinds.**

- You may extend any of the implementations that your teammates worked on in the first iteration, or any combination thereof; it is for the team to decide the best option to proceed with.

- You and your team must develop an automated test suite, written in JUnit 4, for testing your application. Logical test case selection and coverage both matter.

- You and your team must begin every source file in the control software and its test suite with a comment that contains your names and UCID numbers.

- You and your team may provide a supplementary, one (1) page explanation of how your application works.

- You and your team must provide a Git log that demonstrates who performed commits over time. (This can be used partially as evidence regarding individual opinions; see below.)

- Individual Performance Evaluations must be submitted via the relevant survey instrument. The instructions are given there.


## Solution Submission
Relative to the above description, you are required to submit:

1. the com.thelocalmarketplace.software project detailed above, in a ZIP file
2. the com.thelocalmarketplace.software.test project detailed above, in a ZIP file
3. an optional one page of written textual explanations, with the cover page
4. the Git log for your team's Git repository
    


In your **submission comments**, you are required to provide:

1. the names and UCID numbers of yourself and your teammates
2. any sources of information you have used, other than materials directly given you by Prof. Walker or the TAs
    
    
You must also each provide Individual Performance Evaluations regarding the contributions of yourself and your teammates, under the separate survey instrument. 

By default, everyone on the team will receive the same grade; individuals who fail to contribute will be penalized.  Be prepared to offer evidence to support your claims.


## Considerations and Advice

What we are looking for is: (1) Does your application work? (2) Is your test suite designed to demonstrate the presence of bugs? (3) Have you fulfilled the other requirements?

Note that the use case names I have provided are relative to the organization’s use case model, which may have undergone changes since you last looked at it. 

Remember: In a good team, everyone contributes, but not necessarily in the same way.

Do NOT get your teammates to commit YOUR work. Don’t be naïve: you will have no evidence that you did the work.  We are not interested in seeing that you sent a couple of posts in some Discord chat; that’s not real evidence!
