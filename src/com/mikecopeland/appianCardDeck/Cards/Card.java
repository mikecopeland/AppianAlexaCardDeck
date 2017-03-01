package com.mikecopeland.appianCardDeck.Cards;

public class Card {
    private final Ranks rank;
    private final Suits suit;

    public Card(Ranks r, Suits s){
        this.rank = r;
        this.suit = s;
    }

    public Ranks getRank(){
        return this.rank;
    }

    public Suits getSuit(){
        return this.suit;
    }

    public String toString(){
        return this.getRank() + " of " + this.getSuit();
    }
}
