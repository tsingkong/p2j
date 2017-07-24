package p2j.file.access;

import java.io.File;
import java.util.ArrayList;

public class FileList {
    public ArrayList<String> getFiles(String path){
        ArrayList<String> array_list = new ArrayList<String>();
        File directory = new File(path);
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if(file.isDirectory()) {
                    array_list.add(file.getName());
                }
            }
            for(File file : files){

                if(file.isFile()){
                    if(file.getName().endsWith(".pdf")||file.getName().endsWith(".jpg")) {
                        array_list.add(file.getName());
                    }
                }
            }
            return array_list;
        }else{
            return null;
        }
    }

    public String[] toArray(ArrayList<String> list){
        String[] array = new String[list.size()];
        for(int i =0;i<list.size();i++){
            array[i]=list.get(i);
        }
        return array;
    }


    public void deleteFile(String path){
        File dir_or_folder = new File(path);
        if(dir_or_folder.isDirectory()){
            for(File file:dir_or_folder.listFiles()){
                file.delete();
            }
            dir_or_folder.delete();
        }else{
            dir_or_folder.delete();
        }
    }

    public String upDirection(String path){
        String[] path_array = path.split("/");
        StringBuilder path_builder = new StringBuilder();
        path_builder.delete(0,path_builder.length());
        for(int i=1;i<path_array.length-1;i++){
            path_builder.append("/"+path_array[i]);
        }
        return path_builder.toString();
    }
}
