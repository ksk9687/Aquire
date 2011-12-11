package boardGame

import scala.collection.mutable._

class SimpleDeck[T] {
    private val cards = new ArrayBuffer[T];

    def pushTop(card : T) = { card +: cards }
    def pushBottom(card : T) = { cards += card }

    def draw : T = {
        val c = cards.head;
        cards.trimStart(1);
        c
    }
    
    def clear = cards.clear()
    def canDraw : Boolean = cards.length > 0

    //Fisher-Yates
    def shuffle = {
        var i = cards.length;
        while (i > 0) {
        	val j = (Math.random * i).toInt
        	i -= 1
        	val tmp = cards(i)
        	cards(i) = cards(j)
        	cards(j) = tmp
        }
    }
}