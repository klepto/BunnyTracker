package dev.klepto.bunnytracker.record;

import lombok.Value;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Value
public class Record {

    Map map;
    Player player;
    Player[] players;

    @Value
    public static class Map {
        String name;
        String recordsUrl;
        String imageUrl;
    }

    @Value
    public static class Player {
        String name;
        int rank, previousRank;
        int time, previousTime;
        boolean newRecord;
    }

}
