package dev.klepto.bunnytrack.record.api;

import dev.klepto.bunnytracker.record.RecordsProvider;
import dev.klepto.bunnytracker.record.api.ApiRecordsProvider;
import dev.klepto.bunnytracker.record.api.RecordsJsonProvider;
import lombok.*;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class ApiRecordsProviderTest {

    private RecordsProvider recordsProvider;
    private ResourceJsonProvider jsonProvider;

    @Before
    public void init() {
        jsonProvider = new ResourceJsonProvider("records.json");
        recordsProvider = new ApiRecordsProvider(jsonProvider);
        recordsProvider.getNewRecords();
    }

    @Test
    public void getNewRecords_WithSameRecords_ReturnsEmptySet() {
        recordsProvider.getNewRecords();

        val result = recordsProvider.getNewRecords();
        assertThat(result).isEmpty();
    }

    @Test
    public void getNewRecords_WithNewPb_ReturnsNewRecord() {
        recordsProvider.getNewRecords();
        jsonProvider.setResourceName("records_pb.json");

        val result = recordsProvider.getNewRecords();
        assertThat(result).hasSize(1);

        val player = result.iterator().next().getPlayer();
        assertThat(player.getName()).isEqualTo("kleps");
        assertThat(player.getRank()).isEqualTo(1);
        assertThat(player.getPreviousRank()).isEqualTo(1);
        assertThat(player.getTime()).isEqualTo(38354);
        assertThat(player.getPreviousTime()).isEqualTo(39354);
    }

    @Test
    public void getNewRecords_WithNewRank_ReturnsNewRecord() {
        recordsProvider.getNewRecords();
        jsonProvider.setResourceName("records_rank.json");

        val result = recordsProvider.getNewRecords();
        assertThat(result).hasSize(1);

        val player = result.iterator().next().getPlayer();
        assertThat(player.getName()).isEqualTo("Hera");
        assertThat(player.getRank()).isEqualTo(1);
        assertThat(player.getPreviousRank()).isEqualTo(2);
    }

    @Test
    public void getNewRecords_WithNewPlayer_ReturnsNewRecord() {
        recordsProvider.getNewRecords();
        jsonProvider.setResourceName("records_player.json");

        val result = recordsProvider.getNewRecords();
        assertThat(result).hasSize(1);

        val player = result.iterator().next().getPlayer();
        assertThat(player.getName()).isEqualTo("Chatouille");
        assertThat(player.getRank()).isEqualTo(1);
        assertThat(player.getPreviousRank()).isEqualTo(-1);
    }

    @Test
    public void getNewRecords_WithTwoNewPbs_ReturnsTwoNewRecords() {
        recordsProvider.getNewRecords();
        jsonProvider.setResourceName("records_2pbs.json");

        val result = recordsProvider.getNewRecords();
        assertThat(result).hasSize(2);
    }

    @Data
    @AllArgsConstructor
    private static class ResourceJsonProvider implements RecordsJsonProvider {
        private String resourceName;

        @Override
        public String fetchRecordsJson() throws Exception {
            try (val inputStream = getClass().getResourceAsStream("/" + resourceName)) {
                return new String(inputStream.readAllBytes());
            }
        }
    }

}
