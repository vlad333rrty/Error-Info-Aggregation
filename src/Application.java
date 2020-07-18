import com.data.Event;
import com.data.IError;
import com.modules.IErrorContainer;
import com.modules.IConnectionHandler;
import com.modules.IErrorWriter;
import com.modules.IEventHandler;
import com.modules.IInfoManager;
import com.rest.ConnectionHandler;
import com.rest.ErrorContainer;
import com.rest.ErrorWriter;
import com.rest.EventHandler;
import com.rest.InfoManager;
import com.rest.LoggingManager;
import com.rest.ServerException;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private static final int HANDLERS=10;
    private final IErrorContainer container =new ErrorContainer();
    private final IErrorWriter writer =new ErrorWriter();
    private final IConnectionHandler connectionHandler =new ConnectionHandler();
    private final IInfoManager infoManager =new InfoManager();
    private final IEventHandler eventHandler =new EventHandler();
    private final List<ErrorThread> errorHandlers =new ArrayList<>();
    private final EventThread eventThread =new EventThread();

    public void start(){
        connectionHandler.start();
        for (int i=0;i<HANDLERS;i++) errorHandlers.add(new ErrorThread());
        run();
        LoggingManager.logger.info("Application started");
    }

    public void close(){
        try{
            for (ErrorThread errorThread: errorHandlers){
                errorThread.join();
            }
            eventThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        connectionHandler.close();
        LoggingManager.logger.info("Application closed");
    }

    private void run(){
        for (ErrorThread errorThread: errorHandlers){
            errorThread.start();
        }
        eventThread.start();
    }

    private void stop(){
        for (ErrorThread errorThread: errorHandlers){
            errorThread.interrupt();
        }
        eventThread.interrupt();
    }

    private class ErrorThread extends Thread{

        @Override
        public void run() {
            while (!Thread.interrupted()){
                IError error= connectionHandler.getError();
                if (error!=null){
                    try{
                        container.add(error);
                        connectionHandler.setResult(error,null);
                        if (container.shouldBeUnloaded()) writer.write(container.retrieveGroups());
                    }catch (ServerException e){
                        connectionHandler.setResult(error,e);
                    }
                }
            }
        }
    }

    private class EventThread extends Thread{
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Event event= eventHandler.getEvent();
                    switch (event.type){
                        case EXIT:
                            writer.write(container.retrieveGroups());
                            Application.this.stop();
                            break;
                        default:
                            writer.write(container.retrieveGroups());
                            infoManager.displayInfo(event);
                    }
                } catch (ServerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}