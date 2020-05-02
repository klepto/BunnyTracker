package dev.klepto.bunnytracker.record.api;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@FunctionalInterface
public interface RecordsJsonProvider {

    String fetchRecordsJson() throws Exception;

}
