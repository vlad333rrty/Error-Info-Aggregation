import com.data.Event;
import com.data.IError;
import com.modules.*;
import com.rest.ErrorReader;

public class Application {
    private IErrorContainer ec;
    private IErrorWriter ew;
    private IErrorReader er=new ErrorReader();
    private IInfoManager im;
    private IEventHandler eh;
    public Application(){

    }

    public void start(){
        ErrorReader err=(ErrorReader)er;
        err.start();
        run();
    }

    private void run(){
        while (true){
            IError error=er.getError();
            if (error!=null){
                System.out.println(error);
//                ec.add(error);
            }
//            Event event=eh.getEvent();
//            if (event!=null){
//                switch (event.getType()){
//                    case GET_INFO :
//                        im.output(event);
//                        break;
//                    default: throw new IllegalArgumentException("Unknown command type of " + event);
//                }
//            }
        }
    }
}