package server;

import java.io.IOException;
import java.text.ParseException;

/**
 * нтерфейс для обеспечения полиморфизма команд без параметров.
 */

public interface Command {
    /**
     * Метод который исполняется во всех командах без параметров
     */
    void execute() ;
}
