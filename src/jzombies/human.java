package jzombies;

import java.util.List;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;


public class human {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;
	
	public human(ContinuousSpace<Object> space, Grid<Object> grid, int energy){
		this.space=space;
		this.grid=grid;
		this.energy=startingEnergy=energy;
		
	}

@ Watch(watcheeClassName = "jzombies.zombie",
watcheeFieldNames="moved",
query="within_moore 1",
whenToTrigger=WatcherTriggerSchedule.IMMEDIATE)
	
public void run(){
	// Encuentra la ubicación de nuestro hoo-man
	GridPoint pt = grid.getLocation(this);
	// Volvemos a usar la clase GridCellNgh para mirar las
	// celdas de los alrededores. Ahora nos interesan los zombies
	GridCellNgh<zombie> nghCreator= new GridCellNgh<zombie>(grid, pt, zombie.class,1,1);
	List<GridCell<zombie>> gridCells=nghCreator.getNeighborhood(true);
	SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
	
	GridPoint pointWithLeastZombies=null;
	int minCount=Integer.MAX_VALUE;
	for (GridCell<zombie> cell: gridCells){
		if (cell.size()<minCount){
			pointWithLeastZombies = cell.getPoint();
			minCount=cell.size();			
		}
		
	}
	
	if(energy>0){
		moveTowards(pointWithLeastZombies);
	}
	else{
		energy=startingEnergy;
	}
}

public void moveTowards (GridPoint pt){
	// El zombie se moverá hacia pt
	// Solo muévete si no estas en la celda con más humanos
	if (!pt.equals(grid.getLocation(this))){
	// Creates an NdPoint (myPoint) where the Zombie is
		NdPoint myPoint = space.getLocation(this);
	// Convert the GridPoint pt to NdPoint
		NdPoint otherPoint= new NdPoint(pt.getX(), pt.getY());
	// Calculates the angle of the movement
		double angle=SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
	// Sets the vector of movement
		space.moveByVector(this, 2, angle, 0);
		myPoint=space.getLocation(this);
	// Moves the object
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
	energy--;
	}
	
}

}

