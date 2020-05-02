package dev.klepto.bunnytracker.record;

import java.util.Set;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@FunctionalInterface
public interface RecordsProvider {

    Set<Record> getNewRecords();

}
