package com.company;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.concurrent.*;
/**
 * Collection, written to manage prisoners in jail. But it is so good, that I decided to make it generic and publish.
 *
 * @author Pavel Suprun
 *
 * @param <T> Type of elements. Must implement Comparable
 *
 */
public class Prisoners <T extends Comparable<T>> {
    private LinkedBlockingDeque<T> elements = new LinkedBlockingDeque<T>();
    //private LinkedList<T> elements = new LinkedList<T>();
    Date date = new Date();

    /**
     * Return int value of the size of the collection
     * @return size of collection
     */
    public int size () { return elements.size(); }

    /**
     * Delete element from collection by its value
     * @param el Element to remove
     * @return true if any elements were removed
     */
    public boolean remove (T el) {
        if (elements.remove(el)) {
            System.out.println(el.toString() + " - removed");
            return true;
        }
        System.out.println("Nothing was removed");
        return false;
    }

    /**
     * Print all elements of the collection by its String representation to stdout
     */
    public void show () {
        if (elements.size()==0)
            System.out.println("No elements");
        for (T el: elements)
            System.out.println(el);
    }

    /**
     * Add element to the collection if it will become the largest element of the collection
     * @param el Element to add and to compare with
     * @return true if element has been added, false otherwise
     */
    public boolean addIfMax (T el) {
        if (elements.size() < 1) {
            elements.add(el);
            return true;
        }
        //Collections.sort(elements);
        if (el.compareTo(elements.) > 0) {
            elements.addLast(el);
            System.out.println(el.toString()+" added to collection");
            return true;
        }
        System.out.println("Nothing was added to collection");
        return false;
    }

    /**
     * Remove all elements less than parameter element
     * @param el Element to compare with
     * @return true if any elements were removed
     */
    public boolean removeLower (T el) {
        if (elements.removeIf(p -> p.compareTo(el) < 0))
        {
            System.out.println("Elements, lower than " + el.toString() + ", removed");
            return true;
        }
        System.out.println("Nothing was removed from collection");
        return false;
    }

    /**
     * Print some information (size of collection and type of elements, if there are ones) to stdout
     */
    public void info () {
        if (elements.size()==0) {
            System.out.println("Empty collection");
            System.out.println(elements.getClass().getName() +" of "+elements.getFirst().getClass().getName() + " "+date);
            return;
        }
        System.out.printf("Collection of %d elements of type %s, %s, %s\n",
                elements.size(),
                elements.getFirst().getClass().getName(),
                elements.getClass().getName(), date
                );
    }

    /**
     * Clear the collection
     */
    public void clear () {
        System.out.println("Collection is cleared");
        elements.clear();
    }

    /**
     * Add new element to the collection
     * @param el Element to add to the collection
     * @return true if collection changed as a result of the call
     */
    public boolean add (T el) {
        if (elements.add(el)) {
            System.out.println(el.toString() + " added to collection");
            return true;
    }
        return false;
    }

    private boolean addSilent (T el) {
        if (elements.add(el)) {
            return true;
        }
        return false;
    }

    // -----------------------------------------------------------------------------------------------

    private static JSONObject getArgument (String s) {
        try { return new JSONObject(s.substring(s.indexOf('{'))); }
        catch (JSONException e) { System.out.println("Wrong definition of JSON object"); }
        // catch (StringIndexOutOfBoundsException e) { return null; }
        catch (Throwable e) { return null; }
        return null;
    }

    private static Human jsonToHuman (JSONObject obj) {
        try {
            Human h = new Human(obj.getString("name"),
                    obj.getEnum(Space.class, "currentSpace"),
                    obj.getInt("x"),
                    obj.getInt("y"));
            try {
                Resource r = new Resource(obj.getJSONObject("inHands").getString("name"));
                h.inHands = r;
            } catch (Throwable e) {}
            return h;
        } catch (JSONException e) {System.out.println("Wrong definition of JSON object"); }
        catch (Throwable e) {  }
        return null;
    }

