package Schema;

import Gamestate.Account;
import Globals.Style;
import UI.UIListMultibox;
import UI.UIMultibox;
import UI.UIPanel;
import network.NetSerializerUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static Globals.GlobalEnvironment.asyncIOHandler;

public class AccountManager extends VersionedSerializable{
    private static final int SCHEMA_VERSION_NUMBER = 1;

    private ArrayList<Account> accounts;

    public static final String FILE_NAME = "data/server/accountdb.bs";

    public AccountManager() {
        super();
        accounts = new ArrayList<>();
    }

    public AccountManager(DataInputStream dis) throws VersionMismatchException, IOException {
        super(dis);
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
        throw new VersionMismatchException(dataVersion,getVersionNumber(),getSchemaType());
    }

    @Override
    protected void deserialize(DataInputStream dis) throws IOException {
        accounts = new ArrayList<>();
        NetSerializerUtils.deserializeArrayList(accounts, dis, Account::new);
    }

    public void saveOut(){
        asyncIOHandler.requestSave(this, FILE_NAME);
    }

}
