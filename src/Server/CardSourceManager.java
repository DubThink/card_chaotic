package Server;

import Gamestate.CardDefinition;
import Globals.Debug;
import Schema.DiskUtil;
import UI.*;
import network.NetworkClientHandler;
import network.event.DefineCardNetEvent;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Random;

import static Globals.Debug.perfTimeMS;
import static Globals.GlobalEnvironment.asyncIOHandler;
import static Server.ServerEnvironment.svErr;

public class CardSourceManager {
    ArrayList<CardSource> cardSources;
    private int nextCardID;
    int goodCardCount;
    Random random;

    boolean isLoaded;

    static final String cardPath = "data/server/cards/";

    public CardSourceManager() {
        this.cardSources = new ArrayList<>();
        goodCardCount = 0;
        random = new Random();
        nextCardID = cardSources.size();
    }

    public CardSource randomCardSource() {
        int pick = random.nextInt(goodCardCount);
        int testPick = -1;
        int idx = -1;
        while (testPick < pick) {
            idx++;
            if (isGoodCard(cardSources.get(idx)))
                testPick++;
        }

        return cardSources.get(idx);
    }

    boolean isGoodCard(CardSource source) {
//        if(source==null)
//            return false;
        float timesShown = source.timesBanned + source.timesAllowed;
        return !source.hasBeenBanned && (timesShown < 4 || (source.timesBanned / timesShown) < .6);
    }

    public void banCard(CardSource source) {
        if (!source.hasBeenBanned) {
            source.timesBanned++;
            goodCardCount--;
        }
        source.hasBeenBanned = true;
        source.hasBeenIntroed = true;
    }

    public void allowCard(CardSource source) {
        if (!source.hasBeenIntroed)
            source.timesAllowed++;
        source.hasBeenIntroed = true;
    }

    public void defineAllCards(NetworkClientHandler handler) {
        for (CardSource source : cardSources) {
            if (isGoodCard(source)) {
                handler.sendSyncingEvent(new DefineCardNetEvent(source.definition));
            }
        }
    }

    protected void putCardSource(CardSource source) {
        if (source.definition.uid != cardSources.size())
            throw new RuntimeException("Can't create a card in a range that already exists");
        cardSources.add(source);
        if(uiCardList!=null){
            uiCardList.addOption(uiCardStringFromDefinition(source.definition));
            if(cardSources.size()==1){
                // first card loaded, gotta update
                uiCardList.selectionChangedAction.notify(uiCardList);
            }
        }
    }

    private static String uiCardStringFromDefinition(CardDefinition definition){
        return String.format("%1$3d", definition.uid)+" | "+String.format("%1$60s",definition.name);
    }

    public CardSource allocateNextCardSource() {
        CardSource newCard = new CardSource(new CardDefinition(nextCardID++));
        putCardSource(newCard);
        return newCard;
    }

    public void applyCardDefinitionUpdate(CardDefinition definition) {
        if (definition.uid < 0)
            throw new RuntimeException("Should not be defining placeholder card definition in source");
        if (definition.uid >= cardSources.size())
            throw new RuntimeException("Not in range");
        cardSources.get(definition.uid).updateDefinition(definition);
        uiCardList.setOption(definition.uid, uiCardStringFromDefinition(definition));
        if(definition.uid == uiCardList.getSelectionIndex())
            // card updated, gotta update
            uiCardList.selectionChangedAction.notify(uiCardList);
    }

    public void saveCardLibraryToDisk() {
        CardLibraryMetadata metadata = new CardLibraryMetadata();
        metadata.maxCardID=nextCardID-1;
        DiskUtil.saveToFile(metadata, cardPath+"cardLibraryMetadata.bs");
        for (int i = 0; i < cardSources.size(); i++) {
            DiskUtil.saveToFile(cardSources.get(i), cardPath+"card_" + i + ".card");
        }
    }

