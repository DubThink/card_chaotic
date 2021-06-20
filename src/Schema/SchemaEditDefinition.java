package Schema;

import Server.CardSource;
import UI.*;
import network.NetSerializable;
import processing.core.PConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class SchemaEditDefinition {
    Object data;
    UIPanel root;

    public OpenSchema openSchema;

    public static interface OpenSchema {
        void open(Object schema, boolean readonly, OpenSchema openSchema);
    }

    public SchemaEditDefinition(Object data, UIPanel root, boolean dataReadOnly, OpenSchema openSchema) {
        this.data = data;
        this.root = root;
        boolean schemaOptIn = data.getClass().getAnnotation(SchemaEditOptIn.class)!=null;

        boolean validData = data instanceof NetSerializable ||
                data.getClass().getAnnotation(SchemaEditable.class)!=null ||
                schemaOptIn;

        if(!validData){
            System.err.println("Invalid data "+data+" for Schema Edit");
            return;
        }

        Field[] fields = data.getClass().getDeclaredFields();

        boolean allowProtected = data.getClass().getAnnotation(SchemaAllowProtectedEdit.class)!=null;

        // build UI
        int pos=0;


        for(Field f:fields){
      //      System.out.println(f);
            int mods = f.getModifiers();
            Class<?> targetType = f.getType();
            boolean fieldMarkedEditable = f.getAnnotation(SchemaEditable.class)!=null;
            boolean fieldVisible = fieldMarkedEditable || Modifier.isPublic(mods) || (allowProtected&&!Modifier.isPrivate(mods));
            // ignore fields that aren't opted in if we're in opt in mode
            if(schemaOptIn && !fieldMarkedEditable)
                continue;
            if(fieldVisible && !Modifier.isStatic(mods)) {
                f.setAccessible(true);

                root.addChild(new UILabel(10, m(pos), 250, 30, f.getName()))
                        .setJustify(PConstants.RIGHT)
                        .setBigLabel(true)
                        .addTooltip(f.toString());

                Object ob;
                try {
                    ob = f.get(data);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                boolean readOnly = dataReadOnly || Modifier.isFinal(mods) || f.getAnnotation(SchemaEditViewOnly.class)!=null;

                boolean editableSchema = ob instanceof NetSerializable|| f.getAnnotation(SchemaEditable.class)!=null;

                if(ob instanceof ArrayList) {
                    // do list shit here
                    UIListMultibox<Object> lmb=root.addChild(new UIListMultibox<Object>(270,m(pos),-10,150,(ArrayList<Object>)ob,data1 -> data1==null?"null":data1.toString()));
                    root.addChild(new UIButton(140,m(pos+1),120,30, "Open", () -> openSchema.open(lmb.getSelectedObject(), readOnly, openSchema)));
                    pos+=4;
                } else if(ob instanceof Boolean) {
                    root.addChild(new UICheckbox(270, m(pos), 30))
                            .set((Boolean)ob)
                            .setNotify(source -> safeSet(f,source.get()))
                            .setInteractable(!readOnly);
                    pos++;
                } else if(editableSchema && openSchema != null) {
                    UITextBox field = root.addChild(new UITextBox(270, m(pos), -140, 30, true))
                            .setEditable(false)
                            .setText(ob==null?"null":ob.toString());

                    root.addChild(new UIButton(-130,m(pos),-10,30, "Open", () -> openSchema.open(ob, readOnly, openSchema)));
                    pos++;

                } else {
                    UITextBox field = root.addChild(new UITextBox(270, m(pos), -10, 30, true))
                            .setEditable(false)
                            .setText(ob==null?"null":ob.toString());

                    if(!readOnly) {
                        if (ob instanceof Integer) {
                            field.setEditable(true).setTextUpdatedCallback(source -> {
                                try {
                                    int i = Integer.decode(source.getText());
                                    safeSet(f, i);
                                } catch (NumberFormatException ignored) {
                                }
                            });
                        } else if (ob instanceof Float) {
                            field.setEditable(true).setTextUpdatedCallback(source -> {
                                try {
                                    float i = Float.parseFloat(source.getText());
                                    safeSet(f, i);
                                } catch (NumberFormatException ignored) {
                                }
                            });
                        }else if (ob instanceof Double) {
                            field.setEditable(true).setTextUpdatedCallback(source -> {
                                try {
                                    double i = Double.parseDouble(source.getText());
                                    safeSet(f, i);
                                } catch (NumberFormatException ignored) {
                                }
                            });
                        } else if (ob instanceof String) {
                            field.setEditable(true).setTextUpdatedCallback(source -> safeSet(f, source.getText()) );
                        }
                    }

                    pos++;
                }
            }
        }
    }

    private void safeSet(Field f, Object val){
        try {
            f.set(data, val);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private int m(int pos){
        return pos*40+10;
    }

    void test() {

        Field[] fields = data.getClass().getDeclaredFields();

        for(Field f:fields){
            System.out.println(f);
        }
    }

    public static void main(String[] args) {
        CardSource s = DiskUtil.tryToLoadFromFileTyped(CardSource.class, "C:\\devspace\\doxo\\data\\server\\cards/card_0.card");
        //AccountManager s = DiskUtil.tryToLoadFromFileTyped(AccountManager.class, "C:\\devspace\\doxo\\data\\server/accountdb.bs");
        //chemaEditDefinition sed = new SchemaEditDefinition(s);
        //sed.test();
    }
}
