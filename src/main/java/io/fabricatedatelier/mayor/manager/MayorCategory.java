package io.fabricatedatelier.mayor.manager;

public class MayorCategory {

    public static enum BiomeCategory {
        DESERT("biome.category.desert"),
        PLAINS("biome.category.plains"),
        SAVANNA("biome.category.savanna"),
        SNOWY("biome.category.snowy"),
        TAIGA("biome.category.taiga");

        private final String category;

        private BiomeCategory(final String category) {
            this.category = category;
        }

        public String getBiomeCategory() {
            return this.category;
        }
    }

    public static enum BuildingCategory {
        HOUSE("building.category.houses"),
        BARN("building.category.barns"),
        FOUNTAIN("building.category.fountains"),
        ARMORER("building.category.armorer"),
        BUTCHER("building.category.butcher"),
        CARTOGRAPHER("building.category.cartographer"),
        FARMER("building.category.farm"),
        FISHER("building.category.fisher"),
        FLETCHER("building.category.fletcher"),
        LIBRARY("building.category.library"),
        MASON("building.category.mason"),
        SHEPHERD("building.category.shepherd"),
        TANNERY("building.category.tannery"),
        TEMPLE("building.category.butcher"),
        SMITH("building.category.butcher");

        private final String category;

        private BuildingCategory(final String category) {
            this.category = category;
        }

        public String getBuildingCategory() {
            return this.category;
        }
    }
}
