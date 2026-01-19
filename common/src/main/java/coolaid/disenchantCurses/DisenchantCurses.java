package coolaid.disenchantCurses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DisenchantCurses {

    public static final String MOD_ID = "disenchant_curses";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing " + MOD_ID);
    }
}
