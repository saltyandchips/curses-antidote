package cn.blockforge.curses_antidote.item;

public enum AntidoteMode {
    NEXT_TIME(0),
    SO_STRONG(1),
    MAN(2);

    private final int id;

    AntidoteMode(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public String itemNameKey() {
        return "item.curses_antidote.curses_antidote." + id;
    }

    public String nameKey() {
        return "tooltip.curses_antidote.mode." + id;
    }

    public String descKey() {
        return nameKey() + ".desc";
    }

    public static AntidoteMode byId(int id) {
        for (AntidoteMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return NEXT_TIME;
    }
}
