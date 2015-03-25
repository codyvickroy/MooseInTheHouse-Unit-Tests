package models;

import junit.framework.TestCase;
import models.card.Card;
import models.card.bottom.*;
import models.card.top.Door;
import models.card.top.MooseBait;
import models.card.top.MooseInBathroom;
import models.card.top.MooseInBedroom;
import models.game.Deck;
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

        // Create deck
        Deck deck = new Deck();

        players = new Player[] {
                    new Bot(0, null),
                    new Bot(1, null),
                    new Bot(2, null)
        };

        // Offense
        ArrayList<Card> offenseBotHand = new ArrayList<Card>();
        offenseBotHand.add(new Moose());
        offenseBotHand.add(new MooseBait());
        offenseBotHand.add(new MooseInBedroom());
        offenseBotHand.add(new Door());
        offenseBotHand.add(new MooseInBathroom());

        // Create bot with standard behavior
        offenseTestBot = new Bot(3, offenseBotHand, new StandardBehavior());

        // Initialize Player 0 with LOWEST POINTS and NO MOOSE
        players[0].setCardInHouse(0, new EmptyLivingRoom());
        players[0].setCardInHouse(1, new EmptyBathroom());
        players[0].setCardInHouse(2, new EmptyBedroom());
        players[0].setCardInHouse(3, new EmptyKitchen());
        players[0].setPoints(0);

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
        players[2].setCardInHouse(3, new EmptyKitchen());
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
    public void testFirstMove() {
        Move move = offenseTestBot.makeMove(players);

        assertEquals(move.getCard().getCardClass(), Card.CardClass.MOOSE);
        assertEquals(move.getCardPlayerID(), 3);
        assertEquals(move.getRecievingPlayer(), 0);
    }

}
