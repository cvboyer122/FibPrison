package com.fibonacci.prison.mines;

import com.fibonacci.prison.Prison;
import com.fibonacci.prison.api.WeightedRandomMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.List;

public class Cuboid {

	protected final String worldName;
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;

	public Cuboid(Location l1, Location l2) {
		if (!l1.getWorld().equals(l2.getWorld())) throw new IllegalArgumentException("Locations must be on the same world");
		this.worldName = l1.getWorld().getName();
		this.x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		this.y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		this.z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		this.x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		this.y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		this.z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
	}

	public Cuboid(Location l1) {
		this(l1, l1);
	}

	public void setBlockTypes(Location l1, Location l2, List<Integer> chances, List<Material> materials) {
		int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
		int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
		int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
		int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
		int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
		Location min = new Location(l1.getWorld(), x1, y1, z1);
		Location max = new Location(l2.getWorld(), x2, y2, z2);

		if (!min.getWorld().getName().equals(max.getWorld().getName())) {
			System.out.println("Worlds do not match.");
			return;
		}

		World world = l2.getWorld();

		WeightedRandomMaterial<Material> wrm = new WeightedRandomMaterial<>();
		for (int i = 0; i < chances.size(); i++) {
			wrm.add(chances.get(i), materials.get(i));
		}

		Bukkit.getScheduler().runTask(Prison.getInstance(), () -> {
			for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
						Block block = world.getBlockAt(x, y, z);
						block.setType(wrm.next());
					}
				}
			}
		});
	}

	public Cuboid(Cuboid other) {
		this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
	}

	public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.worldName = world.getName();
		this.x1 = Math.min(x1, x2);
		this.x2 = Math.max(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.z2 = Math.max(z1, z2);
	}


	private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.worldName = worldName;
		this.x1 = Math.min(x1, x2);
		this.x2 = Math.max(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.z2 = Math.max(z1, z2);
	}


	public Location getCenter() {
		int x1 = this.getUpperX() + 1;
		int y1 = this.getUpperY() + 1;
		int z1 = this.getUpperZ() + 1;
		return new Location(this.getWorld(), this.getLowerX() + (x1 - this.getLowerX()) / 2.0, this.getLowerY() + (y1 - this.getLowerY()) / 2.0, this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0);
	}


	public World getWorld() {
		World world = Bukkit.getWorld(this.worldName);
		if (world == null) throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
		return world;
	}


	public int getLowerX() {
		return this.x1;
	}


	public int getLowerY() {
		return this.y1;
	}


	public int getLowerZ() {
		return this.z1;
	}


	public int getUpperX() {
		return this.x2;
	}


	public int getUpperY() {
		return this.y2;
	}


	public int getUpperZ() {
		return this.z2;
	}


	public Block[] corners() {
		Block[] res = new Block[8];
		World w = this.getWorld();
		res[0] = w.getBlockAt(this.x1, this.y1, this.z1);
		res[1] = w.getBlockAt(this.x1, this.y1, this.z2);
		res[2] = w.getBlockAt(this.x1, this.y2, this.z1);
		res[3] = w.getBlockAt(this.x1, this.y2, this.z2);
		res[4] = w.getBlockAt(this.x2, this.y1, this.z1);
		res[5] = w.getBlockAt(this.x2, this.y1, this.z2);
		res[6] = w.getBlockAt(this.x2, this.y2, this.z1);
		res[7] = w.getBlockAt(this.x2, this.y2, this.z2);
		return res;
	}

	public boolean contains(int x, int z) {
		return x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
	}


	public Cuboid expand(CuboidDirection dir, int amount) {
		switch (dir) {
			case North:
				return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
			case South:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
			case East:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
			case West:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
			case Down:
				return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
			case Up:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
			default:
				throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}

	public Cuboid outset(CuboidDirection dir, int amount) {
		Cuboid c;
		switch (dir) {
			case Horizontal:
				c = expand(CuboidDirection.North, amount).expand(CuboidDirection.South, amount).expand(CuboidDirection.East, amount).expand(CuboidDirection.West, amount);
				break;
			case Vertical:
				c = expand(CuboidDirection.Down, amount).expand(CuboidDirection.Up, amount);
				break;
			case Both:
				c = outset(CuboidDirection.Horizontal, amount).outset(CuboidDirection.Vertical, amount);
				break;
			default:
				throw new IllegalArgumentException("Invalid direction " + dir);
		}
		return c;
	}


	public boolean contains(int x, int y, int z) {
		return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
	}

	public boolean contains(Block b) {
		return this.contains(b.getLocation());
	}

	public boolean containsExactXZ(Location location) {
		if (!this.worldName.equals(location.getWorld().getName())) return false;
		return location.getX() >= this.x1 && location.getX() <= this.x2 && location.getZ() >= this.z1 && location.getZ() <= this.z2;
	}

	public boolean contains(Location l) {
		if (!this.worldName.equals(l.getWorld().getName())) return false;
		return this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public Cuboid getFace(CuboidDirection dir) {
		switch (dir) {
			case Down:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
			case Up:
				return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
			case North:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
			case South:
				return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
			case East:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
			case West:
				return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
			default:
				throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}

	public Iterator<Block> iterator() {
		return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
	}


	@Override
	public Cuboid clone() {
		return new Cuboid(this);
	}

	@Override
	public String toString() {
		return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
	}


	public class CuboidIterator implements Iterator<Block> {
		private World w;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;

		public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
			this.w = w;
			this.baseX = x1;
			this.baseY = y1;
			this.baseZ = z1;
			this.sizeX = Math.abs(x2 - x1) + 1;
			this.sizeY = Math.abs(y2 - y1) + 1;
			this.sizeZ = Math.abs(z2 - z1) + 1;
			this.x = this.y = this.z = 0;
		}

		public boolean hasNext() {
			return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
		}

		public Block next() {
			Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
			if (++x >= this.sizeX) {
				this.x = 0;
				if (++this.y >= this.sizeY) {
					this.y = 0;
					++this.z;
				}
			}
			return b;
		}

		public void remove() {
		}
	}

	public enum CuboidDirection {
		North, East, South, West, Up, Down, Horizontal, Vertical, Both, Unknown;

		public CuboidDirection opposite() {
			switch (this) {
				case North:
					return South;
				case East:
					return West;
				case South:
					return North;
				case West:
					return East;
				case Horizontal:
					return Vertical;
				case Vertical:
					return Horizontal;
				case Up:
					return Down;
				case Down:
					return Up;
				case Both:
					return Both;
				default:
					return Unknown;
			}
		}

	}

}
