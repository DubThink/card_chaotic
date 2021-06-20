package UI;

import Gamestate.CardDefinition;
import Gamestate.ClientGamestate;
import Globals.Assert;
import Globals.GlobalEnvironment;
import Globals.Style;
import Schema.SchemaEditOptIn;
import Schema.SchemaEditable;
import core.*;
import network.event.GrantCardIDNetEvent;
import network.event.RequestCardIDNetEvent;
import network.event.UpdateCardDefinitionNetEvent;
import processing.core.PConstants;

import static Client.ClientEnvironment.cardDefinitionManager;
import static Client.ClientEnvironment.netClient;
import static Globals.GlobalEnvironment.DEV_MODE;

@SchemaEditOptIn
public class UICardEditor extends UIPanel{
    private final UICardView cardView;
    private final UIBase editPanel;
    private final UIBase filePanel;
    @SchemaEditable
    private CardDefinition definition;
    private final UILabel imgStatus;

    private final UITextBox tbID;
    private final UITextBox tbName;
    private final UITextBox tbType;
    private final UITextBox tbImageName;
    private final UITextBox tbDescription;
    private final UITextBox tbFlavor;
    private final UITextBox tbAttack;
    private final UITextBox tbHealth;
    private final UIMultibox mbArchetypeBox;

    UIListMultibox<CardDefinition> cardDefinitions;

    private UIModal waitingModal;

    abstract class ValNotify implements UIUpdateNotify<UITextBox>{
        @Override
        public void notify(UITextBox source) {
            if(definition!=null){
                updateVal(source.getText());
            }
        }

        public abstract void updateVal(String s);
    }

    public UICardEditor(UIBase sz/*hack for size*/) {
        super(0,0, 0,0);

        cardView = addChild(new UICardView(20,20, 1, UILayer.FIELD));
        cardView.previewMode=true;
        cardView.centerAt(sz.cw/4,sz.ch/2);

        editPanel = addChild(new UIPanel(sz.cw/2,0,0,-200));
        this.setNavRoot(true);

        filePanel = addChild(new UIPanel(sz.cw/2,-200,0,0));

        final int colw=110;
        final int col1=(10+colw);
        final int col2=(10+colw)*2;
        final int col3=(10+colw)*3;
        final int col4=(10+colw)*4;
        // edit panel
        int pos=3;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"ID #").setBigLabel(true));
        tbID = editPanel.addChild(new UITextBox(col1,m(pos),60,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setEditable(false));
        pos++;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Name").setBigLabel(true));
        tbName = editPanel.addChild(new UITextBox(col1,m(pos),-80,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.name=s;
                    }
                }));

        pos++;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Type").setBigLabel(true));
        tbType = editPanel.addChild(new UITextBox(col1,m(pos),-80,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.type=s;
                    }
                }));

        pos++;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Image name").setBigLabel(true));
        tbImageName = editPanel.addChild(new UITextBox(col1,m(pos),-110,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        // force reload
                        GlobalEnvironment.imageLoader.uncacheCardImage(s);
                        definition.invalidateBase();
                        definition.setLocalImageSource(s);

                        imgStatus.setText(GlobalEnvironment.imageLoader.isCardImageValid(s) ?
                                AdvancedApplet.hyperText("/["):AdvancedApplet.hyperText("/]"));
                    }
                }));
        imgStatus = editPanel.addChild(
                new UILabel(-140,m(pos),30,30,AdvancedApplet.hyperText("/]"))
                        .setBigLabel(true)
                        .setJustify(PConstants.CENTER));

        editPanel.addChild(new UIButton(-110,m(pos),30,30,AdvancedApplet.hyperText("/{"),()->GlobalEnvironment.imageLoader.launchCardFolder()));

        pos++;
        editPanel.addChild(new UIButton(col1,m(pos),colw,30,"Small View", () -> definition.setCropCenteredSmall()));
        editPanel.addChild(new UIButton(col2,m(pos),colw,30,"Square View", () -> definition.setCropCenteredSquare()));
        editPanel.addChild(new UIButton(col3,m(pos),colw,30,"Full View", () -> definition.setCropCenteredFull()));

        pos++;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Description").setBigLabel(true));
        tbDescription = editPanel.addChild(new UITextBox(col1,m(pos),-80,150, false)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.desc= AdvancedApplet.hyperText(s);
                    }
                }));

        pos+=4;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Flavor").setBigLabel(true));
        tbFlavor = editPanel.addChild(new UITextBox(col1,m(pos),-80,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.flavor=s;
                    }
                }));


        pos++;

        // right col ----------
        int pos2=pos;
        editPanel.addChild(new UILabel(col3,m(pos2),colw,30,"Attack").setBigLabel(true));
        tbAttack = editPanel.addChild(new UITextBox(col4,m(pos2),colw,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setEditable(false)
                .setText("0")
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        try {
                            definition.attackDefaultValue = Integer.decode(s);
                        } catch (NumberFormatException ignored){};                    }
                }));

        pos2++;
        editPanel.addChild(new UILabel(col3,m(pos2),colw,30,"Health").setBigLabel(true));
        tbHealth = editPanel.addChild(new UITextBox(col4,m(pos2),colw,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setEditable(false)
                .setText("0")
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        try {
                            definition.healthDefaultValue = Integer.decode(s);
                        } catch (NumberFormatException ignored){};
                    }
                }));
        // right col end ----------



        //editPanel.addChild(new UIButton(col1,m(pos),colw,30,"Is Being", () -> definition.isBeing=true,() -> definition.isBeing=false,true));
        mbArchetypeBox = editPanel.addChild(new UIMultibox(col1, m(pos), colw, 30 * 4,
                source -> {
                    int idx = source.getSelectionIndex();
                    definition.archetype = idx;
                    tbHealth.setEditable(idx == CardDefinition.ARCHETYPE_BEING);
                    tbAttack.setEditable(idx == CardDefinition.ARCHETYPE_BEING);
                }));
        Assert.equals(CardDefinition._ARCHETYPE_COUNT,4);

        for (int i = 0; i < CardDefinition._ARCHETYPE_COUNT; i++) {
            mbArchetypeBox.addOption(CardDefinition.getArchetypeName(i));
        }

        // file panel
        pos=0;
        filePanel.addChild(new UIButton(10,m(pos),colw*2,30,"New Card", this::actionNewCard));
        pos++;
        filePanel.addChild(new UIButton(10,m(pos),colw*2,30,"Save Card", this::actionSaveCard));
        pos++;
        filePanel.addChild(new UIButton(10,m(pos),colw*2,30,"Edit Card", this::actionStartEditCard));
        pos++;
