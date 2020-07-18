package com.rest;

import com.data.IGroup;
import com.data.IError;
import com.modules.IErrorLoader;
import com.modules.IErrorWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ErrorWriter implements IErrorWriter {
    public static final String PATH="save";
    private final IErrorLoader loader=new ErrorLoader();

    /**
     *
     * @param groups Groups to be stored
     * @throws ServerException
     */
    @Override
    synchronized
    public void write(List<IGroup> groups) throws ServerException {
        File file=new File(PATH);
        if (!file.exists()) file.mkdir();
        try{
            List<IGroup> loaded=mergeGroups(groups);
            for (IGroup g:loaded){
                String path=String.format("%s/%s",PATH,g.getGroupingFieldValue());
                File f=new File(path);
                if (!f.exists()) f.mkdir();
                int i=0;
                for (IError e:g){
                    File fileToWrite=new File(String.format("%s/%d.%s",path,i++,"txt"));
                    if (!fileToWrite.exists()) fileToWrite.createNewFile();
                    FileWriter writer=new FileWriter(fileToWrite);
                    for (String field :e.getFields()){
                        writer.write(String.format("%s: %s\n",field,e.getFieldValue(field)));
                    }
                    writer.write(String.format("duplicates: %d\n",g.getDuplicatesNumber(e)));
                    writer.flush();
                    writer.close();
                }
            }
        }catch (IOException e){
            throw new ServerException(e);
        }

    }

    /**
     * Merges the given list of groups with a loaded one
     * @param groups Groups to be merged with loaded groups
     * @return result og merging
     * @throws ServerException
     */
    private List<IGroup> mergeGroups(List<IGroup> groups) throws ServerException {
        List<IGroup> loaded=loader.load(groups);
        int size=loaded.size(),i;
        for (IGroup g:groups){
            for (i=0;i<size;i++){
                if (loaded.get(i).getGroupingFieldValue().equals(g.getGroupingFieldValue())){
                    loaded.set(i,g.merge(loaded.get(i)));
                    break;
                }
            }
            if (i==size) loaded.add(g);
        }
        return loaded;
    }
}
