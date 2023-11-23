// Project 2 Iteration Group 3
//Julie Kim 10123567
//Aryaman Sandhu 30017164
//Arcleah Pascual 30056034
//Aoi Ueki 30179305
//Ernest Shukla 30156303
//Shawn Hanlon 10021510
//Jaimie Marchuk 30112841
//Sofia Rubio 30113733
//Maria Munoz 30175339
//Anne Lumumba 30171346
//Nathaniel Dafoe 30181948

/**
 * The CardDeclinedException class represents an exception thrown when a card payment is declined.
 * This exception extends the base Exception class.
 */
public class CardDeclinedException extends Exception{
    public CardDeclinedException(){
        super("Card declined");
    }

    public CardDeclinedException(String errorMessage){
        super(errorMessage);
    }

}
