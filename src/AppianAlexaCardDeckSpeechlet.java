import com.mikecopeland.appianCardDeck.Decks.*;
import com.mikecopeland.appianCardDeck.Cards.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

/**
 * This sample shows how to create a simple speechlet for handling intent requests and managing
 * session interactions.
 */
public class AppianAlexaCardDeckSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(AppianAlexaCardDeckSpeechlet.class);
    private static final String deckKey = "DECK";

    private void resetDeck(Session session) {
        Deck deck = new Deck();
        deck.shuffle();
        session.setAttribute(deckKey, deck);
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {

        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        resetDeck(session);
        return getWelcomeResponse();
    }



    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        // Get intent from the request object.
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        // Note: If the session is started with an intent, no welcome message will be rendered;
        // rather, the intent specific response will be returned.
        if ("NewDeck".equals(intentName)) {
            return setNewDeckInSession(intent, session);
        }
        if ("ShuffleDeck".equals(intentName)) {
            return shuffleDeck(intent, session);
        }
        if ("DrawOneCard".equals(intentName)) {
            return drawOneCard(intent, session);
        }
        else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual welcome message
     */
    private SpeechletResponse getWelcomeResponse() {
        // Create the welcome message.
        String speechText =
                "Welcome to the Appian Card Deck Alexa Skill. I will shuffle and deal cards at your request. "
                        + "You may start with a new deck by saying, use a new deck."
                        + "To shuffle the deck say, shuffle the deck."
                        + "To deal a card say, deal a card."
                        + "I have started with a new shuffled deck";
        String repromptText =
                "Please tell me to deal a card by saying, deal a card";

        return getSpeechletResponse(speechText, repromptText, true);
    }

    private SpeechletResponse shuffleDeck(final Intent intent, final Session session) {
        Shuffleable deck = (Shuffleable)session.getAttribute(deckKey);
        deck.shuffle();
        session.setAttribute(deckKey, deck);
        String speechText = "I have shuffled the deck.";
        String repromptText = "Please draw a card by saying, draw a card.";
        return getSpeechletResponse(speechText, repromptText, true);
    }

    private SpeechletResponse drawOneCard(final Intent intent, final Session session) {
        Dealable deck = (Dealable)session.getAttribute(deckKey);
        Card card = deck.dealOneCard();

        String speechText = String.format("Your card is the %s.", card);
        String repromptText = "Please draw another card by saying, draw a card.";
        return getSpeechletResponse(speechText, repromptText, true);
    }

    private SpeechletResponse setNewDeckInSession(final Intent intent, final Session session) {
        resetDeck(session);
        String speechText = "I am starting with a new unshuffled deck.";
        String repromptText = "Please shuffle the deck by saying, shuffle the deck. "
                + "Or draw a card by saying, draw a card.";
        return getSpeechletResponse(speechText, repromptText, true);
    }

    /**
     * Returns a Speechlet response for a speech and reprompt text.
     */
    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
            boolean isAskResponse) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        if (isAskResponse) {
            // Create reprompt
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
}
