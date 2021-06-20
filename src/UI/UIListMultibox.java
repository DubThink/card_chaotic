package UI;

import aew.Util;
import core.AdvancedApplet;

import java.util.ArrayList;

public class UIListMultibox<T> extends UIMultibox{
    protected ArrayList<T> list;
    protected TextBuilder<T> builder;
    public UIUpdateNotify<UIListMultibox<T>> listSelectionChangedAction;


    public UIListMultibox(int x, int y, int w, int h, ArrayList<T> list, TextBuilder<T> builder) {
        super(x, y, w, h, UIListMultibox::notifySelectionChanged);
        this.list=list;
        this.builder = builder;
        refreshList();
    }

    public UIListMultibox(int x, int y, int w, int h, ArrayList<T> list, TextBuilder<T> builder, UIUpdateNotify<UIListMultibox<T>> action) {
        this(x, y, w, h, list, builder);
        listSelectionChangedAction = action;
    }

    @Override
    protected void _draw(AdvancedApplet p) {
        if(options.size()!=list.size())
            refreshList();
        else
            refreshIndex(selection);
        super._draw(p);
    }

    private static <T> void notifySelectionChanged(UIMultibox e){
        UIListMultibox<T> lmb = (UIListMultibox<T>)e;
        //lmb.refreshIndex(e.getSelectionIndex());
        if(lmb.listSelectionChangedAction!=null)
            lmb.listSelectionChangedAction.notify(lmb);
    }

    public T getSelectedObject() {
        if(list.isEmpty())
            return null;
        return list.get(selection);
    }

    public void refreshList(){
        options.clear();
        selection = Util.max(0,Util.min(selection,list.size()-1));
        for (T t : list) {
            options.add(builder.build(t));

        }
    }

    public void refreshIndex(int i){
        if(i<0||i>=list.size())
            return;
        if(options.size()<=i)
            options.add(i,builder.build(list.get(i)));
        else
            options.set(i,builder.build(list.get(i)));
    }
}
