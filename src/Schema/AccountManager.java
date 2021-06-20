package Schema;

import Gamestate.Account;
import Globals.GlobalEnvironment;
import Globals.Style;
import UI.UIButton;
import UI.UIListMultibox;
import UI.UIPanel;
import network.NetSerializerUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static Globals.GlobalEnvironment.asyncIOHandler;

public class AccountManager extends VersionedSerializable{
    private static final int SCHEMA_VERSION_NUMBER = 1;

    @SchemaEditable
    private ArrayList<Account> accounts;

    public static final String FILE_NAME = "data/server/accountdb.bs";

    public AccountManager() {
        super();
        accounts = new ArrayList<>();
    }

    public AccountManager(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
        deserialize(dis);
    }

    public Account getAccountByID(int id){
        return accounts.get(id);
    }

    public Account getAccountByName(String accountName){
        for(Account a:accounts){
            if(a.accountName.equals(accountName))
                return a;
        }
        return null;
    }

    public Account createAccount(String accountName){
        if(getAccountByName(accountName)!=null)
            throw new RuntimeException("Account already exists with name '"+accountName+"'");
        accounts.add(new Account(accounts.size(), accountName));
        saveOut();
        uiAccountList.refreshList();
        return accounts.get(accounts.size()-1);
    }

    @Override
    public void serialize(DataOutputStream dos) throws IOException {
        super.serialize(dos);
        NetSerializerUtils.serializeArrayList(accounts, dos);
    }

    // ==== UI ==== //

    UIPanel rootPanel;
    UIListMultibox<Account> uiAccountList;

    public void setupControlPanel(UIPanel panel) {
        rootPanel = panel;
        uiAccountList = panel.addChild(new UIListMultibox<Account>(170, 10, -10, -10, accounts, (a)->{
            return String.format("%4d|%-15s",a.accountUID, a.accountName);
        }));
        uiAccountList.setRowHeight(25).setFontFamily(Style.F_CODE);
        panel.addChild(new UIButton(10, m(0), 150, 30, "Save Account DB", this::saveOut));
        panel.addChild(new UIButton(10, m(1), 150, 30, "Edit Account", this::uiActionEditSelectedAccount));

    }

    private int m(int i){
        return 10+40*i;
    }

    @Override
    public int getVersionNumber() {
        return SCHEMA_VERSION_NUMBER;
    }

    @Override
    public int getSchemaType() {
        return SchemaTypeID.ACCOUNT_MANAGER;
    }

    @Override
    protected void deserializeFromVersion(DataInputStream dis, int dataVersion) throws VersionMismatchException, IOException {
        if(dataVersion != getVersionNumber())
            throw new VersionMismatchException(dataVersion,getVersionNumber(),getSchemaType());
        accounts = new ArrayList<>();
        NetSerializerUtils.deserializeArrayList(accounts, dis, Account::new);
    }

    public void saveOut(){
        asyncIOHandler.requestSave(this, FILE_NAME);
    }

    public void uiActionEditSelectedAccount(){
        if(uiAccountList.getSelectedObject()!=null)
            GlobalEnvironment.openSchema(uiAccountList.getSelectedObject(),false);
    }
}
