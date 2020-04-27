package dev.klepto.bunnytracker;

import lombok.Value;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Value
public class BTMap {

    String name;
    BTPlayer[] players;

    public String getIconUrl() {
        switch (getName()) {
            case "BT-Welcome_v5":
                return "https://ut4bt.ga/img/maps/welcome.jpg";
            case "BT-AirRace_V5":
                return "https://ut4bt.ga/img/maps/airrace.jpg";
            case "BT-Crack_v1":
                return "https://ut4bt.ga/img/maps/crack.jpg";
            case "BT-Roaster":
                return "https://ut4bt.ga/img/maps/roaster.jpg";
            case "BT-AirRace2":
                return "https://ut4bt.ga/img/maps/airrace2.jpg";
            case "BT-TheEscape":
                return "https://ut4bt.ga/img/maps/theescape.jpg";
            case "BT-Radioactive_v1":
                return "https://ut4bt.ga/img/maps/radioactive.jpg";
            case "BT-HOP":
                return "https://ut4bt.ga/img/maps/hop.jpg";
            case "BT-RansomKek_Beta":
                return "https://ut4bt.ga/img/maps/ransomkek_beta.jpg";
            case "BT-RadicalTrials1_v5":
                return "https://ut4bt.ga/img/maps/radicaltrials1.jpg";
            case "BT-SwampRuins_v2":
                return "https://ut4bt.ga/img/maps/swampruins.jpg";
            case "BT-Rooms-Training":
                return "https://ut4bt.ga/img/maps/rooms-training.jpg";
            case "BT-RadicalTrials2_v4":
                return "https://ut4bt.ga/img/maps/radicaltrials2.jpg";
            case "BT-CrashOut-V3":
                return "https://ut4bt.ga/img/maps/crashout.jpg";
            case "BT-Rumble_V4":
                return "https://ut4bt.ga/img/maps/rumble.jpg";
            case "BT-GBR_V2":
                return "https://ut4bt.ga/img/maps/gbr.jpg";
            case "BT-LTTT":
                return "https://ut4bt.ga/img/maps/lttt.jpg";
            case "BT-Indus_v6":
                return "https://ut4bt.ga/img/maps/indus.jpg";
            case "BT-Ancient_Halls":
                return "https://ut4bt.ga/img/maps/ancient_halls.jpg";
            case "BT-Planets_V3":
                return "https://ut4bt.ga/img/maps/planets.jpg";
            case "BT-LavaKEK_v1":
                return "https://ut4bt.ga/img/maps/lavakek.jpg";
            case "BT-ChatoKeK":
                return "https://ut4bt.ga/img/maps/chatokek.jpg";
            case "BT-Happy2019_v2":
                return "https://ut4bt.ga/img/maps/happy2019.jpg";
            case "BT-Rooms":
                return "https://ut4bt.ga/img/maps/rooms.jpg";
            case "BT-Joust_v2":
                return "https://ut4bt.ga/img/maps/joust.jpg";
            case "BT-Plinko":
                return "https://ut4bt.ga/img/maps/plinko.jpg";
            case "BT-HOPLite":
                return "https://ut4bt.ga/img/maps/hoplite.jpg";
            case "BT-Oneshot":
                return "https://ut4bt.ga/img/maps/oneshot.jpg";
            case "BT-Grenadier":
                return "https://ut4bt.ga/img/maps/grenadier.jpg";
            case "BT-Osiris":
                return "https://ut4bt.ga/img/maps/osiris.jpg";
            case "BT-CN":
                return "https://ut4bt.ga/img/maps/cn.jpg";
            case "BT-ScoobTest-V3":
                return "https://ut4bt.ga/img/maps/scoobtest.jpg";
            case "BT-youarelol_v2":
                return "https://ut4bt.ga/img/maps/youarelol.jpg";
            case "BT-StarLab_v1":
                return "https://ut4bt.ga/img/maps/starlab.jpg";
            case "BT-2P-DualRun":
                return "https://ut4bt.ga/img/maps/2p-dualrun.jpg";
        }
        return null;
    }

}
