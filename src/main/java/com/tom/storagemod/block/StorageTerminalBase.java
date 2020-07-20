package com.tom.storagemod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.common.ToolType;

import com.tom.storagemod.tile.TileEntityStorageTerminal;

public abstract class StorageTerminalBase extends ContainerBlock implements IWaterLoggable {
	public static final EnumProperty<TerminalPos> TERMINAL_POS = EnumProperty.create("pos", TerminalPos.class);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_N = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 6.0D);
	private static final VoxelShape SHAPE_S = Block.makeCuboidShape(0.0D, 0.0D, 10.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_E = Block.makeCuboidShape(10.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_W = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 6.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_U = Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape SHAPE_D = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
	public StorageTerminalBase() {
		super(Block.Properties.create(Material.WOOD).hardnessAndResistance(3).harvestTool(ToolType.AXE).lightValue(6));
		setDefaultState(getDefaultState().with(TERMINAL_POS, TerminalPos.CENTER).with(WATERLOGGED, false).with(FACING, Direction.NORTH));
	}

	@Override
	public BlockRenderType getRenderType(BlockState p_149645_1_) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, TERMINAL_POS);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos,
			PlayerEntity player, Hand hand, BlockRayTraceResult rtr) {
		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		TileEntity blockEntity_1 = world.getTileEntity(pos);
		if (blockEntity_1 instanceof TileEntityStorageTerminal) {
			player.openContainer((TileEntityStorageTerminal)blockEntity_1);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getFace().getOpposite();
		IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
		TerminalPos pos = TerminalPos.CENTER;
		if(direction.getAxis() == Direction.Axis.Y) {
			if(direction == Direction.UP)pos = TerminalPos.UP;
			if(direction == Direction.DOWN)pos = TerminalPos.DOWN;
			direction = context.getPlacementHorizontalFacing();
		}
		return this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : direction).
				with(TERMINAL_POS, pos).
				with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}

	@Override
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(TERMINAL_POS)) {
		case CENTER:
			switch (state.get(FACING)) {
			case NORTH:
				return SHAPE_N;
			case SOUTH:
				return SHAPE_S;
			case EAST:
				return SHAPE_E;
			case WEST:
				return SHAPE_W;
			default:
				break;
			}
			break;

		case UP:
			return SHAPE_U;

		case DOWN:
			return SHAPE_D;

		default:
			break;
		}

		return SHAPE_N;
	}

	public static enum TerminalPos implements IStringSerializable {
		CENTER("center"),
		UP("up"),
		DOWN("down")
		;
		private String name;
		private TerminalPos(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
