package dev.klepto.bunnytracker;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Value
public class BTPlayer {

    @SerializedName("PlayerName")
    String name;

    @SerializedName("BestTime")
    int time;

}
