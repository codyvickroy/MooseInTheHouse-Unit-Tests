package models;

import junit.framework.TestCase;
import models.card.Card;
import models.card.bottom.*;
import models.card.top.*;
import models.player.Bot;
import models.player.Move;
import models.player.Player;
import models.player.ai.StandardBehavior;

import java.util.ArrayList;

/**
 * Created by Brandt Newton on 3/25/2015.
 */
public class StandardBehavior_OffenseTests extends TestCase {

    Player[] players;
    Bot offenseTestBot;

    public void setUp() throws Exception {
        super.setUp();

        players = new Player[] {
                    new Bot(0, null),
                    new Bot(1, null),
                    new Bot(2, null)
        };

        // Hand
        ArrayList<Card> offenseBotHand = new ArrayList<Card>();
        offenseBotHand.add(new MooseInBedroom());
        offenseBotHand.add(new MooseInKitchen());
        offenseBotHand.add(new MooseInBathroom());
        offenseBotHand.add(new MooseInBedroom());
        offenseBotHand.add(new Moose());

        // Create bot with standard behavior
        offenseTestBot = new Bot(3, offenseBotHand, new StandardBehavior());

        // Initialize Player 0 with LOWEST POINTS and NO MOOSE
        players[0].setCardInHouse(0, new EmptyLivingRoom());
        players[0].setCardInHouse(1, new EmptyBathroom());
        players[0].setCardInHouse(2, new EmptyBedroom());
        players[0].setCardInHouse(3, new EmptyBathroom());
        players[0].setCardInHouse(4, new EmptyKitchen());
        players[0].setPoints(1);

        // Initialize Player 1 with HIGHEST POINTS and MOOSE
        players[1].setCardInHouse(0, new Moose());
        players[1].setCardInHouse(1, new Door());
        players[1].setCardInHouse(2, new EmptyBedroom());
        players[1].setCardInHouse(3, new EmptyKitchen());
        players[1].setPoints(3);

        // Initialize Player 2 with 2ND LOWEST POINTS and NO MOOSE
        players[2].setCardInHouse(0, new EmptyLivingRoom());
        players[2].setCardInHouse(1, new EmptyBathroom());
        players[2].setCardInHouse(2, new EmptyBedroom());
        players[2].setCardInHouse(3, new Moose());
        players[2].setPoints(2);
    }

    // =============================
    // === Threat Prioritization ===
    // =============================
    public void testThreatPrioritization_LowestPointsFirst_v1() {
        int[] priorities = offenseTestBot.getPriorities(players);

        assertEquals(0, priorities[0]);
        assertEquals(2, priorities[1]);
        assertEquals(1, priorities[2]);
    }

    public void testThreatPrioritization_LowestPointsFirst_v2() {
        int[] priorities = offenseTestBot.getPriorities(new Player[]{players[0], players[1]});

        assertEquals(0, priorities[0]);
        assertEquals(1, priorities[1]);
    }

    // =======================
    // === Offensive Moves ===
    // =======================

    /**
     * Player 0 is the first player considered due to their score. A moose is played even though it is
     * the last card considered in the bot's hand.
     */
    public void testFirstMove() {
        Move move = simulateTurnWithMoveCap(1);

        assertEquals(Card.CardClass.MOOSE, move.getCard().getCardClass());
        assertEquals(offenseTestBot.getID(), move.getCardPlayerID());
        assertEquals(0, move.getReceivingPlayer());
        assertEquals(5, move.getHousePosition());
    }

    /**
     * Player 0 is still the first player considered because their score has not changed. A moose is now in their house
     * and a bedroom can now be played at position 2.
     */
    public void testSecondMove() {
        Move move = simulateTurnWithMoveCap(2);

        assertEquals(Card.CardClass.BEDROOM, move.getCard().getCardClass());
        assertEquals(offenseTestBot.getID(), move.getCardPlayerID());
        assertEquals(0, move.getReceivingPlayer());
    }

    /**
     * Player 0 now has the same score as Player 2 but they are still considered first because of an arbitrary comparison
     * in the sorting algorithm.
     */
    public void testThirdMove() {
        Move move = simulateTurnWithMoveCap(3);

        assertEquals(Card.CardClass.KITCHEN, move.getCard().getCardClass());
        assertEquals(offenseTestBot.getID(), move.getCardPlayerID());
        assertEquals(0, move.getReceivingPlayer());
    }

    /**
     * Player 2 now has the lowest score and a Bathroom will now be played on them.
     */
    public void testFourthTurnMove() {
        Move move = simulateTurnWithMoveCap(4);

        assertEquals(Card.CardClass.BATHROOM, move.getCard().getCardClass());
        assertEquals(offenseTestBot.getID(), move.getCardPlayerID());
        assertEquals(2, move.getReceivingPlayer());
    }

    /**
     * All Players have the same score: Player 0 is considered first followed by Player 1 who has an openning.
     */
    public void testFifthTurnMove() {
        Move move = simulateTurnWithMoveCap(5);

        assertEquals(Card.CardClass.BEDROOM, move.getCard().getCardClass());
        assertEquals(offenseTestBot.getID(), move.getCardPlayerID());
        assertEquals(1, move.getReceivingPlayer());
    }

    /**
     * Bot has no more valid moves (Empty hand condition) so it returns a null move.
     */
    public void testFinalTurnMove() {
        Move move = simulateTurnWithMoveCap(6);

        assertNull(move);
    }

    /**
     * Simulate a bot's turn.
     *
     * @param moves number of moves to simulate
     * @return      last move made
     */
    public Move simulateTurnWithMoveCap(int moves) {

        Move move = null;

        for (int i = 0; i < moves; i++) {
            move = offenseTestBot.makeMove(players);
            if (move != null) {
                players[move.getReceivingPlayer()].setCardInHouse(move);
            }
        }

        return move;
    }
}
