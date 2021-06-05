package network;

public interface NetEventTypeID {
    int CHAT_MESSAGE = 1;
    int PLAYER_JOIN = 2;
    int DEFINE_CARD = 3;
    int INTRO_CARD = 4;
    int ADD_FLIGHT = 5;
    int NOTIFY_PHASE_CHANGE = 6;
    int DEFINE_IMAGE = 7;
    int CACHE_IMAGE = 7;
    int REQUEST_CARD_ID = 8;
    int GRANT_CARD_ID = 9;
    int UPDATE_CARD_DEFINITION = 10;
    int SYNC_COMPLETE = 11;

    /**
    * GO PUT THE RIGHT LINE IN {@link NetworkEventTransceiver}
    */

}
