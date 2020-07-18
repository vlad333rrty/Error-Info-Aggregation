package com.rest;

import com.data.IError;
import com.data.IGroup;
import com.modules.IErrorLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorLoader implements IErrorLoader{
    public static final String PATH="save";

    /**
     *
     * @return A list of groups read from the file
     * @throws ServerException
     */

    @Override
    public List<IGroup> load() throws ServerException {
        File file=new File(PATH);
        List<IGroup> res=new ArrayList<>();
        if (file.exists()) {
            for (File dir : file.listFiles()) {
                IGroup g = new GroupByName(dir.getName());
                fillGroup(g, dir);
                res.add(g);
            }
        }
        return res;
    }

    /**
     *
     * @param groups A list of groups which determine data to be read
     * @return A list of groups read from the file
     * @throws ServerException
     */
    @Override
    public List<IGroup> load(List<IGroup> groups) throws ServerException {
        File file=new File(PATH);
        List<IGroup> res=new ArrayList<>();
        if (file.exists()) {
            for (IGroup g : groups) {
                IGroup group = new GroupByName(g.getGroupingFieldValue());
                for (File dir : file.listFiles()) {
                    if (dir.getName().equals(g.getGroupingFieldValue())) {
                        fillGroup(group, dir);
                        res.add(group);
                        break;
                    }
                }
            }
        }
        return res;
    }

    /**
     *
     * @param g Group to be filled
     * @param dir Working directory
     * @throws ServerException
     */
    private void fillGroup(IGroup g,File dir) throws ServerException{
        for (File f:dir.listFiles()){
            try {
                String s="";
                while (s.isEmpty()){
                    StringBuilder builder=new StringBuilder();
                    BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                    String line;
                    while ((line=br.readLine())!=null){
                        builder.append(line).append("\n");
                    }
                    s=builder.toString();
                }
                Pattern p=Pattern.compile("duplicates: (\\d+)");
                Matcher m=p.matcher(s);
                int duplicates;
                if (m.find()) {
                    duplicates=Integer.parseInt(m.group(1));
                }else {
                    throw new ServerException(new IOException("Unexpected error"));
                }
                IError e=ErrorParser.parse(s.replace(m.group(),""));
                g.add(e,duplicates);
            }catch (IOException e){
                throw new ServerException(e);
            }
        }
    }
}
