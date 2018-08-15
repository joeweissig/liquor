import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Recipe {
    public class Alcohol implements Ingredient {
        private String name;
        private double amount;
        private double ABV;
        private double price;

        public Alcohol() {
            name = "";
            amount = 0.0;
            ABV = 0.0;
            price = 0.0;
        }

        public Alcohol(Mixer _m, double _ABV, double _price) {
            
        }

        public Alcohol(String _name, double _amount, double _ABV, double _price) {
            name = _name;
            amount = _amount;
            ABV = _ABV;
            price = _price;
        }
        
        public String getName() {
            return name;
        }

        public double getAmount() {
            return amount;
        }

        public double getABV() {
            return ABV;
        }

        public double getPrice() {
            return price;
        }
    }

    public class Mixer implements Ingredient {
        private String name;
        private double amount;
        
        public Mixer() {
            name = "";
            amount = 0.0;
        }

        public Mixer(String _name, double _amount) {
            name = _name;
            amount = _amount;
        }
        
        public String getName() {
            return name;
        }

        public double getAmount() {
            return amount;
        }
    }

    private ArrayList<Alcohol> alcohols = new ArrayList<Alcohol>();
    private ArrayList<Mixer> mixers = new ArrayList<Mixer>();
    private ArrayList<String> specials = new ArrayList<String>();
    private String name;
    private String prep;

    public Recipe() {
        name = "";
        prep = "";
    }

    public Recipe(String _name, String _prep, JSONArray _ingredients) {
        JSONParser parser = new JSONParser();
        name = _name;
        prep = _prep;

        try {
            JSONObject ingredObject = (JSONObject) parser.parse(new FileReader("iba-cocktails/ingredients.json"));
            for (Object i : _ingredients) {
                JSONObject ingredient = (JSONObject) i;
                if (((String) ingredient.get("special")) != null) {
                    specials.add((String) ingredient.get("special"));
                } else {
                    String ingredientName = (String) ingredient.get("ingredient");
                    double ingredientAmount = Double.parseDouble(ingredient.get("amount").toString());
                    JSONObject ingredInfo = (JSONObject) ingredObject.get(ingredientName);
                    double ingredientABV = Double.parseDouble(ingredInfo.get("abv").toString());
                    if (ingredientABV > 0) {
                        alcohols.add(new Alcohol(ingredientName, ingredientAmount, ingredientABV, getPrice(ingredientName)));
                    } else {
                        mixers.add(new Mixer(ingredientName, ingredientAmount));
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getPrep() {
        return prep;
    }

    public ArrayList<String> display() {
        ArrayList<String> info = new ArrayList<String>();
        info.add(name);
        String temp = "";
        for (Alcohol a : alcohols) {
            temp += a.getName();
            temp += "\t";
            temp += a.getAmount();
            temp += "0 ml\t";
            temp += a.getABV();
            if (a.getPrice() != -1.0) {
                temp += "% ABV\tAvg Price: $";
                temp += String.format("%.2f", a.getPrice());
            } else {
                temp += "%";
            }
            temp += "\n";
        }
        info.add(temp);
        temp = "";
        for (Mixer m : mixers) {
            temp += m.getName();
            temp += "\t";
            temp += m.getAmount();
            temp += "\n";
        }
        info.add(temp);
        temp = "";
        for (String sp : specials) {
            temp += sp;
            temp += "\n";
        }
        info.add(temp);
        info.add(prep);
        return info;
    }

    public double getPrice(String name) {
        // RUM, CORDIALS, TEQUILA, BRANDY / COGNAC, DOMESTIC WHISKEY, GIN, VODKA, SCOTCH, CANADIAN, IRISH, MEZCAL, VERMOUTH, CACHACA, NEUTRAL GRAIN SPIRIT
        if (name.toLowerCase().contains("rum")) {
            return avgPrice("RUM");
        } else if (name.toLowerCase().contains("schnapps")) {
            return avgPrice("CORDIALS");
        } else if (name.toLowerCase().contains("tequila")) {
            return avgPrice("TEQUILA");
        } else if (name.toLowerCase().contains("brandy")) {
            return avgPrice("BRANDY / COGNAC");
        } else if (name.toLowerCase().contains("cognac")) {
            return avgPrice("BRANDY / COGNAC");
        } else if (name.toLowerCase().contains("whiskey")) {
            return avgPrice("DOMESTIC WHISKEY");
        } else if (name.toLowerCase().contains("gin")) {
            return avgPrice("GIN");
        } else if (name.toLowerCase().contains("vodka")) {
            return avgPrice("VODKA");
        } else if (name.toLowerCase().contains("scotch")) {
            return avgPrice("SCOTCH");
        } else if (name.toLowerCase().contains("mezcal")) {
            return avgPrice("MEZCAL");
        } else if (name.toLowerCase().contains("vermouth")) {
            return avgPrice("VERMOUTH");
        } else if (name.toLowerCase().contains("cachaca")) {
            return avgPrice("CACHACA");
        } else {
            // returns this in case something screws up, we have an obviously fake value
            return -1.0;
        }
    }

    public double avgPrice(String kind) {
        JSONParser parser = new JSONParser();
        // initialized to this in case something screws up, we have an obviously fake value
        double average = -1.0;
        try {
            JSONObject liquor = (JSONObject) parser.parse(new FileReader("alcohol.json"));
            JSONArray liquorList = (JSONArray) liquor.get(kind);
            double sum = 0.0;
            for (Object o : liquorList) {
                JSONObject bottle = (JSONObject) o;
                String priceString = ((String) bottle.get("price")).substring(1);
                double priceDouble = Double.parseDouble(priceString);
                sum += priceDouble;
            }
            average = sum / (double) liquorList.size();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return average;
    }
}