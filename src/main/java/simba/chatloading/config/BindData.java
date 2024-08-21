/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading.config;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import simba.chatloading.ChatLoading;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BindData extends SavedData {

    public Map<String, Tuple3<Integer, UUID, String>> Bind_data;
    public static class Tuple3<TInt, TUUID, TString> {
        public TInt FInt;
        public TUUID FUUID;
        public TString FLang;
        Tuple3(TInt FInt, TUUID FUUID, TString FLang) {
            this.FInt = FInt;
            this.FUUID = FUUID;
            this.FLang = FLang;
        }
    }

    private static final String NBT_LOAD_KEY = "BIND";
    private static final String NBT_LANG_KEY = "LANG";
    private static final String NBT_UUID_KEY = "UUID";

    public static BindData BindInstance;

    public BindData() {
        Bind_data = new HashMap<>();
    }

    public static BindData getServerState(MinecraftServer server) {
        // First we get the persistentStateManager for the OVERWORLD
        DimensionDataStorage persistentStateManager = server
                .getLevel(Level.OVERWORLD).getDataStorage();

        // Calling this reads the file from the disk if it exists, or creates a new one and saves it to the disk
        return persistentStateManager.computeIfAbsent(
                BindData::createFromNbt,
                BindData::new,
                ChatLoading.MODID);
    }

    public void Bind(String BindKey, int LoadLength, UUID playerUUID) {
        Bind_data.put(BindKey, new Tuple3<>(LoadLength, playerUUID, ChatLoading.config.getGLOBAL_LANGUAGE()));
        this.setDirty();
    }

    public boolean Unbind(String BindKey) {
        if(this.Bind_data.containsKey(BindKey)) {
            this.Bind_data.remove(BindKey);
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    public Optional<Integer> Query(String BindKey) {
        if (this.Bind_data.containsKey(BindKey)) {
            return Optional.of(this.Bind_data.get(BindKey).FInt);
        } else {
            return Optional.empty();
        }
    }

    public boolean setLength(String BindKey, int loadLength) {
        if (this.Bind_data.containsKey(BindKey)) {
            this.Bind_data.get(BindKey).FInt = loadLength;
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    public String getLangOrDefault(String BindKey) {
        if (this.Bind_data.containsKey(BindKey)) {
            return this.Bind_data.get(BindKey).FLang;
        } else {
            return ChatLoading.config.getGLOBAL_LANGUAGE();
        }
    }

    public static BindData createFromNbt(CompoundTag tag) {
        BindData bindData = new BindData();
        for (String nbtKey : tag.getAllKeys()) {
            CompoundTag keyData = tag.getCompound(nbtKey);
            int loadLength;
            UUID BindUUID;
            String BindLang;
            if (keyData.contains(NBT_LOAD_KEY)) {
                loadLength = keyData.getInt(NBT_LOAD_KEY);
            } else {
                loadLength = 0;
            }
            BindUUID = keyData.getUUID(NBT_UUID_KEY);
            if (tag.contains(NBT_LANG_KEY)) {
                BindLang = keyData.getString(NBT_LANG_KEY);
            } else {
                BindLang = ChatLoading.config.getGLOBAL_LANGUAGE();
            }
            bindData.Bind_data.put(nbtKey, new Tuple3<>(loadLength, BindUUID, BindLang));
        }
        return bindData;
    }

    @Override @MethodsReturnNonnullByDefault
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        for(Map.Entry<String, Tuple3<Integer, UUID, String>> entry : Bind_data.entrySet()) {
            CompoundTag keyData = new CompoundTag();
            keyData.putInt(NBT_LOAD_KEY, entry.getValue().FInt);
            keyData.putUUID(NBT_UUID_KEY, entry.getValue().FUUID);
            keyData.putString(NBT_LANG_KEY, entry.getValue().FLang);
            nbt.put(entry.getKey(), keyData);
        }
        return nbt;
    }

}
