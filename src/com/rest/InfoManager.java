package com.rest;

import com.data.Event;
import com.data.IError;
import com.data.IGroup;
import com.modules.IErrorLoader;
import com.modules.IInfoManager;
import java.util.List;

public class InfoManager implements IInfoManager {
    private final IErrorLoader loader=new ErrorLoader();
    @Override
    public void displayInfo(Event event) throws ServerException {
        switch (event.type){
            case GET_INFO :
                List<IGroup> groups=loader.load();
                for (IGroup g:groups){
                    System.out.format("Error: %s\nNumber: %d\n",g.getGroupingFieldValue(),g.size());
                }
                if (groups.size()==0) System.out.println("There is now info to show");
                break;
            case GET_ERRORS:
                groups=loader.load();
                for (IGroup g:groups){
                    System.out.format("%s: (error number: %d)\n",g.getGroupingFieldValue(),g.size());
                    for (IError e:g){
                        System.out.println(e);
                        System.out.format("duplicates: %d\n\n",g.getDuplicatesNumber(e));
                    }
                }
                if (groups.size()==0) System.out.println("There is no info to show");
                break;
            default: throw new ServerException(new IllegalArgumentException("Wrong event"));
        }
    }
}
