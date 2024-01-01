package Game;

public enum Terrain {
    FARMLAND("Farmland"), MINE("Mining Distric"), URBAN("Urban");
    private final String name;
    Terrain(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
