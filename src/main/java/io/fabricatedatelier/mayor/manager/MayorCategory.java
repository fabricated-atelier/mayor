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
        ARMORER("building.category.armorer"),
        BARN("building.category.barn"),
        BUTCHER("building.category.butcher"),
        CARTOGRAPHER("building.category.cartographer"),
        DECORATION("building.category.decoration"),
        FARMER("building.category.farm"),
        FISHER("building.category.fisher"),
        FLETCHER("building.category.fletcher"),
        HOUSE("building.category.house"),
        LIBRARY("building.category.library"),
        MASON("building.category.mason"),
        SHEPHERD("building.category.shepherd"),
        SMITH("building.category.smith"),
        TANNERY("building.category.tannery"),
        TEMPLE("building.category.temple");

        private final String category;

        private BuildingCategory(final String category) {
            this.category = category;
        }

        public String getBuildingCategory() {
            return this.category;
        }
    }
}
