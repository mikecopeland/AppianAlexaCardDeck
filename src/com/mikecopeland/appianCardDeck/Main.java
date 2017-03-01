package com.mikecopeland.appianCardDeck;

import com.mikecopeland.appianCardDeck.Decks.Deck;

public class Main {

    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.shuffle();
        do{
            System.out.println(deck.dealOneCard());
        }
        while(!deck.isEmpty());
    }
}
