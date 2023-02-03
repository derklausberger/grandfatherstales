package objectClasses;

import GUI.InventoryPanel;

public class Inventory {

    Player player;

    public Inventory(Player player) {
        this.player = player;
    }

    public void loadInventory() {

        InventoryPanel.loadInventory(player);
    }
}
