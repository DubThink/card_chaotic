package UI;

import Gamestate.CardDefinition;
import Globals.Assert;
import Globals.GlobalEnvironment;
import Globals.Style;
import core.AdvancedApplet;
import core.ImageLoader;
import processing.core.PConstants;

import java.awt.*;

public class UICardEditor extends UIPanel{
    private UICardView cardView;
    private UIBase editPanel;
    private CardDefinition definition;
    private UILabel imgStatus;


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

        clearCard();

        editPanel = addChild(new UIPanel(sz.cw/2,0,0,0));
        editPanel.setNavRoot(true);

        final int colw=110;
        final int col1=(10+colw);
        final int col2=(10+colw)*2;
        final int col3=(10+colw)*3;
        final int col4=(10+colw)*4;
        // edit panel
        int pos=4;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Name").setBigLabel(true));
        editPanel.addChild(new UITextBox(col1,m(pos),-80,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.name=s;
                    }
                }));

        pos++;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Type").setBigLabel(true));
        editPanel.addChild(new UITextBox(col1,m(pos),-80,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.type=s;
                    }
                }));

        pos++;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Image name").setBigLabel(true));
        editPanel.addChild(new UITextBox(col1,m(pos),-110,30,true)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.imageFileName=s;
                        // force reload
                        GlobalEnvironment.imageLoader.uncacheCardImage(s);
                        definition.invalidateBase();

                        imgStatus.setText(GlobalEnvironment.imageLoader.isCardImageValid(s) ?
                                AdvancedApplet.hyperText("/]"):AdvancedApplet.hyperText("/["));
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
        editPanel.addChild(new UITextBox(col1,m(pos),-80,150, false)
                .setFontSize(Style.FONT_MEDIUM)
                .setTextUpdatedCallback(new ValNotify() {
                    @Override
                    public void updateVal(String s) {
                        definition.desc= AdvancedApplet.hyperText(s);
                    }
                }));

        pos+=4;
        editPanel.addChild(new UILabel(10,m(pos),colw,30,"Flavor").setBigLabel(true));
        editPanel.addChild(new UITextBox(col1,m(pos),-80,30,true)
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
        UITextBox fieldAttack = editPanel.addChild(new UITextBox(col4,m(pos2),colw,30,true)
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
        UITextBox fieldHealth = editPanel.addChild(new UITextBox(col4,m(pos2),colw,30,true)
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
        UIMultibox archetypeBox = editPanel.addChild(new UIMultibox(col1, m(pos), colw, 30 * 4,
                source -> {
                    int idx = source.getSelectionIndex();
                    definition.archetype = idx;
                    fieldHealth.setEditable(idx == CardDefinition.ARCHETYPE_BEING);
                    fieldAttack.setEditable(idx == CardDefinition.ARCHETYPE_BEING);
                }));
        Assert.equals(CardDefinition._ARCHETYPE_COUNT,4);

        for (int i = 0; i < CardDefinition._ARCHETYPE_COUNT; i++) {
            archetypeBox.addOption(CardDefinition.getArchetypeName(i));
        }



    }

    public void clearCard(){
        definition=new CardDefinition(-1,"","","","","");
        cardView.setCardDefinitionView(definition);
    }

    private int m(int pos){
        return 10+40*pos;
    }

}
