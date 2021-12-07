package cs411league;

import java.util.ArrayList;
import java.util.List;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.League;
import com.merakianalytics.orianna.types.core.league.Leagues;
import com.merakianalytics.orianna.types.core.staticdata.Champion;
import com.merakianalytics.orianna.types.core.staticdata.Champions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistories;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.match.Matches;
import com.merakianalytics.orianna.types.core.match.Timeline;
import com.merakianalytics.orianna.types.core.match.Timelines;
import com.merakianalytics.orianna.types.core.match.TournamentMatches;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.merakianalytics.orianna.types.core.summoner.Summoners;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL; 


public class MainFile {
    public static void main(String[] args) throws Exception {
        Orianna.setRiotAPIKey("RGAPI-4e58cfcb-206a-42a1-bb9c-e4f724555652");
        Orianna.setDefaultRegion(Region.NORTH_AMERICA);

        // Summoner summoner = Orianna.summonerNamed("FatalElement").get();
        // System.out.println(summoner.getName() + " is level " + summoner.getLevel() + " on the " + summoner.getRegion() + " server.");








        League challengerList = Orianna.challengerLeagueInQueue(Queue.RANKED_SOLO).get();
        League materList = Orianna.masterLeagueInQueue(Queue.RANKED_SOLO).get();

        for(int i = 0; i < 3; i++){
            Summoner bestNA = challengerList.get(i).getSummoner();
            String nameID = bestNA.getName().replaceAll(" ", "%");
            System.out.println(nameID);
            String ht = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + nameID + "?api_key=RGAPI-4e58cfcb-206a-42a1-bb9c-e4f724555652";
            String response = call_me(ht);
            if(!response.equals("")){
                String puuid = getPuid(response);
                // System.out.println(puuid);
                
            }

        }


        // Champions champions = Orianna.getChampions();
        // Champion randomChampion = champions.get((int)(Math.random() * champions.size()));
        // System.out.println("He enjoys playing champions such as " + randomChampion.getName());

        // League challengerLeague = Orianna.challengerLeagueInQueue(Queue.RANKED_SOLO).get();
        // Summoner bestNA = challengerLeague.get(0).getSummoner();
        // System.out.println("He's not as good as " + bestNA.getName() + " at League, but probably a better Java programmer!");
    }

    public static void getMatches(String puuid) throws Exception{
        String getMatches = "https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=20?api_key=RGAPI-4e58cfcb-206a-42a1-bb9c-e4f724555652";
        String response2 = call_me(getMatches);
        if(!response2.equals("")){
            List<String> matchlist = new ArrayList<String>();
            while(response2.indexOf("\"") != -1){
                int indexFirst = response2.indexOf("\"");
                int indexSecond = response2.indexOf("\"", indexFirst+1);
                String matchID = response2.substring(indexFirst+1, indexSecond);
                response2 = response2.substring(indexSecond+1);
            }

        }
    }

    public static void getMatchInfo(String matchID) throws Exception{
        String matchInfo = "https://americas.api.riotgames.com/lol/match/v5/matches/" + matchID + "?api_key=RGAPI-4e58cfcb-206a-42a1-bb9c-e4f724555652";
        String response3 = call_me(matchInfo);
        if(!response3.equals("")){

        }
    }


    public static String getPuid(String response){
        int puid = response.indexOf("\"puuid\":\"") + "\"puuid\":\"".length();
        int endpuid = response.indexOf("\"", puid + 1);
        String ret = response.substring(puid, endpuid);
        return ret;

    }
    public static String call_me(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        // System.out.println("\nSending 'GET' request to URL : " + url);
        // System.out.println("Response Code : " + responseCode);
        if(responseCode == 200){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print in String
            String requestResponse = response.toString();
            // System.out.println(requestResponse);
            return requestResponse;
        }
        return "";
        //Read JSON response and print
      }

}
