package cs411league;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

public class Player {
    
    public String puuid;
    public ArrayList<String> mostPlayed;
    public ArrayList<String> recommended;
    public ArrayList<String> match;
    public String bestRole;


    public Player(String puuid){
        this.puuid = puuid;
    }

    public Player(String puuid, ArrayList<String> mostPlayed, ArrayList<String> recommended, ArrayList<String> match, String bestRole){
        this.puuid = puuid;
        this.mostPlayed = mostPlayed;
        this.recommended = recommended;
        this.match = match;
        this.bestRole = bestRole;
    }

    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Summoner name?: ");
        String name = sc.nextLine();
        String temp = name;
        name = name.replaceAll(" ", "%20");
        String puid = getPuid(name);
        if(puid.equals("")){
            System.out.println("Does not exist");
            return;
        }
        // System.out.println(puid);
        Player minkyboba = new Player(puid);

        ArrayList<String> trial = minkyboba.getMatches();
        if(trial.size() == 0){
            System.out.println("oops!");
        }
        int numWins = minkyboba.getNumWins(trial);
        System.out.println(temp + " has won " + numWins + " out of the past " + trial.size() + " games. Here's the list: ");
        for(int i = 0; i < trial.size(); i++){
            System.out.println(trial.get(i));
        }

    }

    public static String getPuid(String name) throws Exception{
        Search cur = new Search();
        String response = cur.call_me("https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name + "?api_key=RGAPI-73415d6f-5618-4796-8995-19739a0c7bc4");
        int puidFirst = response.indexOf("\"puuid\":") + "\"puuid\":".length();
        int puidSecond = response.indexOf("\"", puidFirst + 1);
        return response.substring(puidFirst+1, puidSecond); 
    }

    public int getNumWins(ArrayList<String> trials){
        int won = 0;
        for(int i = 0; i < trials.size(); i++){
            if(trials.get(i).indexOf("won") != -1)
                won++;
        }
        return won;
    }

    public ArrayList<String> getMatches() throws Exception{
        String getMatches = "https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=20&api_key=RGAPI-73415d6f-5618-4796-8995-19739a0c7bc4";
        Search cur = new Search();
        String response = cur.call_me(getMatches);
        ArrayList<String> ret = new ArrayList<String>();
        int count = 0;
        while(count < 1 || response.indexOf("\"") != -1){
            int indexFirst = response.indexOf("\"");
            int indexSecond = response.indexOf("\"", indexFirst+1);
            String matchID = response.substring(indexFirst+1, indexSecond);
            // String matchID = "NA1_4131112347";
            response = response.substring(indexSecond+1);
            String matchtype = getMatchType(matchID, puuid);
            ret.add(matchtype);
            count++;
        }
        return ret;
        
    }

    public String getMatchType(String matchID, String id) throws Exception{
        String matchData = "https://americas.api.riotgames.com/lol/match/v5/matches/" + matchID + "?api_key=RGAPI-73415d6f-5618-4796-8995-19739a0c7bc4";
        Search cur = new Search();
        String response = cur.call_me(matchData);
        int matchModeFirst = response.indexOf("\"gameMode\":\"") + "\"gameMode\":\"".length();
        int matchModeSecond = response.indexOf("\"", matchModeFirst);
        String matchMode = response.substring(matchModeFirst, matchModeSecond);
        int locPuid = response.indexOf(id, matchModeFirst);
        int championFirst = response.lastIndexOf("\"championName\":\"", locPuid) + "\"championName\":\"".length();
        int championSecond = response.indexOf("\"", championFirst);
        String champion = response.substring(championFirst, championSecond);
        response = response.substring(locPuid);
        int winFirst = response.indexOf("\"win\":") + "\"win\":".length();
        int winSecond = response.indexOf("}", winFirst);
        String win = response.substring(winFirst, winSecond);
        if(win.equals("true")){
            win = "won!";
        }
        else{
            win = "lost :(";
        }
        return matchMode + ": " + win + " as " + champion;
    }


}
