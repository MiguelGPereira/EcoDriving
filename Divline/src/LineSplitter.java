import com.sun.tools.javac.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by miguelpereira on 15/05/15.
 */
public class LineSplitter {

    public static LinkedHashMap<String, Float> estacoes_pk = new LinkedHashMap<String, Float>();
    public static LinkedHashMap<Pair<Float,Float>, Float> limits = new LinkedHashMap<Pair<Float, Float>, Float>();
    public static LinkedHashMap<Pair<Float,Float>, Float> gradients = new LinkedHashMap<Pair<Float, Float>, Float>();
    public static ArrayList<Pair<Float,Float>> divisions = new ArrayList<Pair<Float,Float>>();

    public static void getSplittedLine(){

        read('L', "Limit.csv");
        read('S',"Station.csv");
        read('G',"Gradient.csv");

        /*Test Example
        limits.put(new Pair<Float, Float>(0f,3f),30f);
        limits.put(new Pair<Float, Float>(3f,6f),60f);
        gradients.put(new Pair<Float, Float>(0f,2f),11f);
        gradients.put(new Pair<Float, Float>(2f,6f),10f);

        float []stations = {0f,4f,6f};
        */

        //get array of stations pks
        Float[] stations = new Float[estacoes_pk.values().size()];
        stations = estacoes_pk.values().toArray(stations);

        merge(stations, new LinkedHashMap<Pair<Float, Float>, Float>(limits));

        //convert divisions into pks and clear divisions
        ArrayList<Float> divisionsArray = new ArrayList<Float>();
        for (int i = 0; i < divisions.size(); i++) {
            if(i==0)
                divisionsArray.add(divisions.get(i).fst);
            divisionsArray.add(divisions.get(i).snd);
        }
        Float []stations2 = new Float[divisionsArray.size()];
        stations2 = divisionsArray.toArray(stations2);
        divisions.clear();
        //end convert...


        merge(stations2, new LinkedHashMap<Pair<Float, Float>, Float>(gradients));

        //output
        addSpecs();

    }

    /**
     * Add limits and gradients columns
     */
    public static void addSpecs(){
        System.out.println("start pk;end pk;gradient;limit");
        Iterator it = divisions.iterator();
        while (it.hasNext()) {
            Pair<Float,Float> pair = (Pair)it.next();
            Float limit = getSpec(pair.fst, pair.snd, new LinkedHashMap<Pair<Float, Float>, Float>(limits));
            Float gradient = getSpec(pair.fst,pair.snd, new LinkedHashMap<Pair<Float, Float>, Float>(gradients));
            System.out.println(pair.fst+";"+pair.snd+";"+gradient+";"+limit);
            it.remove();
        }
    }
    private static Float getSpec(Float fstPk, Float sndPk, LinkedHashMap<Pair<Float, Float>, Float> hash){
        Float spec = -1f;
        Iterator it = hash.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry map = (HashMap.Entry) it.next();
            Pair<Float, Float> pair = (Pair) map.getKey();
            if (spec == -1f)
                spec = (Float) map.getValue();
            else if(fstPk >= pair.fst && sndPk <= pair.snd)
                spec = (Float) map.getValue();
            it.remove();
        }
        return spec;
    }

    /*
     * Merge pks with pks intervals
     */
    public static void merge(Float []stations2, LinkedHashMap<Pair<Float, Float>, Float> hash){
        Float fst, snd = 0f;// first pk and second pk
        boolean first = true;//first iteration
        for (int i = 0; i <stations2.length-1; i++) {
            //the station is the first point
            //iterate through limits
            Iterator it = hash.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry map = (HashMap.Entry)it.next();
                Pair<Float,Float> pair = (Pair)map.getKey();

                if(pair.fst >= stations2[i+1]) break;

                fst = (first) ? stations2[i] : pair.fst;
                if(fst>snd && !first)//when there is a station shift
                    divisions.add(new Pair<Float, Float>(snd, fst));
                snd = (pair.snd>=stations2[i+1]) ? stations2[i+1] : pair.snd;

                divisions.add(new Pair<Float, Float>(fst, snd));

                it.remove();
                first = false;
            }

            if (snd < stations2[i+1] && snd >= stations2[i+1]) {
                divisions.add(new Pair<Float, Float>(snd, stations2[i + 1]));
                snd = stations2[i + 1];
            }
            else if (snd <= stations2[i]) {
                divisions.add(new Pair<Float, Float>(stations2[i], stations2[i + 1]));
                snd = stations2[i + 1];
            }
        }
    }

    /*
     * Read csv files to memory hashes
     */
    public static void read(char opt, String file){
        //read limits to variables
        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();//dismiss first line
            switch (opt){
                case 'L':
                    while ((line = br.readLine()) != null) {

                        String[] l = line.split(cvsSplitBy);
                        limits.put(
                                new Pair<Float, Float>(Float.parseFloat(l[0]),
                                        Float.parseFloat(l[1])),Float.parseFloat(l[2]));
                    }
                    break;
                case 'S':
                    while ((line = br.readLine()) != null) {

                        String[] stations = line.split(cvsSplitBy);
                        estacoes_pk.put(stations[0], Float.parseFloat(stations[2]));
                    }
                    break;
                case 'G':
                    while ((line = br.readLine()) != null) {

                        String[] g = line.split(cvsSplitBy);
                        gradients.put(
                                new Pair<Float, Float>(Float.parseFloat(g[0]),
                                        Float.parseFloat(g[1])),Float.parseFloat(g[2]));
                    }
                    break;
                default:
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
