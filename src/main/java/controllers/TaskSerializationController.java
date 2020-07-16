package controllers;

import model.Task;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;


/**
 * This class is used to read and write regex and ebnf tasks as xmlret-files
 * @author Fatih Kesikli, Philipp Jung
 */
public class TaskSerializationController
{

    /**
     * saves a task at the specified path
     * @param path path to the to be stored xmlret-file
     * @param t task to save
     */
    public void saveTaskInXML(String path, Task t) throws FileNotFoundException
    {
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(path)));
        encoder.writeObject(t);
        encoder.close();
    }

    /**
     * reads a task from the specified path
     * @param path xmlret file to read
     * @return task stored in xmlret file
     */
    public Task readTaskFromXML(String path) throws FileNotFoundException, IllegalStateException
    {
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
        Object read = decoder.readObject();
        System.out.println(read);
        //System.out.println(((Task) read).getIncludedWords());
        decoder.close();
        return (Task) read;
    }
}
