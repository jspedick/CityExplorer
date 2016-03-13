package com.mobdev.cityexplorer.myapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by wildcat on 4/9/2015.
 */
public class ParseApplication extends Application {
	/*
	 * This class runs when the app is being created. It creates the database.
	 */

    @Override
    public void onCreate() {
        super.onCreate();

        //initializes database with Parse key for City Wits
        Parse.initialize(this, "o6pg70OdlEeYrFPMYaWcEbcMYi8O7FN6gq1V9knz", "dCXJLdwxv7u32TCC6q7rhCcBnfLp9ThjGsyEr7oV");


        try {

            createTrekCategories();

            createTrekFunctions();

            createSubFunctions();


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createTrekCategories() throws ParseException{

        String [] trekCategoriesArray= {"Night Life","Parks & Outdoors", "Historical","Best of Philly", "Arts & Culture", "Good Eats", "Date Night" };

        for (int i = 0; i < trekCategoriesArray.length; i++){

            //checks to see if categories exist
            ParseQuery<ParseObject> queryCategory = ParseQuery.getQuery("TrekCategory");
            queryCategory.whereEqualTo("Name", trekCategoriesArray[i]);

            List<ParseObject> catList = queryCategory.find();

            //if they don't exist, adds them here
            if (catList.size() == 0){

                ParseObject trekCategoryObject = new ParseObject("TrekCategory");
                trekCategoryObject.put("Name", trekCategoriesArray[i]);
                trekCategoryObject.save();
            }
        }
    }
    private void createTreks(String sName, String category, int iVotes, String sTime, String sCost, String sOverview, String sComments )throws ParseException{

        ParseQuery<ParseObject> queryTrek = ParseQuery.getQuery("Trek");
        queryTrek.whereEqualTo("Name", sName);

        List<ParseObject> trekList2 = queryTrek.find();

        //if treks don't exist adds them here
        if (trekList2.size() == 0) {
            ParseObject trekObject = new ParseObject("Trek");
            ParseObject trekCatObjectForRelation = new ParseObject("Trek");

            ParseQuery<ParseObject> queryCategory = ParseQuery.getQuery("TrekCategory");
            queryCategory.whereEqualTo("Name", category);
            trekCatObjectForRelation = queryCategory.getFirst();

            //adds columns
            trekObject.put("Name", sName);
            trekObject.put("Votes", iVotes);
            trekObject.put("Time", sTime);
            trekObject.put("Cost", sCost);
            trekObject.put("Overview", sOverview);
            trekObject.put("Comments", sComments);

            //creates relation between Treks and Categories
            trekObject.put("Category", trekCatObjectForRelation.getString("Name"));
            trekObject.put("Categoryptr", trekCatObjectForRelation);
            trekObject.save();
        }
    }
    private void createSubCategories (String subName, String trekName, int votes,
                                      String description, String sURL, int imageID, double lat, double longit) throws ParseException {

        //checks to see if subCategory already exists
        ParseQuery<ParseObject> querySub = ParseQuery.getQuery("SubCategory");
        querySub.whereEqualTo("SubName", subName);

        List<ParseObject> subList2 = querySub.find();

        if (subList2.size() == 0) {

            ParseQuery<ParseObject> queryTrekName = ParseQuery.getQuery("Trek");
            queryTrekName.whereEqualTo("Name", trekName);
            ParseObject trekObjectForRelation = queryTrekName.getFirst();

            ParseObject subCatObject = new ParseObject("SubCategory");
            subCatObject.put("SubName", subName);
            subCatObject.put("Votes", votes);
            subCatObject.put("Description", description);
            subCatObject.put("URL", sURL);

            //adds trek for relation
            subCatObject.put("TrekName", trekObjectForRelation.getString("Name"));
            subCatObject.put("TrekPtr", trekObjectForRelation);
            subCatObject.put("imageID", imageID);
            subCatObject.put("Lat", lat);
            subCatObject.put("Longit", longit);
            subCatObject.save();
        }

    }
    private void createTrekFunctions(){


        try {
            createTreks("PreAmble", "Historical", 0, "5 Hrs", "$",
                    "History is a whole neighborhood in Philly; this trek is but a guideline down Belgian " +
                    "blocked streets and past a couple testaments to our timelessness, right in the Liberty Bell's backyard.",
                    "Great trek, feel like I got see some of the lesser know historical niches");

            createTreks("Green Spaces", "Parks & Outdoors", 0, "4 Hrs", "$",
                    "Start and end on the water at the Art Museum. Discover the city's most famous green spaces everywhere in between.",
                    "Never knew that Philly had so much green space");

            createTreks("College Field Day", "Arts & Culture", 0, "4 Hrs", "$",
                    "University City is its own world, and you're just living in it. Do brunch, frolic in parks, visit exhibits, and see old things, all on college campuses.",
                    "The philly college scene is awesome, got to see some cool stuff");

            createTreks("Day Full of Love Park", "Date Night", 0, "2 Hrs", "$",
                    "Explore iconic Love Park as you enjoy the best of market street in Center City",
                    "Wife and I had never been in Philly before, and loved the trek. Maybe we'll be back for our anniversary?!");

            createTreks("Cheese Steak Crawl", "Good Eats", 0, "3 Hrs", "$$",
                    "Decide for yourself who makes the best cheestesteak in the city. Stops include the dueling Pat's and Geno's as well as upcoming contender Jim's.",
                    "CHESSE. STEAKS.");

            createTreks("Statue Stroll", "Parks & Outdoors", 0, "3 Hrs", "$",
                    "Walk along the greenery of the park way and stop at some of Philly's most famous statues. " +
                            "Relive the famous moment from 'Rocky', and run up the steps of the art museum to stand next to the boxer's statue.",
                    "feel like a true philadelphian now that I've ran up the stairs and raised my arms with Rocky.  Look out Apollo!");
/*
These treks are future treks that will be added

             createTreks("Weird Art", "Arts & Culture", 6, "5 Hrs", "$",
                    "Murals, music, medical oddities--explore the edgier parts of Philly's downtown, and even ride our one subway line!",
                    "I'll take street art over a boring gallery any day! Some really cool sites here");

             createTreks("Italian Amore", "Date Night", 0, "5 Hrs", "$$$",
                    "Stroll through the bustling markets of Philly's own Little Italy and savor a " +
                    "romantic candlelight dinner with authentic homestyle Italian Cusine " +
                    "before capping off the night with the Opera at Kimmel Center.",
                    "My bf and I had LITERALLY the best chicken parm ever.  The opera was a night touch too (feeling like a true italian now).");

            /*createTreks("River Rink Winterfest", "Date Night", 0, "2 Hrs", "$$",
                    "Enjoy roller or ice skating at the Blue Cross River Rink park, delicious New American meals on 4-masted ship with an outdoor bar and finish it off at an art-house cinema",
                    "The walk along the river is so romantic! There's some nice coffee shops we stopped at along the way too.");
*/

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void createSubFunctions(){

        try {

            createSubCategories("William Penn Statue", "Statue Stroll", 8,
                    "William Penn stands atop City Hall looking over his majestic City.  " +
                            "At one point, no building was allowed to stand taller than the tip of his hat!",
                    "http://www.visitphilly.com/music-art/philadelphia/art-on-city-hall/",R.drawable.william_penn, 39.953466, -75.164116);

            createSubCategories("Rodin Museum", "Statue Stroll", 8,
                    "The famous sculptor is most know for his iconic statue of 'The Thinker'.  " +
                    "Ponder with one of the casts of the famous piece and check out the other statues in the surrounding area as well.",
                    "http://www.rodinmuseum.org/", R.drawable.rodin_museum, 39.960477, -75.174534);

            createSubCategories("Washington Monument", "Statue Stroll", 8,
                    "Washington overlooks the parkway, ready to lead his army of statues down the street from Eakans Oval",
                    "http://www.visitphilly.com/music-art/philadelphia/washington-monument/", R.drawable.washington_ave, 39.964036, -75.178631);

            createSubCategories("Rocky Statue", "Statue Stroll", 8,
                    "Lift your arms and 'go the distance' along side Philly boxing icon Rocky Balboa.",
                    "http://www.visitphilly.com/museums-attractions/philadelphia/the-rocky-statue-and-the-rocky-steps/",
                    R.drawable.rocky_statue, 39.965801, -75.181162);

            createSubCategories("Love Park", "Day Full of Love Park", 8,
                    "Take a picture with the famous LOVE sign and embrace the city of brotherly love",
                    "http://www.visitphilly.com/museums-attractions/philadelphia/love-park/",
                    R.drawable.love_park, 39.954101, -75.166118);
            createSubCategories("Rittenhouse Square Park", "Day Full of Love Park", 8,
                    "Take a break from the city and share a bench in Rittenhouse Square.  The park is also lined with excellent shopping"
                    ,"http://www.ushistory.org/districts/rittenhouse/index.htm",
                    R.drawable.rittenhouse, 39.949466, -75.171864);
            createSubCategories("Reading Terminal Market", "Day Full of Love Park", 5,
                    "Explore the good eats and homey market feel of reading Terminal market.  Find some fresh ingredients to make that special dinner tonight!",
                    "http://www.readingterminalmarket.org/",
                    R.drawable.reading_terminal, 39.953223, -75.158882);

            createSubCategories("Pat's", "Cheese Steak Crawl", 5,
                    "Pat's has long been held as the best cheesesteak in Philly.  Make sure you order it 'wit cheese'.",
                    "http://www.patskingofsteaks.com/",
                    R.drawable.pats_steaks, 39.933178, -75.159274);
            createSubCategories("Geno's", "Cheese Steak Crawl", 5,
                    "Geno's is right across the street from Pat's.  The two have been dueling for steak of the city for ages.",
                    "http://www.genosteaks.com/",
                    R.drawable.genos_steaks, 39.933722, -75.158891);
            createSubCategories("Jim's", "Cheese Steak Crawl", 5,
                    "Jim's isn't as well know as Pat's and Geno's, but it's ideal location on South Street is ideal for a stop while out at the bars!",
                    "http://www.jimssouthstreet.com/",
                    R.drawable.jims_steaks, 39.944054, -75.168765);

            createSubCategories("Fragments of Franklin Court", "PreAmble", 5,
                    "Monument to Ben Franklin, created by revolutionary Philadelphian architect",
                    "http://www.ushistory.org/tour/franklin-court.htm",
                    R.drawable.franklin_court, 39.950156, -75.146637);
            createSubCategories("Athenaeum", "PreAmble", 5,
                    "Free historical art/architecture museum",
                    "http://www.philaathenaeum.org/",
                    R.drawable.athenaeum, 39.946887, -75.150872);
            createSubCategories("Curtis Dream Garden", "PreAmble", 5,
                    "A beautiful mural spans over 50 feet long",
                    "http://www.visitphilly.com/music-art/philadelphia/dream-garden/",
                    R.drawable.dream_garden, 39.947883, -75.151743);
            createSubCategories("Chestnut St Restaurants", "PreAmble", 5,
                    "Khyber Pass Pub, Plough & Stars, Amada, Eulogy, The Gaslight, etc.",
                    "http://www.tripadvisor.com/Attraction_Review-g60795-d143531-Reviews-Chestnut_Street-Philadelphia_Pennsylvania.html",
                    R.drawable.chestnut_broderick, 39.948634, -75.144205);
            createSubCategories("Penn's Landing Overpass/MFL", "PreAmble", 5,
                    "Walk north on 2nd St, to the MFL Station, to the Penn's Landing neon sign",
                    "http://www.visitphilly.com/outdoor-activities/philadelphia/penns-landing/",
                    R.drawable.penns_landing, 39.946829, -75.140674);

/* future subcategories that will be added
            createSubCategories("Mutter Museum", "Weird Art", 5,
                    "The famous mutter museum features the human form and a variety of skeletons",
                    "http://muttermuseum.org/",
                    R.drawable.mutter_museum);
            createSubCategories("Eastern State Penitentiary", "Weird Art", 5,
                    "The former prison used to hold the likes of Al Capone.  Now you can explore its spooky walls",
                    "http://www.easternstate.org/",
                    R.drawable.state_pen);
            createSubCategories("Spring Garden BSL", "Weird Art", 5,
                    "A well-built playground with a view of the skyline",
                    "http://www.visitphilly.com/philadelphia-neighborhoods/spring-garden/",
                    R.drawable.spring_garden);
            createSubCategories("Washington Ave", "Weird Art", 5,
                    "Murals painted by locals adorne the street walls",
                    "http://en.wikipedia.org/wiki/Point_Breeze,_Philadelphia",
                    R.drawable.washington_ave);
            createSubCategories("Milkboy Coffee", "Weird Art", 5,
                    "Good place, if rest needed; live music at night",
                    "http://www.milkboyphilly.com/",
                    R.drawable.milkboy_coffee);
            createSubCategories("Delancey > Pine & 2nd", "PreAmble", 5,
                    "Walk south on 3rd St, arrive at popular farmers market site",
                    "http://www.philaathenaeum.org/",
                    R.drawable.delancey, 39.953801, -75.224146);*/

            createSubCategories("Fountains at Art Museum", "Green Spaces", 5,
                    "Two fountains stand alongside the beautiful Art museum",
                    "http://en.wikipedia.org/wiki/Eakins_Oval",
                    R.drawable.fountains_art_museum, 39.965801, -75.181162);
            createSubCategories("Green Street > Spring Garden", "Green Spaces", 5,
                    "Belgian Café on Green St, good historical architecture on Spring Garden, chosen somewhat for the puns",
                    "http://www.thebelgiancafe.com/menu.cfm",
                    R.drawable.green_street, 39.963111, -75.158761);
            createSubCategories("Logan Square", "Green Spaces", 5,
                    "More fountain, also good vantage point down BFP",
                    "http://www.visitphilly.com/philadelphia-neighborhoods/logan-square/",
                    R.drawable.logan_square, 39.956385, -75.170590);

            createSubCategories("Drexel Playground", "College Field Day", 5,
                    "The ground is spongey, and their athletic facilities are pretty nice.",
                    "http://www.drexel.edu/westphal/news/archive/2010/2010-11-11_Playground_Opening/",
                    R.drawable.drexel_playground, 39.953994, -75.186947);

            createSubCategories("Sabrina's in Powelton", "College Field Day", 5,
                    "Café/brunch place. They're cool",
                    "http://sabrinascafe.com/",
                    R.drawable.sabrinas, 39.960035, -75.190649);
            createSubCategories("Penn's Locust Walk", "College Field Day", 5,
                    "LOVE statue, brick paved colonial style campus",
                    "http://placemaking.pps.org/great_public_spaces/one?public_place_id=631",
                    R.drawable.locust_walk, 39.946544, -75.150944);
            createSubCategories("Institute of Contemporary Art", "College Field Day", 5,
                    "On Penn's campus, near food",
                    "http://icaphila.org/",
                    R.drawable.contemporary_art, 39.954131, -75.194857);
            createSubCategories("World Café Live", "College Field Day", 5,
                    "Live music, concerts, and a chance to be young again",
                    "https://www.worldcafelive.com/",
                    R.drawable.world_cafe, 39.952164, -75.185021);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