    private static JSONObject humanToJson (Human h) {
        JSONObject obj = new JSONObject();
        obj.put("name", h.name);
        obj.put("currentSpace", h.currentSpace);
        obj.put("x",h.x);
        obj.put("y",h.y);
        if (h.inHands != null)
            obj.put("inHands", new JSONObject().put("name", h.inHands.name));
        return obj;
    }

    private static void printGuideToStdOut () {
        System.out.println();
        System.out.println("FORMAT - {element}:{\"name\":\"NAME\",\"x\":VALUE, \"y\":VALUE, \"currentSpace\":\"SPACE_NAME\" }");
        System.out.println("remove {element}: delete element from collection by its value");
        System.out.println("show: print all elements to stdout");
        System.out.println("add_if_max {element}: add element if it is larger than all elements in collection");
        System.out.println("remove_lower {element}: remove elements of collection less than element");
        System.out.println("info: print information about collection");
        System.out.println("clear: clear collection");
        System.out.println("add {element}: add element to collection");
        System.out.println("stop: exit app");
        System.out.println();
        System.out.println("Waiting for command...");
    }

    private static void pressEnterKey (Scanner in) {
        System.out.println("Press Enter to continue");
        try
        {
            in.nextLine();
        }
        catch(Exception e)
        {}
    }

    private static boolean testFilename(String fileName) {
        File test = new File(fileName);
        if (!test.exists()) try { test.createNewFile(); System.out.println("New file created"); }
        catch (IOException e) {  }
        return test.canRead() && test.canWrite();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("You have to type name of file");
            return;
        }
        String fileName = args[0];
        if (!testFilename(fileName)) {
            System.out.println("Something wrong with file");
            return;
        }
        String input = "";
        JSONObject arg;
        String firstWord = "";
        Scanner in = new Scanner(System.in);
        //JSONArray file;
        //Prisoners<Human> p;

        JSONArray file = JsonIO.readArrayFromFile(fileName);

        Prisoners<Human> p = new Prisoners<>();
        try {
            if (file != null && file.length() > 0) {
                System.out.println("Opening " + fileName + "...");
                for (Object obj : file) {
                    p.add(jsonToHuman((JSONObject) obj));
                }
                System.out.println(fileName + " opened");
            }
        } catch (Throwable e) {
            System.out.println("Something wrong with file");
            return;
        }

        while (!input.equals("stop")) {
            try {
                printGuideToStdOut();
                input = in.nextLine();
                firstWord = input.split(" ")[0];
                arg = getArgument(input);
                Human h;
                //read
                /*
                file = JsonIO.readArrayFromFile(fileName);

                p = new Prisoners<Human>();
                try {
                    if (file != null && file.length() > 0) {
                        for (Object obj : file) {
                            p.addSilent(jsonToHuman((JSONObject) obj));
                        }
                    }
                } catch (Throwable e) {
                    System.out.println("Something wrong with file");
                    return;
                }
                */
                //
                switch (firstWord) {
                    case "remove":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.remove(h);
                        break;
                    case "show":
                        p.show();
                        break;
                    case "add_if_max":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.addIfMax(h);
                        break;
                    case "remove_lower":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.removeLower(h);
                        break;
                    case "info":
                        p.info();
                        break;
                    case "clear":
                        p.clear();
                        break;
                    case "add":
                        if (arg==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        h = jsonToHuman(arg);
                        if (h==null) {
                            System.out.println("Wrong argument");
                            break;
                        }
                        p.add(h);
                        break;
                    case "stop":
                        System.out.println("Remind: you're leaving the application");
                        break;
                    default:
                        System.out.println("Unknown command");
                }

                JSONArray newFile = new JSONArray();
                if (p.size()>0) {
                    for (Human hh: p.elements) {
                        JSONObject temp = humanToJson(hh);
                        newFile = newFile.put(temp);
                    }
                }
                JsonIO.writeToFile(newFile, fileName);
                pressEnterKey(in);
                //input = in.next();
            }
            catch (Throwable e) {
                System.out.println("Something went wrong");

            }


        }
    }
}
