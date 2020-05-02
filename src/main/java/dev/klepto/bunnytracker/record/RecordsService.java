package dev.klepto.bunnytracker.record;

import dev.klepto.bunnytracker.record.api.ApiRecordsProvider;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@RequiredArgsConstructor
public class RecordsService {

    private final Duration interval;
    private final RecordsProvider recordsProvider;
    private final RecordsListener recordsListener;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public RecordsService(Duration interval, RecordsListener recordsListener) {
        this(interval, new ApiRecordsProvider(), recordsListener);
    }

    public RecordsService start() {
        executor.scheduleAtFixedRate(this::dispatchRecords, 0, interval.toMillis(), TimeUnit.MILLISECONDS);
        return this;
    }

    private void dispatchRecords() {
        val records = recordsProvider.getNewRecords();
        if (!records.isEmpty()) {
            recordsListener.onNewRecords(records);
        }
    }

}
