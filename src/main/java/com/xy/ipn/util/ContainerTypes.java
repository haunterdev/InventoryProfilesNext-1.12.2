package com.xy.ipn.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ContainerWorkbench;

/**
 * What kind of inventory a container is, which decides the buttons it gets. Port of the original's
 * ContainerTypes and its vanilla table (versionSpecificContainerTypes), with the 1.12.2 classes.
 *
 * Lookup follows the original's getRepresentingClass: the exact class first, then the first table
 * entry the container is an instance of. A container with no slots is temporary slots only, and an
 * unknown (modded) container is sortable storage.
 *
 * DROPPED: the 1.21 classes with no 1.12.2 counterpart (lectern, loom, stonecutter, grindstone,
 * cartography and smithing tables, smoker, blast furnace, crafter), the layout types (RECTANGULAR,
 * WIDTH_9, HEIGHT_3, HORSE_STORAGE), which this port's sorter works out from the slots itself, and
 * the hints file and API registry for other mods' containers, which this port does not have.
 */
public final class ContainerTypes {

    public enum Type {
        PURE_BACKPACK,
        PLAYER,
        CRAFTING,
        TEMP_SLOTS,
        NO_SORTING_STORAGE,
        SORTABLE_STORAGE,
        TRADER,
        ANVIL
    }

    private static final Set<Type> NON_STORAGE = Collections.unmodifiableSet(EnumSet.of(Type.TEMP_SLOTS));
    private static final Set<Type> SIMPLE = Collections.unmodifiableSet(EnumSet.of(Type.PURE_BACKPACK));
    private static final Set<Type> UNKNOWN = Collections.unmodifiableSet(EnumSet.of(Type.SORTABLE_STORAGE));

    private static final Map<Class<?>, Set<Type>> VANILLA = new LinkedHashMap<Class<?>, Set<Type>>();
    static {
        register(ContainerPlayer.class, EnumSet.of(Type.PURE_BACKPACK, Type.PLAYER, Type.CRAFTING));
        register(ContainerEnchantment.class, NON_STORAGE);
        register(ContainerRepair.class, EnumSet.of(Type.PURE_BACKPACK, Type.ANVIL));
        register(ContainerBeacon.class, NON_STORAGE);
        register(ContainerFurnace.class, SIMPLE);
        register(ContainerMerchant.class, EnumSet.of(Type.TRADER));
        register(ContainerWorkbench.class, EnumSet.of(Type.CRAFTING));
        register(ContainerHopper.class, EnumSet.of(Type.NO_SORTING_STORAGE, Type.PURE_BACKPACK));
        register(ContainerBrewingStand.class, NON_STORAGE);
        register(ContainerChest.class, UNKNOWN);
        register(ContainerShulkerBox.class, UNKNOWN);
        register(ContainerHorseInventory.class, UNKNOWN);
        register(ContainerDispenser.class, UNKNOWN);
    }

    private ContainerTypes() {
    }

    private static void register(Class<?> containerClass, Set<Type> types) {
        VANILLA.put(containerClass, Collections.unmodifiableSet(EnumSet.copyOf(types)));
    }

    public static Set<Type> of(Container container) {
        if (container.inventorySlots.isEmpty()) {
            return NON_STORAGE;
        }
        Set<Type> types = VANILLA.get(container.getClass());
        if (types != null) {
            return types;
        }
        for (Map.Entry<Class<?>, Set<Type>> entry : VANILLA.entrySet()) {
            if (entry.getKey().isInstance(container)) {
                return entry.getValue();
            }
        }
        return UNKNOWN;
    }
}
