package com.serjog.createcolony.mixin;

import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.blueprints.v1.BlueprintUtils;
import com.ldtteam.structurize.util.BlockInfo;
import com.serjog.createcolony.ColonyMain;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.UUID;

import static net.minecraft.nbt.DoubleTag.valueOf;

@Mixin(value = BlueprintUtils.class, remap = false)
public class BlueprintUtilsMixin {
    @Inject(
            method = "instantiateTileEntities",
            at = @At("RETURN")
    )
    private static void onCreateColonies$modifyPreviewEntities(Blueprint blueprint, Level level, Map<BlockPos, ModelData> teModelData, CallbackInfoReturnable<Map<BlockPos, BlockEntity>> cir) {
        if (FMLEnvironment.dist != Dist.CLIENT || level == null || !level.getClass().getName().contains("BlueprintBlockAccess")) return;

        Map<BlockPos, BlockEntity> tileEntitiesMap = cir.getReturnValue();
        if (tileEntitiesMap == null || tileEntitiesMap.isEmpty()) return;

        RotationMirror rotationMirror = blueprint.getRotationMirror();
        HolderLookup.Provider registryAccess = blueprint.getRegistryAccess();
        if (registryAccess == null) return;

        for (BlockInfo info : blueprint.getBlockInfoAsList()) {
            CompoundTag nbt = info.getTileEntityData();
            if (nbt == null) continue;

            BlockEntity be = tileEntitiesMap.get(info.getPos());

            if (be instanceof StationBlockEntity || be instanceof TrackBlockEntity || be instanceof  SignalBlockEntity) {
                try {
                    CompoundTag rotatedNbt = nbt.copy();

                    if (be instanceof StationBlockEntity) {
                        create_colony$rotateStationNbtForPreview(rotatedNbt, rotationMirror);
                    } else if (be instanceof SignalBlockEntity) {
                        create_colony$rotateSignalNbtForPreview(rotatedNbt, rotationMirror);
                    } else {
                        create_colony$rotateTrackNbtForPreview(rotatedNbt, rotationMirror);
                    }

                    be.loadWithComponents(rotatedNbt, registryAccess);

                    if (be instanceof StationBlockEntity stationBe) {
                        stationBe.refreshBlockState();
                    }
                    be.setChanged();
                } catch (Exception ignored) {
                }
            }
        }
    }


