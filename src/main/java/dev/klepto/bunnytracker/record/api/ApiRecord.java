package dev.klepto.bunnytracker.record.api;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Value
public class ApiRecord {

    @SerializedName("PlayerName")
    String playerName;

    @SerializedName("BestTime")
    int time;

}
