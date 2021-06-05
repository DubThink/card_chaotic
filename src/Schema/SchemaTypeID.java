package Schema;

import network.NetworkEventTransceiver;

public interface SchemaTypeID {
    int RESERVED_INVALID_SCHEMA=0;
    int CARD_DEFINITION = 1;
    int CARD_SOURCE = 2;
    int ACCOUNT = 3;
    int CARD_LIBRARY_METADATA = 4;
    int LOCAL_PLAYER_PREFS = 5;
    int ACCOUNT_MANAGER = 6;

    /**
     * GO PUT THE RIGHT LINE IN {@link DiskUtil}
     */
}