    @Unique
    private static void create_colony$rotateStationNbtForPreview(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();

        if (nbt.contains("TargetTrack", Tag.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("TargetTrack");
            if (arr.length == 3) {
                BlockPos localPosFromStation = new BlockPos(arr[0], arr[1], arr[2]);
                BlockPos newPosFromStation;
                if (mirror == Mirror.NONE) {
                    newPosFromStation = localPosFromStation.rotate(rotation);
                } else {
                    newPosFromStation = new BlockPos(-localPosFromStation.getX(), localPosFromStation.getY(), localPosFromStation.getZ()).rotate(rotation);
                }
                nbt.putIntArray("TargetTrack", new int[]{newPosFromStation.getX(),  newPosFromStation.getY(), newPosFromStation.getZ()});
                if ((rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90) && mirror == Mirror.NONE && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                } else if ((rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.NONE) && mirror == Mirror.FRONT_BACK && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                }
                if (nbt.contains("Bezier", Tag.TAG_COMPOUND)) {
                    CompoundTag bezierNbt = nbt.getCompound("Bezier");
                    if (bezierNbt.contains("Key", Tag.TAG_INT_ARRAY)) {
                        int[] keyCoords = bezierNbt.getIntArray("Key");

                        int x = keyCoords[0];
                        int y = keyCoords[1];
                        int z = keyCoords[2];

                        BlockPos localPosFromKey = new BlockPos(x, y, z);
                        BlockPos newPosFromKey = localPosFromKey;

                        if (mirror == Mirror.FRONT_BACK) {
                            if (rotation == Rotation.NONE) {
                                newPosFromKey = new BlockPos(-x, y, z);
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                                newPosFromKey = new BlockPos(z, y, x);
                            }  else if (rotation == Rotation.CLOCKWISE_180) {
                                newPosFromKey = new BlockPos(x, y, -z);
                            } else if (rotation == Rotation.CLOCKWISE_90) {
                                newPosFromKey = new BlockPos(-z, y, -x);
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            }
                        }

                        if (mirror == Mirror.NONE) {
                            newPosFromKey = localPosFromKey.rotate(rotation);
                            if (rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90) {
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            }
                        }

                        bezierNbt.putIntArray("Key", new int[]{newPosFromKey.getX(), newPosFromKey.getY(), newPosFromKey.getZ()});
                    }
                }
            }
        }
    }

    @Unique
    private static void create_colony$rotateTrackNbtForPreview(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();
        ListTag connections = nbt.getList("Connections", Tag.TAG_COMPOUND);

        for (int i = 0; i < connections.size(); i++) {
            CompoundTag conn = connections.getCompound(i);

            for (String type : new String[]{"Starts", "Normals", "Axes"}) {
                if (conn.contains(type, Tag.TAG_LIST)) {
                    ListTag vList = conn.getList(type, Tag.TAG_COMPOUND);
                    for (int j = 0; j < vList.size(); j++) {
                        CompoundTag vTag = vList.getCompound(j);
                        ListTag vecData = vTag.getList("V", Tag.TAG_DOUBLE);
                        Vec3 vec = new Vec3(vecData.getDouble(0), vecData.getDouble(1), vecData.getDouble(2));
                        Vec3 transformed = create_colony$transformVec(vec, rotation, mirror);

                        if (type.equals("Starts")) {
                            transformed = transformed.add(create_colony$getStartsOffset(rm));
                        }

                        ListTag newV = new ListTag();
                        newV.add(valueOf(transformed.x));
                        newV.add(valueOf(transformed.y));
                        newV.add(valueOf(transformed.z));
                        vTag.put("V", newV);
                    }
                }
            }

            if (conn.contains("Positions", Tag.TAG_LIST)) {
                ListTag pList = conn.getList("Positions", Tag.TAG_COMPOUND);
                ListTag newP = new ListTag();
                for (int k = 0; k < pList.size(); k++) {
                    BlockPos p = create_colony$readPos(pList.getCompound(k).getIntArray("Pos"));
                    BlockPos mirrored = create_colony$applyMirror(p, mirror);
                    BlockPos rotated = mirrored.rotate(rotation);
                    CompoundTag pTag = new CompoundTag();
                    pTag.putIntArray("Pos", new int[]{rotated.getX(), rotated.getY(), rotated.getZ()});
                    newP.add(pTag);
                }
                conn.put("Positions", newP);
            }
        }
    }

    @Unique
    private static void create_colony$rotateSignalNbtForPreview(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();

        if (nbt.contains("Id")) {
            nbt.putUUID("Id", UUID.randomUUID());
        }

        if (nbt.contains("TargetTrack", Tag.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("TargetTrack");
            ListTag axis = nbt.getList("PrevAxis", Tag.TAG_DOUBLE);
            if (arr.length == 3 && axis.size() == 3) {
                BlockPos localPosFromSignal = new BlockPos(arr[0], arr[1], arr[2]);
                BlockPos newPosFromSignal;
                if (mirror == Mirror.NONE) {
                    newPosFromSignal = localPosFromSignal.rotate(rotation);
                } else {
                    newPosFromSignal = new BlockPos(-localPosFromSignal.getX(), localPosFromSignal.getY(), localPosFromSignal.getZ()).rotate(rotation);
                }
                nbt.putIntArray("TargetTrack", new int[]{newPosFromSignal.getX(),  newPosFromSignal.getY(), newPosFromSignal.getZ()});
                if ((rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180) && mirror == Mirror.NONE && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                } else if ((rotation == Rotation.COUNTERCLOCKWISE_90) && mirror == Mirror.FRONT_BACK && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                }
                if (nbt.contains("Bezier", Tag.TAG_COMPOUND)) {
                    CompoundTag bezierNbt = nbt.getCompound("Bezier");
                    if (bezierNbt.contains("Key", Tag.TAG_INT_ARRAY)) {
                        int[] keyCoords = bezierNbt.getIntArray("Key");

                        int x = keyCoords[0];
                        int y = keyCoords[1];
                        int z = keyCoords[2];

                        BlockPos localPosFromKey = new BlockPos(x, y, z);
                        BlockPos newPosFromKey = localPosFromKey;

                        if (mirror == Mirror.FRONT_BACK) {
                            if (rotation == Rotation.NONE) {
                                newPosFromKey = new BlockPos(-x, y, z);
                            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                                newPosFromKey = new BlockPos(z, y, x);
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            }  else if (rotation == Rotation.CLOCKWISE_180) {
                                newPosFromKey = new BlockPos(x, y, -z);
                            } else if (rotation == Rotation.CLOCKWISE_90) {
                                newPosFromKey = new BlockPos(-z, y, -x);
                            }
                        }

                        if (mirror == Mirror.NONE) {
                            newPosFromKey = localPosFromKey.rotate(rotation);
                            if (rotation == Rotation.CLOCKWISE_90) {
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            }
                        }

                        bezierNbt.putIntArray("Key", new int[]{newPosFromKey.getX(), newPosFromKey.getY(), newPosFromKey.getZ()});
                    }
                }
            }
        }
    }

    @Unique
    private static BlockPos create_colony$applyMirror(BlockPos pos, Mirror mirror) {
        return switch (mirror) {
            case FRONT_BACK -> new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
            case LEFT_RIGHT -> new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
            default -> pos;
        };
    }

    @Unique
    private static Vec3 create_colony$transformVec(Vec3 vec, Rotation rotation, Mirror mirror) {
        double x = vec.x;
        double z = vec.z;

        if (mirror == Mirror.FRONT_BACK) x = -x;
        else if (mirror == Mirror.LEFT_RIGHT) z = -z;

        return switch (rotation) {
            case CLOCKWISE_90 -> new Vec3(-z, vec.y, x);
            case CLOCKWISE_180 -> new Vec3(-x, vec.y, -z);
            case COUNTERCLOCKWISE_90 -> new Vec3(z, vec.y, -x);
            default -> new Vec3(x, vec.y, z);
        };
    }

    @Unique
    private static Vec3 create_colony$getStartsOffset(RotationMirror rm) {
        return switch (rm.ordinal()) {
            case 1 -> new Vec3(1, 0, 0);
            case 2 -> new Vec3(1, 0, 1);
            case 3 -> new Vec3(0, 0, 1);
            case 4 -> new Vec3(1, 0, 0);
            case 5 -> new Vec3(1, 0, 1);
            case 6 -> new Vec3(0, 0, 1);
            default -> new Vec3(0, 0, 0);
        };
    }

    @Unique
    private static BlockPos create_colony$readPos(int[] arr) {
        return (arr.length == 3) ? new BlockPos(arr[0], arr[1], arr[2]) : BlockPos.ZERO;
    }
}
