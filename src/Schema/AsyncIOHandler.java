package Schema;

import Globals.Debug;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class AsyncIOHandler extends Thread {
    BlockingQueue<SaveRequest> saveRequests;

    public AsyncIOHandler() {
        saveRequests=new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while (true)
        {
            while (!saveRequests.isEmpty()){
                // do stuff
                SaveRequest rq = saveRequests.poll();
                DiskUtil.saveToFile(rq.data, rq.targetFile);
                System.out.println("Completed save rq time="+ Debug.perfTimeMS()+" fname='"+rq.targetFile+"'");

            }
            try {
                synchronized (this) {
                    System.out.println("asyncIO idling...");
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void requestSave(VersionedSerializable data, String fname){
        saveRequests.add(new SaveRequest(fname,data));
        System.out.println("Submitting save rq time="+ Debug.perfTimeMS()+" fname='"+fname+"'");
        notify();
    }
}