    public void saveTest() {
        //CardLibraryMetadata metadata = new CardLibraryMetadata();
        //metadata.maxCardID=nextCardID-1;
        //DiskUtil.saveToFile(metadata, cardPath+"cardLibraryMetadata.bs");
//        for (int i = 0; i < 10; i++) {
            asyncIOHandler.requestSave(cardSources.get(0), cardPath+"testcard_" + 0 + ".card");
//        }
    }

    public void loadCardLibraryFromDisk(){
        if(!cardSources.isEmpty()){
            svErr("should not be loading library when there are already cards loaded, canceling");
            return;
        }
        CardLibraryMetadata metadata = DiskUtil.tryToLoadFromFileTyped(CardLibraryMetadata.class, cardPath+"cardLibraryMetadata.bs");

        if(metadata!=null){
            System.out.println("Loading "+(metadata.maxCardID+1)+" cards");
            for (int i = 0; i <= metadata.maxCardID; i++) {
                CardSource card = DiskUtil.tryToLoadFromFileTyped(CardSource.class, cardPath+"card_" + i + ".card");
                if(card!=null){
                    System.out.println("loaded card #"+card.definition.uid+" '"+card.definition.name+"'");
                    putCardSource(card);
                    nextCardID++;
                } else {
                    // insert placeholder card
                    System.out.println("unable to load card #"+i);
                    allocateNextCardSource();
                }
            }
            if(nextCardID != metadata.maxCardID+1)
                throw new RuntimeException("how");

        } else {
            svErr("no library metadata file found; not loading cards");
        }
        isLoaded=true;
    }

    public void clearLibrary(){
        cardSources.clear();
        nextCardID=0;
        goodCardCount=0;
        uiCardList.clearOptions();
    }

    // ==== UI ==== //

    UIMultibox uiCardList;
    UICardView uiCardSmallView;
    UIPanel uiBigViewPanel;
    UICardView uiCardBigView;
    UIPanel rootPanel;

    public void setupControlPanel(UIPanel panel){
        rootPanel=panel;
        panel.addChild(new UIButton(10, m(0), 150, 30, "Load Library", this::loadCardLibraryFromDisk));
        panel.addChild(new UIButton(10, m(1), 150, 30, "Save Library", this::saveCardLibraryToDisk));
        panel.addChild(new UIButton(10, m(2), 150, 30, "Clear Library", this::uiActionClearLibrary));
        panel.addChild(new UIButton(10, m(3), 150, 30, "Save Test", this::saveTest));
        uiCardSmallView = panel.addChild(new UICardView(10,-220,.3125f, UILayer.INTERFACE));
        uiCardSmallView.setCardBackView();

        uiCardList = panel.addChild(new UIMultibox(170,10,-10,-10));
        uiCardList.selectionChangedAction = source -> {
            int cardid = source.getSelectionIndex();
            System.out.println(cardid+" selected");
            if(cardid>=0 && cardid<cardSources.size()){
                uiCardSmallView.setCardDefinitionView(cardSources.get(cardid).definition);
            } else {
                uiCardSmallView.setCardBackView();
            }
        };

        uiBigViewPanel = panel.addChild(new UIPanel(170,10,-10,-10));
        uiCardBigView = uiBigViewPanel.addChild(new UICardView(65,45,1f,UILayer.INTERFACE));
        uiBigViewPanel.setEnabled(false);

        panel.addChild(new UIButton(10, -220,150,210,"", () -> {
            uiBigViewPanel.setEnabled(!uiBigViewPanel.isEnabled());
            uiCardBigView.setCardView(uiCardSmallView.card,true);
        }).setTransparent(true));

        //putCardSource(new CardSource(new CardDefinition(0, "Monkey House", "Abode", "+1 shelter", "Not to be confused with Ape Trailer Home", "monkeyhouse.png")));
        nextCardID = cardSources.size();
    }

    public void uiActionClearLibrary(){
        rootPanel.addChild(new UIModal(UIModal.MODAL_YES_NO, "Clearing the library will discard any unsaved data\nand fuck anyone currently using the editor.\nContinue?",this::clearLibrary));
    }

    private int m(int i){
        return 10+40*i;
    }
}