//        filePanel.addChild(new UIButton(10,m(pos),colw*2,30,"Reload Card", () -> netClient.sendEvent(new ImageNetEvent(GlobalEnvironment.imageLoader.getCardImage(definition.imageFileName)))));

        //modal(UIModal.MODAL_CONTINUE, "Create new card", this::requestNewCard);
        pos=0;
        cardDefinitions = filePanel.addChild(new UIListMultibox<CardDefinition>(240,m(pos),300,150,cardDefinitionManager.getCardList(),data -> data.name));
    }

    protected void actionStartEditCard(){
        CardDefinition definition = cardDefinitions.getSelectedObject();
        if(definition!=null){
            if(definition.authorAccountUID != ClientGamestate.accountUID && !DEV_MODE) {
                modal(UIModal.MODAL_CONTINUE,"You do not have edit permission on this card.");
            } else {
                modal(UIModal.MODAL_YES_NO, "This will destroy all your\ncurrent progress. Continue?",this::completeEditCard);

            }
        }
    }

    protected void completeEditCard(){
        CardDefinition definition = cardDefinitions.getSelectedObject();
        if(definition!=null && (definition.authorAccountUID == ClientGamestate.accountUID || DEV_MODE)) {
            this.definition = definition;
            refreshEditor();
        }
    }

    protected void actionNewCard(){
        modal(UIModal.MODAL_YES_NO, "This will destroy all your\ncurrent progress. Continue?",this::requestNewCard);
    }

    protected void actionSaveCard(){
        if(!netClient.isReady()){
            modal(UIModal.MODAL_CONTINUE,"Not connected");
        } else if(definition.uid==-1){
            modal(UIModal.MODAL_CONTINUE, "Card was created offline,\nand cannot be saved.");
        } else if(definition.getSourceImage()==GlobalEnvironment.imageLoader.nullimg){ // !GlobalEnvironment.imageLoader.isCardImageValid(definition.localSourceImageFilename)
            modal(UIModal.MODAL_CONTINUE, "Image must be valid to save.");
        } else if (definition.validateDefinition()!=null){
            modal(UIModal.MODAL_CONTINUE, "Cannot save:\n"+definition.validateDefinition());
        } else {
            netClient.sendEvent(new UpdateCardDefinitionNetEvent(definition));
        }
    }

    protected void requestNewCard(){
        if(netClient == null || !netClient.isReady()){
            modal(UIModal.MODAL_CONTINUE, "You are offline. New cards\ncreated offline cannot be saved,\neven once connected.",()->createNewCard(-1));
            createNewCard(-1);
            return;
        }
        waitingModal=modal(UIModal.MODAL_INFO_ONLY,"Requesting new card id...");
        netClient.sendEvent(new RequestCardIDNetEvent());
    }

    public void createNewCard(int cardUID){
        definition=new CardDefinition(cardUID, ClientGamestate.accountUID);
        refreshEditor();
    }

    public void handleNetEvent(GrantCardIDNetEvent event){
        createNewCard(event.id);
        waitingModal.closePositive();
    }

    private void refreshEditor(){
        tbID.setText(""+definition.uid);
        tbName.setText(definition.name);
        tbType.setText(definition.type);
        if(definition.localSourceImageFilename!=null)
            tbImageName.setText(definition.localSourceImageFilename);
        tbDescription.setText(definition.desc);
        tbFlavor.setText(definition.flavor);
        tbAttack.setText(""+definition.attackDefaultValue);
        tbHealth.setText(""+definition.healthDefaultValue);
        mbArchetypeBox.selection=definition.archetype;
        cardView.setCardDefinitionView(definition);
    }

    private int m(int pos){
        return 10+40*pos;
    }

    private UIModal modal(int type, String message) {
        return modal(type,message,null,null);
    }

    private UIModal modal(int type, String message, Action positive) {
        return modal(type, message, positive,null);
    }

    private UIModal modal(int type, String message, Action positive, Action negative){
        return addChild(new UIModal(type,message,positive,negative));
    }

}
