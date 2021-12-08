package cs411league;

public class Champion {
    public String name;
    public double kills, deaths, assists;
    public int matches, wins;
    public Champion(String name, double kills, double deaths, double assists, int matches, int wins){
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.matches = matches;
        this.wins = wins;
    }
    public Champion(String name, int wins){
        this.name = name;
        this.wins = wins;
    }
    
    public void addWin(){
        matches++;
        wins++;
    }
    public void addLose(){
        matches++;
    }
    public void addKills(int newKills){
        kills = ((kills * (matches - 1)) + newKills) / matches;
    }
    public void addAssists(int newAssists){
        assists = ((assists * (matches - 1)) + newAssists) / matches;
    }
    public void addDeaths(int newDeaths){
        deaths = ((deaths * (matches - 1)) + newDeaths) / matches;
    }
    public String toString(){
        String ret = "";
        double winrate = wins * 100.0 / matches;
        ret += name + " of " + String.format("%.2f", kills) + "/" + String.format("%.2f", deaths) + "/" + String.format("%.2f", assists) + " KDA and a winrate of " + String.format("%.2f", winrate) + "% over " + matches;
        if(matches == 1){
            ret += " game";
        }
        else{
            ret += " games";
        }
        return ret;
    }
    


}