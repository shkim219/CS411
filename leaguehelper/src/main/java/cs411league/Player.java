package cs411league;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


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
    public ArrayList<String> matchHistory;


    public Player(String puuid){
        this.puuid = puuid;
        this.matchHistory = new ArrayList<String>();
    }

    public Player(String puuid, ArrayList<String> mostPlayed, ArrayList<String> recommended, ArrayList<String> match, String bestRole){
        this.puuid = puuid;
        this.mostPlayed = mostPlayed;
        this.recommended = recommended;
        this.match = match;
        this.bestRole = bestRole;
        this.matchHistory = new ArrayList<String>();
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
        for(int i = 0; i < 20; i++){
            System.out.println(trial.get(i));
        }
        System.out.println("More?");
        String ans = sc.nextLine();
        if(ans.indexOf("y") != -1){
            for(int i = 20; i < trial.size(); i++){
                System.out.println(trial.get(i));
            }
        }
        System.out.println();
        System.out.println(temp + "'s best champions recently were: ");
        ArrayList<Champion> topList = minkyboba.getSoloData();
        int champListCounter = 0;
        int curChampCounter = 0;
        while(curChampCounter < topList.size() && champListCounter < Math.min(5, topList.size())){
            Champion champCur = topList.get(curChampCounter);
            if(champCur.matches >= 3){
                System.out.println(champCur);
                champListCounter++;
            }
            curChampCounter++;
        }
        minkyboba.getChampionList();
        

    }


    public static String getPuid(String name) throws Exception{
        Search cur = new Search();
        String response = cur.requestAPI("https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name + "?api_key=RGAPI-URKEYHERE");
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
        String getMatches = "https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=98&api_key=RGAPI-URKEYHERE";
        Search cur = new Search();
        String response = cur.requestAPI(getMatches);
        ArrayList<String> ret = new ArrayList<String>();
        int count = 0;
        while(count < 100 && response.indexOf("NA") != -1){
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
        String matchData = "https://americas.api.riotgames.com/lol/match/v5/matches/" + matchID + "?api_key=RGAPI-URKEYHERE";
        Search cur = new Search();
        String response = cur.requestAPI(matchData);
        matchHistory.add(response);
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


    public ArrayList<Champion> getSoloData(){
        ArrayList<Champion> topList = new ArrayList<Champion>();
        HashMap<String, Champion> table = new HashMap<String, Champion>();
        for(int i = 0; i < matchHistory.size(); i++){
            String response = matchHistory.get(i);
            int locPuid = response.lastIndexOf(puuid);
            int championFirst = response.lastIndexOf("\"championName\":\"", locPuid) + "\"championName\":\"".length();
            int championSecond = response.indexOf("\"", championFirst);
            String champion = response.substring(championFirst, championSecond);
            int assistsFirst = response.lastIndexOf("\"assists\":", locPuid) + "\"assists\":".length();
            int assistsSecond = response.indexOf(",", assistsFirst);
            int assists = Integer.parseInt(response.substring(assistsFirst, assistsSecond));
            int deathsFirst = response.lastIndexOf("\"deaths\":", locPuid) + "\"deaths\":".length();
            int deathsSecond = response.indexOf(",", deathsFirst);
            int deaths = Integer.parseInt(response.substring(deathsFirst, deathsSecond));
            int killsFirst = response.lastIndexOf("\"kills\":", locPuid) + "\"kills\":".length();
            int killsSecond = response.indexOf(",",killsFirst);
            int kills = Integer.parseInt(response.substring(killsFirst, killsSecond));
            int winFirst = response.indexOf("\"win\":", locPuid) + "\"win\":".length();
            int winSecond = response.indexOf("}", winFirst);
            String win = response.substring(winFirst, winSecond);
            Champion cur = table.get(champion);
            if(cur == null){
                if(win.equals("true")){
                    Champion champCur = new Champion(champion, (kills*1.0), (deaths*1.0), (assists*1.0), 1, 1);
                    table.put(champion, champCur);
                }
                else{
                    Champion champCur = new Champion(champion, (kills*1.0), (deaths*1.0), (assists*1.0), 1, 0);
                    table.put(champion, champCur);
                }
                
            }
            else{
                if(win.equals("true")){
                    cur.addWin();
                }
                else{
                    cur.addLose();
                }
                cur.addAssists(assists);
                cur.addDeaths(deaths);
                cur.addKills(kills);
            }
        }
        for (HashMap.Entry<String, Champion> entry : table.entrySet()) {
            String key = entry.getKey();
            Champion value = entry.getValue();
            if(Math.abs(value.deaths) < 0.1){
                value.deaths = 1;
            }
            double kda = (value.kills + value.assists)/value.deaths;


            boolean putIn = false;
            for(int i = 0; i < topList.size(); i++){
                Champion toCompare = topList.get(i);
                if(Math.abs(toCompare.deaths) < 0.0001){
                    toCompare.deaths = 0.0001;
                }
                double toCompareKDA = (toCompare.kills + toCompare.assists)/toCompare.deaths;
                if(toCompareKDA < kda){
                    topList.add(i,value);
                    putIn = true;
                    break;
                }
            }
            if(!putIn)
                topList.add(value);
        }
        return topList;
        
    }


    public void getChampionList(){
        HashMap<String, HashMap<String,Champion>> matchups = new HashMap<String, HashMap<String,Champion>>();
        for(int i = 0; i < matchHistory.size(); i++){
            String response = matchHistory.get(i);
            ArrayList<Champion> temp = new ArrayList<Champion>();
            Champion mainPlayer = null;
            int count = 0;
            while(count < 10){
                int assistsFirst = response.indexOf("\"assists\":") + "\"assists\":".length();
                int assistsSecond = response.indexOf(",", assistsFirst);
                int assists = Integer.parseInt(response.substring(assistsFirst, assistsSecond));
                int deathsFirst = response.indexOf("\"deaths\":") + "\"deaths\":".length();
                int deathsSecond = response.indexOf(",", deathsFirst);
                int deaths = Integer.parseInt(response.substring(deathsFirst, deathsSecond));
                int killsFirst = response.indexOf("\"kills\":") + "\"kills\":".length();
                int killsSecond = response.indexOf(",",killsFirst);
                int kills = Integer.parseInt(response.substring(killsFirst, killsSecond));
                int championFirst = response.indexOf("\"championName\":\"") + "\"championName\":\"".length();
                int championSecond = response.indexOf("\"", championFirst);
                String champion = response.substring(championFirst, championSecond);
                response = response.substring(championSecond);
                int puidFirst = response.indexOf("\"puuid\":") + "\"puuid\":".length();
                int puidSecond = response.indexOf("\"", puidFirst + 1);
                String puid = response.substring(puidFirst+1, puidSecond); 
                response = response.substring(puidSecond);
                int winFirst = response.indexOf("\"win\":") + "\"win\":".length();
                int winSecond = response.indexOf("}", winFirst);
                String win = response.substring(winFirst, winSecond);
                if(puid.equals(puuid)){
                    if(win.equals("true")){
                        mainPlayer = new Champion(champion, kills*1.0, deaths*1.0, assists*1.0, 1, 1);
                    }
                    else{
                        mainPlayer = new Champion(champion, kills*1.0, deaths*1.0, assists*1.0, 1, 0);
                    }
                    
                }
                if(win.equals("true")){
                    Champion tempChamps = new Champion(champion, 1);
                    temp.add(tempChamps);
                }
                else{
                    Champion tempChamps = new Champion(champion, 0);
                    temp.add(tempChamps);
                }
                count++;
            }
            if(mainPlayer != null){
                int mainPlayerScore = mainPlayer.wins;
                String mainPlayerCharacter = mainPlayer.name;
                for(int j = 0; j < temp.size(); j++){
                    Champion tempChamp = temp.get(j);
                    if(tempChamp.wins != mainPlayerScore){
                        String opponentChampion = tempChamp.name;
                        HashMap<String, Champion> inMap = matchups.get(opponentChampion);
                        if(inMap == null){
                            inMap = new HashMap<String, Champion>();
                            inMap.put(mainPlayerCharacter, mainPlayer);
                            matchups.put(opponentChampion, inMap);
                        }
                        else{
                            Champion toCompare = inMap.get(mainPlayerCharacter);
                            if(toCompare == null){
                                inMap.put(mainPlayerCharacter, mainPlayer);
                            }
                            else{
                                if(mainPlayerScore == 1){
                                    toCompare.addWin();
                                }
                                else{
                                    toCompare.addLose();
                                }
                                toCompare.addAssists((int)(mainPlayer.assists));
                                toCompare.addKills((int)(mainPlayer.kills));
                                toCompare.addDeaths((int)(mainPlayer.deaths));
                            }
                        }
                    }
                }
            }
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to see matchups?");
        String ans = sc.nextLine();
        while(ans.indexOf("y") != -1){
            getMatchUp(matchups);
            System.out.println("Do you want to see more matchups?");
            ans = sc.nextLine();
        }
    }

    public void getMatchUp(HashMap<String, HashMap<String,Champion>> matchups){
        Scanner sc = new Scanner(System.in);
        System.out.println("Who is your opponent champion?");
        String opponent = sc.nextLine();
        opponent = opponent.replaceAll(" ","");
        HashMap<String, Champion> listFromOpponent = matchups.get(opponent);
        if(listFromOpponent == null){
            System.out.println("Sorry! Could not find champion, try with another champion! (i.e. Jinx)");
            getMatchUp(matchups);
        }
        else{
            ArrayList<Champion> topList = new ArrayList<Champion>();
            for (HashMap.Entry<String, Champion> entry : listFromOpponent.entrySet()) {
                String key = entry.getKey();
                Champion value = entry.getValue();
                if(Math.abs(value.deaths) < 0.1){
                    value.deaths = 1;
                }
                double kda = (value.kills + value.assists)/value.deaths;
    
    
                boolean putIn = false;
                for(int i = 0; i < topList.size(); i++){
                    Champion toCompare = topList.get(i);
                    if(Math.abs(toCompare.deaths) < 0.1){
                        toCompare.deaths = 1;
                    }
                    double toCompareKDA = (toCompare.kills + toCompare.assists)/toCompare.deaths;
                    if(toCompareKDA < kda){
                        topList.add(i,value);
                        putIn = true;
                        break;
                    }
                }
                if(!putIn)
                    topList.add(value);
            }
            int champListCounter = 0;
            int curChampCounter = 0;
            while(curChampCounter < topList.size() && champListCounter < Math.min(5,topList.size())){
                Champion champCur = topList.get(curChampCounter);
                if(champCur.matches >= 1){
                    System.out.println(champCur);
                    champListCounter++;
                }
                curChampCounter++;
            }
            if(champListCounter == 0){
                System.out.println("It seems you haven't played enough matchups against this champion :((");
            }
        }
    }

}


