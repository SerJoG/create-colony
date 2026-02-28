package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.serjog.createcolony.resources.CreateResources.Blocks.track;
import static com.serjog.createcolony.resources.CreateResources.Items.metalGirder;

public class TrackPlacementHandler extends SimplePlacementHandler {
    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return track.isBound() && blockState.is(track.get());
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {

        final List<ItemStack> neededItems = new ArrayList<>();
        neededItems.add(new ItemStack(blockState.getBlock().asItem()));

        if (compoundTag != null && compoundTag.contains("Connections", Tag.TAG_LIST)) {
            ListTag connections = compoundTag.getList("Connections", Tag.TAG_COMPOUND);
            for (int i = 0; i < connections.size(); i++) {
                CompoundTag conn = connections.getCompound(i);
                if (conn.getByte("Primary") != 0 && conn.contains("Positions", Tag.TAG_LIST)) {
                    ListTag posList = conn.getList("Positions", Tag.TAG_COMPOUND);
                    if (posList.size() == 2) {
                        BlockPos p0 = readPos(posList.getCompound(0).getIntArray("Pos"));
                        BlockPos p1 = readPos(posList.getCompound(1).getIntArray("Pos"));

                        double distance = Math.sqrt(p0.distSqr(p1));
                        int extraTracks = (int) Math.ceil(distance);

                        if (extraTracks > 1) {
                            neededItems.add(new ItemStack(blockState.getBlock().asItem(), extraTracks));
                        }

                        if (conn.getByte("Girder") != 0 && metalGirder.isBound()) {
                            neededItems.add(new ItemStack(metalGirder.get(), extraTracks * 2));
                        }
                    }
                }
            }
        }
        return neededItems;
    }

    @Override
    public ActionProcessingResult handle(Blueprint blueprint, Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, RotationMirror settings) {
        if (blockState.hasProperty(TrackBlock.SHAPE)) {
            TrackShape trackShape = blockState.getValue(TrackBlock.SHAPE);
            //TrackShape rotatedShape = rotateTrackShape(trackShape, settings.rotation());
            //blockState = blockState.setValue(TrackBlock.SHAPE, rotatedShape);
        }

        if (tileEntityData != null) {
            rotateTrackNbt(tileEntityData, settings);
        }

        boolean success = world.setBlock(pos, blockState, 3);

        if (!success) {
            world.setBlock(pos, blockState, 2);
        }

        if (tileEntityData != null) {
            var be = world.getBlockEntity(pos);
            if (be instanceof TrackBlockEntity trackBe) {
                trackBe.loadWithComponents(tileEntityData, world.registryAccess());
                //trackBe.refreshBlockState();
                trackBe.setChanged();
            }
        }

        return ActionProcessingResult.SUCCESS;
    }

    private TrackShape rotateTrackShape(TrackShape shape, Rotation rotation) {
        TrackShape current = shape;
        for (int i = 0; i < rotation.ordinal(); i++) {
            current = switch (current) {
                // Rette orizzontali
                case XO -> TrackShape.ZO;
                case ZO -> TrackShape.XO;
                // Rotazione dei nodi terminali Bezier
                case TE -> TrackShape.TW;
                case TW -> TrackShape.TN;
                case TN -> TrackShape.TS;
                case TS -> TrackShape.TE;
                // Aggiungi le pendenze (slopes) se le usi nelle schematiche
                case AE -> TrackShape.AW;
                case AW -> TrackShape.AN;
                case AN -> TrackShape.AS;
                case AS -> TrackShape.AE;
                default -> current;
            };
        }
        return current;
    }

    private void rotateTrackNbt(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();
        ListTag connections = nbt.getList("Connections", Tag.TAG_COMPOUND);

        for (int i = 0; i < connections.size(); i++) {
            CompoundTag conn = connections.getCompound(i);

            if (rotation != Rotation.NONE || mirror != Mirror.NONE) {
                for (String type : new String[]{"Starts", "Normals", "Axes"}) {
                    if (conn.contains(type, Tag.TAG_LIST)) {
                        ListTag vList = conn.getList(type, Tag.TAG_COMPOUND);
                        for (int j = 0; j < vList.size(); j++) {
                            CompoundTag vTag = vList.getCompound(j);
                            ListTag vecData = vTag.getList("V", Tag.TAG_DOUBLE);
                            Vec3 vec = new Vec3(vecData.getDouble(0), vecData.getDouble(1), vecData.getDouble(2));
                            Vec3 transformed = transformVec(vec, rotation, mirror);

                            if (type.equals("Starts")) {
                                transformed = transformed.add(getStartsOffset(rm));
                            }

                            ListTag newV = new ListTag();
                            newV.add(net.minecraft.nbt.DoubleTag.valueOf(transformed.x));
                            newV.add(net.minecraft.nbt.DoubleTag.valueOf(transformed.y));
                            newV.add(net.minecraft.nbt.DoubleTag.valueOf(transformed.z));
                            vTag.put("V", newV);
                        }
                    }
                }
            }

            if (conn.contains("Positions", Tag.TAG_LIST)) {
                ListTag pList = conn.getList("Positions", Tag.TAG_COMPOUND);
                ListTag newP = new ListTag();
                for (int k = 0; k < pList.size(); k++) {
                    BlockPos p = readPos(pList.getCompound(k).getIntArray("Pos"));
                    BlockPos mirrored = applyMirror(p, mirror);
                    BlockPos rotated = mirrored.rotate(rotation);
                    CompoundTag pTag = new CompoundTag();
                    pTag.putIntArray("Pos", new int[]{rotated.getX(), rotated.getY(), rotated.getZ()});
                    newP.add(pTag);
                }
                conn.put("Positions", newP);
            }
        }
    }

    private BlockPos applyMirror(BlockPos pos, Mirror mirror) {
        return switch (mirror) {
            case FRONT_BACK -> new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
            case LEFT_RIGHT -> new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
            default -> pos;
        };
    }

    private Vec3 transformVec(Vec3 vec, Rotation rotation, Mirror mirror) {
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

    private Vec3 getStartsOffset(RotationMirror rm) {
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

    private BlockPos readPos(int[] arr) {
        return (arr.length == 3) ? new BlockPos(arr[0], arr[1], arr[2]) : BlockPos.ZERO;
    }
}
