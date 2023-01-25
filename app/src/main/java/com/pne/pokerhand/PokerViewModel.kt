package com.pne.pokerhand

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PokerViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(PokerState())

    init {
        populateDeck()
    }

    private fun populateDeck() {
        val deck = mutableListOf<Card>()
        repeat(13) {
            deck.add(Card(CardType.Hearts, it + 1))
        }
        repeat(13) {
            deck.add(Card(CardType.Diamonds, it + 1))
        }
        repeat(13) {
            deck.add(Card(CardType.Spades, it + 1))
        }
        repeat(13) {
            deck.add(Card(CardType.Clubs, it + 1))
        }

        state = state.copy(deck = deck)
    }

    fun shuffleNewHand(cheatHand: List<Card>?) {
        val hand = mutableListOf<Card>()
        val deck = mutableListOf<Card>()

        state.hand.forEach {
            hand.add(it)
        }
        state.deck.forEach {
            deck.add(it)
        }

        //empty hand into deck
        hand.forEach {
            deck.add(it)
        }
        hand.clear()

        if (cheatHand.isNullOrEmpty()) {
            //draw new cards from deck
            repeat(5) {
                val r = Random.nextInt(0, deck.size)
                val newCard = deck.elementAt(r)
                hand.add(newCard)
                deck.remove(newCard)
                if (deck.contains(newCard)) {
                    println("contains wrong card")
                }
            }
        } else {
            cheatHand.forEach { cheatCard ->
                var cardFromDeck = Card(CardType.Diamonds, 0)
                deck.forEach { deckCard ->
                    if(cheatCard.value == deckCard.value && cheatCard.type == deckCard.type){
                        cardFromDeck = deckCard
                    }
                }
                if(cardFromDeck.value!=0){
                    deck.remove(cardFromDeck)
                    hand.add(cardFromDeck)
                }
            }
        }

        state = state.copy(hand = hand, deck = deck)

        analyzeCards()
    }

    fun showVisualValue(value: Int): String {
        return when (value) {
            13 -> "k"
            12 -> "q"
            11 -> "j"
            10 -> "t"
            else -> value.toString()
        }
    }

    private fun analyzeCards() {
        val values = mutableListOf<Int>()
        val sameValue1 = mutableListOf<Int>()
        val sameValue2 = mutableListOf<Int>()
        val sameType = mutableListOf<Card>()
        var isStraight = true

        //get values on hand
        state.hand.forEach { card ->
            values.add(card.value)

            if (sameType.isEmpty() || sameType.first().type == card.type) {
                sameType.add(card)
            }
        }
        values.sort()

        val isFlush = sameType.size == state.hand.size

        values.forEach {
            if (values[0] + values.indexOf(it) != it)
                isStraight = false
        }

        //check for similar values
        values.forEach { value ->
            val predicate: (Int) -> Boolean = { it == value }
            val result = values.count(predicate)
            if (result > 1) {
                if (sameValue1.isEmpty() || value == sameValue1.first()) {
                    sameValue1.add(value)
                } else if (sameValue2.isEmpty() || value == sameValue2.first()) {
                    sameValue2.add(value)
                }
            }
        }

        //"analyze
        val analysis = if (isStraight) {
            if (isFlush) {
                if (values[4] == 13) {
                    "You have a royal flush"
                } else {
                    "You have a straight flush"
                }
            } else {
                "You have a straight"
            }
        } else if (sameValue1.size == 4 || sameValue2.size == 4) {
            "You have four of a kind"
        } else if (sameValue1.size == 3 || sameValue2.size == 3) {
            if (sameValue2.size == 2 || sameValue1.size == 2) {
                "You have a full house"
            } else {
                if (isFlush) {
                    "You have a flush"
                } else {
                    "You have a three of a kind"
                }
            }
        } else if (sameValue1.size == 2 || sameValue2.size == 2) {
            if (isFlush) {
                "You have a flush"
            } else if (sameValue1.size == 2 && sameValue2.size == 2) {
                "You have two pair"
            } else {
                "You have a pair"
            }
        } else {
            "Your highest card is ${showVisualValue(values[values.size - 1])}"
        }

        println("1 $sameValue1")
        println("2 $sameValue2")
        println("t $sameType")
        state = state.copy(description = analysis)
    }

    fun toggleSelector() {
        var isOpen = state.selectorIsOpen
        isOpen = !isOpen
        state = state.copy(selectorIsOpen = isOpen)
    }

    fun addToSelectedHand(newCard: Card) {
        var isInHand = false
        val newHand = mutableListOf<Card>()

        state.selectedHand.forEach {
            newHand.add(it)
        }

        newHand.forEach { comparedCard ->
            if (newCard.value == comparedCard.value && newCard.type == comparedCard.type) {
                isInHand = true
            }
        }

        if (!isInHand && state.selectedHand.size < 5) {
            newHand.add(newCard)
            state = state.copy(selectedHand = newHand)
        }
        println("this is selected hand ${state.selectedHand}")
    }

    fun emptySelectedHand(){
        val newHand = emptyList<Card>()
        state = state.copy(selectedHand = newHand)
    }
}

data class PokerState(
    val deck: List<Card> = emptyList(),
    val hand: List<Card> = emptyList(),
    val selectedHand: List<Card> = emptyList(),
    val description: String = "",
    val selectorIsOpen: Boolean = false,
)

class Card(
    val type: CardType,
    val value: Int,
)

enum class CardType {
    Hearts,
    Diamonds,
    Clubs,
    Spades,
}